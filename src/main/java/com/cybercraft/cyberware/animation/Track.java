package com.cybercraft.cyberware.animation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public record Track(String bone, List<Key> keys) {
  public Track {
    if (bone == null || bone.isBlank()) throw new IllegalArgumentException("blank bone");
    if (keys.isEmpty()) throw new IllegalArgumentException("track " + bone + " has no keys");
    List<Key> sorted = new ArrayList<>(keys);
    sorted.sort(Comparator.comparingInt(Key::time));
    keys = List.copyOf(sorted);
  }

  public int endTime() {
    return keys.getLast().time();
  }

  public Pose sample(int time) {
    Key first = keys.getFirst();
    if (time <= first.time()) return first.pose();
    Key last = keys.getLast();
    if (time >= last.time()) return last.pose();
    for (int i = 1; i < keys.size(); i++) {
      Key to = keys.get(i);
      if (time < to.time()) {
        Key from = keys.get(i - 1);
        float span = to.time() - from.time();
        if (span <= 0) return to.pose();
        float t = from.ease().apply((time - from.time()) / span);
        return from.pose().lerp(to.pose(), t);
      }
    }
    return last.pose();
  }
}
