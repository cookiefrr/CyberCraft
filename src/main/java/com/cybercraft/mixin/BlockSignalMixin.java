package com.cybercraft.mixin;

import com.cybercraft.netrunning.BlockHacks;
import net.minecraft.core.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockSignalMixin {
  @Inject(
      method = {"getSignal", "getDirectSignal"},
      at = @At("HEAD"),
      cancellable = true)
  private void cybercraft$output(
      BlockGetter getter, BlockPos pos, Direction direction, CallbackInfoReturnable<Integer> cir) {
    if (getter instanceof Level level && BlockHacks.disrupted(level, pos)) cir.setReturnValue(0);
  }
}
