package com.cybercraft.client;

import com.cybercraft.network.CastHackPayload;
import com.cybercraft.network.DoubleJumpPayload;
import com.cybercraft.network.OpenCyberwarePayload;
import com.cybercraft.network.SandevistanPayload;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public final class CyberCraftClient implements net.fabricmc.api.ClientModInitializer {

  public static final KeyMapping SANDEVISTAN_KEY =
      key("key.cybercraft.sandevistan", GLFW.GLFW_KEY_V);
  public static final KeyMapping CYBERWARE_KEY = key("key.cybercraft.cyberware", GLFW.GLFW_KEY_C);
  public static final KeyMapping SCANNER_KEY = key("key.cybercraft.scanner", GLFW.GLFW_KEY_R);
  public static final KeyMapping EXECUTE_HACK_KEY =
      key("key.cybercraft.execute_hack", GLFW.GLFW_KEY_G);
  public static final KeyMapping HACK_PREV_KEY = key("key.cybercraft.hack_prev", GLFW.GLFW_KEY_X);
  public static final KeyMapping HACK_NEXT_KEY = key("key.cybercraft.hack_next", GLFW.GLFW_KEY_Z);

  public static final KeyMapping MANTIS_BLADES_KEY =
      key("key.cybercraft.mantis_blades", GLFW.GLFW_KEY_M);

  private static KeyMapping key(String translationKey, int defaultKey) {
    return new KeyMapping(
        translationKey, InputConstants.Type.KEYSYM, defaultKey, "category.cybercraft");
  }

  public void onInitializeClient() {
    for (var key :
        new KeyMapping[] {
          SANDEVISTAN_KEY,
          CYBERWARE_KEY,
          SCANNER_KEY,
          EXECUTE_HACK_KEY,
          HACK_PREV_KEY,
          HACK_NEXT_KEY,
          MANTIS_BLADES_KEY
        }) net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.registerKeyBinding(key);
    net.minecraft.client.gui.screens.MenuScreens.register(
        com.cybercraft.registry.ModMenus.CYBERWARE, CyberwareScreen::new);
    net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(
        CyberCraftClient::tick);
    net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.DISCONNECT.register(
        (handler, client) -> {
          EntityOutlineManager.reset();
          MantisBladeClientState.clear();
          ScannerState.close();
          ClientCyberwareState.set(com.cybercraft.cyberware.CyberwareData.DEFAULT);
        });
    net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback.EVENT.register(
        (g, t) -> {
          SandevistanFx.renderHud(g, t);
          ScannerHud.render(g, t);
        });
    net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.registerGlobalReceiver(
        com.cybercraft.network.CyberwareSyncPayload.TYPE, (p, c) -> ClientPayloads.sync(p));
    net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.registerGlobalReceiver(
        com.cybercraft.network.OpenBookPayload.TYPE, (p, c) -> ClientPayloads.openBook());
    net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.registerGlobalReceiver(
        com.cybercraft.network.HackResultPayload.TYPE, (p, c) -> ClientPayloads.result(p));
    net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.registerGlobalReceiver(
        com.cybercraft.network.MantisBladeStatePayload.TYPE, (p, c) -> ClientPayloads.mantis(p));
    ClientEquipment.init();
  }

  private static void tick(Minecraft client) {
    if (client.player == null) {
      ScannerState.close();
      return;
    }
    SandevistanFx.tick(client);
    EntityOutlineManager.tick(client);
    if (client.player.isDeadOrDying()
        || !com.cybercraft.cyberware.CyberwareManager.operational(client.player)
        || !com.cybercraft.cyberware.CyberwareManager.canScan(ClientCyberwareState.get())) {
      ScannerState.close();
    }

    if (client.screen != null) {
      drainKeys();
      return;
    }

    while (SANDEVISTAN_KEY.consumeClick()) {
      net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(
          new SandevistanPayload());
    }

    if (client.options.keyJump.consumeClick() && !client.player.onGround()) {
      net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(
          new DoubleJumpPayload());
    }

    while (CYBERWARE_KEY.consumeClick()) {
      ScannerState.close();
      net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(
          new OpenCyberwarePayload());
    }
    while (SCANNER_KEY.consumeClick()) {
      if (com.cybercraft.cyberware.CyberwareManager.canScan(ClientCyberwareState.get()))
        ScannerState.toggle();
      else
        client.player.displayClientMessage(
            net.minecraft.network.chat.Component.translatable("msg.cybercraft.optics_required"),
            true);
    }

    while (MANTIS_BLADES_KEY.consumeClick()) {
      if (com.cybercraft.cyberware.CyberwareManager.operational(client.player)
          && ClientCyberwareState.get().has(com.cybercraft.cyberware.Cyberware.MANTIS_BLADES))
        net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(
            new com.cybercraft.network.MantisBladePayload());
    }

    if (!ScannerState.active) {
      while (EXECUTE_HACK_KEY.consumeClick()) {}
      while (HACK_PREV_KEY.consumeClick()) {}
      while (HACK_NEXT_KEY.consumeClick()) {}
      return;
    }

    while (HACK_PREV_KEY.consumeClick()) ScannerTargets.cycle(client, -1);
    while (HACK_NEXT_KEY.consumeClick()) ScannerTargets.cycle(client, 1);
    while (EXECUTE_HACK_KEY.consumeClick()) {
      var target = ScannerTargets.target(client);
      if (target == null
          || !com.cybercraft.cyberware.CyberwareManager.canHack(ClientCyberwareState.get()))
        continue;
      net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(
          new CastHackPayload(ScannerTargets.selected(client).resource(), target));
      ScannerState.beginUpload();
    }
  }

  private static void drainKeys() {
    while (MANTIS_BLADES_KEY.consumeClick()) {}
    while (SANDEVISTAN_KEY.consumeClick()) {}
    while (CYBERWARE_KEY.consumeClick()) {}
    while (SCANNER_KEY.consumeClick()) {}
    while (EXECUTE_HACK_KEY.consumeClick()) {}
    while (HACK_PREV_KEY.consumeClick()) {}
    while (HACK_NEXT_KEY.consumeClick()) {}
  }
}
