package com.cybercraft.client;

import com.cybercraft.cyberware.Cyberware;
import com.cybercraft.cyberware.CyberwareData;
import com.cybercraft.netrunning.Quickhack;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public final class ScannerHud {

  public static final int CYAN = 0xFF00F0FF;
  public static final int AMBER = 0xFFFF9900;
  public static final int RED = 0xFFFF003C;
  public static final int LIME = 0xFF00FF66;
  private static final int WHITE = 0xFFE8F6FA;
  private static final int DIM = 0xFF6E8994;

  private ScannerHud() {}

  public static void render(GuiGraphics gui, DeltaTracker tickCounter) {
    Minecraft client = Minecraft.getInstance();
    if (!ScannerState.active
        || client.player == null
        || client.level == null
        || client.screen != null
        || client.options.hideGui) {
      return;
    }
    int actualW = client.getWindow().getGuiScaledWidth(),
        actualH = client.getWindow().getGuiScaledHeight();
    float scale = Math.min(1, Math.min(actualW / 640f, actualH / 360f));
    gui.pose().pushPose();
    gui.pose().scale(scale, scale, 1);
    int w = (int) (actualW / scale), h = (int) (actualH / scale);
    var font = client.font;

    drawFilter(gui, w, h);

    LivingEntity target = currentTarget(client);
    ScannerState.onTarget(target);

    CyberwareData data = ClientCyberwareState.get();
    drawRam(gui, client, data);
    drawReticle(gui, w, h, target);
    if (target != null || ScannerTargets.block(client) != null) {
      drawQuickhacks(gui, client, w, h, data);
      if (target != null) drawUploadAboveTarget(gui, client, w, h, target);
      else drawBlockPanel(gui, client, w, h);
    }

    LivingEntity panelTarget = target != null ? target : ScannerState.fadingTarget(client);
    if (panelTarget != null) {
      float alpha = ScannerState.fadeAlpha();
      if (alpha < 1.0f) {
        gui.setColor(1.0f, 1.0f, 1.0f, alpha);
      }
      drawTargetPanel(gui, client, w, h, panelTarget);
      if (alpha < 1.0f) {
        gui.setColor(1.0f, 1.0f, 1.0f, 1.0f);
      }
    }
    drawHints(gui, client, w, h);
    drawDamageGlitch(gui, client, w, h);
    gui.pose().popPose();
  }

  private static void drawBlockPanel(GuiGraphics gui, Minecraft client, int w, int h) {
    var hit = ScannerTargets.block(client);
    if (hit == null) return;
    int x = w / 2 + 32, y = h / 2 - 35;
    var state = client.level.getBlockState(hit.getBlockPos());
    gui.fill(x, y, x + 180, y + 75, 0xDD07141C);
    frame(gui, x, y, 180, 75, AMBER);
    gui.drawString(client.font, "NETWORK DEVICE", x + 8, y + 8, AMBER, false);
    gui.drawString(
        client.font,
        truncate(client, state.getBlock().getName().getString(), 164),
        x + 8,
        y + 23,
        WHITE,
        false);
    gui.drawString(client.font, hit.getBlockPos().toShortString(), x + 8, y + 36, DIM, false);
    gui.drawString(
        client.font,
        ScannerTargets.selected(client).translationKey().equals("quickhack.cybercraft.disruption")
            ? "Disrupt output // 5 seconds"
            : "Remote access // toggle",
        x + 8,
        y + 52,
        CYAN,
        false);
    if (ScannerState.uploadResultAt > 0)
      gui.drawString(
          client.font,
          ScannerState.uploadSuccess ? "UPLOAD COMPLETE" : "ACCESS DENIED",
          x + 8,
          y + 64,
          ScannerState.uploadSuccess ? LIME : RED,
          false);
  }

  private static void drawFilter(GuiGraphics gui, int w, int h) {

    gui.fill(0, 0, w, h, 0x5C030A13);
    gui.fillGradient(0, 0, w, h / 3, 0x66020A12, 0x05020A12);
    gui.fillGradient(0, h - h / 3, w, h, 0x05020A12, 0x77020A12);

    for (int y = 0; y < h; y += 4) {
      gui.fill(0, y, w, y + 1, 0x0B00F0FF);
    }

    gui.fill(0, 0, w, 1, 0x66FF003C);
    gui.fill(0, 1, w, 2, 0x3300F0FF);
    gui.fill(0, h - 1, w, h, 0x6600F0FF);
    gui.fill(0, h - 2, w, h - 1, 0x33FF003C);
    gui.fill(0, 0, 1, h, 0x55FF003C);
    gui.fill(1, 0, 2, h, 0x2200F0FF);
    gui.fill(w - 1, 0, w, h, 0x5500F0FF);
    gui.fill(w - 2, 0, w - 1, h, 0x22FF003C);
  }

  public static LivingEntity currentTarget(Minecraft client) {
    var hit = ScannerTargets.pick(client);
    return hit instanceof EntityHitResult e && e.getEntity() instanceof LivingEntity living
        ? living
        : null;
  }

  private static void drawReticle(GuiGraphics gui, int w, int h, LivingEntity target) {
    int cx = w / 2;
    int cy = h / 2;
    boolean tech = target != null && isTech(target);
    int color = target == null ? CYAN : tech ? AMBER : target instanceof Enemy ? RED : CYAN;

    if (tech) {

      for (int angle = 0; angle < 360; angle += 30) {
        if ((angle / 30) % 2 != 0) {
          continue;
        }
        double rad = Math.toRadians(angle);
        int x = cx + (int) Math.round(Math.cos(rad) * 13);
        int y = cy + (int) Math.round(Math.sin(rad) * 13);
        gui.fill(x - 1, y - 1, x + 2, y + 2, color);
        int x2 = cx + (int) Math.round(Math.cos(rad) * 18);
        int y2 = cy + (int) Math.round(Math.sin(rad) * 18);
        gui.fill(x2 - 1, y2 - 1, x2 + 2, y2 + 2, color);
      }
    } else {

      int r = 13;
      gui.fill(cx - r, cy - r, cx - r + 7, cy - r + 1, color);
      gui.fill(cx - r, cy - r, cx - r + 1, cy - r + 7, color);
      gui.fill(cx + r - 7, cy - r, cx + r, cy - r + 1, color);
      gui.fill(cx + r - 1, cy - r, cx + r, cy - r + 7, color);
      gui.fill(cx - r, cy + r - 1, cx - r + 7, cy + r, color);
      gui.fill(cx - r, cy + r - 7, cx - r + 1, cy + r, color);
      gui.fill(cx + r - 7, cy + r - 1, cx + r, cy + r, color);
      gui.fill(cx + r - 1, cy + r - 7, cx + r, cy + r, color);
    }

    gui.fill(cx - 1, cy - 1, cx + 2, cy + 2, color);
    gui.fill(cx - 24, cy, cx - 18, cy + 1, 0xAAFFFFFF);
    gui.fill(cx + 18, cy, cx + 24, cy + 1, 0xAAFFFFFF);
    gui.fill(cx, cy - 24, cx + 1, cy - 18, 0xAAFFFFFF);
    gui.fill(cx, cy + 18, cx + 1, cy + 24, 0xAAFFFFFF);

    if (target == null) {
      String scanning = Component.translatable("hud.cybercraft.scan.searching").getString();
      gui.drawString(
          Minecraft.getInstance().font,
          scanning,
          cx - Minecraft.getInstance().font.width(scanning) / 2,
          cy + 28,
          0xAA00F0FF,
          false);
    }
    gui.drawString(
        Minecraft.getInstance().font,
        Component.translatable(
                tech
                    ? "hud.cybercraft.scan.tech"
                    : target == null ? "hud.cybercraft.scan.idle" : "hud.cybercraft.scan.organic")
            .getString(),
        cx - 18,
        cy + 34,
        color,
        false);
  }

  private static boolean isTech(Entity entity) {
    return entity.getType() == EntityType.IRON_GOLEM
        || entity.getType() == EntityType.SNOW_GOLEM
        || entity.getType() == EntityType.ALLAY
        || entity.getType() == EntityType.VEX
        || entity.getType() == EntityType.ARMOR_STAND;
  }

  private static void drawRam(GuiGraphics gui, Minecraft client, CyberwareData data) {
    var font = client.font;
    int x = 14;
    int y = 14;
    gui.drawString(font, "CYBERDECK // RAM", x, y, CYAN, false);

    int max = Math.max(1, (int) Math.ceil(data.maxRam()));
    int current = (int) Math.floor(data.ram());
    int segments = 20;
    int segW = 6;
    int segH = 11;
    int gap = 2;
    int filled = Math.min(segments, (int) Math.ceil(data.ram() / data.maxRam() * segments));
    for (int i = 0; i < segments; i++) {
      int sx = x + i * (segW + gap);
      boolean on = i < filled;
      int color = data.overloaded() ? RED : on ? CYAN : 0xFF2B3740;
      gui.fill(sx, y + 13, sx + segW, y + 13 + segH, color);
      gui.fill(sx, y + 13, sx + segW, y + 14, on ? 0xCCFFFFFF : 0x33FFFFFF);
      gui.fill(sx, y + 13 + segH - 1, sx + segW, y + 13 + segH, 0x55000000);
    }

    String value = String.format(Locale.ROOT, "%02d / %02d", current, max);
    gui.drawString(font, value, x, y + 28, data.overloaded() ? RED : CYAN, false);
    String status =
        data.overloaded()
            ? Component.translatable("hud.cybercraft.scan.buffer.offline").getString()
            : Component.translatable("hud.cybercraft.scan.buffer.online").getString();
    gui.drawString(font, status, x, y + 39, data.overloaded() ? RED : LIME, false);
    gui.drawString(
        font,
        "SANDEVISTAN: " + (data.has(Cyberware.SANDEVISTAN) ? "LINKED" : "N/A"),
        x,
        y + 50,
        DIM,
        false);
  }

  private static void drawQuickhacks(
      GuiGraphics gui, Minecraft client, int w, int h, CyberwareData data) {
    var font = client.font;
    int panelW = 196;
    int headerH = 21;
    int rowH = 26;
    int x = Math.max(10, w / 2 - 238);
    int y = h / 2 - 84;
    Quickhack[] hacks = ScannerTargets.available(client).toArray(Quickhack[]::new);
    int panelH = headerH + hacks.length * rowH + 4;

    gui.fill(x + 3, y + 3, x + panelW + 3, y + panelH + 3, 0x66000000);
    gui.fillGradient(x, y, x + panelW, y + panelH, 0xD90A161C, 0xB3050C10);
    frame(gui, x, y, panelW, panelH, 0x8800F0FF);
    gui.fill(x + 1, y + 1, x + panelW - 1, y + 2, 0xCC00F0FF);

    String title = Component.translatable("hud.cybercraft.scan.quickhacks").getString();
    gui.drawString(font, title, x + 8, y + 6, CYAN, false);
    String ram = ((int) Math.floor(data.ram())) + "/" + ((int) Math.ceil(data.maxRam()));
    gui.drawString(
        font, ram, x + panelW - font.width(ram) - 8, y + 6, data.overloaded() ? RED : LIME, false);
    gui.fill(x + 6, y + headerH - 3, x + panelW - 6, y + headerH - 2, 0x5500F0FF);

    for (int i = 0; i < hacks.length; i++) {
      Quickhack hack = hacks[i];
      float cost = quickhackCost(data, hack);
      boolean insufficient =
          data.ram() < cost || !com.cybercraft.cyberware.CyberwareManager.canHack(data);
      boolean selected = hack == ScannerTargets.selected(client);
      int rowY = y + headerH + i * rowH;

      if (selected) {
        gui.fill(x + 3, rowY, x + panelW - 3, rowY + rowH - 1, 0x3300F0FF);
        gui.fill(x + 3, rowY, x + 5, rowY + rowH - 1, CYAN);
        gui.fill(x + 3, rowY, x + panelW - 3, rowY + 1, 0xAA00F0FF);
        gui.fill(x + 3, rowY + rowH - 2, x + panelW - 3, rowY + rowH - 1, 0x6600F0FF);
      } else {
        gui.fill(x + 6, rowY + rowH - 1, x + panelW - 6, rowY + rowH, 0x1A88D8E0);
      }

      int costColor = insufficient ? RED : CYAN;
      gui.fill(x + 7, rowY + 4, x + 34, rowY + 22, insufficient ? 0x44FF003C : 0x2A00F0FF);
      frame(gui, x + 7, rowY + 4, 27, 18, costColor);
      String costText = String.valueOf(Math.round(cost));
      gui.drawString(font, costText, x + 20 - font.width(costText) / 2, rowY + 8, costColor, false);

      String name =
          Component.translatable(hack.translationKey()).getString().toUpperCase(Locale.ROOT);
      int nameColor = insufficient ? RED : selected ? WHITE : 0xFFB9D7DE;
      if (insufficient) {
        String blocked = Component.translatable("hud.cybercraft.scan.blocked").getString();
        int blockedW = font.width(blocked);
        gui.drawString(
            font,
            truncate(client, name, panelW - 54 - blockedW),
            x + 42,
            rowY + 9,
            nameColor,
            false);
        gui.drawString(font, blocked, x + panelW - blockedW - 8, rowY + 9, RED, false);
      } else {
        gui.drawString(
            font, truncate(client, name, panelW - 54), x + 42, rowY + 9, nameColor, false);
        drawHackIcon(gui, x + panelW - 18, rowY + 8, i, costColor);
      }
    }
  }

  private static float quickhackCost(CyberwareData data, Quickhack hack) {
    float cost = hack.ramCost();
    if (data.has(Cyberware.BEHAVIORAL_IMPRINT)) {
      cost *= 0.9f;
    }
    return Math.max(1.0f, Math.round(cost * 10.0f) / 10.0f);
  }

  private static void drawHackIcon(GuiGraphics gui, int x, int y, int index, int color) {
    switch (index) {
      case 0 -> {
        gui.fill(x + 5, y, x + 8, y + 4, color);
        gui.fill(x + 3, y + 4, x + 8, y + 7, color);
        gui.fill(x + 5, y + 7, x + 9, y + 11, color);
      }
      case 1 -> {
        gui.fill(x, y + 3, x + 10, y + 4, color);
        gui.fill(x + 2, y + 5, x + 8, y + 6, color);
        gui.fill(x + 4, y + 4, x + 6, y + 7, color);
        gui.fill(x + 2, y + 7, x + 8, y + 8, color);
        gui.fill(x, y + 8, x + 10, y + 9, color);
      }
      default -> {
        gui.fill(x + 4, y, x + 6, y + 3, color);
        gui.fill(x, y + 5, x + 7, y + 7, color);
        gui.fill(x + 3, y + 8, x + 5, y + 11, color);
      }
    }
  }

  private static void drawTargetPanel(
      GuiGraphics gui, Minecraft client, int w, int h, LivingEntity target) {
    var font = client.font;
    long now = System.currentTimeMillis();
    long elapsed = now - ScannerState.targetChangedAt;
    int offset = (int) ((1.0 - Math.min(1.0, elapsed / 200.0)) * 26.0);
    int x = w / 2 + 40 + offset;
    int y = h / 2 - 104;
    int panelW = 212;
    int panelH = 240;

    boolean tech = isTech(target);
    boolean hostile = isHostile(target);
    int border = tech ? AMBER : hostile ? RED : CYAN;

    gui.fill(x, y, x + panelW, y + panelH, 0xC7081118);
    frame(gui, x, y, panelW, panelH, border);
    gui.fill(x + 1, y + 1, x + panelW - 1, y + 2, 0x5500F0FF);
    for (int sy = y + 4; sy < y + panelH; sy += 4) {
      gui.fill(x + 1, sy, x + panelW - 1, sy + 1, 0x0800F0FF);
    }

    String name = target.getDisplayName().getString().toUpperCase(Locale.ROOT);
    gui.drawString(font, truncate(client, name, panelW - 16), x + 8, y + 8, border, false);

    String type =
        Component.translatable("hud.cybercraft.scan.type").getString()
            + ": "
            + target.getType().getDescription().getString().toUpperCase(Locale.ROOT);
    gui.drawString(font, truncate(client, type, panelW - 16), x + 8, y + 20, DIM, false);

    int barX = x + 8;
    int barY = y + 33;
    int barW = panelW - 16;
    int segments = 12;
    int segW = (barW - (segments - 1)) / segments;
    float health = target.getHealth() / Math.max(1.0f, target.getMaxHealth());
    int hpColor = health > 0.6f ? LIME : health > 0.3f ? AMBER : RED;
    gui.fill(barX, barY, barX + barW, barY + 7, 0x55202830);
    for (int i = 0; i < segments; i++) {
      int sx = barX + i * (segW + 1);
      if ((i + 0.5f) / segments <= health) {
        gui.fill(sx, barY, sx + segW, barY + 7, hpColor);
      }
    }
    gui.drawString(
        font,
        String.format(
            Locale.ROOT,
            "HP %d / %d",
            Math.round(target.getHealth()),
            Math.round(target.getMaxHealth())),
        barX,
        y + 43,
        DIM,
        false);

    int armor = target.getArmorValue();
    int toughness = (int) Math.round(target.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
    gui.drawString(
        font, "ARMOR " + armor + "    TOUGHNESS " + toughness, x + 8, y + 55, 0xFF9FBEC7, false);

    String faction = Component.translatable(factionKey(target)).getString();
    gui.drawString(
        font,
        Component.translatable("hud.cybercraft.scan.faction").getString() + ": " + faction,
        x + 8,
        y + 67,
        factionColor(target),
        false);

    gui.drawString(
        font,
        Component.translatable("hud.cybercraft.scan.status").getString(),
        x + 8,
        y + 80,
        0xFF9FBEC7,
        false);
    List<MobEffectInstance> effects = new ArrayList<>(target.getActiveEffects());
    effects.sort(Comparator.comparingInt(MobEffectInstance::getDuration).reversed());
    if (effects.isEmpty()) {
      gui.drawString(
          font,
          Component.translatable("hud.cybercraft.scan.status.none").getString(),
          x + 8,
          y + 91,
          DIM,
          false);
    } else {
      for (int i = 0; i < Math.min(4, effects.size()); i++) {
        MobEffectInstance effect = effects.get(i);
        String effectName =
            effect.getEffect().value().getDisplayName().getString().toUpperCase(Locale.ROOT);
        String duration = effect.isInfiniteDuration() ? "INF" : (effect.getDuration() / 20) + "s";
        String line =
            truncate(
                client,
                effectName + " " + romanAmplifier(effect.getAmplifier()) + "  " + duration,
                panelW - 30);
        gui.drawString(
            font,
            line,
            x + 8,
            y + 91 + i * 11,
            effect.getEffect().value().isBeneficial() ? LIME : RED,
            false);
      }
    }

    gui.drawString(
        font,
        Component.translatable("hud.cybercraft.scan.vulnerabilities").getString(),
        x + 8,
        y + 145,
        0xFF9FBEC7,
        false);
    List<String[]> vulns = vulnerabilities(target);
    int vy = y + 156;
    for (String[] vuln : vulns) {
      gui.fill(
          x + 8,
          vy + 2,
          x + 13,
          vy + 7,
          vuln[2].equals("red") ? RED : vuln[2].equals("amber") ? AMBER : CYAN);
      gui.drawString(font, vuln[0] + "  " + vuln[1], x + 17, vy, WHITE, false);
      vy += 11;
    }

    drawUploadBar(gui, x + 8, y + panelH - 13, panelW - 16);
    drawCompile(gui, client, x, y, panelW, panelH, elapsed);
    var implants = com.cybercraft.cyberware.CyberwareManager.get(target).equipment();
    gui.drawString(font, "CYBERWARE // " + implants.size(), x + 8, y + 192, AMBER, false);
    int implantX = x + 8;
    for (var implant : implants) {
      if (implantX > x + panelW - 20) break;
      gui.renderItem(implant.stack(), implantX, y + 204);
      implantX += 18;
    }
  }

  private static boolean isHostile(Entity entity) {
    return entity instanceof Enemy || entity instanceof NeutralMob neutral && neutral.isAngry();
  }

  private static String factionKey(Entity entity) {
    if (entity instanceof Player) {
      return "hud.cybercraft.scan.faction.player";
    }
    if (isTech(entity)) {
      return "hud.cybercraft.scan.faction.tech";
    }
    if (isHostile(entity)) {
      return "hud.cybercraft.scan.faction.hostile";
    }
    if (entity instanceof AbstractVillager) {
      return "hud.cybercraft.scan.faction.civilian";
    }
    return "hud.cybercraft.scan.faction.passive";
  }

  private static int factionColor(Entity entity) {
    if (entity instanceof Player) {
      return CYAN;
    }
    if (isTech(entity)) {
      return AMBER;
    }
    if (isHostile(entity)) {
      return RED;
    }
    if (entity instanceof AbstractVillager) {
      return LIME;
    }
    return CYAN;
  }

  private static String romanAmplifier(int amplifier) {
    return switch (amplifier) {
      case 0 -> "I";
      case 1 -> "II";
      case 2 -> "III";
      case 3 -> "IV";
      case 4 -> "V";
      case 5 -> "VI";
      case 6 -> "VII";
      case 7 -> "VIII";
      case 8 -> "IX";
      case 9 -> "X";
      default -> "+" + (amplifier + 1);
    };
  }

  private static List<String[]> vulnerabilities(LivingEntity target) {
    List<String[]> list = new ArrayList<>();
    if (target.getType().is(EntityTypeTags.UNDEAD)) {
      list.add(new String[] {"FUOCO", "+20%", "red"});
      list.add(new String[] {"ELETTRICO", "-10%", "red"});
    } else if (target.getType() == EntityType.BLAZE) {
      list.add(new String[] {"ACQUA", "+25%", "cyan"});
      list.add(new String[] {"ELETTRICO", "-15%", "red"});
    } else if (target.getType() == EntityType.ENDERMAN) {
      list.add(new String[] {"ACQUA", "+25%", "cyan"});
      list.add(new String[] {"FISICO", "-10%", "amber"});
    } else if (target.getType() == EntityType.IRON_GOLEM) {
      list.add(new String[] {"ELETTRICO", "+20%", "amber"});
      list.add(new String[] {"FISICO", "-30%", "amber"});
    } else if (target instanceof Enemy) {
      list.add(new String[] {"ELETTRICO", "+15%", "amber"});
      list.add(new String[] {"FISICO", "+10%", "cyan"});
    } else {
      list.add(new String[] {"FISICO", "+5%", "cyan"});
      list.add(new String[] {"FUOCO", "-5%", "red"});
    }
    list.add(new String[] {"CHIMICO", "+5%", "cyan"});
    return list;
  }

  private static void drawCompile(
      GuiGraphics gui, Minecraft client, int x, int y, int w, int h, long elapsed) {
    if (elapsed >= 200) {
      return;
    }
    RandomSource random = RandomSource.create(ScannerState.targetChangedAt);
    int alpha = (int) (0xCC * (1.0 - elapsed / 200.0));
    for (int i = 0; i < 14; i++) {
      int bx = x + 6 + random.nextInt(Math.max(1, w - 60));
      int by = y + 6 + random.nextInt(Math.max(1, h - 12));
      StringBuilder bits = new StringBuilder();
      for (int b = 0; b < 8; b++) {
        bits.append(random.nextBoolean() ? '1' : '0');
      }
      gui.drawString(
          client.font, bits.toString(), bx, by, (alpha << 24) | (CYAN & 0xFFFFFF), false);
    }
  }

  private static void drawUploadBar(GuiGraphics gui, int x, int y, int w) {
    long now = System.currentTimeMillis();
    boolean uploading = ScannerState.uploadStart > 0 && now - ScannerState.uploadStart < 650;
    boolean result = ScannerState.uploadResultAt > 0 && now - ScannerState.uploadResultAt < 500;
    if (!uploading && !result) {
      return;
    }
    int color = ScannerState.uploadResultAt > 0 ? (ScannerState.uploadSuccess ? LIME : RED) : CYAN;
    float progress = Mth.clamp((now - ScannerState.uploadStart) / 450.0f, 0.0f, 1.0f);
    gui.fill(x, y, x + w, y + 4, 0x66202830);
    gui.fill(x, y, x + (int) (w * progress), y + 4, color);
    gui.drawString(
        Minecraft.getInstance().font,
        Component.translatable(
                ScannerState.uploadSuccess
                    ? "hud.cybercraft.scan.upload.ok"
                    : ScannerState.uploadResultAt > 0
                        ? "hud.cybercraft.scan.upload.fail"
                        : "hud.cybercraft.scan.upload")
            .getString(),
        x,
        y - 9,
        color,
        false);
  }

  private static void drawUploadAboveTarget(
      GuiGraphics gui, Minecraft client, int w, int h, LivingEntity target) {
    long now = System.currentTimeMillis();
    boolean uploading = ScannerState.uploadStart > 0 && now - ScannerState.uploadStart < 650;
    boolean result = ScannerState.uploadResultAt > 0 && now - ScannerState.uploadResultAt < 500;
    if (!uploading && !result) {
      return;
    }
    Vec3 pos = target.getPosition(1.0f).add(0, target.getBbHeight() + 0.55, 0);
    int[] screen = project(client, w, h, pos);
    if (screen == null) {
      return;
    }
    int barW = 54;
    int x = screen[0] - barW / 2;
    int y = screen[1];
    if (x < 2 || x + barW > w - 2 || y < 2 || y > h - 6) {
      return;
    }
    int color = ScannerState.uploadResultAt > 0 ? (ScannerState.uploadSuccess ? LIME : RED) : CYAN;
    float progress = Mth.clamp((now - ScannerState.uploadStart) / 450.0f, 0.0f, 1.0f);
    gui.fill(x - 1, y - 1, x + barW + 1, y + 5, 0xBB02070B);
    gui.fill(x, y, x + (int) (barW * progress), y + 4, color);
  }

  private static int[] project(Minecraft client, int w, int h, Vec3 pos) {
    Camera camera = client.gameRenderer.getMainCamera();
    Vec3 delta = pos.subtract(camera.getPosition());
    double yaw = Math.toRadians(camera.getYRot());
    double pitch = Math.toRadians(camera.getXRot());
    Vec3 forward =
        new Vec3(
            Math.sin(yaw) * Math.cos(pitch), -Math.sin(pitch), Math.cos(yaw) * Math.cos(pitch));
    Vec3 right = new Vec3(-Math.cos(yaw), 0, Math.sin(yaw));
    Vec3 up = right.cross(forward);
    double z = delta.dot(forward);
    if (z <= 0.05) {
      return null;
    }
    double fov = Math.toRadians(client.options.fov().get());
    double focal = (h / 2.0) / Math.tan(fov / 2.0);
    int sx = (int) (w / 2.0 + delta.dot(right) * focal / z);
    int sy = (int) (h / 2.0 - delta.dot(up) * focal / z);
    return new int[] {sx, sy};
  }

  private static void drawHints(GuiGraphics gui, Minecraft client, int w, int h) {
    var font = client.font;
    String execute =
        "["
            + CyberCraftClient.EXECUTE_HACK_KEY.getTranslatedKeyMessage().getString()
            + "] "
            + Component.translatable("hud.cybercraft.scan.hint.execute").getString();
    String navigate =
        "["
            + CyberCraftClient.HACK_PREV_KEY.getTranslatedKeyMessage().getString()
            + "/"
            + CyberCraftClient.HACK_NEXT_KEY.getTranslatedKeyMessage().getString()
            + "] "
            + Component.translatable("hud.cybercraft.scan.hint.navigate").getString();
    String exit =
        "["
            + CyberCraftClient.SCANNER_KEY.getTranslatedKeyMessage().getString()
            + "] "
            + Component.translatable("hud.cybercraft.scan.hint.exit").getString();

    int y = h - 14;
    gui.drawString(font, exit, w - font.width(exit) - 14, y, 0xFFB8D4DB, false);
    gui.drawString(font, navigate, w - font.width(navigate) - 14, y - 12, 0xFFB8D4DB, false);
    gui.drawString(font, execute, w - font.width(execute) - 14, y - 24, CYAN, false);
  }

  private static void drawDamageGlitch(GuiGraphics gui, Minecraft client, int w, int h) {
    if (client.player == null || client.player.hurtTime <= 0) {
      return;
    }
    RandomSource random = RandomSource.create(client.player.tickCount * 1103515245L + 12345L);
    for (int i = 0; i < 20; i++) {
      int y = random.nextInt(h);
      int x = random.nextInt(Math.max(1, w));
      int len = 20 + random.nextInt(Math.max(1, w / 2));
      int color = (0x44 << 24) | (i % 2 == 0 ? 0xFF003C : 0x00F0FF);
      gui.fill(x, y, Math.min(w, x + len), Math.min(h, y + 1), color);
    }
    gui.fill(0, 0, w, h, 0x12FF003C);
  }

  private static String truncate(Minecraft client, String text, int maxWidth) {
    if (client.font.width(text) <= maxWidth) {
      return text;
    }
    String result = text;
    while (!result.isEmpty() && client.font.width(result + "…") > maxWidth) {
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
}
