package com.cybercraft.client;

import com.cybercraft.CyberCraft;
import com.cybercraft.cyberware.animation.Pose;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.Map;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.client.event.RenderArmEvent;

public final class MantisBladeRenderer {
  private static final ResourceLocation TEXTURE =
      CyberCraft.id("textures/item/mantis_blade.png");
  private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(TEXTURE);

  private static final Uv METAL = uv(.5f, .5f, 15.5f, 15.5f);
  private static final Uv STEEL = uv(16.5f, .5f, 31.5f, 15.5f);
  private static final Uv RED = uv(.5f, 16.5f, 15.5f, 23.5f);
  private static final Uv DARK = uv(16.5f, 16.5f, 31.5f, 23.5f);

  public static void thirdPerson(
      PoseStack pose,
      MultiBufferSource buffer,
      int light,
      ModelPart arm,
      LivingEntity entity,
      Map<String, Pose> bones) {
    float progress = MantisBladeClientState.progress(entity);
    pose.pushPose();
    arm.translateAndRotate(pose);
    draw(pose, buffer, light, progress, bones);
    pose.popPose();
  }

  public static void firstPerson(RenderArmEvent event) {
    boolean right = event.getArm() == HumanoidArm.RIGHT;
    var pose = event.getPoseStack();
    pose.pushPose();
    pose.translate((right ? 5f : -5f) / 16f, 2f / 16f, 0);
    draw(
        pose,
        event.getMultiBufferSource(),
        event.getPackedLight(),
        MantisBladeClientState.progress(event.getPlayer()),
        ImplantAnimationClient.bones(event.getPlayer(), "mantis_blades"));
    pose.popPose();
  }

  private static void draw(
      PoseStack pose,
      MultiBufferSource buffer,
      int light,
      float progress,
      Map<String, Pose> bones) {
    VertexConsumer vc = buffer.getBuffer(RENDER_TYPE);
    Pose plate = bones.getOrDefault("plate", Pose.REST);
    pose.pushPose();
    bone(pose, plate, .30f, -.17f);
    box(pose, vc, light, -.10f, .12f, -.23f, .10f, .47f, -.115f, METAL);
    box(pose, vc, light, -.082f, .135f, -.242f, .082f, .30f, -.228f, RED);
    for (int i = 0; i < 4; i++) {
      float y = .16f + i * .038f;
      box(pose, vc, light, -.075f, y, -.248f, .075f, y + .018f, -.240f, DARK);
    }
    box(pose, vc, light, -.108f, .19f, -.222f, -.098f, .45f, -.12f, STEEL);
    box(pose, vc, light, .098f, .19f, -.222f, .108f, .45f, -.12f, STEEL);
    box(pose, vc, light, -.06f, .46f, -.215f, .06f, .545f, -.13f, DARK);
    box(pose, vc, light, -.035f, .465f, -.222f, .035f, .53f, -.212f, RED);
    pose.popPose();
    if (progress <= .002f) return;
    Pose bladeBone = bones.getOrDefault("blade", Pose.REST);
    Pose edge = bones.getOrDefault("edge", Pose.REST);
    pose.pushPose();
    pose.translate(0, .30f, -.17f);
    pose.mulPose(Axis.XP.rotationDegrees(-14f * progress));
    pose.scale(1, progress, 1);
    pose.translate(0, -.30f, .17f);
    bone(pose, bladeBone, .30f, -.17f);
    pose.scale(edge.sx(), edge.sy(), edge.sz());
    blade(pose, vc, light);
    pose.popPose();
  }

  private static void bone(PoseStack pose, Pose bone, float pivotY, float pivotZ) {
    pose.translate(0, pivotY, pivotZ);
    if (bone.ry() != 0) pose.mulPose(Axis.YP.rotationDegrees(bone.ry()));
    if (bone.rx() != 0) pose.mulPose(Axis.XP.rotationDegrees(bone.rx()));
    if (bone.rz() != 0) pose.mulPose(Axis.ZP.rotationDegrees(bone.rz()));
    if (bone.sx() != 1 || bone.sy() != 1 || bone.sz() != 1)
      pose.scale(bone.sx(), bone.sy(), bone.sz());
    pose.translate(0, -pivotY, -pivotZ);
    if (bone.tx() != 0 || bone.ty() != 0 || bone.tz() != 0)
      pose.translate(bone.tx() / 16f, bone.ty() / 16f, bone.tz() / 16f);
  }

  private static void blade(PoseStack pose, VertexConsumer vc, int light) {
    box(pose, vc, light, -.045f, 0, -.020f, .045f, .34f, .020f, METAL);
    box(pose, vc, light, -.042f, .34f, -.018f, .042f, .70f, .018f, METAL);
    box(pose, vc, light, -.036f, .70f, -.015f, .036f, .86f, .015f, METAL);
    box(pose, vc, light, -.026f, .86f, -.011f, .026f, .94f, .011f, METAL);
    box(pose, vc, light, -.014f, .94f, -.007f, .014f, .99f, .007f, STEEL);
    box(pose, vc, light, -.005f, .99f, -.003f, .005f, 1.03f, .003f, STEEL);
    box(pose, vc, light, .042f, .02f, -.012f, .052f, .86f, .012f, STEEL);
    box(pose, vc, light, -.052f, .03f, -.012f, -.045f, .70f, .012f, DARK);
    for (int i = 0; i < 6; i++) {
      float y = .10f + i * .105f;
      box(pose, vc, light, -.048f, y, -.012f, -.064f, y + .075f, .012f, STEEL);
      box(pose, vc, light, -.064f, y + .014f, -.009f, -.076f, y + .061f, .009f, STEEL);
      box(pose, vc, light, -.076f, y + .027f, -.006f, -.084f, y + .048f, .006f, STEEL);
    }
    box(pose, vc, light, -.024f, .05f, -.026f, .024f, .095f, .026f, RED);
    for (float y : new float[] {.20f, .42f, .62f}) {
      box(pose, vc, light, -.014f, y, -.026f, .014f, y + .02f, -.018f, RED);
      box(pose, vc, light, -.014f, y, .018f, .014f, y + .02f, .026f, RED);
    }
  }

  private static void box(
      PoseStack pose,
      VertexConsumer vc,
      int light,
      float x0,
      float y0,
      float z0,
      float x1,
      float y1,
      float z1,
      Uv uv) {
    quad(pose, vc, light, x0, y0, z0, x1, y0, z0, x1, y1, z0, x0, y1, z0, 0, 0, -1, uv);
    quad(pose, vc, light, x1, y0, z1, x0, y0, z1, x0, y1, z1, x1, y1, z1, 0, 0, 1, uv);
    quad(pose, vc, light, x0, y0, z1, x0, y0, z0, x0, y1, z0, x0, y1, z1, -1, 0, 0, uv);
    quad(pose, vc, light, x1, y0, z0, x1, y0, z1, x1, y1, z1, x1, y1, z0, 1, 0, 0, uv);
    quad(pose, vc, light, x0, y1, z0, x1, y1, z0, x1, y1, z1, x0, y1, z1, 0, 1, 0, uv);
    quad(pose, vc, light, x0, y0, z1, x1, y0, z1, x1, y0, z0, x0, y0, z0, 0, -1, 0, uv);
  }

  private static void quad(
      PoseStack pose,
      VertexConsumer vc,
      int light,
      float ax,
      float ay,
      float az,
      float bx,
      float by,
      float bz,
      float cx,
      float cy,
      float cz,
      float dx,
      float dy,
      float dz,
      float nx,
      float ny,
      float nz,
      Uv uv) {
    var entry = pose.last();
    vertex(vc, entry, light, ax, ay, az, uv.u0, uv.v0, nx, ny, nz);
    vertex(vc, entry, light, bx, by, bz, uv.u1, uv.v0, nx, ny, nz);
    vertex(vc, entry, light, cx, cy, cz, uv.u1, uv.v1, nx, ny, nz);
    vertex(vc, entry, light, dx, dy, dz, uv.u0, uv.v1, nx, ny, nz);
  }

  private static void vertex(
      VertexConsumer vc,
      PoseStack.Pose pose,
      int light,
      float x,
      float y,
      float z,
      float u,
      float v,
      float nx,
      float ny,
      float nz) {
    vc.addVertex(pose, x, y, z)
        .setColor(-1)
        .setUv(u, v)
        .setOverlay(OverlayTexture.NO_OVERLAY)
        .setLight(light)
        .setNormal(pose, nx, ny, nz);
  }

  private static Uv uv(float x0, float y0, float x1, float y1) {
    return new Uv(x0 / 32f, y0 / 32f, x1 / 32f, y1 / 32f);
  }

  private record Uv(float u0, float v0, float u1, float v1) {}

  private MantisBladeRenderer() {}
}
