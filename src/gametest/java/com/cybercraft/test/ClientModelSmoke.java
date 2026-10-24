package com.cybercraft.test;

import com.cybercraft.CyberCraft;
import com.cybercraft.cyberware.Cyberware;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.RandomSource;

public final class ClientModelSmoke implements ClientModInitializer {
  private boolean finished;

  public void onInitializeClient() {
    if (!Boolean.getBoolean("cybercraft.clientSmoke")) return;
    ClientTickEvents.END_CLIENT_TICK.register(
        client -> {
          if (finished || client.getOverlay() != null || client.screen == null) return;
          finished = true;
          var models = client.getModelManager();
          var graphics =
              new net.minecraft.client.gui.GuiGraphics(
                  client, client.renderBuffers().bufferSource());
          for (var implant : Cyberware.values()) {
            var id = CyberCraft.id(implant.id());
            var nativeModel = models.getModel(new ModelResourceLocation(id, "inventory"));
            var icon = models.getModel(id.withPrefix("item/icons/"));
            var gui = models.getModel(id.withPrefix("item/gui/"));
            if (nativeModel == models.getMissingModel()
                || nativeModel.isCustomRenderer()
                || nativeModel.getQuads(null, null, RandomSource.create()).isEmpty())
              throw new AssertionError("Missing native model: " + id);
            if (icon == models.getMissingModel()
                || icon.isCustomRenderer()
                || icon.getQuads(null, null, RandomSource.create()).isEmpty())
              throw new AssertionError("Missing flat icon: " + id);
            if (gui == models.getMissingModel() || !gui.isCustomRenderer())
              throw new AssertionError("Missing GUI rendering entry: " + id);
            graphics.renderItem(
                new net.minecraft.world.item.ItemStack(
                    com.cybercraft.registry.ModItems.implant(implant)),
                0,
                0);
          }
          graphics.flush();
          CyberCraft.LOGGER.info(
              "CLIENT MODEL SMOKE PASSED: 43 native models, 43 icons, 43 GUI renderer entries");
          client.stop();
        });
  }
}
