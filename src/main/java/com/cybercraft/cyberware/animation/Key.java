package com.cybercraft.cyberware.animation;

public record Key(int time, Pose pose, Ease ease) {
  public Key {
    if (time < 0) throw new IllegalArgumentException("negative key time " + time);
    if (ease == null) ease = Ease.LINEAR;
  }

  public static Key at(int time, Pose pose) {
    return new Key(time, pose, Ease.LINEAR);
  }

  public static Key at(int time, Pose pose, Ease ease) {
    return new Key(time, pose, ease);
  }
}
