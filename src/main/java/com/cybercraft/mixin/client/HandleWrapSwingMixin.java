package com.cybercraft.mixin.client;

import com.cybercraft.cyberware.Cyberware;
import com.cybercraft.cyberware.CyberwareManager;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class HandleWrapSwingMixin {
  @Inject(
      method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V",
      at = @At("HEAD"))
  private void cybercraft$dampSwing(
      LivingEntity entity,
      float limbSwing,
      float limbSwingAmount,
      float ageInTicks,
      float netHeadYaw,
      float headPitch,
      CallbackInfo ci) {
    HumanoidModel<?> model = (HumanoidModel<?>) (Object) this;
    if (model.attackTime > 0 && CyberwareManager.get(entity).has(Cyberware.HANDLE_WRAP))
      model.attackTime *= 0.7f;
  }
}
