package com.cybercraft.cyberware.animation;

import java.util.Map;

public record AnimationSet(Clip idle, Clip active, Map<Trigger, Clip> triggers) {
  public AnimationSet {
    triggers = Map.copyOf(triggers);
  }

  public static AnimationSet of(Clip idle, Clip active, Map<Trigger, Clip> triggers) {
    return new AnimationSet(idle, active, triggers);
  }

  public Clip trigger(Trigger trigger) {
    return triggers.get(trigger);
  }
}
