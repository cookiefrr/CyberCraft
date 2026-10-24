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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import org.lwjgl.glfw.GLFW;

/**
 * Entry point client: keybind rimappabili, cache dello stato cyberware,
 * schermate (impianti e manuale) e HUD dello scanner.
 */
@EventBusSubscriber(modid="cybercraft", value=Dist.CLIENT, bus=EventBusSubscriber.Bus.MOD)
public final class CyberCraftClient {

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

    @SubscribeEvent
    public static void keys(RegisterKeyMappingsEvent event) {
        for (var key : new KeyMapping[] {SANDEVISTAN_KEY,CYBERWARE_KEY,SCANNER_KEY,EXECUTE_HACK_KEY,HACK_PREV_KEY,HACK_NEXT_KEY}) event.register(key);
    }
    @SubscribeEvent
    public static void layers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(com.cybercraft.CyberCraft.id("sandevistan"), SandevistanFx::renderHud);
        event.registerAboveAll(com.cybercraft.CyberCraft.id("scanner"), ScannerHud::render);
    }
    @EventBusSubscriber(modid="cybercraft", value=Dist.CLIENT)
    public static final class Events {
        @SubscribeEvent public static void tick(ClientTickEvent.Post e) { CyberCraftClient.tick(Minecraft.getInstance()); }
        @SubscribeEvent public static void logout(ClientPlayerNetworkEvent.LoggingOut e) {
            EntityOutlineManager.reset(); ScannerState.close(); ClientCyberwareState.set(com.cybercraft.cyberware.CyberwareData.DEFAULT);
        }
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
            PacketDistributor.sendToServer(new SandevistanPayload());
        }
        // Il client manda l'intenzione; il server verifica impianto e stato.
        if (client.options.keyJump.consumeClick() && !client.player.onGround()) {
            PacketDistributor.sendToServer(new DoubleJumpPayload());
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
            PacketDistributor.sendToServer(new SelectHackPayload(-1));
        }
        while (HACK_NEXT_KEY.consumeClick()) {
            ClientCyberwareState.cycleHack(1);
            PacketDistributor.sendToServer(new SelectHackPayload(1));
        }
        while (EXECUTE_HACK_KEY.consumeClick()) {
            LivingEntity target = ScannerHud.currentTarget(client);
            if (target == null) {
                continue;
            }
            PacketDistributor.sendToServer(new CastHackPayload(ClientCyberwareState.get().selectedHack(), target.getId()));
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
