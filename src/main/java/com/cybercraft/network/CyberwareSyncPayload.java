package com.cybercraft.network;

import com.cybercraft.CyberCraft;
import com.cybercraft.cyberware.CyberwareData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.PacketDistributor;

public record CyberwareSyncPayload(int entityId, CyberwareData data)
    implements CustomPacketPayload {
  public static final Type<CyberwareSyncPayload> TYPE = new Type<>(CyberCraft.id("cyberware_sync"));
  public static final StreamCodec<RegistryFriendlyByteBuf, CyberwareSyncPayload> CODEC =
      StreamCodec.composite(
          ByteBufCodecs.VAR_INT,
          CyberwareSyncPayload::entityId,
          ByteBufCodecs.fromCodecWithRegistries(CyberwareData.CODEC),
          CyberwareSyncPayload::data,
          CyberwareSyncPayload::new);

  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }

  public static void send(LivingEntity entity, CyberwareData data) {
    if (entity.level().isClientSide) return;
    var packet = new CyberwareSyncPayload(entity.getId(), data);
    if (entity instanceof ServerPlayer player) PacketDistributor.sendToPlayer(player, packet);
    PacketDistributor.sendToPlayersTrackingEntity(entity, packet);
  }
}
