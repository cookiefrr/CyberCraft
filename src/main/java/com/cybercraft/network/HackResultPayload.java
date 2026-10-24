package com.cybercraft.network;

import com.cybercraft.CyberCraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public record HackResultPayload(boolean success, int code) implements CustomPacketPayload {

  public static final Type<HackResultPayload> TYPE = new Type<>(CyberCraft.id("hack_result"));

  public static final StreamCodec<RegistryFriendlyByteBuf, HackResultPayload> CODEC =
      StreamCodec.of(
          (buf, payload) -> {
            buf.writeBoolean(payload.success);
            buf.writeVarInt(payload.code);
          },
          buf -> new HackResultPayload(buf.readBoolean(), buf.readVarInt()));

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }

  public static void send(ServerPlayer player, boolean success, int code) {
    PacketDistributor.sendToPlayer(player, new HackResultPayload(success, code));
  }
}
