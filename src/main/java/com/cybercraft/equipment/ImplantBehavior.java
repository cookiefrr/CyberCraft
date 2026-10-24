package com.cybercraft.equipment;

import net.minecraft.world.entity.LivingEntity;

public interface ImplantBehavior {
  default void tick(LivingEntity entity) {}

  default void equipped(LivingEntity entity) {}

  default void unequipped(LivingEntity entity) {}
}
