package com.cybercraft.network;

import com.cybercraft.CyberCraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SelectHackPayload(ResourceLocation hack) implements CustomPacketPayload {
  public static final Type<SelectHackPayload> TYPE = new Type<>(CyberCraft.id("select_hack"));
  public static final StreamCodec<RegistryFriendlyByteBuf, SelectHackPayload> CODEC =
      StreamCodec.of(
          (b, p) -> b.writeResourceLocation(p.hack()),
          b -> new SelectHackPayload(b.readResourceLocation()));

  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}
