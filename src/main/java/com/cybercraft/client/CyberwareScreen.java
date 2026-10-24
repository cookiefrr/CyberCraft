package com.cybercraft.client;

import com.cybercraft.cyberware.Cyberware;
import com.cybercraft.cyberware.CyberwareData;
import com.cybercraft.cyberware.CyberwareSlot;
import com.cybercraft.network.RemoveImplantPayload;
import com.cybercraft.registry.ModItems;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;

/**
 * Interfaccia "Ripperdoc Link" a corpo centrale, come la schermata
 * di installazione di Cyberpunk 2077: barra Capacità a sinistra, slot
 * disposti simmetricamente attorno al giocatore, dettaglio e rimozione
 * nel pannello inferiore.
 */
public class CyberwareScreen extends Screen {

    private static final int CYAN = 0xFF00F0FF;
    private static final int CYAN_DIM = 0x6600F0FF;
    private static final int MINT = 0xFF53FFC1;
    private static final int GREY = 0xFFB8C6CE;
    private static final int GREY_DIM = 0xFF73838D;
    private static final int PANEL = 0xE60D171D;
    private static final int PANEL_LIGHT = 0xE615262E;
    private static final int BORDER = 0xFF35525C;
    private static final int RED = 0xFFFF003C;
    private static final int AMBER = 0xFFFF9900;

    /** Ordine visivo degli slot: 5 a sinistra e 5 a destra del corpo. */
    private static final CyberwareSlot[] LEFT_SLOTS = {
            CyberwareSlot.FRONTAL_CORTEX, CyberwareSlot.ARMS, CyberwareSlot.SKELETON,
            CyberwareSlot.NERVOUS, CyberwareSlot.INTEGUMENTARY
    };
    private static final CyberwareSlot[] RIGHT_SLOTS = {
            CyberwareSlot.OPERATING_SYSTEM, CyberwareSlot.OCULAR, CyberwareSlot.HANDS,
            CyberwareSlot.CIRCULATORY, CyberwareSlot.LEGS
    };

    private int panelX;
    private int panelY;
    private int panelW;
    private int panelH;

    private int contentY;
    private int contentH;
    private int capX;
    private int capY;
    private int capW;
    private int capH;

    private int cardW;
    private int rowH;
    private int leftX;
    private int rightX;
    private int rowGap;
    private int slotsTop;
    private boolean compact;
    private boolean footerVisible;

    private int bodyX1;
    private int bodyY1;
    private int bodyX2;
    private int bodyY2;
    private boolean showBody;

    private int detailX;
    private int detailY;
    private int detailW;
    private int detailH;
    private int removeX;
    private int removeY;
    private int removeW;
    private int removeH;

    private CyberwareSlot selectedSlot;

    public CyberwareScreen() {
        super(Component.translatable("screen.cybercraft.cyberware"));
    }

    @Override
    protected void init() {
        panelW = Math.min(620, width - 16);
        panelH = Math.min(340, height - 16);
        panelX = (width - panelW) / 2;
        panelY = (height - panelH) / 2;

        int margin = 10;
        int gap = 8;
        compact = panelH < 300;
        contentY = panelY + 38;
        detailH = compact ? 52 : 76;
        footerVisible = !compact;
        contentH = panelH - 38 - detailH - (compact ? 8 : 16);

        capW = 40;
        capX = panelX + margin;
        capY = contentY;
        capH = contentH;

        int naturalBodyW = Math.max(150, Math.min(210, panelW / 3));
        showBody = panelW >= 540;
        int bodyW = showBody ? naturalBodyW : 0;

        cardW = (panelW - margin * 2 - capW - bodyW - gap * 3) / 2;
        leftX = capX + capW + gap;
        rightX = panelX + panelW - margin - cardW;

        bodyX1 = leftX + cardW + gap;
        bodyX2 = rightX - gap;
        bodyY1 = contentY;
        bodyY2 = contentY + contentH;
        if (bodyX2 - bodyX1 < 60) {
            showBody = false;
        }

        rowGap = compact ? 2 : 5;
        int headerSpace = compact ? 10 : 15;
        rowH = Math.min(40, Math.max(18, (contentH - headerSpace - rowGap * 4) / 5));
        slotsTop = contentY + headerSpace;

        detailX = panelX + margin;
        detailY = panelY + panelH - detailH - (compact ? 6 : 10);
        detailW = panelW - margin * 2;

        removeW = compact ? 94 : 116;
        removeH = compact ? 18 : 20;
        removeX = detailX + detailW - removeW - 8;
        removeY = detailY + (compact ? 12 : 18);

        CyberwareData data = ClientCyberwareState.get();
        if (selectedSlot == null) {
            selectedSlot = firstInstalled(data);
        }
    }

    private static CyberwareSlot firstInstalled(CyberwareData data) {
        for (CyberwareSlot slot : CyberwareSlot.values()) {
            if (data.inSlot(slot).isPresent()) {
                return slot;
            }
        }
        return null;
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        renderBackground(gui, mouseX, mouseY, partialTick);
        gui.fill(0, 0, width, height, 0xC0030709);

        gui.fill(panelX + 6, panelY + 6, panelX + panelW + 6, panelY + panelH + 6, 0x77000000);
        gui.fillGradient(panelX, panelY, panelX + panelW, panelY + panelH, 0xF00C1A20, 0xF0050B0E);
        frame(gui, panelX, panelY, panelW, panelH, BORDER);
        scanlines(gui, panelX + 1, panelY + 1, panelW - 2, panelH - 2);
        gui.fill(panelX + 1, panelY + 1, panelX + panelW - 1, panelY + 2, 0x9900F0FF);

        renderHeader(gui, mouseX, mouseY);
        renderCapacity(gui, mouseX, mouseY);
        renderSlots(gui, mouseX, mouseY);
        if (showBody) {
            renderBody(gui, mouseX, mouseY);
        }
        renderDetail(gui, mouseX, mouseY);

        if (footerVisible) {
            gui.drawString(font, Component.translatable("screen.cybercraft.footer").getString(),
                    panelX + 12, panelY + panelH - 9, GREY_DIM, false);
        }
    }

    private void renderHeader(GuiGraphics gui, int mouseX, int mouseY) {
        gui.drawString(font, truncate(Component.translatable("screen.cybercraft.cyberware.title").getString(),
                panelW - 100), panelX + 12, panelY + 9, CYAN, false);

        CyberwareData data = ClientCyberwareState.get();
        int installed = data.implants().size();
        String count = installed + " / " + CyberwareSlot.values().length;
        gui.drawString(font, count, panelX + panelW - font.width(count) - 12, panelY + 20,
                installed > 0 ? MINT : GREY_DIM, false);
        gui.drawString(font, truncate(Component.translatable("screen.cybercraft.cyberware.subtitle").getString(),
                panelW - font.width(count) - 36), panelX + 12, panelY + 20, GREY_DIM, false);

        String close = "[" + CyberCraftClient.CYBERWARE_KEY.getTranslatedKeyMessage().getString()
                + "] " + Component.translatable("screen.cybercraft.close").getString();
        gui.drawString(font, close, panelX + panelW - font.width(close) - 12, panelY + 9, GREY, false);

        gui.fill(panelX + 10, panelY + 32, panelX + panelW - 10, panelY + 33, 0x6600F0FF);
        gui.fill(panelX + 10, panelY + 33, panelX + 10 + Math.min(90, panelW / 5), panelY + 34, CYAN);
    }

    // ------------------------------------------------------------------
    // Barra capacità verticale
    // ------------------------------------------------------------------

    private void renderCapacity(GuiGraphics gui, int mouseX, int mouseY) {
        gui.fill(capX, capY, capX + capW, capY + capH, 0xB30A1419);
        frame(gui, capX, capY, capW, capH, BORDER);

        gui.drawString(font, Component.translatable("screen.cybercraft.capacity").getString(),
                capX + 5, capY + 5, MINT, false);

        CyberwareData data = ClientCyberwareState.get();
        float used = usedCapacity(data);
        float max = maxCapacity(data);
        boolean over = used > max + 0.01f;

        int segments = 18;
        int barTop = capY + 20;
        int barBottom = capY + capH - 40;
        int total = Math.max(segments, barBottom - barTop);
        int segH = Math.max(3, total / segments - 2);
        int filled = Math.max(0, Math.min(segments, Math.round(segments * used / Math.max(1.0f, max))));
        int fillColor = over ? RED : used / Math.max(1.0f, max) > 0.8f ? AMBER : MINT;

        for (int i = 0; i < segments; i++) {
            int sy = barBottom - (i + 1) * (segH + 2);
            boolean on = i < filled;
            gui.fill(capX + 8, sy, capX + capW - 8, sy + segH, on ? fillColor : 0xFF22343B);
            if (on) {
                gui.fill(capX + 8, sy, capX + capW - 8, sy + 1, 0xAAFFFFFF);
            }
        }

        String value = Math.round(used) + "/" + Math.round(max);
        gui.drawString(font, value, capX + (capW - font.width(value)) / 2, capY + capH - 32,
                over ? RED : MINT, false);
        String label = Component.translatable("screen.cybercraft.used").getString();
        gui.drawString(font, label, capX + (capW - font.width(label)) / 2, capY + capH - 21, GREY_DIM, false);
    }

    private static float usedCapacity(CyberwareData data) {
        float used = 0.0f;
        for (String id : data.implants()) {
            used += Cyberware.byId(id).map(Cyberware::capacityCost).orElse(0.0f);
        }
        return used;
    }

    private static float maxCapacity(CyberwareData data) {
        Minecraft client = Minecraft.getInstance();
        int level = client.player != null ? client.player.experienceLevel : 0;
        float base = Math.min(60.0f, 20.0f + level * 2.0f);
        return base + (data.has(Cyberware.CHROME_COMPRESSOR) ? 20.0f : 0.0f);
    }

    // ------------------------------------------------------------------
    // Slot attorno al corpo
    // ------------------------------------------------------------------

    private void renderSlots(GuiGraphics gui, int mouseX, int mouseY) {
        CyberwareData data = ClientCyberwareState.get();
        for (int i = 0; i < LEFT_SLOTS.length; i++) {
            renderCard(gui, LEFT_SLOTS[i], leftX, slotsTop + i * (rowH + rowGap),
                    cardW, rowH, data, mouseX, mouseY);
            renderCard(gui, RIGHT_SLOTS[i], rightX, slotsTop + i * (rowH + rowGap),
                    cardW, rowH, data, mouseX, mouseY);
        }
    }

    private void renderCard(GuiGraphics gui, CyberwareSlot slot, int x, int y, int w, int h,
                            CyberwareData data, int mouseX, int mouseY) {
        Optional<Cyberware> installed = data.inSlot(slot);
        boolean hover = mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;
        boolean selected = selectedSlot == slot;

        gui.fill(x + 1, y + 1, x + w + 1, y + h + 1, 0x66000000);
        gui.fill(x, y, x + w, y + h, selected ? PANEL_LIGHT : PANEL);
        frame(gui, x, y, w, h, selected ? CYAN : hover ? GREY : BORDER);

        if (selected) {
            gui.fill(x, y, x + 2, y + h, CYAN);
            gui.fill(x + w - 1, y, x + w, y + 1, CYAN);
        } else if (hover) {
            gui.fill(x, y, x + 2, y + h, CYAN_DIM);
        }

        String slotLabel = Component.translatable(slot.translationKey()).getString().toUpperCase(Locale.ROOT);
        String implantName = installed
                .map(cyberware -> Component.translatable(cyberware.translationKey()).getString())
                .orElse(Component.translatable("screen.cybercraft.empty").getString());

        int slotLabelY = compact ? y + 2 : y + 5;
        int nameY = compact ? y + 11 : y + 17;
        if (installed.isPresent()) {
            Cyberware cyberware = installed.get();
            ItemStack icon = new ItemStack(ModItems.implant(cyberware));
            gui.renderItem(icon, x + 5, y + (h - 16) / 2);
            gui.drawString(font, truncate(slotLabel, w - 30), x + 25, slotLabelY,
                    selected ? CYAN : GREY_DIM, false);
            String cap = Math.round(cyberware.capacityCost()) + " CAP";
            int capW = font.width(cap);
            gui.drawString(font, cap, x + w - capW - 5, nameY,
                    cyberware.tier() >= 3 ? AMBER : CYAN_DIM, false);
            gui.drawString(font, truncate(implantName, w - 35 - capW), x + 25, nameY, GREY, false);
        } else {
            gui.fill(x + 7, y + h / 2 - 3, x + 13, y + h / 2 + 3, 0xFF33464E);
            gui.drawString(font, truncate(slotLabel, w - 30), x + 20, slotLabelY, GREY_DIM, false);
            gui.drawString(font, Component.translatable("screen.cybercraft.empty").getString(),
                    x + 20, nameY, 0xFF53656D, false);
        }
    }

    // ------------------------------------------------------------------
    // Anteprima corpo
    // ------------------------------------------------------------------

    private void renderBody(GuiGraphics gui, int mouseX, int mouseY) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) {
            return;
        }

        gui.fillGradient(bodyX1, bodyY1, bodyX2, bodyY2, 0x3220D9C0, 0x08000000);
        cornerBrackets(gui, bodyX1, bodyY1, bodyX2 - bodyX1, bodyY2 - bodyY1, CYAN_DIM);

        String name = truncate(client.player.getDisplayName().getString().toUpperCase(Locale.ROOT),
                bodyX2 - bodyX1 - 8);
        gui.drawString(font, name, bodyX1 + (bodyX2 - bodyX1 - font.width(name)) / 2,
                bodyY1 + 5, GREY, false);

        int platformY = bodyY2 - 18;
        int half = Math.max(18, (bodyX2 - bodyX1) / 3);
        int cx = (bodyX1 + bodyX2) / 2;
        for (int i = 0; i < 5; i++) {
            int lineW = half - i * 4;
            int y = platformY + i * 2;
            gui.fill(cx - lineW, y, cx + lineW, y + 1, i == 0 ? CYAN : CYAN_DIM);
        }

        long now = System.currentTimeMillis();
        int scanY = bodyY1 + 16 + (int) ((now / 28) % Math.max(1, bodyY2 - bodyY1 - 34));
        gui.fill(bodyX1 + 8, scanY, bodyX2 - 8, scanY + 1, 0x6600F0FF);

        int scale = Math.max(30, Math.round((bodyY2 - bodyY1) * 0.36f));
        InventoryScreen.renderEntityInInventoryFollowsMouse(gui,
                bodyX1 + 4, bodyY1 + 14, bodyX2 - 4, bodyY2 - 8,
                scale, 0.0f, (float) mouseX, (float) mouseY, client.player);

        String level = "LVL " + client.player.experienceLevel;
        gui.drawString(font, level, cx - font.width(level) / 2, bodyY2 - 10, MINT, false);
    }

    // ------------------------------------------------------------------
    // Pannello dettaglio e rimozione
    // ------------------------------------------------------------------

    private void renderDetail(GuiGraphics gui, int mouseX, int mouseY) {
        gui.fill(detailX, detailY, detailX + detailW, detailY + detailH, 0xCC091116);
        frame(gui, detailX, detailY, detailW, detailH, BORDER);
        gui.fill(detailX + 1, detailY + 1, detailX + detailW - 1, detailY + 2, 0x5500F0FF);

        CyberwareData data = ClientCyberwareState.get();
        String header = Component.translatable("screen.cybercraft.detail").getString();
        if (selectedSlot != null) {
            header += " // " + Component.translatable(selectedSlot.translationKey()).getString()
                    .toUpperCase(Locale.ROOT);
        }
        gui.drawString(font, truncate(header, detailW - 16), detailX + 8,
                detailY + (compact ? 4 : 6), CYAN, false);

        int textLine = compact ? 9 : 10;
        if (selectedSlot == null) {
            gui.drawString(font, Component.translatable("screen.cybercraft.overview").getString(),
                    detailX + 8, detailY + (compact ? 15 : 24), GREY, false);
            List<FormattedCharSequence> lines = font.split(
                    Component.translatable("screen.cybercraft.slot_empty.hint"), detailW - 16);
            int lineY = detailY + (compact ? 26 : 38);
            for (FormattedCharSequence line : lines) {
                if (lineY > detailY + detailH - (compact ? 4 : 10)) {
                    break;
                }
                gui.drawString(font, line, detailX + 8, lineY, GREY_DIM, false);
                lineY += textLine;
            }
            return;
        }

        Optional<Cyberware> installed = data.inSlot(selectedSlot);
        if (installed.isEmpty()) {
            gui.drawString(font, Component.translatable("screen.cybercraft.slot_empty").getString(),
                    detailX + 8, detailY + (compact ? 15 : 24), GREY, false);
            List<FormattedCharSequence> lines = font.split(
                    Component.translatable("screen.cybercraft.slot_empty.hint"), detailW - 24);
            int lineY = detailY + (compact ? 26 : 38);
            for (FormattedCharSequence line : lines) {
                if (lineY > detailY + detailH - (compact ? 4 : 10)) {
                    break;
                }
                gui.drawString(font, line, detailX + 8, lineY, GREY_DIM, false);
                lineY += textLine;
            }
            return;
        }

        Cyberware cyberware = installed.get();
        ItemStack icon = new ItemStack(ModItems.implant(cyberware));
        float iconScale = compact ? 1.0f : 2.0f;
        gui.pose().pushPose();
        gui.pose().translate(detailX + 8, detailY + (compact ? 16 : 20), 0);
        gui.pose().scale(iconScale, iconScale, 1.0f);
        gui.renderItem(icon, 0, 0);
        gui.pose().popPose();

        int textX = detailX + (compact ? 30 : 44);
        int textW = detailW - (textX - detailX) - removeW - (compact ? 16 : 24);
        int nameY = detailY + (compact ? 13 : 20);
        int statsY = detailY + (compact ? 24 : 31);
        int descY = detailY + (compact ? 34 : 43);

        gui.drawString(font, truncate(Component.translatable(cyberware.translationKey()).getString(),
                textW), textX, nameY, GREY, false);

        String stats = Component.translatable("tooltip.cybercraft.capacity",
                Math.round(cyberware.capacityCost())).getString()
                + "   " + Component.translatable("tooltip.cybercraft.tier", cyberware.tier()).getString();
        gui.drawString(font, truncate(stats, textW), textX, statsY, CYAN, false);

        List<FormattedCharSequence> lines = font.split(
                Component.translatable(cyberware.descriptionKey()), textW);
        int lineY = descY;
        int drawn = 0;
        int maxLines = compact ? 1 : 2;
        for (FormattedCharSequence line : lines) {
            if (drawn >= maxLines || lineY > detailY + detailH - 8) {
                break;
            }
            gui.drawString(font, line, textX, lineY, GREY_DIM, false);
            lineY += textLine;
            drawn++;
        }

        boolean hover = mouseX >= removeX && mouseX < removeX + removeW
                && mouseY >= removeY && mouseY < removeY + removeH;
        gui.fill(removeX + 2, removeY + 2, removeX + removeW + 2, removeY + removeH + 2, 0x55000000);
        gui.fill(removeX, removeY, removeX + removeW, removeY + removeH, hover ? 0xBB3A0C15 : 0x99180A0F);
        frame(gui, removeX, removeY, removeW, removeH, hover ? 0xFFFF5577 : RED);
        String remove = Component.translatable("screen.cybercraft.remove").getString();
        gui.drawString(font, remove, removeX + (removeW - font.width(remove)) / 2,
                removeY + 6, hover ? 0xFFFFB3C2 : RED, false);

        if (!compact) {
            String hint = Component.translatable("screen.cybercraft.remove.hint").getString();
            gui.drawString(font, truncate(hint, removeW + 8),
                    removeX + removeW - font.width(truncate(hint, removeW + 8)),
                    removeY + 27, GREY_DIM, false);
        }
    }

    // ------------------------------------------------------------------
    // Input
    // ------------------------------------------------------------------

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            CyberwareData data = ClientCyberwareState.get();
            if (selectedSlot != null && data.inSlot(selectedSlot).isPresent()
                    && mouseX >= removeX && mouseX < removeX + removeW
                    && mouseY >= removeY && mouseY < removeY + removeH) {
                PacketDistributor.sendToServer(new RemoveImplantPayload(selectedSlot.ordinal()));
                return true;
            }

            for (int i = 0; i < LEFT_SLOTS.length; i++) {
                int y = slotsTop + i * (rowH + rowGap);
                if (hit(mouseX, mouseY, leftX, y, cardW, rowH)) {
                    selectedSlot = LEFT_SLOTS[i];
                    return true;
                }
                if (hit(mouseX, mouseY, rightX, y, cardW, rowH)) {
                    selectedSlot = RIGHT_SLOTS[i];
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private static boolean hit(double mouseX, double mouseY, int x, int y, int w, int h) {
        return mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) { // ESC
            onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    // ------------------------------------------------------------------
    // Disegno
    // ------------------------------------------------------------------

    private static void scanlines(GuiGraphics gui, int x, int y, int w, int h) {
        for (int sy = y + 3; sy < y + h; sy += 4) {
            gui.fill(x, sy, x + w, sy + 1, 0x0800F0FF);
        }
    }

    private static void frame(GuiGraphics gui, int x, int y, int w, int h, int color) {
        gui.fill(x, y, x + w, y + 1, color);
        gui.fill(x, y + h - 1, x + w, y + h, color);
        gui.fill(x, y, x + 1, y + h, color);
        gui.fill(x + w - 1, y, x + w, y + h, color);
    }

    private static void cornerBrackets(GuiGraphics gui, int x, int y, int w, int h, int color) {
        int len = 8;
        gui.fill(x, y, x + len, y + 1, color);
        gui.fill(x, y, x + 1, y + len, color);
        gui.fill(x + w - len, y, x + w, y + 1, color);
        gui.fill(x + w - 1, y, x + w, y + len, color);
        gui.fill(x, y + h - 1, x + len, y + h, color);
        gui.fill(x, y + h - len, x + 1, y + h, color);
        gui.fill(x + w - len, y + h - 1, x + w, y + h, color);
        gui.fill(x + w - 1, y + h - len, x + w, y + h, color);
    }

    private static String truncate(String text, int maxWidth) {
        Minecraft minecraft = Minecraft.getInstance();
        if (maxWidth <= 0) {
            return "";
        }
        if (minecraft.font.width(text) <= maxWidth) {
            return text;
        }
        String result = text;
        while (!result.isEmpty() && minecraft.font.width(result + "…") > maxWidth) {
            result = result.substring(0, result.length() - 1);
        }
        return result + "…";
    }
}
