package com.cybercraft.network;

import com.cybercraft.client.ClientPayloads;
import com.cybercraft.cyberware.*;
import com.cybercraft.netrunning.NetrunnerManager;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class ModNetworking {
  private ModNetworking() {}

  public static void register(RegisterPayloadHandlersEvent event) {
    var r = event.registrar("1");
    r.playToServer(
        DoubleJumpPayload.TYPE,
        DoubleJumpPayload.CODEC,
        (p, c) -> DoubleJumpHandler.tryDoubleJump((ServerPlayer) c.player()));
    r.playToServer(
        SandevistanPayload.TYPE,
        SandevistanPayload.CODEC,
        (p, c) -> SandevistanManager.activate((ServerPlayer) c.player()));
    r.playToServer(
        CastHackPayload.TYPE,
        CastHackPayload.CODEC,
        (p, c) -> NetrunnerManager.cast((ServerPlayer) c.player(), p.hack(), p.target()));
    r.playToServer(
        SelectHackPayload.TYPE,
        SelectHackPayload.CODEC,
        (p, c) -> NetrunnerManager.select((ServerPlayer) c.player(), p.hack()));
    r.playToServer(
        OpenCyberwarePayload.TYPE,
        OpenCyberwarePayload.CODEC,
        (p, c) -> CyberwareManager.openMenu((ServerPlayer) c.player()));
    r.playToServer(
        MantisBladePayload.TYPE,
        MantisBladePayload.CODEC,
        (p, c) -> MantisBladeManager.toggle((ServerPlayer) c.player()));
    r.playToClient(
        CyberwareSyncPayload.TYPE, CyberwareSyncPayload.CODEC, (p, c) -> ClientPayloads.sync(p));
    r.playToClient(
        MantisBladeStatePayload.TYPE,
        MantisBladeStatePayload.CODEC,
        (p, c) -> ClientPayloads.mantis(p));
    r.playToClient(
        OpenBookPayload.TYPE, OpenBookPayload.CODEC, (p, c) -> ClientPayloads.openBook());
    r.playToClient(
        HackResultPayload.TYPE, HackResultPayload.CODEC, (p, c) -> ClientPayloads.result(p));
    r.playToClient(
        ImplantAnimationPayload.TYPE,
        ImplantAnimationPayload.CODEC,
        (p, c) -> ClientPayloads.animation(p));
  }
}
