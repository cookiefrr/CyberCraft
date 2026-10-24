package com.cybercraft.netrunning;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

public enum Quickhack {
  SHORT_CIRCUIT(
      "short_circuit",
      4.0f,
      (level, hacker, target) -> {
        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
        if (bolt != null) {
          bolt.setPos(target.position());
          bolt.setVisualOnly(true);
          level.addFreshEntity(bolt);
        }
        float damage = 6.0f;
        if (com.cybercraft.cyberware.CyberwareManager.get(hacker)
            .has(com.cybercraft.cyberware.Cyberware.COX2_OPTIMIZER)) {
          damage *= 1.25f;
        }
        target.hurt(level.damageSources().indirectMagic(hacker, hacker), damage);
        target.igniteForSeconds(4.0f);
      }),

  OPTIC_REBOOT(
      "optic_reboot",
      3.0f,
      (level, hacker, target) -> {
        target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 200, 0));
        if (target instanceof Mob mob) {
          mob.setTarget(null);
        }
        level.sendParticles(
            ParticleTypes.SMOKE,
            target.getX(),
            target.getEyeY(),
            target.getZ(),
            15,
            0.2,
            0.2,
            0.2,
            0.02);
      }),

  CONTAGION(
      "contagion",
      5.0f,
      (level, hacker, target) -> {
        target.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 1));
        target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 0));
        level.sendParticles(
            ParticleTypes.SCULK_SOUL,
            target.getX(),
            target.getY(0.5),
            target.getZ(),
            10,
            0.3,
            0.4,
            0.3,
            0.02);
      }),
  REMOTE_ACCESS("remote_access", 3, (level, hacker, target) -> {}),
  DISRUPTION("disruption", 5, (level, hacker, target) -> {});

  @FunctionalInterface
  public interface Effect {
    void apply(ServerLevel level, LivingEntity hacker, LivingEntity target);
  }

  private final String id;
  private final float ramCost;
  private final Effect effect;

  Quickhack(String id, float ramCost, Effect effect) {
    this.id = id;
    this.ramCost = ramCost;
    this.effect = effect;
  }

  public float ramCost() {
    return ramCost;
  }

  public net.minecraft.resources.ResourceLocation resource() {
    return com.cybercraft.CyberCraft.id(id);
  }

  public boolean blockHack() {
    return this == REMOTE_ACCESS || this == DISRUPTION;
  }

  public static java.util.Optional<Quickhack> byId(net.minecraft.resources.ResourceLocation id) {
    return java.util.Arrays.stream(values()).filter(h -> h.resource().equals(id)).findFirst();
  }

  public static int indexOf(net.minecraft.resources.ResourceLocation id) {
    return byId(id).map(Enum::ordinal).orElse(0);
  }

  public void apply(ServerLevel level, LivingEntity hacker, LivingEntity target) {
    effect.apply(level, hacker, target);
  }

  public String translationKey() {
    return "quickhack.cybercraft." + id;
  }

  public static Quickhack byIndex(int index) {
    Quickhack[] all = values();
    return all[Math.floorMod(index, all.length)];
  }
}
