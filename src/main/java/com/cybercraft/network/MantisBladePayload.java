package com.cybercraft.network;

import com.cybercraft.CyberCraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record MantisBladePayload() implements CustomPacketPayload {
  public static final CustomPacketPayload.Type<MantisBladePayload> TYPE =
      new CustomPacketPayload.Type<>(CyberCraft.id("mantis_blade_toggle"));

  public static final StreamCodec<RegistryFriendlyByteBuf, MantisBladePayload> CODEC =
      StreamCodec.unit(new MantisBladePayload());

  @Override
  public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}
