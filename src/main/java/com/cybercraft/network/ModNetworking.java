package com.cybercraft.network;

import com.cybercraft.cyberware.CyberwareManager;
import com.cybercraft.cyberware.CyberwareSlot;
import com.cybercraft.cyberware.DoubleJumpHandler;
import com.cybercraft.cyberware.SandevistanManager;
import com.cybercraft.netrunning.NetrunnerManager;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

/**
 * Canali della mod. I payload C2S sono intenzioni: il server ricontrolla
 * sempre impianti, stato, RAM, cooldown e bersaglio.
 */
public final class ModNetworking {

    private ModNetworking() {}

    public static void init() {
        PayloadTypeRegistry.playC2S().register(DoubleJumpPayload.TYPE, DoubleJumpPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(SandevistanPayload.TYPE, SandevistanPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(CastHackPayload.TYPE, CastHackPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(SelectHackPayload.TYPE, SelectHackPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(RemoveImplantPayload.TYPE, RemoveImplantPayload.CODEC);

        PayloadTypeRegistry.playS2C().register(CyberwareSyncPayload.TYPE, CyberwareSyncPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(OpenCyberwareScreenPayload.TYPE, OpenCyberwareScreenPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(OpenBookPayload.TYPE, OpenBookPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(HackResultPayload.TYPE, HackResultPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(DoubleJumpPayload.TYPE, (payload, context) ->
                context.server().execute(() -> DoubleJumpHandler.tryDoubleJump(context.player())));

        ServerPlayNetworking.registerGlobalReceiver(SandevistanPayload.TYPE, (payload, context) ->
                context.server().execute(() -> SandevistanManager.activate(context.player())));

        ServerPlayNetworking.registerGlobalReceiver(CastHackPayload.TYPE, (payload, context) ->
                context.server().execute(() -> NetrunnerManager.cast(context.player(), payload.hackIndex(), payload.targetId())));

        ServerPlayNetworking.registerGlobalReceiver(SelectHackPayload.TYPE, (payload, context) ->
                context.server().execute(() -> NetrunnerManager.select(context.player(), payload.delta())));

        ServerPlayNetworking.registerGlobalReceiver(RemoveImplantPayload.TYPE, (payload, context) ->
                context.server().execute(() -> {
                    CyberwareSlot[] slots = CyberwareSlot.values();
                    if (payload.slot() >= 0 && payload.slot() < slots.length) {
                        CyberwareManager.tryRemove(context.player(), slots[payload.slot()]);
                    }
                }));

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                server.execute(() -> CyberwareManager.sendSync(handler.getPlayer())));
    }
}
