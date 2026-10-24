package com.cybercraft.cyberware.animation;

public record Pose(
    float tx,
    float ty,
    float tz,
    float rx,
    float ry,
    float rz,
    float sx,
    float sy,
    float sz,
    float alpha) {
  public static final Pose REST = new Pose(0, 0, 0, 0, 0, 0, 1, 1, 1, 1);

  public static Pose translation(float x, float y, float z) {
    return new Pose(x, y, z, 0, 0, 0, 1, 1, 1, 1);
  }

  public static Pose rotation(float x, float y, float z) {
    return new Pose(0, 0, 0, x, y, z, 1, 1, 1, 1);
  }

  public static Pose scale(float x, float y, float z) {
    return new Pose(0, 0, 0, 0, 0, 0, x, y, z, 1);
  }

  public static Pose alpha(float alpha) {
    return new Pose(0, 0, 0, 0, 0, 0, 1, 1, 1, alpha);
  }

  public Pose lerp(Pose other, float t) {
    return new Pose(
        tx + (other.tx - tx) * t,
        ty + (other.ty - ty) * t,
        tz + (other.tz - tz) * t,
        rx + (other.rx - rx) * t,
        ry + (other.ry - ry) * t,
        rz + (other.rz - rz) * t,
        sx + (other.sx - sx) * t,
        sy + (other.sy - sy) * t,
        sz + (other.sz - sz) * t,
        alpha + (other.alpha - alpha) * t);
  }
}
