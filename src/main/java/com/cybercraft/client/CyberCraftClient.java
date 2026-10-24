package com.cybercraft.client;

import com.cybercraft.network.CastHackPayload;
import com.cybercraft.network.CyberwareSyncPayload;
import com.cybercraft.network.DoubleJumpPayload;
import com.cybercraft.network.HackResultPayload;
import com.cybercraft.network.OpenBookPayload;
import com.cybercraft.network.OpenCyberwareScreenPayload;
import com.cybercraft.network.SandevistanPayload;
import com.cybercraft.network.SelectHackPayload;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import org.lwjgl.glfw.GLFW;

/**
 * Entry point client: keybind rimappabili, cache dello stato cyberware,
 * schermate (impianti e manuale) e HUD dello scanner.
 */
public final class CyberCraftClient implements ClientModInitializer {

    /** Tasto predefinito: V. Il giocatore può cambiarlo dalle Opzioni > Comandi. */
    public static final KeyMapping SANDEVISTAN_KEY = key("key.cybercraft.sandevistan", GLFW.GLFW_KEY_V);
    public static final KeyMapping CYBERWARE_KEY = key("key.cybercraft.cyberware", GLFW.GLFW_KEY_C);
    public static final KeyMapping SCANNER_KEY = key("key.cybercraft.scanner", GLFW.GLFW_KEY_R);
    public static final KeyMapping EXECUTE_HACK_KEY = key("key.cybercraft.execute_hack", GLFW.GLFW_KEY_G);
    public static final KeyMapping HACK_PREV_KEY = key("key.cybercraft.hack_prev", GLFW.GLFW_KEY_X);
    public static final KeyMapping HACK_NEXT_KEY = key("key.cybercraft.hack_next", GLFW.GLFW_KEY_Z);

    private static KeyMapping key(String translationKey, int defaultKey) {
        return new KeyMapping(translationKey, InputConstants.Type.KEYSYM, defaultKey, "category.cybercraft");
    }

    @Override
    public void onInitializeClient() {
        KeyBindingHelper.registerKeyBinding(SANDEVISTAN_KEY);
        KeyBindingHelper.registerKeyBinding(CYBERWARE_KEY);
        KeyBindingHelper.registerKeyBinding(SCANNER_KEY);
        KeyBindingHelper.registerKeyBinding(EXECUTE_HACK_KEY);
        KeyBindingHelper.registerKeyBinding(HACK_PREV_KEY);
        KeyBindingHelper.registerKeyBinding(HACK_NEXT_KEY);

        ClientPlayNetworking.registerGlobalReceiver(CyberwareSyncPayload.TYPE, (payload, context) ->
                context.client().execute(() -> ClientCyberwareState.set(payload.toData())));

        ClientPlayNetworking.registerGlobalReceiver(OpenCyberwareScreenPayload.TYPE, (payload, context) ->
                context.client().execute(() -> context.client().setScreen(new CyberwareScreen())));

        ClientPlayNetworking.registerGlobalReceiver(OpenBookPayload.TYPE, (payload, context) ->
                context.client().execute(() -> context.client().setScreen(new EdgerunnersBookScreen())));

        ClientPlayNetworking.registerGlobalReceiver(HackResultPayload.TYPE, (payload, context) ->
                context.client().execute(() -> {
                    if (ScannerState.active) {
                        ScannerState.finishUpload(payload.success(), payload.code());
                    }
                }));

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            EntityOutlineManager.reset();
            ScannerState.close();
        });

        HudRenderCallback.EVENT.register(SandevistanFx::renderHud);
        HudRenderCallback.EVENT.register(ScannerHud::render);

        ClientTickEvents.END_CLIENT_TICK.register(CyberCraftClient::tick);
    }

    private static void tick(Minecraft client) {
        if (client.player == null) {
            ScannerState.close();
            return;
        }
        SandevistanFx.tick(client);
        EntityOutlineManager.tick(client);
        if (client.player.isDeadOrDying()) {
            ScannerState.close();
        }

        if (client.screen != null) {
            drainKeys();
            return;
        }

        while (SANDEVISTAN_KEY.consumeClick()) {
            ClientPlayNetworking.send(new SandevistanPayload());
        }
        // Il client manda l'intenzione; il server verifica impianto e stato.
        if (client.options.keyJump.consumeClick() && !client.player.onGround()) {
            ClientPlayNetworking.send(new DoubleJumpPayload());
        }

        while (CYBERWARE_KEY.consumeClick()) {
            ScannerState.close();
            client.setScreen(new CyberwareScreen());
        }
        while (SCANNER_KEY.consumeClick()) {
            ScannerState.toggle();
        }

        if (!ScannerState.active) {
            while (EXECUTE_HACK_KEY.consumeClick()) { /* scarta i click accumulati */ }
            while (HACK_PREV_KEY.consumeClick()) { }
            while (HACK_NEXT_KEY.consumeClick()) { }
            return;
        }

        while (HACK_PREV_KEY.consumeClick()) {
            ClientCyberwareState.cycleHack(-1);
            ClientPlayNetworking.send(new SelectHackPayload(-1));
        }
        while (HACK_NEXT_KEY.consumeClick()) {
            ClientCyberwareState.cycleHack(1);
            ClientPlayNetworking.send(new SelectHackPayload(1));
        }
        while (EXECUTE_HACK_KEY.consumeClick()) {
            LivingEntity target = ScannerHud.currentTarget(client);
            if (target == null) {
                continue;
            }
            ClientPlayNetworking.send(new CastHackPayload(ClientCyberwareState.get().selectedHack(), target.getId()));
            ScannerState.beginUpload();
        }
    }

    private static void drainKeys() {
        while (SANDEVISTAN_KEY.consumeClick()) { }
        while (CYBERWARE_KEY.consumeClick()) { }
        while (SCANNER_KEY.consumeClick()) { }
        while (EXECUTE_HACK_KEY.consumeClick()) { }
        while (HACK_PREV_KEY.consumeClick()) { }
        while (HACK_NEXT_KEY.consumeClick()) { }
    }
}
