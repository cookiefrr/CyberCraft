package com.cybercraft.mixin.client;

import com.cybercraft.client.ClientEquipment;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerArmMixin {
  @Inject(method = "renderHand", at = @At("TAIL"))
  private void cybercraft$arm(
      PoseStack pose,
      MultiBufferSource buffer,
      int light,
      AbstractClientPlayer player,
      ModelPart arm,
      ModelPart sleeve,
      CallbackInfo ci) {
    pose.pushPose();
    arm.translateAndRotate(pose);
    ClientEquipment.arm(player, pose, buffer, light);
    pose.popPose();
  }
}
