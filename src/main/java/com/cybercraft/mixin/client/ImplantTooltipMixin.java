package com.cybercraft.mixin.client;

import com.cybercraft.equipment.EquipmentRegistry;
import com.llamalad7.mixinextras.injector.wrapoperation.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphics.class)
public abstract class ImplantTooltipMixin {
  @Unique private boolean cybercraft$implantTooltip;

  @Inject(
      method =
          "renderTooltip(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V",
      at = @At("HEAD"))
  private void cybercraft$begin(Font font, ItemStack stack, int x, int y, CallbackInfo ci) {
    var level = Minecraft.getInstance().level;
    cybercraft$implantTooltip = level != null && EquipmentRegistry.find(level, stack).isPresent();
  }

  @Inject(
      method =
          "renderTooltip(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V",
      at = @At("RETURN"))
  private void cybercraft$end(Font font, ItemStack stack, int x, int y, CallbackInfo ci) {
    cybercraft$implantTooltip = false;
  }

  // Vanilla draws the background inside this synthetic callback.
  @WrapOperation(
      method = "method_51743",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil;renderTooltipBackground(Lnet/minecraft/client/gui/GuiGraphics;IIIII)V"))
  private void cybercraft$panel(
      GuiGraphics g, int x, int y, int width, int height, int z, Operation<Void> original) {
    if (!cybercraft$implantTooltip) {
      original.call(g, x, y, width, height, z);
      return;
    }
    g.fillGradient(x - 3, y - 3, x + width + 3, y + height + 3, z, 0xFA10232D, 0xFA050E15);
    g.fill(x - 3, y - 3, x + width + 3, y - 2, z, 0xFF00DDE8);
    g.fill(x - 3, y + height + 2, x + width + 3, y + height + 3, z, 0xFF285262);
    g.fillGradient(x - 3, y - 2, x - 2, y + height + 2, z, 0xFF00DDE8, 0xFF285262);
    g.fillGradient(x + width + 2, y - 2, x + width + 3, y + height + 2, z, 0xFF00DDE8, 0xFF285262);
  }
}
