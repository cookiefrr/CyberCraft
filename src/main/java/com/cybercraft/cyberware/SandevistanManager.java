package com.cybercraft.cyberware;

import com.cybercraft.CyberCraft;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.AABB;

/**
 * Sandevistan (slot Operating System).
 *
 * Attivazione via keybind (payload C2S) o tasto destro con il Neural Link.
 * Per 10 secondi il giocatore riceve Speed III + Haste II e i mob entro 15
 * blocchi vengono rallentati dell'85% tramite un AttributeModifier.
 *
 * Il rallentamento usa l'attributo MOVEMENT_SPEED con ADD_MULTIPLIED_TOTAL -0.85:
 * è l'unico modo per ottenere un valore esatto, perché l'effetto Slowness
 * vanilla lavora a scatti del 15%. Il modificatore è "transient", quindi non
 * viene salvato su disco.
 */
public final class SandevistanManager {

    public static final int DURATION_TICKS = 200;   // 10 secondi
    public static final int COOLDOWN_TICKS = 600;   // 30 secondi, dopo la fine dell'effetto
    public static final double RADIUS = 15.0;
    public static final double SLOW_AMOUNT = -0.85;

    private static final ResourceLocation SLOW_MODIFIER_ID = CyberCraft.id("sandevistan_slow");

    /** Mob rallentati e tick in cui va rimosso il modificatore. */
    private static final Map<UUID, Long> SLOWED_MOBS = new HashMap<>();

    private SandevistanManager() {}

    /** Attiva il Sandevistan (o il Berserk) se installato, non sovraccarico e fuori cooldown. */
    public static void activate(ServerPlayer player) {
        CyberwareData data = CyberwareManager.get(player);
        ServerLevel level = (ServerLevel) player.level();
        long now = level.getGameTime();

        if (!data.has(Cyberware.SANDEVISTAN) && !data.has(Cyberware.BERSERK)) {
            CyberwareManager.say(player, Component.translatable("msg.cybercraft.no_sandevistan"));
            return;
        }
        if (data.overloaded()) {
            CyberwareManager.say(player, Component.translatable("msg.cybercraft.overloaded")
                    .withStyle(ChatFormatting.RED));
            return;
        }
        if (now < data.sandevistanEnd()) {
            CyberwareManager.say(player, Component.translatable("msg.cybercraft.cooldown",
                    CyberwareManager.secondsLeft(data.sandevistanEnd(), now))
                    .withStyle(ChatFormatting.YELLOW));
            return;
        }

        if (data.has(Cyberware.SANDEVISTAN)) {
            activateSandevistan(player, data, level, now);
        } else {
            activateBerserk(player, data, level, now);
        }
    }

    private static void activateSandevistan(ServerPlayer player, CyberwareData data,
                                            ServerLevel level, long now) {
        if (!CyberwareManager.spendTolerance(player, Cyberware.SANDEVISTAN.activationCost())) {
            return;
        }
        // Axolotl allunga l'effetto; Quantum Tuner e Newton accorciano il cooldown.
        int duration = DURATION_TICKS + (data.has(Cyberware.AXOLOTL) ? 40 : 0);
        int cooldown = COOLDOWN_TICKS
                - (data.has(Cyberware.QUANTUM_TUNER) ? 120 : 0)
                - (data.has(Cyberware.NEWTON_MEMORY_BOOST) ? 60 : 0);
        cooldown = Math.max(300, cooldown);

        long activeUntil = now + duration;
        data = CyberwareManager.get(player);
        CyberwareManager.set(player, data.withSandevistan(activeUntil, activeUntil + cooldown));
        CyberwareManager.sendSync(player);

        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, duration, 2, false, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, duration, 1, false, false, true));
        slowNearbyMobs(level, player, activeUntil);

        level.sendParticles(ParticleTypes.ELECTRIC_SPARK, player.getX(), player.getY(0.5), player.getZ(),
                40, 0.6, 0.8, 0.6, 0.3);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0f, 1.6f);
        CyberwareManager.say(player, Component.translatable("msg.cybercraft.sandevistan_active")
                .withStyle(ChatFormatting.GREEN));
    }

    private static void activateBerserk(ServerPlayer player, CyberwareData data,
                                        ServerLevel level, long now) {
        if (!CyberwareManager.spendTolerance(player, Cyberware.BERSERK.activationCost())) {
            return;
        }
        int duration = DURATION_TICKS + (data.has(Cyberware.AXOLOTL) ? 40 : 0);
        int cooldown = Math.max(300, COOLDOWN_TICKS
                - (data.has(Cyberware.QUANTUM_TUNER) ? 120 : 0)
                - (data.has(Cyberware.NEWTON_MEMORY_BOOST) ? 60 : 0));

        long activeUntil = now + duration;
        data = CyberwareManager.get(player);
        CyberwareManager.set(player, data.withSandevistan(activeUntil, activeUntil + cooldown));
        CyberwareManager.sendSync(player);

        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, duration, 1, false, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, duration, 1, false, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, duration, 0, false, false, true));

        level.sendParticles(ParticleTypes.LAVA, player.getX(), player.getY(0.8), player.getZ(),
                30, 0.5, 0.7, 0.5, 0.1);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.RAVAGER_ROAR, SoundSource.PLAYERS, 1.0f, 0.9f);
        CyberwareManager.say(player, Component.translatable("msg.cybercraft.berserk_active")
                .withStyle(ChatFormatting.RED));
    }

    /** Da chiamare ogni tick: mantiene attivi gli effetti e pulisce i rallentamenti scaduti. */
    public static void tick(MinecraftServer server) {
        long now = server.overworld().getGameTime();
        cleanupExpired(server, now);

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            CyberwareData data = CyberwareManager.get(player);
            long activeUntil = data.sandevistanActiveUntil();
            if (activeUntil <= now || !data.has(Cyberware.SANDEVISTAN)) {
                continue;
            }
            // Ogni mezzo secondo aggancia anche i mob appena entrati nel raggio.
            if (now % 10 == 0) {
                slowNearbyMobs((ServerLevel) player.level(), player, activeUntil);
            }
            // Rinnova Speed III / Haste II senza lasciarli attivi oltre la durata.
            if (now % 20 == 0) {
                int remaining = (int) Math.min(40L, activeUntil - now);
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, remaining, 2, true, false, true));
                player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, remaining, 1, true, false, true));
            }
        }
    }

    private static void slowNearbyMobs(ServerLevel level, ServerPlayer player, long activeUntil) {
        AABB area = player.getBoundingBox().inflate(RADIUS);
        for (Mob mob : level.getEntitiesOfClass(Mob.class, area, Mob::isAlive)) {
            AttributeInstance speed = mob.getAttribute(Attributes.MOVEMENT_SPEED);
            if (speed == null) {
                continue;
            }
            if (!speed.hasModifier(SLOW_MODIFIER_ID)) {
                speed.addTransientModifier(new AttributeModifier(SLOW_MODIFIER_ID, SLOW_AMOUNT,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            }
            SLOWED_MOBS.merge(mob.getUUID(), activeUntil, Math::max);
        }
    }

    private static void cleanupExpired(MinecraftServer server, long now) {
        if (SLOWED_MOBS.isEmpty()) {
            return;
        }
        Iterator<Map.Entry<UUID, Long>> iterator = SLOWED_MOBS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Long> entry = iterator.next();
            if (entry.getValue() > now) {
                continue;
            }
            Entity entity = findEntity(server, entry.getKey());
            if (entity instanceof LivingEntity living) {
                AttributeInstance speed = living.getAttribute(Attributes.MOVEMENT_SPEED);
                if (speed != null) {
                    speed.removeModifier(SLOW_MODIFIER_ID);
                }
            }
            iterator.remove();
        }
    }

    private static Entity findEntity(MinecraftServer server, UUID uuid) {
        for (ServerLevel level : server.getAllLevels()) {
            Entity entity = level.getEntity(uuid);
            if (entity != null) {
                return entity;
            }
        }
        return null;
    }

    /** Pulizia alla chiusura del server (utile in singleplayer quando si cambia mondo). */
    public static void clear() {
        SLOWED_MOBS.clear();
    }
}
