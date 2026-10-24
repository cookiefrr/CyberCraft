package com.cybercraft.network;

import com.cybercraft.CyberCraft;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

/** Apre la schermata degli slot cyberware (tavolo di installazione o keybind). */
public record OpenCyberwareScreenPayload() implements CustomPacketPayload {

    public static final Type<OpenCyberwareScreenPayload> TYPE =
            new Type<>(CyberCraft.id("open_cyberware"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenCyberwareScreenPayload> CODEC =
            StreamCodec.unit(new OpenCyberwareScreenPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void send(ServerPlayer player) {
        ServerPlayNetworking.send(player, new OpenCyberwareScreenPayload());
    }
}
