package com.cybercraft.client;

import com.cybercraft.cyberware.Cyberware;
import com.cybercraft.cyberware.CyberwareData;
import java.util.List;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;

public final class SandevistanFx {

  private static final int TINT = 0x1C00FF88;
  private static final double RANGE = 24.0;

  private SandevistanFx() {}

  public static boolean isActive(Minecraft client) {
    if (client.player == null || client.level == null) {
      return false;
    }
    CyberwareData data = ClientCyberwareState.get();
    return data.has(Cyberware.SANDEVISTAN)
        && data.sandevistanActiveUntil() > client.level.getGameTime();
  }

  public static void tick(Minecraft client) {
    if (!isActive(client) || client.player == null || client.level == null) {
      return;
    }
    ClientLevel level = client.level;
    RandomSource random = level.random;
    long phase = level.getGameTime() & 1L;

    List<LivingEntity> entities =
        level.getEntitiesOfClass(
            LivingEntity.class,
            client.player.getBoundingBox().inflate(RANGE),
            entity -> entity.isAlive() && !entity.isSpectator() && entity != client.player);

    int processed = 0;
    for (LivingEntity entity : entities) {
      if (processed++ >= 20) {
        break;
      }
      if ((entity.getId() & 1) != phase) {
        continue;
      }
      spawnTrail(level, entity, random);
    }
    if ((client.player.getId() & 1) == phase) {
      spawnTrail(level, client.player, random);
    }
  }

  private static void spawnTrail(ClientLevel level, LivingEntity entity, RandomSource random) {
    double dx = entity.getX() - entity.xOld;
    double dy = entity.getY() - entity.yOld;
    double dz = entity.getZ() - entity.zOld;
    double speed = dx * dx + dz * dz + Math.abs(dy) * 0.25;
    if (speed < 0.0025) {
      return;
    }

    double back = 2.0 + random.nextDouble() * 4.0;
    double x = entity.getX() - dx * back + (random.nextDouble() - 0.5) * 0.35;
    double y = entity.getY() + entity.getBbHeight() * (0.25 + random.nextDouble() * 0.6);
    double z = entity.getZ() - dz * back + (random.nextDouble() - 0.5) * 0.35;

    boolean hostile = entity instanceof Enemy;
    ParticleOptions primary = hostile ? ParticleTypes.FLAME : ParticleTypes.SOUL_FIRE_FLAME;
    ParticleOptions secondary = hostile ? ParticleTypes.SMOKE : ParticleTypes.ELECTRIC_SPARK;
    level.addParticle(primary, x, y, z, 0.0, 0.01, 0.0);
    if (random.nextBoolean()) {
      level.addParticle(secondary, x, y + 0.2, z, 0.0, 0.02, 0.0);
    }
  }

  public static void renderHud(GuiGraphics gui, DeltaTracker delta) {
    Minecraft client = Minecraft.getInstance();
    if (!isActive(client)
        || client.screen != null
        || client.options.hideGui
        || client.player == null
        || client.level == null) {
      return;
    }
    int w = client.getWindow().getGuiScaledWidth();
    int h = client.getWindow().getGuiScaledHeight();

    gui.fill(0, 0, w, h, TINT);

    int edge = Math.max(24, w / 7);
    gui.fillGradient(0, 0, edge, h, 0x4400FFB4, 0x0000FFB4);
    gui.fillGradient(w - edge, 0, w, h, 0x0000FFB4, 0x4400FFB4);
    gui.fillGradient(0, 0, w, Math.max(18, h / 9), 0x3300FFB4, 0x0000FFB4);
    gui.fillGradient(0, h - Math.max(18, h / 9), w, h, 0x0000FFB4, 0x3300FFB4);

    long now = client.level.getGameTime();
    RandomSource random = RandomSource.create(now / 2);
    for (int i = 0; i < 16; i++) {
      int y = random.nextInt(h);
      int len = 24 + random.nextInt(Math.max(24, w / 5));
      int alpha = 0x14 + random.nextInt(0x1E);
      int color = (alpha << 24) | (i % 4 == 0 ? 0xFFFFFF : 0x00FFC8);
      if (random.nextBoolean()) {
        gui.fill(0, y, len, y + 1, color);
      } else {
        gui.fill(w - len, y, w, y + 1, color);
      }
    }

    for (int y = 0; y < h; y += 4) {
      gui.fill(0, y, w, y + 1, 0x0800FFC8);
    }

    String label =
        Component.translatable(
                "hud.cybercraft.sandevistan.active",
                Math.max(
                    1L, (ClientCyberwareState.get().sandevistanActiveUntil() - now + 19L) / 20L))
            .getString();
    int labelX = w - client.font.width(label) - 14;
    gui.drawString(client.font, label, labelX, 14, 0xFF7CFFD2, false);
    int barW = client.font.width(label);
    long remaining = Math.max(0L, ClientCyberwareState.get().sandevistanActiveUntil() - now);
    float ratio = Math.min(1.0f, remaining / 240.0f);
    gui.fill(labelX, 25, labelX + barW, 27, 0x5500FFC8);
    gui.fill(labelX, 25, labelX + Math.round(barW * ratio), 27, 0xFF53FFC1);
  }
}
