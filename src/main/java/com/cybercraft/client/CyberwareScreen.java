package com.cybercraft.client;

import com.cybercraft.cyberware.*;
import com.cybercraft.equipment.*;
import com.cybercraft.menu.CyberwareMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class CyberwareScreen extends AbstractContainerScreen<CyberwareMenu> {
  private int selectedSlot = -1;
  private static final int CYAN = 0xFF00E6EF, DIM = 0xFF78949F;

  public CyberwareScreen(CyberwareMenu menu, Inventory inventory, Component title) {
    super(menu, inventory, title);
    imageWidth = 320;
    imageHeight = 232;
    inventoryLabelY = 141;
  }

  @Override
  protected void init() {
    super.init();
    addRenderableWidget(
        Button.builder(Component.literal("Back"), b -> back())
            .bounds(leftPos + 242, topPos + 5, 66, 18)
            .tooltip(
                net.minecraft.client.gui.components.Tooltip.create(
                    Component.literal("Back to Inventory")))
            .build());
    addRenderableWidget(
        Button.builder(
                Component.literal("<"), b -> menu.page = Math.floorMod(menu.page - 1, menu.pages()))
            .bounds(leftPos + 132, topPos + 27, 20, 16)
            .build());
    addRenderableWidget(
        Button.builder(Component.literal(">"), b -> menu.page = (menu.page + 1) % menu.pages())
            .bounds(leftPos + 168, topPos + 27, 20, 16)
            .build());
  }

  private void back() {
    if (minecraft == null || minecraft.player == null) return;
    minecraft.player.closeContainer();
    minecraft.setScreen(
        minecraft.player.isCreative()
            ? new CreativeModeInventoryScreen(
                minecraft.player,
                minecraft.player.connection.enabledFeatures(),
                minecraft.options.operatorItemsTab().get())
            : new InventoryScreen(minecraft.player));
  }

  @Override
  protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
    int x = leftPos, y = topPos;
    g.fillGradient(x, y, x + imageWidth, y + imageHeight, 0xFA12252D, 0xFA060F15);
    g.renderOutline(x, y, imageWidth, imageHeight, 0xFF315562);
    g.fill(x + 1, y + 1, x + imageWidth - 1, y + 2, CYAN);
    int sweep = (int) (net.minecraft.Util.getMillis() / 18 % (imageWidth - 24));
    g.fill(x + 2 + sweep, y + 2, x + 22 + sweep, y + 3, 0xCC99FCFF);
    for (int line = 24; line < 138; line += 4)
      g.fill(x + 2, y + line, x + imageWidth - 2, y + line + 1, 0x0600E6EF);
    g.fill(x + 118, y + 46, x + 202, y + 135, 0x6602070C);
    g.renderOutline(x + 118, y + 46, 84, 89, 0xFF23424E);
    InventoryScreen.renderEntityInInventoryFollowsMouse(
        g, x + 122, y + 46, x + 198, y + 134, 37, .0625f, mouseX, mouseY, menu.player);
    int begin = menu.page * CyberwareMenu.PAGE_SIZE;
    for (int i = begin; i < Math.min(menu.equipmentSlots, begin + CyberwareMenu.PAGE_SIZE); i++) {
      var slot = menu.slots.get(i);
      int sx = x + slot.x, sy = y + slot.y;
      boolean recovery = i >= menu.positions.size();
      g.fill(sx - 1, sy - 1, sx + 17, sy + 17, 0xFF071017);
      g.renderOutline(sx - 1, sy - 1, 18, 18, recovery ? 0xFFFFAB42 : 0xFF3F6978);
      g.fill(sx, sy, sx + 16, sy + 1, 0xFF142C36);
      if (i == selectedSlot) g.renderOutline(sx - 2, sy - 2, 20, 20, CYAN);
      if (slot.hasItem()) {
        var def = EquipmentRegistry.find(menu.player.level(), slot.getItem());
        int accent =
            def.map(
                    d ->
                        d.getValue().tier() == 3
                            ? 0xFFFFA148
                            : d.getValue().tier() == 2 ? CYAN : 0xFF83A9B6)
                .orElse(CYAN);
        g.fill(sx - 1, sy + 16, sx + 17, sy + 17, accent);
      }
      Component label =
          recovery
              ? Component.literal("Recovered")
              : Component.translatable(menu.positions.get(i).definition().name());
      String text = font.plainSubstrByWidth(label.getString(), 80);
      g.drawString(
          font, text, i % 12 < 6 ? sx + 23 : sx - 6 - font.width(text), sy + 4, DIM, false);
    }
    for (int i = menu.equipmentSlots; i < menu.slots.size(); i++) {
      var slot = menu.slots.get(i);
      g.fill(x + slot.x - 1, y + slot.y - 1, x + slot.x + 17, y + slot.y + 17, 0xFF061017);
      g.renderOutline(x + slot.x - 1, y + slot.y - 1, 18, 18, 0xFF3C5661);
    }
    var data = ClientCyberwareState.get();
    float used = CyberwareManager.usedCapacity(menu.player, data),
        max = CyberwareManager.maxCapacity(menu.player);
    g.fill(x + 10, y + 141, x + 310, y + 142, 0xFF243F4D);
    g.fill(
        x + 10,
        y + 141,
        x + 10 + Math.round(300 * Math.min(1, used / Math.max(1, max))),
        y + 142,
        used > max ? 0xFFFF4265 : CYAN);
    g.drawString(
        font, "CAP " + Math.round(used) + "/" + Math.round(max), x + 10, y + 5, CYAN, false);
    g.drawString(
        font,
        "TOL " + Math.round(data.tolerance()) + "   RAM " + Math.round(data.ram()),
        x + 10,
        y + 16,
        DIM,
        false);
    g.drawCenteredString(font, (menu.page + 1) + " / " + menu.pages(), x + 160, y + 32, DIM);
  }

  @Override
  protected void renderLabels(GuiGraphics g, int mx, int my) {
    g.drawCenteredString(font, "RIPPERDOC // CYBERWARE", 160, 8, CYAN);
  }

  @Override
  public boolean mouseClicked(double x, double y, int button) {
    for (int i = menu.page * 12; i < Math.min(menu.equipmentSlots, (menu.page + 1) * 12); i++) {
      var slot = menu.slots.get(i);
      int labelX = leftPos + (i % 12 < 6 ? 35 : 204);
      if (x >= labelX && x < labelX + 80 && y >= topPos + slot.y && y < topPos + slot.y + 16) {
        selectedSlot = i;
        return true;
      }
    }
    return super.mouseClicked(x, y, button);
  }

  private void detail(GuiGraphics g, int mouseX, int mouseY) {
    if (selectedSlot < 0 || selectedSlot >= menu.equipmentSlots) return;
    var slot = menu.slots.get(selectedSlot);
    if (!slot.hasItem()) return;
    int available = width - leftPos - imageWidth - 12;
    if (available < 115) return;
    int x = leftPos + imageWidth + 6, y = topPos + 26, w = Math.min(170, available);
    g.fillGradient(x, y, x + w, y + 144, 0xF012252D, 0xF0060F15);
    g.renderOutline(x, y, w, 144, CYAN);
    g.renderItem(slot.getItem(), x + 8, y + 8);
    var lines = font.split(slot.getItem().getHoverName(), w - 16);
    int textY = y + 30;
    for (var line : lines) {
      g.drawString(font, line, x + 8, textY, CYAN, false);
      textY += 10;
    }
    var definition = EquipmentRegistry.find(menu.player.level(), slot.getItem());
    if (definition.isPresent()) {
      var d = definition.get().getValue();
      g.drawString(
          font,
          "TIER " + d.tier() + " // CAP " + Math.round(d.capacity()),
          x + 8,
          textY + 4,
          DIM,
          false);
      textY += 20;
      for (var line :
          font.split(
              Component.translatable(
                  "book.cybercraft.desc." + definition.get().getKey().location().getPath()),
              w - 16)) {
        if (textY > y + 128) break;
        g.drawString(font, line, x + 8, textY, DIM, false);
        textY += 10;
      }
    }
  }

  @Override
  public void render(GuiGraphics g, int mx, int my, float partial) {
    super.render(g, mx, my, partial);
    detail(g, mx, my);
    renderTooltip(g, mx, my);
    if (hoveredSlot != null && !hoveredSlot.hasItem() && hoveredSlot.index < menu.positions.size())
      g.renderTooltip(
          font,
          Component.translatable(menu.positions.get(hoveredSlot.index).definition().name()),
          mx,
          my);
  }

  @Override
  public void onClose() {
    back();
  }
}
