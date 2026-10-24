package com.cybercraft.mixin.client;

import com.cybercraft.client.EntityOutlineManager;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class ScannerColorMixin {
  @Inject(method = "getTeamColor", at = @At("HEAD"), cancellable = true)
  private void cybercraft$color(CallbackInfoReturnable<Integer> cir) {
    if (EntityOutlineManager.outlined((Entity) (Object) this)) cir.setReturnValue(0xFF003C);
  }
}
