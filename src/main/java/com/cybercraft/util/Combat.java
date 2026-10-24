package com.cybercraft.util;

import java.util.Optional;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** Funzioni condivise per mira e danno. Usate da Cyberdeck Pistola Smart e Cyberware. */
public final class Combat {

    private Combat() {}

    /** Infligge danno da attacco del giocatore. Solo lato server. */
    public static void hurt(ServerPlayer attacker, LivingEntity target, float amount) {
        ServerLevel level = (ServerLevel) attacker.level();
        target.hurt(level.damageSources().playerAttack(attacker), amount);
    }

    /**
     * Trova il bersaglio più vicino alla linea di mira.
     *
     * @param range   distanza massima in blocchi
     * @param minDot  1.0 = mira perfetta. 0.985 = circa 10 gradi di tolleranza. 0.9945 = circa 6 gradi
     */
    public static Optional<LivingEntity> findTarget(ServerPlayer player, double range, double minDot) {
        ServerLevel level = (ServerLevel) player.level();
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle().normalize();
        AABB box = new AABB(eye, eye.add(look.scale(range))).inflate(3.0);

        LivingEntity best = null;
        double bestDot = minDot;
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, box,
                c -> c != player && c.isAlive() && !c.isSpectator())) {
            Vec3 toTarget = e.getBoundingBox().getCenter().subtract(eye);
            double dist = toTarget.length();
            if (dist < 0.5 || dist > range) {
                continue;
            }
            double dot = look.dot(toTarget.scale(1.0 / dist));
            // Serve visuale libera: niente hack o colpi attraverso i muri.
            if (dot >= bestDot && player.hasLineOfSight(e)) {
                best = e;
                bestDot = dot;
            }
        }
        return Optional.ofNullable(best);
    }

    /** Punto in cui lo sguardo del giocatore incontra un blocco (o fine portata). */
    public static Vec3 clippedEnd(ServerPlayer player, double range) {
        Vec3 eye = player.getEyePosition();
        Vec3 end = eye.add(player.getLookAngle().normalize().scale(range));
        BlockHitResult hit = player.level().clip(
                new ClipContext(eye, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        return hit.getType() == HitResult.Type.MISS ? end : hit.getLocation();
    }

    /** Disegna una scia di particelle tra due punti (il "tracciante"). */
    public static void drawTracer(ServerLevel level, Vec3 from, Vec3 to) {
        Vec3 delta = to.subtract(from);
        double length = delta.length();
        if (length < 0.1) {
            return;
        }
        Vec3 step = delta.scale(0.6 / length);
        Vec3 pos = from;
        for (double d = 0; d < length; d += 0.6) {
            level.sendParticles(ParticleTypes.END_ROD, pos.x, pos.y, pos.z, 1, 0.0, 0.0, 0.0, 0.0);
            pos = pos.add(step);
        }
    }
}
