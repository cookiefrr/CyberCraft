package com.cybercraft.client;

import com.cybercraft.cyberware.Cyberware;
import com.cybercraft.cyberware.CyberwareData;
import com.cybercraft.cyberware.CyberwareManager;
import com.cybercraft.cyberware.animation.AnimationSet;
import com.cybercraft.cyberware.animation.Clip;
import com.cybercraft.cyberware.animation.ImplantAnimations;
import com.cybercraft.cyberware.animation.Pose;
import com.cybercraft.network.ImplantAnimationPayload;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public final class ImplantAnimationClient {
  private static final long IDLE_STEP_MS = 100;

  private record Playing(Clip clip, long start) {}

  private static final Map<Integer, Map<String, Playing>> PLAYING = new HashMap<>();
  private static final Map<String, Boolean> ACTIVE = new HashMap<>();
  private static final Map<String, Long> SWING_AT = new HashMap<>();
  private static final Map<String, Boolean> MANTIS_RIGHT = new HashMap<>();
  private static final Map<Integer, Boolean> SCANNING = new HashMap<>();
  private static Level level;

  private ImplantAnimationClient() {}

  public static void play(ImplantAnimationPayload payload) {
    var client = Minecraft.getInstance();
    var entity = client.level == null ? null : client.level.getEntity(payload.entityId());
    if (!(entity instanceof LivingEntity living)) return;
    ensureLevel(living.level());
    play(living, payload.implant(), payload.clip(), now(living.level()));
  }

  public static Map<String, Pose> bones(LivingEntity entity, String implant) {
    AnimationSet set = ImplantAnimations.get(implant);
    if (set == null) return Map.of();
    ensureLevel(entity.level());
    var client = Minecraft.getInstance();
    if (client.player != null
        && entity != client.player
        && client.player.distanceToSqr(entity) > 64 * 64) return Map.of();

    long now = now(entity.level());
    scanTransition(entity, now);
    CyberwareData data = CyberwareManager.get(entity);
    boolean active = isActive(entity, implant, data, client);
    transitions(entity, implant, active, now);
    swing(entity, implant, now);

    Map<String, Pose> poses = new HashMap<>();
    if (set.idle() != null) poses.putAll(set.idle().sampleAll(now - now % IDLE_STEP_MS));
    Clip activeClip = active ? set.active() : null;
    if (activeClip != null) poses.putAll(activeClip.sampleAll(now));
    Playing playing = current(entity.getId(), implant, now);
    if (playing != null) poses.putAll(playing.clip().sampleAll(now - playing.start()));
    return poses;
  }

  public static void clear() {
    PLAYING.clear();
    ACTIVE.clear();
    SWING_AT.clear();
    MANTIS_RIGHT.clear();
    SCANNING.clear();
    level = null;
  }

  private static boolean isActive(
      LivingEntity entity, String implant, CyberwareData data, Minecraft client) {
    long gameTime = entity.level().getGameTime();
    float max = entity.getMaxHealth();
    float health = max <= 0 ? 1 : entity.getHealth() / max;
    boolean sandevistan = data.sandevistanActiveUntil() > gameTime;
    return switch (implant) {
      case "sandevistan" -> sandevistan;
      case "berserk" -> sandevistan && !data.has(Cyberware.SANDEVISTAN);
      case "optical_camo" -> entity.isInvisible();
      case "smart_link" ->
          entity == client.player && ScannerState.active && ScannerState.targetId != null;
      case "kerenzikov" -> sandevistan || health < 0.35f;
      case "biomonitor" -> health < 0.35f;
      case "lynx_paws" -> entity.isShiftKeyDown();
      case "jenkins_tendons" -> entity.isSprinting();
      case "fireproof_grounding" -> entity.isOnFire();
      case "mantis_blades" -> MantisBladeClientState.progress(entity) > 0.5f;
      default -> false;
    };
  }

  private static void transitions(
      LivingEntity entity, String implant, boolean active, long now) {
    String key = entity.getId() + "|" + implant;
    Boolean previous = ACTIVE.put(key, active);
    if (previous == null || previous == active) return;
    switch (implant) {
      case "sandevistan", "berserk" ->
          play(entity, implant, active ? "activate" : "deactivate", now);
      case "optical_camo" -> play(entity, implant, active ? "fade_in" : "fade_out", now);
      case "mantis_blades" -> play(entity, implant, active ? "deploy" : "retract", now);
      default -> {}
    }
  }

  private static void scanTransition(LivingEntity entity, long now) {
    if (Minecraft.getInstance().player != entity) return;
    boolean scanning = ScannerState.active;
    Boolean previous = SCANNING.put(entity.getId(), scanning);
    if (previous == null || previous == scanning || !scanning) return;
    play(entity, "kiroshi_optics", "scan", now);
    play(entity, "behavioral_imprint", "imprint", now);
  }

  private static void swing(LivingEntity entity, String implant, long now) {
    if (!entity.swinging || entity.swingTime > 2) return;
    String key = entity.getId() + "|" + implant;
    Long last = SWING_AT.get(key);
    if (last != null && now - last < 150) return;
    SWING_AT.put(key, now);
    switch (implant) {
      case "smart_link" -> play(entity, implant, "attack", now);
      case "handle_wrap" -> play(entity, implant, "grip", now);
      case "mantis_blades" -> {
        boolean right = !MANTIS_RIGHT.getOrDefault(key, false);
        MANTIS_RIGHT.put(key, right);
        play(entity, implant, right ? "slash_r" : "slash_l", now);
      }
      default -> {}
    }
  }

  private static void play(LivingEntity entity, String implant, String clipId, long now) {
    Clip clip = ImplantAnimations.find(ImplantAnimations.get(implant), clipId);
    if (clip == null) return;
    PLAYING.computeIfAbsent(entity.getId(), id -> new HashMap<>()).put(implant, new Playing(clip, now));
  }

  private static Playing current(int entityId, String implant, long now) {
    var byImplant = PLAYING.get(entityId);
    if (byImplant == null) return null;
    Playing playing = byImplant.get(implant);
    if (playing == null) return null;
    if (playing.clip().finished(now - playing.start())) {
      byImplant.remove(implant);
      if (byImplant.isEmpty()) PLAYING.remove(entityId);
      return null;
    }
    return playing;
  }

  private static void ensureLevel(Level current) {
    if (level != current) {
      clear();
      level = current;
    }
  }

  private static long now(Level level) {
    return level.getGameTime() * 50L;
  }
}
