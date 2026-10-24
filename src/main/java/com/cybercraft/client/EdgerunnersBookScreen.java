package com.cybercraft.client;

import com.cybercraft.cyberware.Cyberware;
import com.cybercraft.registry.ModItems;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;

/**
 * Edgerunner's Book: manuale illustrato con descrizione, ricetta e icona
 * di ogni contenuto della mod. Le ricette sono lette dal RecipeManager
 * del client, quindi restano sempre allineate ai datapack.
 */
public class EdgerunnersBookScreen extends Screen {

    private static final int CYAN = 0xFF00F0FF;
    private static final int INK = 0xFF1B2329;
    private static final int INK_DIM = 0xFF4A5A63;
    private static final int PAPER = 0xFFD8DEE2;
    private static final int PAPER_DARK = 0xFFB9C4CB;
    private static final int SPINE = 0xFF27323A;

    private record Entry(ItemStack stack, String path, String category) {}

    private final List<Entry> entries = new ArrayList<>();
    private int selected;
    private int scroll;
    private int bookX;
    private int bookY;
    private int bookW;
    private int bookH;
    private int leftPageX;
    private int leftPageY;
    private int leftPageW;
    private int leftPageH;
    private int rightPageX;

    public EdgerunnersBookScreen() {
        super(Component.translatable("screen.cybercraft.book"));
    }

    @Override
    protected void init() {
        bookW = Math.min(460, width - 20);
        bookH = Math.min(280, height - 20);
        bookX = (width - bookW) / 2;
        bookY = (height - bookH) / 2;
        leftPageX = bookX + 14;
        leftPageY = bookY + 34;
        leftPageW = (bookW - 42) / 2;
        leftPageH = bookH - 48;
        rightPageX = leftPageX + leftPageW + 14;
        buildEntries();
    }

    private void buildEntries() {
        entries.clear();
        for (Cyberware cyberware : Cyberware.values()) {
            add(ModItems.implant(cyberware), "Impianti");
        }
        add(ModItems.NEURAL_LINK.get(), "Strumenti");
        add(ModItems.CYBERDECK.get(), "Strumenti");
        add(ModItems.EDGERUNNERS_BOOK.get(), "Strumenti");
        add(ModItems.MONOWIRE.get(), "Strumenti");
        add(ModItems.RAW_TITANIUM.get(), "Materiali");
        add(ModItems.TITANIUM_INGOT.get(), "Materiali");
        add(ModItems.RAW_NEODYMIUM.get(), "Materiali");
        add(ModItems.NEODYMIUM_INGOT.get(), "Materiali");
        add(ModItems.RAW_LITHIUM.get(), "Materiali");
        add(ModItems.LITHIUM_INGOT.get(), "Materiali");
        add(ModItems.CIRCUIT_BOARD.get(), "Materiali");
        add(ModItems.SERVO_MOTOR.get(), "Materiali");
        add(ModItems.BIO_GEL.get(), "Materiali");
        add(ModItems.OPTICS_LENS.get(), "Materiali");
        for (Item item : BuiltInRegistries.ITEM) {
            if (item instanceof BlockItem && BuiltInRegistries.ITEM.getKey(item).getNamespace().equals("cybercraft")) {
                add(item, "Blocchi");
            }
        }
        if (selected >= entries.size()) {
            selected = Math.max(0, entries.size() - 1);
        }
    }

    private void add(Item item, String category) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
        entries.add(new Entry(new ItemStack(item), id.getPath(), category));
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        renderBackground(gui, mouseX, mouseY, partialTick);
        gui.fill(0, 0, width, height, 0xB0040609);

        // Copertina e pagine.
        gui.fill(bookX + 5, bookY + 5, bookX + bookW + 5, bookY + bookH + 5, 0x88000000);
        gui.fill(bookX, bookY, bookX + bookW, bookY + bookH, 0xF00E1418);
        frame(gui, bookX, bookY, bookW, bookH, 0xFF6F818B);
        gui.fill(leftPageX, leftPageY, leftPageX + leftPageW, leftPageY + leftPageH, PAPER);
        gui.fillGradient(leftPageX, leftPageY, leftPageX + leftPageW, leftPageY + leftPageH,
                PAPER, PAPER_DARK);
        gui.fill(rightPageX, leftPageY, rightPageX + leftPageW, leftPageY + leftPageH, PAPER);
        gui.fillGradient(rightPageX, leftPageY, rightPageX + leftPageW, leftPageY + leftPageH,
                PAPER, PAPER_DARK);
        gui.fill(leftPageX + leftPageW + 2, leftPageY, rightPageX - 2, leftPageY + leftPageH, SPINE);

        gui.drawString(font, "EDGERUNNER'S BOOK", bookX + 14, bookY + 12, CYAN, false);
        String subtitle = Component.translatable("screen.cybercraft.book.subtitle").getString();
        gui.drawString(font, subtitle, bookX + 14, bookY + 23, 0xFF7E909A, false);
        String close = "[ESC] " + Component.translatable("screen.cybercraft.close").getString();
        gui.drawString(font, close, bookX + bookW - font.width(close) - 14, bookY + 12, 0xFF9FB0B9, false);

        renderIndex(gui, mouseX, mouseY);
        renderDetail(gui);

        String hint = Component.translatable("screen.cybercraft.book.hint").getString();
        gui.drawString(font, hint, leftPageX, bookY + bookH - 12, 0xFF7E909A, false);
    }

    private void renderIndex(GuiGraphics gui, int mouseX, int mouseY) {
        gui.drawString(font, Component.translatable("screen.cybercraft.book.index").getString(),
                leftPageX + 8, leftPageY + 8, INK, false);
        gui.fill(leftPageX + 8, leftPageY + 19, leftPageX + leftPageW - 8, leftPageY + 20, 0x664A5A63);

        int rowY = leftPageY + 26;
        int visibleRows = (leftPageH - 38) / 14;
        int maxScroll = Math.max(0, entries.size() - visibleRows);
        scroll = Math.max(0, Math.min(scroll, maxScroll));

        for (int i = scroll; i < Math.min(entries.size(), scroll + visibleRows); i++) {
            Entry entry = entries.get(i);
            int y = rowY + (i - scroll) * 14;
            boolean hover = mouseX >= leftPageX + 4 && mouseX < leftPageX + leftPageW - 4
                    && mouseY >= y - 1 && mouseY < y + 13;
            if (i == selected) {
                gui.fill(leftPageX + 4, y - 2, leftPageX + leftPageW - 4, y + 13, 0x4400A8B8);
                gui.fill(leftPageX + 4, y - 2, leftPageX + 5, y + 13, CYAN);
            } else if (hover) {
                gui.fill(leftPageX + 4, y - 2, leftPageX + leftPageW - 4, y + 13, 0x22FFFFFF);
            }
            gui.renderItem(entry.stack(), leftPageX + 7, y - 1);
            String name = entry.stack().getHoverName().getString();
            gui.drawString(font, truncate(name, leftPageW - 46), leftPageX + 26, y + 1, INK, false);
            String cat = entry.category().substring(0, Math.min(3, entry.category().length())).toUpperCase(Locale.ROOT);
            gui.drawString(font, cat, leftPageX + leftPageW - 24, y + 1, INK_DIM, false);
        }

        // Barra di scorrimento.
        if (maxScroll > 0) {
            int barX = leftPageX + leftPageW - 3;
            int barY = rowY;
            int barH = leftPageH - 40;
            gui.fill(barX, barY, barX + 2, barY + barH, 0x334A5A63);
            int thumbH = Math.max(12, barH * visibleRows / entries.size());
            int thumbY = barY + (barH - thumbH) * scroll / maxScroll;
            gui.fill(barX, thumbY, barX + 2, thumbY + thumbH, 0xFF4A5A63);
        }
    }

    private void renderDetail(GuiGraphics gui) {
        if (entries.isEmpty()) {
            return;
        }
        Entry entry = entries.get(Math.max(0, Math.min(selected, entries.size() - 1)));
        int x = rightPageX + 10;
        int y = leftPageY + 10;
        int w = leftPageW - 20;

        gui.pose().pushPose();
        gui.pose().translate(x, y, 0);
        gui.pose().scale(3.0f, 3.0f, 1.0f);
        gui.renderItem(entry.stack(), 0, 0);
        gui.pose().popPose();

        gui.drawString(font, truncate(entry.stack().getHoverName().getString(), w - 64),
                x + 58, y + 4, INK, false);
        gui.drawString(font, entry.category().toUpperCase(Locale.ROOT), x + 58, y + 16, 0xFF007A88, false);
        gui.drawString(font, "ID: " + entry.path(), x + 58, y + 27, INK_DIM, false);

        int textY = y + 44;
        List<FormattedCharSequence> lines = font.split(description(entry), w);
        for (FormattedCharSequence line : lines) {
            if (textY > leftPageY + leftPageH - 120) {
                break;
            }
            gui.drawString(font, line, x, textY, INK, false);
            textY += 10;
        }

        renderRecipe(gui, entry, x, leftPageY + leftPageH - 112, w);
    }

    private Component description(Entry entry) {
        if (!entry.path().isEmpty()) {
            return Component.translatable("book.cybercraft.desc." + entry.path());
        }
        return entry.stack().getHoverName();
    }

    private void renderRecipe(GuiGraphics gui, Entry entry, int x, int y, int w) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }
        RecipeHolder<?> found = null;
        for (RecipeHolder<?> holder : minecraft.level.getRecipeManager().getRecipes()) {
            ItemStack result = holder.value().getResultItem(minecraft.level.registryAccess());
            if (!result.isEmpty() && result.is(entry.stack().getItem())) {
                found = holder;
                break;
            }
        }

        gui.fill(x, y - 10, x + w, y - 9, 0x664A5A63);
        gui.drawString(font, Component.translatable("screen.cybercraft.book.recipe").getString(),
                x, y - 8, INK, false);

        if (found == null) {
            List<FormattedCharSequence> lines = font.split(
                    Component.translatable("screen.cybercraft.book.no_recipe"), w);
            int lineY = y + 4;
            for (FormattedCharSequence line : lines) {
                gui.drawString(font, line, x, lineY, INK_DIM, false);
                lineY += 10;
            }
            return;
        }

        List<Ingredient> ingredients = found.value().getIngredients();
        int gridX = x + 2;
        int gridY = y + 4;
        int cell = 20;
        int index = 0;
        long cycle = System.currentTimeMillis() / 1000L;

        int width = 3;
        int height = 3;
        if (found.value() instanceof ShapedRecipe shaped) {
            width = shaped.getWidth();
            height = shaped.getHeight();
        }
        int offsetX = (3 - width) * cell / 2;
        int offsetY = (3 - height) * cell / 2;
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int sx = gridX + offsetX + col * cell;
                int sy = gridY + offsetY + row * cell;
                drawSlot(gui, sx, sy, cell);
                if (index < ingredients.size()) {
                    ItemStack[] options = ingredients.get(index).getItems();
                    if (options.length > 0) {
                        ItemStack shown = options[(int) (cycle % options.length)];
                        gui.renderItem(shown, sx + 2, sy + 2);
                    }
                }
                index++;
            }
        }

        int arrowX = gridX + 3 * cell + 12;
        int arrowY = gridY + 3 * cell / 2 - 5;
        gui.drawString(font, "→", arrowX, arrowY, INK, false);
        int resultX = arrowX + 18;
        drawSlot(gui, resultX, gridY + 20, 24);
        gui.renderItem(found.value().getResultItem(minecraft.level.registryAccess()), resultX + 4, gridY + 24);
    }

    private static void drawSlot(GuiGraphics gui, int x, int y, int size) {
        gui.fill(x, y, x + size, y + size, 0x33FFFFFF);
        gui.fill(x, y, x + size, y + 1, 0x994A5A63);
        gui.fill(x, y + size - 1, x + size, y + size, 0x994A5A63);
        gui.fill(x, y, x + 1, y + size, 0x994A5A63);
        gui.fill(x + size - 1, y, x + size, y + size, 0x994A5A63);
    }

    private static String truncate(String text, int maxWidth) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.font.width(text) <= maxWidth) {
            return text;
        }
        String result = text;
        while (!result.isEmpty() && minecraft.font.width(result + "…") > maxWidth) {
            result = result.substring(0, result.length() - 1);
        }
        return result + "…";
    }

    private static void frame(GuiGraphics gui, int x, int y, int w, int h, int color) {
        gui.fill(x, y, x + w, y + 1, color);
        gui.fill(x, y + h - 1, x + w, y + h, color);
        gui.fill(x, y, x + 1, y + h, color);
        gui.fill(x + w - 1, y, x + w, y + h, color);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int rowY = leftPageY + 26;
            int visibleRows = (leftPageH - 38) / 14;
            for (int i = scroll; i < Math.min(entries.size(), scroll + visibleRows); i++) {
                int y = rowY + (i - scroll) * 14;
                if (mouseX >= leftPageX + 4 && mouseX < leftPageX + leftPageW - 4
                        && mouseY >= y - 2 && mouseY < y + 13) {
                    selected = i;
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (mouseX >= leftPageX && mouseX < leftPageX + leftPageW) {
            int visibleRows = (leftPageH - 38) / 14;
            int maxScroll = Math.max(0, entries.size() - visibleRows);
            scroll = Math.max(0, Math.min(maxScroll, scroll - (int) Math.signum(scrollY)));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
