package com.cybercraft.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.*;

public final class Targeting {
  public static final double MAX_RANGE = 64;

  public static double rangeForRenderDistance(int chunks) {
    return Math.min(MAX_RANGE, Math.max(16, chunks * 16.0));
  }

  public static boolean isValidTarget(Entity target, Entity viewer) {
    return target instanceof LivingEntity living
        && target != viewer
        && living.isAlive()
        && !living.isSpectator()
        && !living.isRemoved()
        && target.level() == viewer.level();
  }

  public static HitResult pick(LivingEntity viewer, double range) {
    Vec3 eye = viewer.getEyePosition();
    Vec3 end = eye.add(viewer.getLookAngle().scale(range));
    BlockHitResult block =
        viewer
            .level()
            .clip(
                new ClipContext(
                    eye, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, viewer));
    double distance =
        block.getType() == HitResult.Type.MISS
            ? range * range
            : eye.distanceToSqr(block.getLocation());
    EntityHitResult entity =
        ProjectileUtil.getEntityHitResult(
            viewer,
            eye,
            end,
            new AABB(eye, end).inflate(1),
            e -> isValidTarget(e, viewer),
            distance);
    return entity == null ? block : entity;
  }

  private Targeting() {}
}
