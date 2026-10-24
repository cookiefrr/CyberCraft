package com.cybercraft.test;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.server.level.ServerLevel;

public final class TestPlayers extends FakePlayer {
  private TestPlayers(ServerLevel level, GameProfile profile) {
    super(level, profile);
  }

  public static TestPlayers get(ServerLevel level, GameProfile profile) {
    return new TestPlayers(level, profile);
  }

  @Override
  public net.minecraft.world.scores.PlayerTeam getTeam() {
    return level().getScoreboard().getPlayersTeam(getScoreboardName());
  }

  @Override
  public boolean isInvulnerableTo(net.minecraft.world.damagesource.DamageSource source) {
    return false;
  }
}
