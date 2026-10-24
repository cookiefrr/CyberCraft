package com.cybercraft.network;

import com.cybercraft.CyberCraft;
import com.cybercraft.cyberware.CyberwareData;
import java.util.List;
import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

/** Stato cyberware completo inviato al client per HUD scanner e schermata impianti. */
public record CyberwareSyncPayload(
        List<String> implants,
        float tolerance,
        float ram,
        boolean overloaded,
        int selectedHack,
        long sandevistanEnd,
        long sandevistanActiveUntil) implements CustomPacketPayload {

    public static final Type<CyberwareSyncPayload> TYPE =
            new Type<>(CyberCraft.id("cyberware_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CyberwareSyncPayload> CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeCollection(payload.implants, (buffer, value) -> buffer.writeUtf(value));
                buf.writeFloat(payload.tolerance);
                buf.writeFloat(payload.ram);
                buf.writeBoolean(payload.overloaded);
                buf.writeVarInt(payload.selectedHack);
                buf.writeLong(payload.sandevistanEnd);
                buf.writeLong(payload.sandevistanActiveUntil);
            },
            buf -> new CyberwareSyncPayload(
                    buf.readList(buffer -> buffer.readUtf()),
                    buf.readFloat(),
                    buf.readFloat(),
                    buf.readBoolean(),
                    buf.readVarInt(),
                    buf.readLong(),
                    buf.readLong()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public CyberwareData toData() {
        return new CyberwareData(implants, tolerance, ram, overloaded, selectedHack,
                sandevistanEnd, sandevistanActiveUntil);
    }

    public static void send(ServerPlayer player, CyberwareData data) {
        PacketDistributor.sendToPlayer(player, new CyberwareSyncPayload(
                data.implants(), data.tolerance(), data.ram(), data.overloaded(),
                data.selectedHack(), data.sandevistanEnd(), data.sandevistanActiveUntil()));
    }
}
