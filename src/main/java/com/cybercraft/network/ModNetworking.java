package com.cybercraft.network;

import com.cybercraft.cyberware.*;
import com.cybercraft.netrunning.NetrunnerManager;
import net.fabricmc.fabric.api.networking.v1.*;

public final class ModNetworking {
  public static void init() {
    PayloadTypeRegistry.playC2S().register(MantisBladePayload.TYPE, MantisBladePayload.CODEC);
    PayloadTypeRegistry.playS2C()
        .register(MantisBladeStatePayload.TYPE, MantisBladeStatePayload.CODEC);
    ServerPlayNetworking.registerGlobalReceiver(
        MantisBladePayload.TYPE, (p, c) -> MantisBladeManager.toggle(c.player()));
    PayloadTypeRegistry.playC2S().register(DoubleJumpPayload.TYPE, DoubleJumpPayload.CODEC);
    PayloadTypeRegistry.playC2S().register(SandevistanPayload.TYPE, SandevistanPayload.CODEC);
    PayloadTypeRegistry.playC2S().register(CastHackPayload.TYPE, CastHackPayload.CODEC);
    PayloadTypeRegistry.playC2S().register(SelectHackPayload.TYPE, SelectHackPayload.CODEC);
    PayloadTypeRegistry.playC2S().register(OpenCyberwarePayload.TYPE, OpenCyberwarePayload.CODEC);
    PayloadTypeRegistry.playS2C().register(CyberwareSyncPayload.TYPE, CyberwareSyncPayload.CODEC);
    PayloadTypeRegistry.playS2C().register(OpenBookPayload.TYPE, OpenBookPayload.CODEC);
    PayloadTypeRegistry.playS2C().register(HackResultPayload.TYPE, HackResultPayload.CODEC);
    ServerPlayNetworking.registerGlobalReceiver(
        DoubleJumpPayload.TYPE, (p, c) -> DoubleJumpHandler.tryDoubleJump(c.player()));
    ServerPlayNetworking.registerGlobalReceiver(
        SandevistanPayload.TYPE, (p, c) -> SandevistanManager.activate(c.player()));
    ServerPlayNetworking.registerGlobalReceiver(
        CastHackPayload.TYPE, (p, c) -> NetrunnerManager.cast(c.player(), p.hack(), p.target()));
    ServerPlayNetworking.registerGlobalReceiver(
        SelectHackPayload.TYPE, (p, c) -> NetrunnerManager.select(c.player(), p.hack()));
    ServerPlayNetworking.registerGlobalReceiver(
        OpenCyberwarePayload.TYPE, (p, c) -> CyberwareManager.openMenu(c.player()));
  }
}
