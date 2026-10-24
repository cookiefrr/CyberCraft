package com.cybercraft.cyberware;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

public final class DoubleJumpHandler {

  public static final double JUMP_POWER = 0.55;

  private static final Set<UUID> USED = new HashSet<>();

  private DoubleJumpHandler() {}

  public static void tick(MinecraftServer server) {
    for (net.minecraft.world.entity.LivingEntity player : server.getPlayerList().getPlayers()) {
      if (canReset(player)) {
        USED.remove(player.getUUID());
      }
    }
  }

  public static void tryDoubleJump(net.minecraft.world.entity.LivingEntity player) {
    if (canReset(player)) {
      return;
    }
    CyberwareData data = CyberwareManager.get(player);
    if (!data.has(Cyberware.REINFORCED_TENDONS) || !CyberwareManager.operational(player)) {
      return;
    }
    if (!USED.add(player.getUUID())) {
      return;
    }

    Vec3 movement = player.getDeltaMovement();
    player.setDeltaMovement(movement.x, JUMP_POWER, movement.z);
    player.resetFallDistance();
    player.hasImpulse = true;
    player.hurtMarked = true;

    if (player instanceof ServerPlayer serverPlayer)
      serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(player));

    ServerLevel level = (ServerLevel) player.level();
    level.sendParticles(
        ParticleTypes.CLOUD,
        player.getX(),
        player.getY(),
        player.getZ(),
        10,
        0.25,
        0.1,
        0.25,
        0.05);
    level.playSound(
        null,
        player.getX(),
        player.getY(),
        player.getZ(),
        SoundEvents.SLIME_JUMP,
        SoundSource.PLAYERS,
        0.7f,
        1.5f);
  }

  public static void tickEntity(net.minecraft.world.entity.LivingEntity entity) {
    if (canReset(entity)) USED.remove(entity.getUUID());
  }

  private static boolean canReset(net.minecraft.world.entity.LivingEntity player) {
    return player.onGround()
        || player.isInWater()
        || player.isInLava()
        || player.isFallFlying()
        || player instanceof net.minecraft.world.entity.player.Player p && p.getAbilities().flying
        || player.isPassenger()
        || player.isSpectator();
  }

  public static void forget(net.minecraft.world.entity.Entity entity) {
    USED.remove(entity.getUUID());
  }

  public static void clear() {
    USED.clear();
  }
}
