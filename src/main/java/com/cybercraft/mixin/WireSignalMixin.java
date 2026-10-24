package com.cybercraft.mixin;

import com.cybercraft.netrunning.BlockHacks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RedStoneWireBlock.class)
public abstract class WireSignalMixin {
  @Inject(method = "calculateTargetStrength", at = @At("HEAD"), cancellable = true)
  private void cybercraft$self(Level level, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
    if (BlockHacks.disrupted(level, pos)) cir.setReturnValue(0);
  }

  @Redirect(
      method = "calculateTargetStrength",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
  private BlockState cybercraft$neighbor(Level level, BlockPos pos) {
    var state = level.getBlockState(pos);
    return state.getBlock() instanceof RedStoneWireBlock && BlockHacks.disrupted(level, pos)
        ? state.setValue(RedStoneWireBlock.POWER, 0)
        : state;
  }
}
