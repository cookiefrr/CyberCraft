package com.cybercraft.mixin.client;

import com.cybercraft.CyberCraft;
import com.cybercraft.item.CyberwareImplantItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.*;
import net.minecraft.world.item.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(ItemRenderer.class)
public abstract class ItemContextMixin {
  @ModifyVariable(
      method =
          "render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V",
      at = @At("HEAD"),
      argsOnly = true)
  private BakedModel cybercraft$gui(
      BakedModel model,
      ItemStack stack,
      ItemDisplayContext context,
      boolean left,
      PoseStack pose,
      MultiBufferSource buffer,
      int light,
      int overlay,
      BakedModel original) {
    if (context == ItemDisplayContext.GUI
        && stack.getItem() instanceof CyberwareImplantItem item
        && model
            != Minecraft.getInstance()
                .getModelManager()
                .getModel(CyberCraft.id("item/icons/" + item.implant().id())))
      return Minecraft.getInstance()
          .getModelManager()
          .getModel(CyberCraft.id("item/gui/" + item.implant().id()));
    return model;
  }
}
