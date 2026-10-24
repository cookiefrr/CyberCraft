package com.cybercraft.mixin;

import com.cybercraft.netrunning.BlockHacks;
import net.minecraft.core.*;
import net.minecraft.world.level.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SignalGetter.class)
public interface SignalMixin {
  @Inject(
      method = {"getSignal", "getDirectSignal"},
      at = @At("HEAD"),
      cancellable = true)
  private void cybercraft$output(
      BlockPos pos, Direction direction, CallbackInfoReturnable<Integer> cir) {
    if ((Object) this instanceof Level level && BlockHacks.disrupted(level, pos))
      cir.setReturnValue(0);
  }

  @Inject(method = "getControlInputSignal", at = @At("HEAD"), cancellable = true)
  private void cybercraft$control(
      BlockPos pos, Direction direction, boolean diode, CallbackInfoReturnable<Integer> cir) {
    if ((Object) this instanceof Level level && BlockHacks.disrupted(level, pos))
      cir.setReturnValue(0);
  }
}
