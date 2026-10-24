package com.cybercraft.client;

import com.cybercraft.network.CastHackPayload;
import com.cybercraft.network.DoubleJumpPayload;
import com.cybercraft.network.MantisBladePayload;
import com.cybercraft.network.OpenCyberwarePayload;
import com.cybercraft.network.SandevistanPayload;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = "cybercraft", value = Dist.CLIENT)
public final class CyberCraftClient {

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

  @SubscribeEvent
  public static void keys(RegisterKeyMappingsEvent event) {
    for (var key :
        new KeyMapping[] {
          SANDEVISTAN_KEY,
          CYBERWARE_KEY,
          SCANNER_KEY,
          EXECUTE_HACK_KEY,
          HACK_PREV_KEY,
          HACK_NEXT_KEY,
          MANTIS_BLADES_KEY
        }) event.register(key);
  }

  @SubscribeEvent
  public static void screens(net.neoforged.neoforge.client.event.RegisterMenuScreensEvent event) {
    event.register(com.cybercraft.registry.ModMenus.CYBERWARE.get(), CyberwareScreen::new);
  }

  @SubscribeEvent
  public static void layers(RegisterGuiLayersEvent event) {
    event.registerAboveAll(com.cybercraft.CyberCraft.id("sandevistan"), SandevistanFx::renderHud);
    event.registerAboveAll(com.cybercraft.CyberCraft.id("scanner"), ScannerHud::render);
  }

  @EventBusSubscriber(modid = "cybercraft", value = Dist.CLIENT)
  public static final class Events {
    @SubscribeEvent
    public static void tick(ClientTickEvent.Post e) {
      CyberCraftClient.tick(Minecraft.getInstance());
    }

    @SubscribeEvent
    public static void logout(ClientPlayerNetworkEvent.LoggingOut e) {
      EntityOutlineManager.reset();
      ScannerState.close();
      MantisBladeClientState.clear();
      ClientCyberwareState.set(com.cybercraft.cyberware.CyberwareData.DEFAULT);
    }
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
      PacketDistributor.sendToServer(new SandevistanPayload());
    }

    if (client.options.keyJump.consumeClick() && !client.player.onGround()) {
      PacketDistributor.sendToServer(new DoubleJumpPayload());
    }

    while (CYBERWARE_KEY.consumeClick()) {
      ScannerState.close();
      PacketDistributor.sendToServer(new OpenCyberwarePayload());
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
          && ClientCyberwareState.get()
              .has(com.cybercraft.cyberware.Cyberware.MANTIS_BLADES))
        PacketDistributor.sendToServer(new MantisBladePayload());
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
      PacketDistributor.sendToServer(
          new CastHackPayload(ScannerTargets.selected(client).resource(), target));
      ScannerState.beginUpload();
    }
  }

  private static void drainKeys() {
    while (SANDEVISTAN_KEY.consumeClick()) {}
    while (CYBERWARE_KEY.consumeClick()) {}
    while (SCANNER_KEY.consumeClick()) {}
    while (EXECUTE_HACK_KEY.consumeClick()) {}
    while (HACK_PREV_KEY.consumeClick()) {}
    while (HACK_NEXT_KEY.consumeClick()) {}
    while (MANTIS_BLADES_KEY.consumeClick()) {}
  }
}
