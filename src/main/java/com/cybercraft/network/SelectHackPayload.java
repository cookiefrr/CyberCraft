package com.cybercraft.network;

import com.cybercraft.CyberCraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Navigazione della lista Quickhack: +1 successivo, -1 precedente. */
public record SelectHackPayload(int delta) implements CustomPacketPayload {

    public static final Type<SelectHackPayload> TYPE = new Type<>(CyberCraft.id("select_hack"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SelectHackPayload> CODEC = StreamCodec.of(
            (buf, payload) -> buf.writeVarInt(payload.delta),
            buf -> new SelectHackPayload(buf.readVarInt()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
