package com.cybercraft.client;

import com.cybercraft.cyberware.Cyberware;
import com.cybercraft.cyberware.CyberwareManager;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class MantisBladeClientState {
  private static final Map<UUID, BladeAnimation> ANIMATIONS = new HashMap<>();

  public static void set(int entityId, boolean extended, boolean animate) {
    var level = Minecraft.getInstance().level;
    if (level == null) return;
    var entity = level.getEntity(entityId);
    if (entity == null) return;
    var animation = ANIMATIONS.get(entity.getUUID());
    if (animation == null) {
      animation = new BladeAnimation();
      ANIMATIONS.put(entity.getUUID(), animation);
    }
    if (animate) animation.animateTo(extended, System.nanoTime());
    else animation.snapTo(extended);
  }

  public static float progress(LivingEntity entity) {
    if (!CyberwareManager.operational(entity)
        || !CyberwareManager.get(entity).has(Cyberware.MANTIS_BLADES)) return 0;
    if (!(entity instanceof Player)) return 1;
    var animation = ANIMATIONS.get(entity.getUUID());
    return animation == null ? 0 : animation.progress(System.nanoTime());
  }

  public static void clear() {
    ANIMATIONS.clear();
  }

  private MantisBladeClientState() {}
}
