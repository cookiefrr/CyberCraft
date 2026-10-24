package com.cybercraft.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;

public final class ScannerState {

  private static final long FADE_OUT_MS = 120L;

  public static boolean active;
  public static long targetChangedAt;
  public static java.util.UUID targetId;
  public static long targetLostAt;
  public static LivingEntity lastTarget;
  public static long uploadStart;
  public static long uploadResultAt;
  public static boolean uploadSuccess;
  public static int uploadCode;

  private ScannerState() {}

  public static void toggle() {
    active = !active;
    targetId = null;
    lastTarget = null;
    targetLostAt = 0L;
    targetChangedAt = 0L;
    uploadStart = 0L;
    uploadResultAt = 0L;
  }

  public static void close() {
    active = false;
    targetId = null;
    lastTarget = null;
    targetLostAt = 0L;
    uploadStart = 0L;
    uploadResultAt = 0L;
  }

  public static void onTarget(LivingEntity target) {
    long now = System.currentTimeMillis();
    if (target == null) {
      if (targetId != null) {
        targetLostAt = now;
      }
      targetId = null;
      return;
    }
    lastTarget = target;
    targetLostAt = 0L;
    if (!target.getUUID().equals(targetId)) {
      targetId = target.getUUID();
      targetChangedAt = now;
    }
  }

  public static LivingEntity fadingTarget(Minecraft client) {
    if (targetLostAt == 0L || System.currentTimeMillis() - targetLostAt > FADE_OUT_MS) {
      lastTarget = null;
      return null;
    }
    if (lastTarget == null || lastTarget.level() != client.level || lastTarget.isRemoved()) {
      return null;
    }
    return lastTarget;
  }

  public static float fadeAlpha() {
    if (targetLostAt == 0L) {
      return 1.0f;
    }
    float remaining = 1.0f - (System.currentTimeMillis() - targetLostAt) / (float) FADE_OUT_MS;
    return Math.max(0.0f, Math.min(1.0f, remaining));
  }

  public static void beginUpload() {
    uploadStart = System.currentTimeMillis();
    uploadResultAt = 0L;
    uploadSuccess = false;
  }

  public static void finishUpload(boolean success, int code) {
    uploadResultAt = System.currentTimeMillis();
    uploadSuccess = success;
    uploadCode = code;
  }
}
