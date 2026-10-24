package com.cybercraft.client;

public final class BladeAnimation {
  public static final long DURATION_NANOS = 300_000_000L;
  private float from;
  private float target;
  private long startNanos;

  public BladeAnimation() {
    snapTo(false);
  }

  public void animateTo(boolean extended, long nowNanos) {
    float next = extended ? 1 : 0;
    if (target == next) return;
    from = progress(nowNanos);
    target = next;
    startNanos = nowNanos;
    if (Math.abs(from - target) < 1e-4f) from = target;
  }

  public void snapTo(boolean extended) {
    from = target = extended ? 1 : 0;
    startNanos = 0;
  }

  public float progress(long nowNanos) {
    if (from == target) return target;
    float t = Math.max(0, Math.min(1, (nowNanos - startNanos) / (float) DURATION_NANOS));
    return from + (target - from) * t * t * (3 - 2 * t);
  }
}
