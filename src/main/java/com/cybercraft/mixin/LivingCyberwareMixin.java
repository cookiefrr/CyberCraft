package com.cybercraft.mixin;

import com.cybercraft.CyberCraft;
import com.cybercraft.cyberware.*;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingCyberwareMixin {
  @Inject(method = "tick", at = @At("TAIL"))
  private void cybercraft$tick(CallbackInfo ci) {
    CyberCraft.tickEntity((LivingEntity) (Object) this);
  }

  @ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true, ordinal = 0)
  private float cybercraft$melee(float amount, DamageSource source, float original) {
    LivingEntity target = (LivingEntity) (Object) this;
    if (!target.level().isClientSide
        && source.getEntity() instanceof LivingEntity attacker
        && source.getDirectEntity() == attacker
        && (source.is(DamageTypes.PLAYER_ATTACK) || source.is(DamageTypes.MOB_ATTACK)))
      return amount + ImplantEffects.meleeBonus(attacker, target);
    return amount;
  }
}
