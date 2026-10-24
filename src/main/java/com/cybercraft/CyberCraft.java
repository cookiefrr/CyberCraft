package com.cybercraft;

import com.cybercraft.cyberware.CyberwareManager;
import com.cybercraft.cyberware.DoubleJumpHandler;
import com.cybercraft.cyberware.ImplantEffects;
import com.cybercraft.cyberware.SandevistanManager;
import com.cybercraft.netrunning.NetrunnerManager;
import com.cybercraft.network.ModNetworking;
import com.cybercraft.registry.ModAttachments;
import com.cybercraft.registry.ModBlocks;
import com.cybercraft.registry.ModCreativeTabs;
import com.cybercraft.registry.ModItems;
import com.cybercraft.registry.ModWorldGen;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entry point della mod CyberCraft (lato comune: client e server).
 * Registra contenuti ed eventi. Tutta la logica di gioco vive sul server.
 */
public class CyberCraft implements ModInitializer {

    public static final String MOD_ID = "cybercraft";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    /** Crea un ResourceLocation nel namespace della mod. */
    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        // L'ordine conta: gli attachment servono prima di tutto il resto.
        ModAttachments.init();
        ModNetworking.init(); // canali C2S/S2C per keybind, scanner e sincronizzazione
        ModItems.init();
        ModBlocks.init();
        ModWorldGen.init(); // dopo i blocchi: i minerali devono esistere
        ModCreativeTabs.init(); // dopo item e blocchi perché li elenca

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            // Ogni tick: reset del doppio salto e mantenimento del rallentamento Sandevistan.
            DoubleJumpHandler.tick(server);
            SandevistanManager.tick(server);

            // Una volta al secondo: rigenerazione, drain passivo, effetti impianti e sync client.
            if (server.getTickCount() % 20 != 0) {
                return;
            }
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                CyberwareManager.tick(player);
            }
        });

        // Evita che gli stati temporanei sopravvivano al cambio di mondo in singleplayer.
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            DoubleJumpHandler.clear();
            SandevistanManager.clear();
            ImplantEffects.clear();
            NetrunnerManager.clear();
        });

        // Danno subito dal giocatore: Biomonitor, Adrenalina, Shock-n-Awe.
        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamage, damageTaken, blocked) -> {
            if (blocked || damageTaken <= 0.0f) {
                return;
            }
            if (entity instanceof ServerPlayer player) {
                CyberwareManager.onDamaged(player, source);
            }
            // Lame da Mantide / Gorilla / Microgeneratore sul colpo in mischia.
            if (source.getEntity() instanceof ServerPlayer player
                    && source.getDirectEntity() == player) {
                CyberwareManager.onMeleeAttack(player, entity);
            }
        });

        // Guarigione al kill (Heal-On-Kill).
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (source.getEntity() instanceof ServerPlayer player) {
                CyberwareManager.onKill(player, entity);
            }
        });

        // Secondo Cuore: annulla la morte e riporta il giocatore in piedi.
        ServerPlayerEvents.ALLOW_DEATH.register((player, source, amount) -> CyberwareManager.allowDeath(player));

        // Dopo la respawn gli attributi transient vanno riapplicati e il client risincronizzato.
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            CyberwareManager.sendSync(newPlayer);
            ImplantEffects.tick(newPlayer, CyberwareManager.get(newPlayer));
        });

        LOGGER.info("CyberCraft caricato. Benvenuto a Night City.");
    }
}
