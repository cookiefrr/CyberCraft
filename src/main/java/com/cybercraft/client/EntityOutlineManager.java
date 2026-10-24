package com.cybercraft.client;

import com.cybercraft.util.Targeting;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class EntityOutlineManager {
  private static final Set<Integer> OUTLINED = new HashSet<>();
  private static ClientLevel level;
  private static int ticks;

  public static boolean outlined(Entity entity) {
    return entity.level() == level && ScannerState.active && OUTLINED.contains(entity.getId());
  }

  public static void tick(Minecraft client) {
    if (client.level != level) {
      reset();
      level = client.level;
    }
    if (client.player == null || !ScannerState.active) {
      OUTLINED.clear();
      return;
    }
    if (++ticks % 5 != 0) return;
    OUTLINED.clear();
    double range = Targeting.rangeForRenderDistance(client.options.renderDistance().get());
    var eye = client.player.getEyePosition();
    var look = client.player.getLookAngle();
    client
        .level
        .getEntitiesOfClass(
            LivingEntity.class,
            client.player.getBoundingBox().inflate(range),
            e -> Targeting.isValidTarget(e, client.player))
        .stream()
        .filter(
            e ->
                e.distanceToSqr(client.player) <= range * range
                    && look.dot(e.getBoundingBox().getCenter().subtract(eye).normalize()) > 0
                    && client.player.hasLineOfSight(e))
        .sorted(Comparator.comparingDouble(e -> e.distanceToSqr(client.player)))
        .limit(48)
        .forEach(e -> OUTLINED.add(e.getId()));
  }

  public static void reset() {
    OUTLINED.clear();
    level = null;
    ticks = 0;
  }

  private EntityOutlineManager() {}
}
