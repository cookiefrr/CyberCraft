package com.cybercraft.registry;

import com.cybercraft.CyberCraft;
import com.cybercraft.cyberware.CyberwareData;
import java.util.function.Supplier;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class ModAttachments {
  private static final DeferredRegister<AttachmentType<?>> TYPES =
      DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, CyberCraft.MOD_ID);
  public static final Supplier<AttachmentType<CyberwareData>> CYBERWARE =
      TYPES.register(
          "cyberware",
          () ->
              AttachmentType.builder(() -> CyberwareData.DEFAULT)
                  .serialize(CyberwareData.CODEC)
                  .copyOnDeath()
                  .build());

  public static void init(net.neoforged.bus.api.IEventBus bus) {
    TYPES.register(bus);
  }

  private ModAttachments() {}
}
