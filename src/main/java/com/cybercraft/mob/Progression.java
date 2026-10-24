package com.cybercraft.mob;

public final class Progression {
  public record Settings(
      int firstDay,
      int capDay,
      double initialChance,
      double maxChance,
      int maxImplants,
      int maxTier) {}

  public record Stage(double chance, int implants, int tier) {}

  public static Stage at(long day, Settings s) {
    if (day < s.firstDay()) return new Stage(0, 0, 0);
    double fraction =
        Math.clamp((double) (day - s.firstDay()) / Math.max(1, s.capDay() - s.firstDay()), 0, 1);
    return new Stage(
        s.initialChance() + (s.maxChance() - s.initialChance()) * fraction,
        Math.min(s.maxImplants(), day >= 30 ? 3 : day >= 15 ? 2 : 1),
        Math.min(s.maxTier(), day >= 30 ? 3 : day >= 15 ? 2 : 1));
  }

  private Progression() {}
}
