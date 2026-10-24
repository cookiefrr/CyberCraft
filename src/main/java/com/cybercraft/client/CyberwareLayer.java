package com.cybercraft.client;

import com.cybercraft.CyberCraft;
import com.cybercraft.cyberware.CyberwareManager;
import com.cybercraft.cyberware.ImplantModels;
import com.cybercraft.equipment.*;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;

public final class CyberwareLayer<T extends LivingEntity, M extends EntityModel<T>>
    extends RenderLayer<T, M> {
  public CyberwareLayer(RenderLayerParent<T, M> parent) {
    super(parent);
  }

  @Override
  public void render(
      PoseStack pose,
      MultiBufferSource buffer,
      int light,
      T entity,
      float limb,
      float amount,
      float partial,
      float age,
      float yaw,
      float pitch) {
    if (!(getParentModel() instanceof HumanoidModel<?> model)) return;
    boolean invisible = entity.isInvisible();
    int fallback = 0;
    for (var entry : CyberwareManager.get(entity).equipment()) {
      var slot =
          entity
              .level()
              .registryAccess()
              .registryOrThrow(EquipmentRegistry.SLOTS)
              .get(entry.slot());
      if (slot == null) continue;
      var definition = EquipmentRegistry.definition(entity.level(), entry.definition());
      if (definition == null) continue;
      boolean camo = definition.model().equals(CyberCraft.id("optical_camo"));
      if (invisible && !camo) continue;
      if (definition.model().equals(CyberCraft.id("mantis_blades"))) {
        var bones = ImplantAnimationClient.bones(entity, "mantis_blades");
        MantisBladeRenderer.thirdPerson(pose, buffer, light, model.rightArm, entity, bones);
        MantisBladeRenderer.thirdPerson(pose, buffer, light, model.leftArm, entity, bones);
        continue;
      }
      ImplantModels.Model spec = ImplantModels.get(definition.model().getPath());
      if (spec == null) {
        legacy(pose, buffer, light, model, slot.anchor(), entry, entity, fallback++);
        continue;
      }
      ImplantRenderer.draw(
          pose,
          buffer,
          light,
          spec,
          model,
          null,
          ImplantAnimationClient.bones(entity, definition.model().getPath()));
    }
  }

  private static void legacy(
      PoseStack pose,
      MultiBufferSource buffer,
      int light,
      HumanoidModel<?> model,
      String anchor,
      InstalledImplant entry,
      LivingEntity entity,
      int n) {
    switch (anchor) {
      case "head" ->
          attached(pose, buffer, light, model.head, entry, entity, 0, -.23, -.25, .33f);
      case "arms" -> {
        attached(pose, buffer, light, model.rightArm, entry, entity, -.01, .30, -.01, .34f);
        attached(pose, buffer, light, model.leftArm, entry, entity, .01, .30, -.01, .34f);
      }
      case "legs" -> {
        attached(pose, buffer, light, model.rightLeg, entry, entity, 0, .30, -.05, .36f);
        attached(pose, buffer, light, model.leftLeg, entry, entity, 0, .30, -.05, .36f);
      }
      default ->
          attached(
              pose,
              buffer,
              light,
              model.body,
              entry,
              entity,
              (n % 3 - 1) * .17,
              .18 + (n % 2) * .17,
              -.18,
              .28f);
    }
  }

  private static void attached(
      PoseStack pose,
      MultiBufferSource buffer,
      int light,
      ModelPart part,
      InstalledImplant entry,
      LivingEntity entity,
      double x,
      double y,
      double z,
      float scale) {
    pose.pushPose();
    part.translateAndRotate(pose);
    pose.translate(x, y, z);
    pose.scale(scale, scale, scale);
    draw(pose, buffer, light, entry, entity);
    pose.popPose();
  }

  public static void draw(
      PoseStack pose,
      MultiBufferSource buffer,
      int light,
      InstalledImplant entry,
      LivingEntity entity) {
    var client = Minecraft.getInstance();
    var definition = EquipmentRegistry.definition(entity.level(), entry.definition());
    if (definition == null) return;
    var model =
        client
            .getModelManager()
            .getModel(new ModelResourceLocation(definition.model(), "inventory"));
    client
        .getItemRenderer()
        .render(
            entry.stack(),
            ItemDisplayContext.FIXED,
            false,
            pose,
            buffer,
            light,
            OverlayTexture.NO_OVERLAY,
            model);
  }
}
