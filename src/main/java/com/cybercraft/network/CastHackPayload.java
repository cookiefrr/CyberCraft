package com.cybercraft.network;

import com.cybercraft.CyberCraft;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

/** Il client chiede di eseguire un Quickhack sull'indice indicato (scanner HUD). */
public record CastHackPayload(int hackIndex) implements CustomPacketPayload {

    public static final Type<CastHackPayload> TYPE = new Type<>(CyberCraft.id("cast_hack"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CastHackPayload> CODEC = StreamCodec.of(
            (buf, payload) -> buf.writeVarInt(payload.hackIndex),
            buf -> new CastHackPayload(buf.readVarInt()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
