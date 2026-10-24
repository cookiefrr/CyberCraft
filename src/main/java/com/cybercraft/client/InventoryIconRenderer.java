package com.cybercraft.client;

import com.cybercraft.item.CyberwareImplantItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.*;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public final class InventoryIconRenderer extends GeoItemRenderer<CyberwareImplantItem> {
  public InventoryIconRenderer(CyberwareImplantItem item) {
    super(item);
  }

  @Override
  public void renderByItem(
      ItemStack stack,
      ItemDisplayContext context,
      PoseStack pose,
      MultiBufferSource buffer,
      int light,
      int overlay) {
    var client = Minecraft.getInstance();
    var id = BuiltInRegistries.ITEM.getKey(stack.getItem());
    var model =
        client
            .getModelManager()
            .getModel(ModelResourceLocation.standalone(id.withPrefix("item/icons/")));
    pose.pushPose();
    pose.translate(.5, .5, .5);
    client
        .getItemRenderer()
        .render(stack, ItemDisplayContext.GUI, false, pose, buffer, light, overlay, model);
    pose.popPose();
  }
}
