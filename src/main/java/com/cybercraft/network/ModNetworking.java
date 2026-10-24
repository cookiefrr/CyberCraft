package com.cybercraft.network;
import com.cybercraft.cyberware.*;
import com.cybercraft.netrunning.NetrunnerManager;
import com.cybercraft.client.ClientPayloads;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
public final class ModNetworking {
    private ModNetworking() {}
    public static void register(RegisterPayloadHandlersEvent event) {
        var r = event.registrar("1");
        r.playToServer(DoubleJumpPayload.TYPE, DoubleJumpPayload.CODEC, (p,c) -> DoubleJumpHandler.tryDoubleJump((ServerPlayer)c.player()));
        r.playToServer(SandevistanPayload.TYPE, SandevistanPayload.CODEC, (p,c) -> SandevistanManager.activate((ServerPlayer)c.player()));
        r.playToServer(CastHackPayload.TYPE, CastHackPayload.CODEC, (p,c) -> NetrunnerManager.cast((ServerPlayer)c.player(),p.hackIndex(),p.targetId()));
        r.playToServer(SelectHackPayload.TYPE, SelectHackPayload.CODEC, (p,c) -> NetrunnerManager.select((ServerPlayer)c.player(),p.delta()));
        r.playToServer(RemoveImplantPayload.TYPE, RemoveImplantPayload.CODEC, (p,c) -> {
            if (p.slot() >= 0 && p.slot() < CyberwareSlot.values().length) CyberwareManager.tryRemove((ServerPlayer)c.player(), CyberwareSlot.values()[p.slot()]);
        });
        r.playToClient(CyberwareSyncPayload.TYPE, CyberwareSyncPayload.CODEC, (p,c) -> ClientPayloads.sync(p));
        r.playToClient(OpenCyberwareScreenPayload.TYPE, OpenCyberwareScreenPayload.CODEC, (p,c) -> ClientPayloads.openCyberware());
        r.playToClient(OpenBookPayload.TYPE, OpenBookPayload.CODEC, (p,c) -> ClientPayloads.openBook());
        r.playToClient(HackResultPayload.TYPE, HackResultPayload.CODEC, (p,c) -> ClientPayloads.result(p));
    }
}
