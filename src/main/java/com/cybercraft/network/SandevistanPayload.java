package com.cybercraft.network;

import com.cybercraft.CyberCraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Payload vuoto: il client chiede al server di attivare il Sandevistan. */
public record SandevistanPayload() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SandevistanPayload> TYPE =
            new CustomPacketPayload.Type<>(CyberCraft.id("sandevistan_activate"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SandevistanPayload> CODEC =
            StreamCodec.unit(new SandevistanPayload());

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
