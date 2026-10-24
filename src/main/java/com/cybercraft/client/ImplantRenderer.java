package com.cybercraft.client;

import com.cybercraft.cyberware.ImplantModels;
import com.cybercraft.cyberware.ImplantModels.Box;
import com.cybercraft.cyberware.ImplantModels.Group;
import com.cybercraft.cyberware.ImplantModels.Material;
import com.cybercraft.cyberware.ImplantModels.Part;
import com.cybercraft.cyberware.animation.Pose;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Map;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

public final class ImplantRenderer {
  private static final ResourceLocation PALETTE =
      ResourceLocation.fromNamespaceAndPath("cybercraft", "textures/item/cyberware_palette.png");
  private static final RenderType SOLID = RenderType.entityCutoutNoCull(PALETTE);
  private static final RenderType TRANSLUCENT = RenderType.entityTranslucent(PALETTE);

  public static void draw(
      PoseStack pose,
      MultiBufferSource buffer,
      int light,
      ImplantModels.Model spec,
      HumanoidModel<?> model,
      HumanoidArm onlyArm,
      Map<String, Pose> bones) {
    VertexConsumer solid = buffer.getBuffer(SOLID);
    for (Group group : spec.groups()) {
      if (onlyArm != null && armOf(group.part()) != onlyArm) continue;
      drawPart(pose, solid, light, model, group, bones, false);
    }
    VertexConsumer translucent = buffer.getBuffer(TRANSLUCENT);
    for (Group group : spec.groups()) {
      if (onlyArm != null && armOf(group.part()) != onlyArm) continue;
      drawPart(pose, translucent, light, model, group, bones, true);
    }
  }

  private static void drawPart(
      PoseStack pose,
      VertexConsumer consumer,
      int light,
      HumanoidModel<?> model,
      Group group,
      Map<String, Pose> bones,
      boolean translucent) {
    ModelPart part = part(model, group.part());
    if (part == null) return;
    boolean animated = bones.containsKey(group.bone());
    if (group.hidden() && !animated) return;
    Pose bone = animated ? bones.get(group.bone()) : Pose.REST;
    if (translucent && bone.alpha() <= 0) return;
    pose.pushPose();
    part.translateAndRotate(pose);
    applyBone(pose, group, bone);
    for (Box box : group.boxes()) {
      if (isTranslucent(box.material()) != translucent) continue;
      box(pose, consumer, light, box, group.mirror(), bone.alpha());
    }
    pose.popPose();
  }

  private static void applyBone(PoseStack pose, Group group, Pose bone) {
    Vec3 pivot = group.pivot();
    float px = (float) pivot.x() * (group.mirror() ? -1f : 1f) / 16f;
    float py = (float) pivot.y() / 16f;
    float pz = (float) pivot.z() / 16f;
    pose.translate(px, py, pz);
    if (bone.ry() != 0)
      pose.mulPose(new Quaternionf().rotationY((float) Math.toRadians(bone.ry())));
    if (bone.rx() != 0)
      pose.mulPose(new Quaternionf().rotationX((float) Math.toRadians(bone.rx())));
    if (bone.rz() != 0)
      pose.mulPose(new Quaternionf().rotationZ((float) Math.toRadians(bone.rz())));
    if (bone.sx() != 1 || bone.sy() != 1 || bone.sz() != 1)
      pose.scale(bone.sx(), bone.sy(), bone.sz());
    pose.translate(-px, -py, -pz);
    if (bone.tx() != 0 || bone.ty() != 0 || bone.tz() != 0)
      pose.translate(bone.tx() / 16f, bone.ty() / 16f, bone.tz() / 16f);
  }

  private static void box(
      PoseStack pose,
      VertexConsumer consumer,
      int light,
      Box box,
      boolean mirror,
      float alpha) {
    float x0 = (mirror ? -box.x1() : box.x0()) / 16f;
    float x1 = (mirror ? -box.x0() : box.x1()) / 16f;
    float y0 = box.y0() / 16f;
    float y1 = box.y1() / 16f;
    float z0 = box.z0() / 16f;
    float z1 = box.z1() / 16f;
    int swatch = ImplantModels.swatch(box.material(), ImplantModels.Tone.BASE);
    float u0 = ImplantModels.u0(swatch) / 16f;
    float v0 = ImplantModels.v0(swatch) / 16f;
    float u1 = ImplantModels.u1(swatch) / 16f;
    float v1 = ImplantModels.v1(swatch) / 16f;
    int lighter = swatch - ImplantModels.Tone.BASE.ordinal() + ImplantModels.Tone.LIGHT.ordinal();
    int darker = swatch - ImplantModels.Tone.BASE.ordinal() + ImplantModels.Tone.DARK.ordinal();
    float lu0 = ImplantModels.u0(lighter) / 16f;
    float lv0 = ImplantModels.v0(lighter) / 16f;
    float lu1 = ImplantModels.u1(lighter) / 16f;
    float lv1 = ImplantModels.v1(lighter) / 16f;
    float du0 = ImplantModels.u0(darker) / 16f;
    float dv0 = ImplantModels.v0(darker) / 16f;
    float du1 = ImplantModels.u1(darker) / 16f;
    float dv1 = ImplantModels.v1(darker) / 16f;
    int a = Math.max(0, Math.min(255, Math.round(alpha * 255f)));

    // up
    face(
        pose,
        consumer,
        light,
        a,
        lu0,
        lv0,
        lu1,
        lv1,
        x0,
        y1,
        z1,
        x1,
        y1,
        z1,
        x1,
        y1,
        z0,
        x0,
        y1,
        z0,
        0,
        1,
        0);
    // down
    face(
        pose,
        consumer,
        light,
        a,
        du0,
        dv0,
        du1,
        dv1,
        x0,
        y0,
        z0,
        x1,
        y0,
        z0,
        x1,
        y0,
        z1,
        x0,
        y0,
        z1,
        0,
        -1,
        0);
    // south
    face(
        pose,
        consumer,
        light,
        a,
        u0,
        v0,
        u1,
        v1,
        x1,
        y0,
        z1,
        x1,
        y1,
        z1,
        x0,
        y1,
        z1,
        x0,
        y0,
        z1,
        0,
        0,
        1);
    // north
    face(
        pose,
        consumer,
        light,
        a,
        u0,
        v0,
        u1,
        v1,
        x0,
        y0,
        z0,
        x0,
        y1,
        z0,
        x1,
        y1,
        z0,
        x1,
        y0,
        z0,
        0,
        0,
        -1);
    // east
    face(
        pose,
        consumer,
        light,
        a,
        u0,
        v0,
        u1,
        v1,
        x1,
        y0,
        z0,
        x1,
        y1,
        z0,
        x1,
        y1,
        z1,
        x1,
        y0,
        z1,
        1,
        0,
        0);
    // west
    face(
        pose,
        consumer,
        light,
        a,
        u0,
        v0,
        u1,
        v1,
        x0,
        y0,
        z1,
        x0,
        y1,
        z1,
        x0,
        y1,
        z0,
        x0,
        y0,
        z0,
        -1,
        0,
        0);
  }

  private static void face(
      PoseStack pose,
      VertexConsumer consumer,
      int light,
      int alpha,
      float u0,
      float v0,
      float u1,
      float v1,
      float x0,
      float y0,
      float z0,
      float x1,
      float y1,
      float z1,
      float x2,
      float y2,
      float z2,
      float x3,
      float y3,
      float z3,
      float nx,
      float ny,
      float nz) {
    PoseStack.Pose transform = pose.last();
    vertex(consumer, transform, light, alpha, u0, v0, x0, y0, z0, nx, ny, nz);
    vertex(consumer, transform, light, alpha, u1, v0, x1, y1, z1, nx, ny, nz);
    vertex(consumer, transform, light, alpha, u1, v1, x2, y2, z2, nx, ny, nz);
    vertex(consumer, transform, light, alpha, u0, v1, x3, y3, z3, nx, ny, nz);
  }

  private static void vertex(
      VertexConsumer consumer,
      PoseStack.Pose pose,
      int light,
      int alpha,
      float u,
      float v,
      float x,
      float y,
      float z,
      float nx,
      float ny,
      float nz) {
    consumer
        .addVertex(pose, x, y, z)
        .setColor(255, 255, 255, alpha)
        .setUv(u, v)
        .setOverlay(OverlayTexture.NO_OVERLAY)
        .setLight(light)
        .setNormal(pose, nx, ny, nz);
  }

  public static boolean isTranslucent(Material material) {
    return material == Material.GLASS || material == Material.HOLO || material == Material.FLASH;
  }

  private static HumanoidArm armOf(Part part) {
    return part == Part.RIGHT_ARM
        ? HumanoidArm.RIGHT
        : part == Part.LEFT_ARM ? HumanoidArm.LEFT : null;
  }

  private static ModelPart part(HumanoidModel<?> model, Part part) {
    return switch (part) {
      case HEAD -> model.head;
      case BODY -> model.body;
      case RIGHT_ARM -> model.rightArm;
      case LEFT_ARM -> model.leftArm;
      case RIGHT_LEG -> model.rightLeg;
      case LEFT_LEG -> model.leftLeg;
    };
  }

  private ImplantRenderer() {}
}
