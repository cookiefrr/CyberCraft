package com.cybercraft.cyberware;

import com.cybercraft.network.MantisBladeStatePayload;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

public final class MantisBladeManager {
  private static final Set<UUID> EXTENDED = new HashSet<>();

  public static boolean active(LivingEntity entity) {
    if (!CyberwareManager.operational(entity)
        || !CyberwareManager.get(entity).has(Cyberware.MANTIS_BLADES)) return false;
    return !(entity instanceof Player) || EXTENDED.contains(entity.getUUID());
  }

  public static boolean extended(LivingEntity entity) {
    return EXTENDED.contains(entity.getUUID());
  }

  public static boolean toggle(ServerPlayer player) {
    if (!CyberwareManager.operational(player)
        || !CyberwareManager.get(player).has(Cyberware.MANTIS_BLADES)) return false;
    boolean extended = !EXTENDED.contains(player.getUUID());
    if (extended) EXTENDED.add(player.getUUID());
    else EXTENDED.remove(player.getUUID());
    send(player, extended, true);
    return true;
  }

  public static void reset(LivingEntity entity) {
    if (EXTENDED.remove(entity.getUUID())) send(entity, false, false);
  }

  public static void clear() {
    EXTENDED.clear();
  }

  private static void send(LivingEntity entity, boolean extended, boolean animate) {
    if (entity.level().isClientSide) return;
    PacketDistributor.sendToPlayersTrackingEntityAndSelf(
        entity, new MantisBladeStatePayload(entity.getId(), extended, animate));
  }

  private MantisBladeManager() {}
}
