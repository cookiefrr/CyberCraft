package com.cybercraft.cyberware;

import com.cybercraft.CyberCraft;
import java.util.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.*;

public final class SandevistanManager {
  public static final int DURATION_TICKS = 200, COOLDOWN_TICKS = 600;
  public static final double RADIUS = 15, SLOW_AMOUNT = -.85;
  private static final net.minecraft.resources.ResourceLocation SLOW =
      CyberCraft.id("sandevistan_slow");
  private static final Map<UUID, LivingEntity> ACTIVE = new HashMap<>();
  private static final Map<UUID, Mob> SLOWED = new HashMap<>();

  public static void track(LivingEntity entity) {
    if (CyberwareManager.get(entity).sandevistanActiveUntil() > entity.level().getGameTime())
      ACTIVE.put(entity.getUUID(), entity);
  }

  public static void activate(LivingEntity entity) {
    var data = CyberwareManager.get(entity);
    long now = entity.level().getGameTime();
    if (!CyberwareManager.operational(entity)
        || !data.has(Cyberware.SANDEVISTAN) && !data.has(Cyberware.BERSERK)
        || now < data.sandevistanEnd()) return;
    var os = data.has(Cyberware.SANDEVISTAN) ? Cyberware.SANDEVISTAN : Cyberware.BERSERK;
    if (!CyberwareManager.spendTolerance(entity, os.activationCost())
        || !CyberwareManager.operational(entity)) return;
    int duration = DURATION_TICKS + (data.has(Cyberware.AXOLOTL) ? 40 : 0);
    int cooldown =
        Math.max(
            300,
            COOLDOWN_TICKS
                - (data.has(Cyberware.QUANTUM_TUNER) ? 120 : 0)
                - (data.has(Cyberware.NEWTON_MEMORY_BOOST) ? 60 : 0));
    CyberwareManager.set(
        entity,
        CyberwareManager.get(entity).withSandevistan(now + duration, now + duration + cooldown));
    ACTIVE.put(entity.getUUID(), entity);
    refresh(entity);
    var level = (ServerLevel) entity.level();
    level.sendParticles(
        ParticleTypes.ELECTRIC_SPARK,
        entity.getX(),
        entity.getY(.5),
        entity.getZ(),
        40,
        .6,
        .8,
        .6,
        .3);
    level.playSound(
        null,
        entity.blockPosition(),
        net.minecraft.sounds.SoundEvents.BEACON_ACTIVATE,
        net.minecraft.sounds.SoundSource.PLAYERS,
        1,
        1.6f);
    CyberwareManager.sendSync(entity);
  }

  private static void refresh(LivingEntity entity) {
    var data = CyberwareManager.get(entity);
    int remaining =
        (int)
            Math.min(
                Integer.MAX_VALUE, data.sandevistanActiveUntil() - entity.level().getGameTime());
    if (remaining <= 0) return;
    if (data.has(Cyberware.SANDEVISTAN)) {
      OwnedEffects.grant(
          entity,
          "sandevistan",
          new MobEffectInstance(MobEffects.MOVEMENT_SPEED, remaining, 2, true, false, true));
      OwnedEffects.grant(
          entity,
          "sandevistan",
          new MobEffectInstance(MobEffects.DIG_SPEED, remaining, 1, true, false, true));
    } else {
      OwnedEffects.grant(
          entity,
          "berserk",
          new MobEffectInstance(MobEffects.MOVEMENT_SPEED, remaining, 0, true, false, true));
      OwnedEffects.grant(
          entity,
          "berserk",
          new MobEffectInstance(MobEffects.DAMAGE_BOOST, remaining, 1, true, false, true));
      OwnedEffects.grant(
          entity,
          "berserk",
          new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, remaining, 1, true, false, true));
    }
  }

  public static void tick(MinecraftServer server) {
    ACTIVE
        .values()
        .removeIf(
            e ->
                e.isRemoved()
                    || !CyberwareManager.operational(e)
                    || CyberwareManager.get(e).sandevistanActiveUntil() <= e.level().getGameTime());
    Set<UUID> current = new HashSet<>();
    for (var entity : ACTIVE.values()) {
      if (!CyberwareManager.get(entity).has(Cyberware.SANDEVISTAN)) continue;
      for (Mob mob :
          ((ServerLevel) entity.level())
              .getEntitiesOfClass(
                  Mob.class,
                  entity.getBoundingBox().inflate(RADIUS),
                  m -> m != entity && m.isAlive() && m.distanceToSqr(entity) <= RADIUS * RADIUS)) {
        var speed = mob.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) continue;
        if (!speed.hasModifier(SLOW))
          speed.addTransientModifier(
              new AttributeModifier(
                  SLOW, SLOW_AMOUNT, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        SLOWED.put(mob.getUUID(), mob);
        current.add(mob.getUUID());
      }
    }
    for (var mob : new ArrayList<>(SLOWED.values()))
      if (mob.isRemoved() || !current.contains(mob.getUUID())) {
        var speed = mob.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null) speed.removeModifier(SLOW);
        SLOWED.remove(mob.getUUID());
      }
  }

  public static void clear() {
    for (var mob : SLOWED.values()) {
      var speed = mob.getAttribute(Attributes.MOVEMENT_SPEED);
      if (speed != null) speed.removeModifier(SLOW);
    }
    SLOWED.clear();
    ACTIVE.clear();
  }

  private SandevistanManager() {}
}
