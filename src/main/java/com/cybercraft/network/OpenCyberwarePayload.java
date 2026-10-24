package com.cybercraft.network;

import com.cybercraft.CyberCraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record OpenCyberwarePayload() implements CustomPacketPayload {
  public static final Type<OpenCyberwarePayload> TYPE = new Type<>(CyberCraft.id("open_cyberware"));
  public static final StreamCodec<RegistryFriendlyByteBuf, OpenCyberwarePayload> CODEC =
      StreamCodec.unit(new OpenCyberwarePayload());

  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}
