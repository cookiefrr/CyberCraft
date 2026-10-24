package com.cybercraft.network;

import com.cybercraft.CyberCraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ImplantAnimationPayload(int entityId, String implant, String clip)
    implements CustomPacketPayload {
  public static final CustomPacketPayload.Type<ImplantAnimationPayload> TYPE =
      new CustomPacketPayload.Type<>(CyberCraft.id("implant_animation"));

  public static final StreamCodec<RegistryFriendlyByteBuf, ImplantAnimationPayload> CODEC =
      StreamCodec.of(
          (buffer, payload) -> {
            buffer.writeVarInt(payload.entityId());
            buffer.writeUtf(payload.implant(), 64);
            buffer.writeUtf(payload.clip(), 64);
          },
          buffer ->
              new ImplantAnimationPayload(
                  buffer.readVarInt(), buffer.readUtf(64), buffer.readUtf(64)));

  @Override
  public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}
