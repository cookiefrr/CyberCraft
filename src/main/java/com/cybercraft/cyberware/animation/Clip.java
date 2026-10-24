package com.cybercraft.cyberware.animation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record Clip(String id, int duration, Loop loop, int priority, List<Track> tracks) {
  public static final int PRIORITY_IDLE = 10;
  public static final int PRIORITY_ACTIVE = 20;
  public static final int PRIORITY_TRIGGER = 30;

  public Clip {
    if (id == null || id.isBlank()) throw new IllegalArgumentException("blank clip id");
    if (duration <= 0) throw new IllegalArgumentException("clip " + id + " duration " + duration);
    if (loop == null) loop = Loop.ONCE;
    tracks = List.copyOf(tracks);
  }

  public static Clip of(String id, int duration, Loop loop, List<Track> tracks) {
    return new Clip(id, duration, loop, PRIORITY_TRIGGER, tracks);
  }

  public static Clip idle(String id, int duration, List<Track> tracks) {
    return new Clip(id, duration, Loop.LOOP, PRIORITY_IDLE, tracks);
  }

  public static Clip active(String id, int duration, List<Track> tracks) {
    return new Clip(id, duration, Loop.LOOP, PRIORITY_ACTIVE, tracks);
  }

  public boolean owns(String bone) {
    for (Track track : tracks) if (track.bone().equals(bone)) return true;
    return false;
  }

  public int timeAt(long elapsedMs) {
    if (elapsedMs < 0) return 0;
    if (loop == Loop.LOOP) return (int) (elapsedMs % duration);
    return (int) Math.min(elapsedMs, duration);
  }

  public boolean finished(long elapsedMs) {
    return loop == Loop.ONCE && elapsedMs >= duration;
  }

  public Pose sample(String bone, long elapsedMs) {
    for (Track track : tracks) {
      if (track.bone().equals(bone)) return track.sample(timeAt(elapsedMs));
    }
    return null;
  }

  public Map<String, Pose> sampleAll(long elapsedMs) {
    int time = timeAt(elapsedMs);
    Map<String, Pose> poses = new HashMap<>(tracks.size() * 2);
    for (Track track : tracks) poses.put(track.bone(), track.sample(time));
    return poses;
  }
}
