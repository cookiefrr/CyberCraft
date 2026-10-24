package com.cybercraft.cyberware.animation;

public enum Ease {
  LINEAR {
    @Override
    public float apply(float t) {
      return t;
    }
  },
  STEP {
    @Override
    public float apply(float t) {
      return t < 1 ? 0 : 1;
    }
  },
  SMOOTH {
    @Override
    public float apply(float t) {
      return t * t * (3 - 2 * t);
    }
  },
  EASE_OUT {
    @Override
    public float apply(float t) {
      float inv = 1 - t;
      return 1 - inv * inv;
    }
  },
  EASE_IN {
    @Override
    public float apply(float t) {
      return t * t;
    }
  };

  public abstract float apply(float t);
}
