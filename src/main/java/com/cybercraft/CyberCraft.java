package com.cybercraft;
import com.cybercraft.cyberware.*;
import com.cybercraft.netrunning.NetrunnerManager;
import com.cybercraft.network.ModNetworking;
import com.cybercraft.registry.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Mod(CyberCraft.MOD_ID)
public final class CyberCraft {
    public static final String MOD_ID = "cybercraft";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath(MOD_ID, path); }
    public CyberCraft(IEventBus bus) {
        ModAttachments.init(bus);
        ModItems.init(bus);
        ModBlocks.init(bus);
        ModCreativeTabs.init(bus);
        bus.addListener(ModNetworking::register);
        NeoForge.EVENT_BUS.addListener(CyberCraft::tick);
        NeoForge.EVENT_BUS.addListener(CyberCraft::damage);
        NeoForge.EVENT_BUS.addListener(CyberCraft::death);
        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent e) -> sync(e));
        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerRespawnEvent e) -> sync(e));
        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerChangedDimensionEvent e) -> sync(e));
        NeoForge.EVENT_BUS.addListener((ServerStoppedEvent e) -> {
            DoubleJumpHandler.clear(); SandevistanManager.clear(); ImplantEffects.clear(); NetrunnerManager.clear();
        });
        LOGGER.info("CyberCraft loaded. Welcome to Night City.");
    }
    private static void tick(ServerTickEvent.Post event) {
        var server = event.getServer();
        DoubleJumpHandler.tick(server);
        SandevistanManager.tick(server);
        if (server.getTickCount() % 20 == 0)
            for (ServerPlayer player : server.getPlayerList().getPlayers()) CyberwareManager.tick(player);
    }
    private static void damage(LivingDamageEvent.Post event) {
        if (event.getNewDamage() <= 0) return;
        if (event.getEntity() instanceof ServerPlayer player) CyberwareManager.onDamaged(player, event.getSource());
        if (event.getSource().getEntity() instanceof ServerPlayer player && event.getSource().getDirectEntity() == player)
            CyberwareManager.onMeleeAttack(player, event.getEntity());
    }
    private static void death(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && !CyberwareManager.allowDeath(player)) {
            event.setCanceled(true); return;
        }
        if (event.getSource().getEntity() instanceof ServerPlayer player) CyberwareManager.onKill(player, event.getEntity());
    }
    private static void sync(PlayerEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            CyberwareManager.sendSync(player);
            ImplantEffects.tick(player, CyberwareManager.get(player));
        }
    }
}
