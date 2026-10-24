package com.cybercraft.netrunning;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LightningBolt;

/**
 * Catalogo dei Quickhack. Ogni voce ha un costo in RAM e un effetto.
 * Per aggiungerne uno basta una nuova costante con la sua lambda.
 */
public enum Quickhack {

    /** Cortocircuito: fulmine visivo più danno elettrico e fuoco. */
    SHORT_CIRCUIT("short_circuit", 4.0f, (level, hacker, target) -> {
        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
        if (bolt != null) {
            bolt.setPos(target.position());
            bolt.setVisualOnly(true); // solo effetto grafico: niente incendi di zona né villager-strega
            level.addFreshEntity(bolt);
        }
        float damage = 6.0f;
        if (com.cybercraft.cyberware.CyberwareManager.get(hacker)
                .has(com.cybercraft.cyberware.Cyberware.COX2_OPTIMIZER)) {
            damage *= 1.25f;
        }
        target.hurt(level.damageSources().lightningBolt(), damage);
        target.igniteForSeconds(4.0f);
    }),

    /** Riavvio Ottico: cecità e il mob perde il bersaglio. */
    OPTIC_REBOOT("optic_reboot", 3.0f, (level, hacker, target) -> {
        target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 200, 0));
        if (target instanceof Mob mob) {
            mob.setTarget(null); // dimentica chi lo stava inseguendo
        }
        level.sendParticles(ParticleTypes.SMOKE, target.getX(), target.getEyeY(), target.getZ(),
                15, 0.2, 0.2, 0.2, 0.02);
    }),

    /** Contagio: veleno e debolezza. */
    CONTAGION("contagion", 5.0f, (level, hacker, target) -> {
        target.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 1));
        target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 0));
        level.sendParticles(ParticleTypes.SCULK_SOUL, target.getX(), target.getY(0.5), target.getZ(),
                10, 0.3, 0.4, 0.3, 0.02);
    });

    /** Effetto applicato al bersaglio. Gira solo sul server. */
    @FunctionalInterface
    public interface Effect {
        void apply(ServerLevel level, ServerPlayer hacker, LivingEntity target);
    }

    private final String id;
    private final float ramCost;
    private final Effect effect;

    Quickhack(String id, float ramCost, Effect effect) {
        this.id = id;
        this.ramCost = ramCost;
        this.effect = effect;
    }

    public float ramCost() { return ramCost; }

    public void apply(ServerLevel level, ServerPlayer hacker, LivingEntity target) {
        effect.apply(level, hacker, target);
    }

    public String translationKey() {
        return "quickhack.cybercraft." + id;
    }

    /** Indice ciclico: valori fuori range tornano all'inizio. */
    public static Quickhack byIndex(int index) {
        Quickhack[] all = values();
        return all[Math.floorMod(index, all.length)];
    }
}
