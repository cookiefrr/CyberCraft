package com.cybercraft.client;

import com.cybercraft.CyberCraft;
import com.cybercraft.cyberware.ImplantModels;
import com.cybercraft.cyberware.ImplantModels.Material;
import com.cybercraft.cyberware.ImplantModels.Tone;
import com.cybercraft.cyberware.animation.Ease;
import com.cybercraft.registry.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHandEvent;

@EventBusSubscriber(modid = CyberCraft.MOD_ID, value = Dist.CLIENT)
public final class MonowireRenderer {
  private static final ResourceLocation PALETTE =
      CyberCraft.id("textures/item/cyberware_palette.png");
  private static final RenderType SOLID = RenderType.entityCutoutNoCull(PALETTE);
  private static final int CERAMIC = ImplantModels.swatch(Material.CERAMIC, Tone.BASE);
  private static final int CARBON = ImplantModels.swatch(Material.CARBON, Tone.BASE);
  private static final int GUNMETAL = ImplantModels.swatch(Material.GUNMETAL, Tone.BASE);
  private static final int RUBBER = ImplantModels.swatch(Material.RUBBER, Tone.BASE);
  private static final int ORANGE = ImplantModels.swatch(Material.NEON_ORANGE, Tone.BASE);
  private static final int YELLOW = ImplantModels.swatch(Material.NEON_YELLOW, Tone.BASE);
  private static final int SEGMENTS = 8;
  private static final float SEGMENT_LENGTH = .11f;

  private MonowireRenderer() {}

  @SubscribeEvent
  public static void hand(RenderHandEvent event) {
    if (!event.getItemStack().is(ModItems.MONOWIRE.get())) return;
    event.setCanceled(true);
    boolean main = event.getHand() == InteractionHand.MAIN_HAND;
    int side = main ? 1 : -1;
    var pose = event.getPoseStack();
    pose.pushPose();
    pose.translate(side * .56f, -.52f - event.getEquipProgress() * .6f, -.72f);
    draw(
        pose,
        event.getMultiBufferSource(),
        event.getPackedLight(),
        event.getSwingProgress(),
        side);
    pose.popPose();
  }

  private static void draw(PoseStack pose, MultiBufferSource buffer, int light, float swing, int side) {
    VertexConsumer vc = buffer.getBuffer(SOLID);
    float unfurl = unfurl(swing);
    box(pose, vc, light, -.055f, -.025f, -.11f, .055f, .025f, .05f, GUNMETAL);
    box(pose, vc, light, -.05f, .025f, -.10f, .05f, .035f, .04f, CERAMIC);
    box(pose, vc, light, -.04f, .035f, -.09f, .02f, .041f, -.05f, ORANGE);
    box(pose, vc, light, -.02f, -.035f, -.02f, .02f, -.025f, .03f, RUBBER);
    box(pose, vc, light, -.03f, -.01f, -.13f, .03f, .01f, -.11f, CARBON);
    if (unfurl <= .01f) {
      for (int i = 0; i < 3; i++) {
        float z = -.115f - i * .012f;
        box(pose, vc, light, -.026f, -.011f, z, .026f, .011f, z + .006f, ORANGE);
      }
      return;
    }
    pose.pushPose();
    pose.translate(0, 0, -.13f);
    for (int i = 0; i < SEGMENTS; i++) {
      float lag = Mth.clamp(unfurl * SEGMENTS - i, 0f, 1f);
      pose.mulPose(Axis.YP.rotationDegrees(22.5f * lag * side));
      box(
          pose,
          vc,
          light,
          -.009f,
          -.007f,
          -SEGMENT_LENGTH,
          .009f,
          .007f,
          0,
          ORANGE);
      if (i == SEGMENTS - 1 && lag > .5f)
        box(
            pose,
            vc,
            light,
            -.013f,
            -.013f,
            -SEGMENT_LENGTH - .02f,
            .013f,
            .013f,
            -SEGMENT_LENGTH,
            YELLOW);
      pose.translate(0, 0, -SEGMENT_LENGTH);
    }
    pose.popPose();
  }

  private static float unfurl(float swing) {
    if (swing <= 0) return 0;
    if (swing < .55f) return Ease.EASE_OUT.apply(swing / .55f);
    return 1f - Ease.EASE_IN.apply((swing - .55f) / .45f);
  }

  private static void box(
      PoseStack pose,
      VertexConsumer consumer,
      int light,
      float x0,
      float y0,
      float z0,
      float x1,
      float y1,
      float z1,
      int swatch) {
    float[] base = uv(swatch, Tone.BASE);
    float[] top = uv(swatch, Tone.LIGHT);
    float[] bottom = uv(swatch, Tone.DARK);
    face(
        pose, consumer, light, top, x0, y1, z1, x1, y1, z1, x1, y1, z0, x0, y1, z0, 0, 1, 0);
    face(
        pose, consumer, light, bottom, x0, y0, z0, x1, y0, z0, x1, y0, z1, x0, y0, z1, 0, -1,
        0);
    face(
        pose, consumer, light, base, x1, y0, z1, x1, y1, z1, x0, y1, z1, x0, y0, z1, 0, 0, 1);
    face(
        pose, consumer, light, base, x0, y0, z0, x0, y1, z0, x1, y1, z0, x1, y0, z0, 0, 0, -1);
    face(
        pose, consumer, light, base, x1, y0, z0, x1, y1, z0, x1, y1, z1, x1, y0, z1, 1, 0, 0);
    face(
        pose, consumer, light, base, x0, y0, z1, x0, y1, z1, x0, y1, z0, x0, y0, z0, -1, 0, 0);
  }

  private static float[] uv(int swatch, Tone tone) {
    int index = swatch - Tone.BASE.ordinal() + tone.ordinal();
    return new float[] {
      ImplantModels.u0(index) / 16f,
      ImplantModels.v0(index) / 16f,
      ImplantModels.u1(index) / 16f,
      ImplantModels.v1(index) / 16f
    };
  }

  private static void face(
      PoseStack pose,
      VertexConsumer consumer,
      int light,
      float[] uv,
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
    var transform = pose.last();
    vertex(consumer, transform, light, uv[0], uv[1], x0, y0, z0, nx, ny, nz);
    vertex(consumer, transform, light, uv[2], uv[1], x1, y1, z1, nx, ny, nz);
    vertex(consumer, transform, light, uv[2], uv[3], x2, y2, z2, nx, ny, nz);
    vertex(consumer, transform, light, uv[0], uv[3], x3, y3, z3, nx, ny, nz);
  }

  private static void vertex(
      VertexConsumer consumer,
      PoseStack.Pose pose,
      int light,
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
        .setColor(255, 255, 255, 255)
        .setUv(u, v)
        .setOverlay(OverlayTexture.NO_OVERLAY)
        .setLight(light)
        .setNormal(pose, nx, ny, nz);
  }
}
