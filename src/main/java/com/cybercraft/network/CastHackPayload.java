package com.cybercraft.network;

import com.cybercraft.CyberCraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record CastHackPayload(ResourceLocation hack, HackTarget target)
    implements CustomPacketPayload {
  public static final Type<CastHackPayload> TYPE = new Type<>(CyberCraft.id("cast_hack"));
  public static final StreamCodec<RegistryFriendlyByteBuf, CastHackPayload> CODEC =
      StreamCodec.of(
          (b, p) -> {
            b.writeResourceLocation(p.hack());
            HackTarget.write(b, p.target());
          },
          b -> new CastHackPayload(b.readResourceLocation(), HackTarget.read(b)));

  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}
