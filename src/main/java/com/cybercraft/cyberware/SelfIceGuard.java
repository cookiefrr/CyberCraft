package com.cybercraft.cyberware;

import com.cybercraft.cyberware.animation.Trigger;
import com.cybercraft.registry.ModAttachments;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

public final class SelfIceGuard {
  private static final long COOLDOWN = 1200;

  private SelfIceGuard() {}

  public static void applicable(MobEffectEvent.Applicable event) {
    MobEffectInstance effect = event.getEffectInstance();
    if (effect.getEffect().value().isBeneficial()) return;
    LivingEntity entity = event.getEntity();
    if (entity.level().isClientSide || !entity.hasData(ModAttachments.CYBERWARE)) return;
    CyberwareData data = CyberwareManager.get(entity);
    if (!data.has(Cyberware.SELF_ICE) || !CyberwareManager.operationalForDeath(entity)) return;
    long now = entity.level().getGameTime();
    if (now < data.cooldowns().getOrDefault("self_ice", 0L)) return;
    CyberwareManager.set(entity, data.withCooldown("self_ice", now + COOLDOWN));
    event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
    ServerLevel level = (ServerLevel) entity.level();
    level.sendParticles(
        ParticleTypes.END_ROD,
        entity.getX(),
        entity.getY() + entity.getBbHeight() * 0.55,
        entity.getZ(),
        24,
        0.45,
        entity.getBbHeight() * 0.4,
        0.45,
        0.02);
    level.playSound(
        null,
        entity.getX(),
        entity.getY(),
        entity.getZ(),
        SoundEvents.AMETHYST_BLOCK_CHIME,
        SoundSource.PLAYERS,
        1.0f,
        1.4f);
    CyberwareManager.say(
        entity,
        Component.translatable("msg.cybercraft.self_ice").withStyle(ChatFormatting.AQUA));
    CyberwareManager.sendSync(entity);
    ImplantTriggers.play(entity, Trigger.BLOCK);
  }
}
