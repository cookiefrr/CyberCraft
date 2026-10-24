package com.cybercraft.network;

import com.cybercraft.CyberCraft;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public record OpenBookPayload() implements CustomPacketPayload {

  public static final Type<OpenBookPayload> TYPE = new Type<>(CyberCraft.id("open_book"));

  public static final StreamCodec<RegistryFriendlyByteBuf, OpenBookPayload> CODEC =
      StreamCodec.unit(new OpenBookPayload());

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }

  public static void send(ServerPlayer player) {
    ServerPlayNetworking.send(player, new OpenBookPayload());
  }
}
