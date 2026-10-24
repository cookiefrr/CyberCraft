package com.cybercraft.client;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class BladeAnimationTest {
  private static final long DURATION = 300_000_000L;

  @Test
  void extendsOverTheFullDuration() {
    var animation = new BladeAnimation();
    assertEquals(0, animation.progress(0), 1e-4);
    animation.animateTo(true, 0);
    assertEquals(0, animation.progress(0), 1e-4);
    float middle = animation.progress(DURATION / 2);
    assertTrue(middle > .4f && middle < .6f, "Midpoint should be roughly halfway: " + middle);
    assertEquals(1, animation.progress(DURATION), 1e-4);
    assertEquals(1, animation.progress(DURATION * 4), 1e-4);
  }

  @Test
  void reversesFromTheCurrentProgress() {
    var animation = new BladeAnimation();
    animation.animateTo(true, 0);
    float before = animation.progress(DURATION / 2);
    animation.animateTo(false, DURATION / 2);
    float after = animation.progress(DURATION / 2);
    assertEquals(before, after, 1e-4, "Reversal must not jump");
    assertTrue(after > 0, "Reversal should start from the visible position");
    assertEquals(0, animation.progress(DURATION * 2), 1e-4);
  }

  @Test
  void snapJumpsWithoutAnimating() {
    var animation = new BladeAnimation();
    animation.animateTo(true, 0);
    animation.snapTo(true);
    assertEquals(1, animation.progress(DURATION), 1e-4);
    animation.snapTo(false);
    assertEquals(0, animation.progress(DURATION), 1e-4);
  }
}
