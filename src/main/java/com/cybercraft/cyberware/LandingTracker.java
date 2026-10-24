package com.cybercraft.cyberware;

import com.cybercraft.cyberware.animation.Trigger;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class LandingTracker {
  private static final Map<UUID, Boolean> AIRBORNE = new HashMap<>();
  private static final Map<UUID, Float> FALL = new HashMap<>();

  private LandingTracker() {}

  public static void tick(LivingEntity entity) {
    UUID id = entity.getUUID();
    boolean airborne = !entity.onGround();
    Boolean was = AIRBORNE.put(id, airborne);
    float fall = FALL.getOrDefault(id, 0f);
    if (was != null && was && entity.onGround() && fall > 0.5f)
      ImplantTriggers.play(entity, Trigger.LAND);
    FALL.put(id, airborne ? (float) entity.fallDistance : 0f);
  }

  public static void forget(Entity entity) {
    AIRBORNE.remove(entity.getUUID());
    FALL.remove(entity.getUUID());
  }

  public static void clear() {
    AIRBORNE.clear();
    FALL.clear();
  }
}
