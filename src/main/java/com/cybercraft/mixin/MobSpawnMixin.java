package com.cybercraft.mixin;

import com.cybercraft.mob.MobProgression;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MobSpawnMixin {
  @Inject(method = "finalizeSpawn", at = @At("RETURN"))
  private void cybercraft$spawn(
      ServerLevelAccessor level,
      DifficultyInstance difficulty,
      MobSpawnType reason,
      SpawnGroupData group,
      CallbackInfoReturnable<SpawnGroupData> cir) {
    MobProgression.spawn((Mob) (Object) this, reason);
  }
}
