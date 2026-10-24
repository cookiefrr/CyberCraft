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
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = CyberCraft.MOD_ID, value = Dist.CLIENT)
public final class ClientEquipment {
  @SubscribeEvent
  public static void iconModels(ModelEvent.RegisterAdditional event) {
    for (var implant : Cyberware.values()) {
      event.register(
          net.minecraft.client.resources.model.ModelResourceLocation.standalone(
              CyberCraft.id("item/icons/" + implant.id())));
      var spec = ImplantModels.get(implant.id());
      if (spec == null) continue;
      for (int i = 0; i < spec.groups().size(); i++) {
        event.register(
            net.minecraft.client.resources.model.ModelResourceLocation.standalone(
                CyberCraft.id("item/" + implant.id() + "_worn_" + i)));
        if (spec.groups().get(i).mirror())
          event.register(
              net.minecraft.client.resources.model.ModelResourceLocation.standalone(
                  CyberCraft.id("item/" + implant.id() + "_worn_" + i + "_left")));
      }
    }
  }

  @SubscribeEvent
  public static void layers(EntityRenderersEvent.AddLayers event) {
    for (var skin : event.getSkins()) {
      PlayerRenderer renderer = event.getSkin(skin);
      if (renderer != null) renderer.addLayer(new CyberwareLayer<>(renderer));
    }
    for (var type : event.getEntityTypes()) {
      var renderer = event.getRenderer(type);
      if (renderer instanceof LivingEntityRenderer living
          && living.getModel() instanceof net.minecraft.client.model.HumanoidModel)
        living.addLayer(new CyberwareLayer(living));
    }
  }

  @SubscribeEvent
  public static void inventoryTab(ScreenEvent.Init.Post event) {
    if (!(event.getScreen() instanceof InventoryScreen)
        && !(event.getScreen() instanceof CreativeModeInventoryScreen)) return;
    var screen = (AbstractContainerScreen<?>) event.getScreen();
    event.addListener(
        Button.builder(
                Component.literal("CW"),
                b -> {
                  ScannerState.close();
                  PacketDistributor.sendToServer(new OpenCyberwarePayload());
                })
            .bounds(screen.getGuiLeft() + screen.getXSize() + 2, screen.getGuiTop() + 4, 28, 25)
            .tooltip(
                net.minecraft.client.gui.components.Tooltip.create(Component.literal("Cyberware")))
            .build());
  }

  @SubscribeEvent
  public static void tooltip(ItemTooltipEvent event) {
    var client = Minecraft.getInstance();
    if (client.level == null) return;
    var entry = EquipmentRegistry.find(client.level, event.getItemStack());
    if (entry.isEmpty()) return;
    var def = entry.get().getValue();
    var text = event.getToolTip();
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

  @SubscribeEvent
  public static void tooltipColor(RenderTooltipEvent.Color event) {
    var client = Minecraft.getInstance();
    if (client.level == null
        || EquipmentRegistry.find(client.level, event.getItemStack()).isEmpty()) return;
    event.setBackgroundStart(0xFA10232D);
    event.setBackgroundEnd(0xFA050E15);
    event.setBorderStart(0xFF00DDE8);
    event.setBorderEnd(0xFF285262);
  }

  @SubscribeEvent
  public static void arm(RenderArmEvent event) {
    var entity = event.getPlayer();
    var dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
    if (!(dispatcher.getRenderer(entity) instanceof PlayerRenderer renderer)) return;
    var model = renderer.getModel();
    var part =
        event.getArm() == net.minecraft.world.entity.HumanoidArm.RIGHT
            ? model.rightArm
            : model.leftArm;
    for (var entry : CyberwareManager.get(entity).equipment()) {
      var slot =
          entity
              .level()
              .registryAccess()
              .registryOrThrow(EquipmentRegistry.SLOTS)
              .get(entry.slot());
      if (slot == null || !slot.anchor().equals("arms")) continue;
      var definition = EquipmentRegistry.definition(entity.level(), entry.definition());
      if (definition == null) continue;
      if (definition.model().equals(CyberCraft.id("mantis_blades"))) {
        MantisBladeRenderer.firstPerson(event);
        continue;
      }
      var spec = ImplantModels.get(definition.model().getPath());
      if (spec == null) {
        var pose = event.getPoseStack();
        pose.pushPose();
        pose.translate(0, -.08, -.08);
        pose.scale(.35f, .35f, .35f);
        CyberwareLayer.draw(
            pose, event.getMultiBufferSource(), event.getPackedLight(), entry, entity);
        pose.popPose();
        continue;
      }
      var arm = event.getArm();
      for (int i = 0; i < spec.groups().size(); i++) {
        var group = spec.groups().get(i);
        boolean matches =
            arm == net.minecraft.world.entity.HumanoidArm.RIGHT
                ? group.part() == ImplantModels.Part.RIGHT_ARM
                : group.part() == ImplantModels.Part.LEFT_ARM;
        if (!matches) continue;
        String path =
            definition.model().getPath()
                + "_worn_"
                + i
                + (group.mirror() && ImplantModels.isLeft(group.part()) ? "_left" : "");
        var pose = event.getPoseStack();
        pose.pushPose();
        part.translateAndRotate(pose);
        CyberwareLayer.drawWorn(
            pose,
            event.getMultiBufferSource(),
            event.getPackedLight(),
            entry,
            CyberCraft.id("item/" + path));
        pose.popPose();
      }
    }
  }

  private ClientEquipment() {}
}
