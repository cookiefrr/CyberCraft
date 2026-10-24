package com.cybercraft.network;

import com.cybercraft.CyberCraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record MantisBladeStatePayload(int entityId, boolean extended, boolean animate)
    implements CustomPacketPayload {
  public static final CustomPacketPayload.Type<MantisBladeStatePayload> TYPE =
      new CustomPacketPayload.Type<>(CyberCraft.id("mantis_blade_state"));

  public static final StreamCodec<RegistryFriendlyByteBuf, MantisBladeStatePayload> CODEC =
      StreamCodec.of(
          (buffer, payload) -> {
            buffer.writeVarInt(payload.entityId());
            buffer.writeBoolean(payload.extended());
            buffer.writeBoolean(payload.animate());
          },
          buffer ->
              new MantisBladeStatePayload(
                  buffer.readVarInt(), buffer.readBoolean(), buffer.readBoolean()));

  @Override
  public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}
