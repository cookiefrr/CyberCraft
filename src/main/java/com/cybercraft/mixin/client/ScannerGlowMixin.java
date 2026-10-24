package com.cybercraft.mixin.client;

import com.cybercraft.client.EntityOutlineManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class ScannerGlowMixin {
  @Inject(method = "shouldEntityAppearGlowing", at = @At("HEAD"), cancellable = true)
  private void cybercraft$glow(Entity entity, CallbackInfoReturnable<Boolean> cir) {
    if (EntityOutlineManager.outlined(entity)) cir.setReturnValue(true);
  }
}
