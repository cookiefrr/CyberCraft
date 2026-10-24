package com.cybercraft.network;

import com.cybercraft.CyberCraft;
import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

/** Apre l'Edgerunner's Book sul client. */
public record OpenBookPayload() implements CustomPacketPayload {

    public static final Type<OpenBookPayload> TYPE = new Type<>(CyberCraft.id("open_book"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenBookPayload> CODEC =
            StreamCodec.unit(new OpenBookPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void send(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new OpenBookPayload());
    }
}
