package com.cybercraft.cyberware;

import com.cybercraft.network.CyberwareSyncPayload;
import com.cybercraft.registry.ModAttachments;
import com.cybercraft.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * Cuore del sistema Cyberware.
 *
 * Gestisce la Tolleranza (barra stile mana), la RAM del Cyberdeck e la
 * Capacità Cyberware (la "Humanity" occupata dagli impianti installati).
 *  - La Tolleranza si rigenera col tempo e viene spesa dagli impianti.
 *  - Se arriva a zero scatta il SOVRACCARICO: Nausea e danno periodico
 *    finché la barra non risale sopra OVERLOAD_EXIT.
 *  - La Capacità massima cresce con il livello XP del giocatore.
 *
 * Tutti i metodi vanno chiamati SOLO lato server.
 */
public final class CyberwareManager {

    // --- Parametri di bilanciamento (modificali qui) ---
    public static final float BASE_REGEN = 3.0f;              // tolleranza al secondo
    public static final float OVERLOAD_REGEN_FACTOR = 0.5f;   // rigenerazione dimezzata in sovraccarico
    public static final float OVERLOAD_EXIT = 25.0f;          // soglia per uscire dal sovraccarico
    public static final float OVERLOAD_DAMAGE = 2.0f;         // 1 cuore ogni 3 secondi
    public static final float RAM_REGEN = 0.34f;              // RAM al secondo (circa 1 ogni 3 s)

    public static final float MANTIS_BONUS_DAMAGE = 6.0f;

    // Capacità cyberware: 20 punti di base, +2 per ogni livello XP, fino a 60.
    public static final float BASE_CAPACITY = 20.0f;
    public static final float CAPACITY_PER_LEVEL = 2.0f;
    public static final float MAX_CAPACITY = 60.0f;

    /** Evita che il danno bonus rientri nell'evento AFTER_DAMAGE. */
    private static boolean applyingBonus = false;

    private CyberwareManager() {}

    // ------------------------------------------------------------------
    // Accesso ai dati
    // ------------------------------------------------------------------

    public static CyberwareData get(ServerPlayer player) {
        return player.getAttachedOrCreate(ModAttachments.CYBERWARE);
    }

    public static void set(ServerPlayer player, CyberwareData data) {
        player.setAttached(ModAttachments.CYBERWARE, data);
    }

    public static void sendSync(ServerPlayer player) {
        CyberwareSyncPayload.send(player, get(player));
    }

    // ------------------------------------------------------------------
    // Capacità cyberware (Humanity)
    // ------------------------------------------------------------------

    /** Capacità massima in base al livello XP: 20 + 2 per livello, massimo 60. */
    public static float maxCapacity(ServerPlayer player) {
        CyberwareData data = get(player);
        float bonus = data.has(Cyberware.CHROME_COMPRESSOR) ? 20.0f : 0.0f;
        return Math.min(MAX_CAPACITY, BASE_CAPACITY + player.experienceLevel * CAPACITY_PER_LEVEL) + bonus;
    }

    /** Capacità già occupata dagli impianti installati. */
    public static float usedCapacity(CyberwareData data) {
        float used = 0.0f;
        for (String id : data.implants()) {
            used += Cyberware.byId(id).map(Cyberware::capacityCost).orElse(0.0f);
        }
        return used;
    }

    // ------------------------------------------------------------------
    // Tick (chiamato una volta al secondo)
    // ------------------------------------------------------------------

    public static void tick(ServerPlayer player) {
        CyberwareData data = get(player);

        // Drain passivo: somma di tutti gli impianti sempre attivi.
        float drain = 0.0f;
        for (String id : data.implants()) {
            drain += Cyberware.byId(id).map(Cyberware::passiveDrain).orElse(0.0f);
        }
        float regen = data.overloaded() ? BASE_REGEN * OVERLOAD_REGEN_FACTOR : BASE_REGEN;

        CyberwareData next = data
                .withTolerance(data.tolerance() + regen - drain)
                .withRam(data.ram() + ramRegen(data));

        // Transizioni dello stato di sovraccarico.
        if (!next.overloaded() && next.tolerance() <= 0.0f) {
            next = next.withOverloaded(true);
        } else if (next.overloaded() && next.tolerance() >= OVERLOAD_EXIT) {
            next = next.withOverloaded(false);
            say(player, Component.translatable("msg.cybercraft.recovered").withStyle(ChatFormatting.GREEN));
        }
        next = next.withRam(Math.min(next.ram(), next.maxRam()));
        set(player, next);

        if (next.overloaded()) {
            applyOverloadPenalty(player);
        }
        ImplantEffects.tick(player, next);
        CyberwareSyncPayload.send(player, next);
        sendHud(player, next);
    }

    /** RAM al secondo, migliorata dagli impianti dedicati. */
    public static float ramRegen(CyberwareData data) {
        float regen = RAM_REGEN;
        if (data.has(Cyberware.EX_DISK_RAM_UPGRADE)) {
            regen += 0.10f;
        }
        if (data.has(Cyberware.NEWTON_MEMORY_BOOST)) {
            regen += 0.08f;
        }
        if (data.has(Cyberware.COX2_OPTIMIZER)) {
            regen += 0.04f;
        }
        return regen;
    }

    /** Cyberpsicosi: Nausea continua e un po' di danno ogni 3 secondi. */
    private static void applyOverloadPenalty(ServerPlayer player) {
        player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 140, 0, false, false));
        if ((player.tickCount / 20) % 3 == 0) {
            ServerLevel level = (ServerLevel) player.level();
            player.hurt(level.damageSources().magic(), OVERLOAD_DAMAGE);
        }
    }

    // ------------------------------------------------------------------
    // Spesa di risorse
    // ------------------------------------------------------------------

    /**
     * Consuma tolleranza. Se non basta la barra va a zero e scatta il sovraccarico:
     * è il prezzo di un impianto usato al limite.
     *
     * @return false se il giocatore è già in sovraccarico (impianti bloccati)
     */
    public static boolean spendTolerance(ServerPlayer player, float cost) {
        CyberwareData data = get(player);
        if (data.overloaded()) {
            say(player, Component.translatable("msg.cybercraft.overloaded").withStyle(ChatFormatting.RED));
            return false;
        }
        CyberwareData next = data.withTolerance(data.tolerance() - cost);
        if (next.tolerance() <= 0.0f) {
            next = next.withOverloaded(true);
            say(player, Component.translatable("msg.cybercraft.overload_start").withStyle(ChatFormatting.DARK_RED));
            ServerLevel level = (ServerLevel) player.level();
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS, 1.0f, 0.8f);
        }
        set(player, next);
        CyberwareSyncPayload.send(player, next);
        return true;
    }

    /** Consuma RAM del Cyberdeck. false se non ce n'è abbastanza. */
    public static boolean spendRam(ServerPlayer player, float cost) {
        CyberwareData data = get(player);
        if (data.ram() < cost) {
            return false;
        }
        CyberwareData next = data.withRam(data.ram() - cost);
        set(player, next);
        CyberwareSyncPayload.send(player, next);
        return true;
    }

    // ------------------------------------------------------------------
    // Installazione
    // ------------------------------------------------------------------

    public enum InstallResult { INSTALLED, ALREADY_INSTALLED, SLOT_OCCUPIED, NOT_ENOUGH_CAPACITY }

    public static InstallResult tryInstall(ServerPlayer player, Cyberware cyberware) {
        CyberwareData data = get(player);

        if (data.has(cyberware)) {
            say(player, Component.translatable("msg.cybercraft.already_installed"));
            return InstallResult.ALREADY_INSTALLED;
        }
        if (data.inSlot(cyberware.slot()).isPresent()) {
            say(player, Component.translatable("msg.cybercraft.slot_occupied",
                    Component.translatable(cyberware.slot().translationKey())));
            return InstallResult.SLOT_OCCUPIED;
        }

        float used = usedCapacity(data);
        float max = maxCapacity(player);
        if (used + cyberware.capacityCost() > max) {
            say(player, Component.translatable("msg.cybercraft.not_enough_capacity",
                    Math.round(used), Math.round(max), Math.round(cyberware.capacityCost()))
                    .withStyle(ChatFormatting.RED));
            return InstallResult.NOT_ENOUGH_CAPACITY;
        }

        set(player, data.withImplant(cyberware));
        say(player, Component.translatable("msg.cybercraft.installed",
                Component.translatable(cyberware.translationKey())).withStyle(ChatFormatting.AQUA));
        CyberwareSyncPayload.send(player, get(player));
        ImplantEffects.tick(player, get(player));
        return InstallResult.INSTALLED;
    }

    /**
     * Disinstalla l'impianto nello slot e restituisce l'item al giocatore.
     * Se l'inventario è pieno l'item cade ai piedi.
     */
    public static boolean tryRemove(ServerPlayer player, CyberwareSlot slot) {
        CyberwareData data = get(player);
        java.util.Optional<Cyberware> installed = data.inSlot(slot);
        if (installed.isEmpty()) {
            say(player, Component.translatable("msg.cybercraft.slot_empty",
                    Component.translatable(slot.translationKey())).withStyle(ChatFormatting.YELLOW));
            return false;
        }

        Cyberware cyberware = installed.get();
        CyberwareData next = data.withoutImplant(cyberware);
        if (cyberware == Cyberware.SANDEVISTAN || cyberware == Cyberware.BERSERK) {
            next = next.withSandevistan(0L, 0L);
        }
        set(player, next);

        ItemStack removed = new ItemStack(ModItems.implant(cyberware));
        if (!player.getInventory().add(removed)) {
            player.drop(removed, false);
        }

        ImplantEffects.removeModifiers(player, cyberware);
        ImplantEffects.tick(player, next);
        say(player, Component.translatable("msg.cybercraft.removed",
                Component.translatable(cyberware.translationKey())).withStyle(ChatFormatting.YELLOW));
        CyberwareSyncPayload.send(player, next);
        sendHud(player, next);
        return true;
    }

    // ------------------------------------------------------------------
    // Abilità
    // ------------------------------------------------------------------

    /** Attacco in mischia: delega a ImplantEffects (Lame, Gorilla, Microgeneratore, Lanciarazzi). */
    public static void onMeleeAttack(ServerPlayer player, LivingEntity target) {
        if (applyingBonus || !target.isAlive()) {
            return;
        }
        applyingBonus = true;
        try {
            ImplantEffects.onMeleeHit(player, target);
        } finally {
            applyingBonus = false;
        }
    }

    public static void onKill(ServerPlayer player, LivingEntity victim) {
        ImplantEffects.onKill(player, victim);
    }

    public static void onDamaged(ServerPlayer player, net.minecraft.world.damagesource.DamageSource source) {
        ImplantEffects.onDamaged(player, source);
    }

    public static boolean allowDeath(ServerPlayer player) {
        return ImplantEffects.allowDeath(player, get(player));
    }

    // ------------------------------------------------------------------
    // HUD sulla action bar
    // ------------------------------------------------------------------

    /**
     * HUD compatto. Compare tenendo in mano Neural Link o Cyberdeck, oppure
     * mentre il Sandevistan è attivo/in raffreddamento (così il cooldown è sempre visibile).
     */
    private static void sendHud(ServerPlayer player, CyberwareData data) {
        long now = player.level().getGameTime();
        boolean sandevistanActive = data.sandevistanActiveUntil() > now;
        boolean sandevistanCooling = data.sandevistanEnd() > now;
        boolean berserk = data.has(Cyberware.BERSERK) && !data.has(Cyberware.SANDEVISTAN);

        boolean holding = player.getMainHandItem().is(ModItems.NEURAL_LINK)
                || player.getMainHandItem().is(ModItems.CYBERDECK)
                || player.getOffhandItem().is(ModItems.NEURAL_LINK)
                || player.getOffhandItem().is(ModItems.CYBERDECK);
        if (!holding && !sandevistanActive && !sandevistanCooling) {
            return;
        }

        MutableComponent hud = Component.translatable("hud.cybercraft.status",
                bar(data.tolerance(), CyberwareData.MAX_TOLERANCE, 10),
                Math.round(data.tolerance()), Math.round(CyberwareData.MAX_TOLERANCE),
                bar(data.ram(), data.maxRam(), 10),
                Math.round(data.ram()), Math.round(data.maxRam()),
                Math.round(usedCapacity(data)), Math.round(maxCapacity(player)))
                .withStyle(data.overloaded() ? ChatFormatting.RED : ChatFormatting.AQUA);

        if (sandevistanActive) {
            hud.append(Component.literal("   "));
            hud.append(Component.translatable(berserk ? "hud.cybercraft.berserk.active"
                    : "hud.cybercraft.sandevistan.active",
                    secondsLeft(data.sandevistanActiveUntil(), now)).withStyle(ChatFormatting.GREEN));
        } else if (sandevistanCooling) {
            hud.append(Component.literal("   "));
            hud.append(Component.translatable(berserk ? "hud.cybercraft.berserk.cooldown"
                    : "hud.cybercraft.sandevistan.cooldown",
                    secondsLeft(data.sandevistanEnd(), now)).withStyle(ChatFormatting.YELLOW));
        }
        say(player, hud);
    }

    /** Secondi rimanenti arrotondati per eccesso (per il conto alla rovescia nell'HUD). */
    public static long secondsLeft(long until, long now) {
        return Math.max(0L, (until - now + 19L) / 20L);
    }

    /** Barra testuale. Esempio: ███████░░░ */
    public static String bar(float value, float max, int length) {
        int filled = Math.round(length * value / max);
        filled = Math.max(0, Math.min(length, filled));
        return "█".repeat(filled) + "░".repeat(length - filled);
    }

    /** Messaggio sulla action bar sopra l'inventario. */
    public static void say(ServerPlayer player, Component message) {
        player.displayClientMessage(message, true);
    }
}
