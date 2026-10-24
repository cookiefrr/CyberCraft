package com.cybercraft.client;

import com.cybercraft.CyberCraft;
import com.cybercraft.cyberware.*;
import com.cybercraft.equipment.*;
import com.cybercraft.network.OpenCyberwarePayload;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.network.chat.Component;

public final class ClientEquipment {
  public static void init() {
    net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin.register(
        context -> {
          for (var implant : Cyberware.values())
            context.addModels(
                CyberCraft.id("item/icons/" + implant.id()),
                CyberCraft.id("item/gui/" + implant.id()));
        });
    net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback
        .EVENT
        .register(
            (type, renderer, helper, context) -> {
              if (renderer.getModel() instanceof net.minecraft.client.model.HumanoidModel)
                helper.register(new CyberwareLayer(renderer));
            });
    net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.AFTER_INIT.register(
        (client, screen, w, h) -> inventoryTab(screen));
    net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback.EVENT.register(
        (stack, context, flag, text) -> tooltip(stack, text));
  }

  private static void inventoryTab(Screen original) {
    if (!(original instanceof InventoryScreen)
        && !(original instanceof CreativeModeInventoryScreen)) return;
    var screen = (AbstractContainerScreen<?>) original;
    net.fabricmc.fabric.api.client.screen.v1.Screens.getButtons(screen)
        .add(
            Button.builder(
                    Component.literal("CW"),
                    b -> {
                      ScannerState.close();
                      net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(
                          new OpenCyberwarePayload());
                    })
                .bounds(screen.leftPos + screen.imageWidth + 2, screen.topPos + 4, 28, 25)
                .tooltip(
                    net.minecraft.client.gui.components.Tooltip.create(
                        Component.literal("Cyberware")))
                .build());
  }

  public static void tooltip(
      net.minecraft.world.item.ItemStack stack, java.util.List<Component> text) {
    var client = Minecraft.getInstance();
    if (client.level == null) return;
    var entry = EquipmentRegistry.find(client.level, stack);
    if (entry.isEmpty()) return;
    var def = entry.get().getValue();
    var color =
        def.tier() == 3
            ? ChatFormatting.GOLD
            : def.tier() == 2 ? ChatFormatting.AQUA : ChatFormatting.GRAY;
    text.add(
        Component.literal(
                "TIER " + def.tier() + "  //  " + Math.round(def.capacity()) + " CAPACITY")
            .withStyle(color));
    for (var slot : def.slots()) {
      var type = client.level.registryAccess().registryOrThrow(EquipmentRegistry.SLOTS).get(slot);
      if (type != null)
        text.add(Component.translatable(type.name()).withStyle(ChatFormatting.DARK_AQUA));
    }
    text.add(
        Component.translatable("book.cybercraft.desc." + entry.get().getKey().location().getPath())
            .withStyle(ChatFormatting.GRAY));
    if (Screen.hasShiftDown()) {
      Cyberware.byId(def.behavior().getPath())
          .ifPresent(
              implant -> {
                text.add(
                    Component.literal("ACTIVATION  " + implant.activationCost() + " tolerance")
                        .withStyle(ChatFormatting.AQUA));
                text.add(
                    Component.literal("PASSIVE DRAIN  " + implant.passiveDrain() + " / second")
                        .withStyle(ChatFormatting.DARK_GRAY));
                if (implant == Cyberware.SANDEVISTAN || implant == Cyberware.BERSERK)
                  text.add(
                      Component.literal("Base: 10 s duration // 30 s cooldown after expiry")
                          .withStyle(ChatFormatting.GOLD));
                if (implant == Cyberware.CYBERDECK)
                  text.add(
                      Component.literal("Requires installed Kiroshi optics to hack")
                          .withStyle(ChatFormatting.GOLD));
              });
      text.add(
          Component.literal("Installed components persist through death")
              .withStyle(ChatFormatting.DARK_GRAY));
    } else
      text.add(
          Component.literal("Hold SHIFT for technical details")
              .withStyle(ChatFormatting.DARK_GRAY));
  }

  public static void arm(
      net.minecraft.client.player.AbstractClientPlayer entity,
      com.mojang.blaze3d.vertex.PoseStack pose,
      net.minecraft.client.renderer.MultiBufferSource buffer,
      int light) {
    for (var entry : CyberwareManager.get(entity).equipment()) {
      var slot =
          entity
              .level()
              .registryAccess()
              .registryOrThrow(EquipmentRegistry.SLOTS)
              .get(entry.slot());
      if (slot == null || !slot.anchor().equals("arms")) continue;
      if (entry.definition().equals(CyberCraft.id("mantis_blades"))) {
        MantisBladeRenderer.firstPerson(pose, buffer, light, entity);
        continue;
      }
      pose.pushPose();
      pose.translate(0, -.08, -.08);
      pose.scale(.35f, .35f, .35f);
      CyberwareLayer.draw(pose, buffer, light, entry, entity);
      pose.popPose();
    }
  }

  private ClientEquipment() {}
}
