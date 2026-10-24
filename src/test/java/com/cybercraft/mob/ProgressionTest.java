package com.cybercraft.mob;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ProgressionTest {
  private final Progression.Settings defaults = new Progression.Settings(5, 50, .05, .35, 3, 3);

  @Test
  void startsOnDayFive() {
    assertEquals(new Progression.Stage(0, 0, 0), Progression.at(4, defaults));
    assertEquals(.05, Progression.at(5, defaults).chance(), 1e-9);
  }

  @Test
  void reachesAndHoldsCaps() {
    for (long day : new long[] {50, 100, 1000000}) {
      var s = Progression.at(day, defaults);
      assertEquals(.35, s.chance(), 1e-9);
      assertEquals(3, s.implants());
      assertEquals(3, s.tier());
    }
  }

  @Test
  void respectsConfiguredLimits() {
    var s = Progression.at(100, new Progression.Settings(5, 50, .05, .2, 1, 2));
    assertEquals(1, s.implants());
    assertEquals(2, s.tier());
    assertEquals(.2, s.chance(), 1e-9);
  }

  @Test
  void progressesMonotonically() {
    double previous = 0;
    for (int d = 0; d < 100; d++) {
      var s = Progression.at(d, defaults);
      assertTrue(s.chance() >= previous);
      previous = s.chance();
    }
  }

  @Test
  void tierBoundaries() {
    assertEquals(1, Progression.at(14, defaults).tier());
    assertEquals(2, Progression.at(15, defaults).tier());
    assertEquals(2, Progression.at(29, defaults).tier());
    assertEquals(3, Progression.at(30, defaults).tier());
  }
}
