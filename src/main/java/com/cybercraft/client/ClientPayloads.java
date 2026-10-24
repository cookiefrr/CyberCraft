package com.cybercraft.client;

import com.cybercraft.network.*;
import net.minecraft.client.Minecraft;

public final class ClientPayloads {
  public static void sync(CyberwareSyncPayload p) {
    var client = Minecraft.getInstance();
    if (client.level == null) return;
    var entity = client.level.getEntity(p.entityId());
    if (entity instanceof net.minecraft.world.entity.LivingEntity living)
      living.setData(com.cybercraft.registry.ModAttachments.CYBERWARE, p.data());
    if (client.player != null && p.entityId() == client.player.getId()) {
      ClientCyberwareState.set(p.data());
      if (!com.cybercraft.cyberware.CyberwareManager.canScan(p.data())) ScannerState.close();
    }
  }

  public static void openBook() {
    Minecraft.getInstance().setScreen(new EdgerunnersBookScreen());
  }

  public static void result(HackResultPayload p) {
    if (ScannerState.active) ScannerState.finishUpload(p.success(), p.code());
  }
}
