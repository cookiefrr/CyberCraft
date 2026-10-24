package com.cybercraft.network;

import com.cybercraft.CyberCraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Richiesta di disinstallare l'impianto nello slot indicato. */
public record RemoveImplantPayload(int slot) implements CustomPacketPayload {

    public static final Type<RemoveImplantPayload> TYPE =
            new Type<>(CyberCraft.id("remove_implant"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RemoveImplantPayload> CODEC = StreamCodec.of(
            (buf, payload) -> buf.writeVarInt(payload.slot),
            buf -> new RemoveImplantPayload(buf.readVarInt()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
