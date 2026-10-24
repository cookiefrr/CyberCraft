package com.cybercraft.network;

import com.cybercraft.CyberCraft;
import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

/**
 * Il client chiede di eseguire un Quickhack sull'indice indicato (scanner HUD).
 * {@code targetId} è l'entità sotto il mirino: il server la rivalida prima di applicare l'effetto.
 */
public record CastHackPayload(int hackIndex, int targetId) implements CustomPacketPayload {

    public static final Type<CastHackPayload> TYPE = new Type<>(CyberCraft.id("cast_hack"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CastHackPayload> CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeVarInt(payload.hackIndex);
                buf.writeVarInt(payload.targetId);
            },
            buf -> new CastHackPayload(buf.readVarInt(), buf.readVarInt()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
