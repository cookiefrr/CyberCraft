package com.cybercraft.registry;

import com.cybercraft.CyberCraft;
import com.cybercraft.cyberware.CyberwareData;
import net.fabricmc.fabric.api.attachment.v1.*;

public final class ModAttachments {
  public static final AttachmentType<CyberwareData> CYBERWARE =
      AttachmentRegistry.create(
          CyberCraft.id("cyberware"),
          b ->
              b.initializer(() -> CyberwareData.DEFAULT)
                  .persistent(CyberwareData.CODEC)
                  .copyOnDeath());

  public static void init() {}
}
