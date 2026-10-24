package com.cybercraft.network;

import com.cybercraft.CyberCraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record DoubleJumpPayload() implements CustomPacketPayload {

  public static final CustomPacketPayload.Type<DoubleJumpPayload> TYPE =
      new CustomPacketPayload.Type<>(CyberCraft.id("double_jump"));

  public static final StreamCodec<RegistryFriendlyByteBuf, DoubleJumpPayload> CODEC =
      StreamCodec.unit(new DoubleJumpPayload());

  @Override
  public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}
