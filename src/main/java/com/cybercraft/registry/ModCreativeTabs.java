package com.cybercraft.registry;

import com.cybercraft.CyberCraft;
import com.cybercraft.cyberware.Cyberware;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public final class ModCreativeTabs {

  public static final CreativeModeTab MAIN =
      net.minecraft.core.Registry.register(
          net.minecraft.core.registries.BuiltInRegistries.CREATIVE_MODE_TAB,
          CyberCraft.id("main"),
          net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup.builder()
              .title(Component.translatable("itemGroup.cybercraft.main"))
              .icon(() -> new ItemStack(ModItems.CYBERDECK))
              .displayItems(
                  (parameters, output) -> {
                    output.accept(ModItems.NEURAL_LINK);
                    output.accept(ModItems.CYBERDECK);
                    output.accept(ModItems.EDGERUNNERS_BOOK);
                    output.accept(ModItems.MONOWIRE);

                    for (Cyberware cyberware : Cyberware.values()) {
                      if (cyberware != Cyberware.CYBERDECK)
                        output.accept(ModItems.implant(cyberware));
                    }

                    output.accept(ModItems.RAW_TITANIUM);
                    output.accept(ModItems.TITANIUM_INGOT);
                    output.accept(ModItems.RAW_NEODYMIUM);
                    output.accept(ModItems.NEODYMIUM_INGOT);
                    output.accept(ModItems.RAW_LITHIUM);
                    output.accept(ModItems.LITHIUM_INGOT);
                    output.accept(ModItems.CIRCUIT_BOARD);
                    output.accept(ModItems.SERVO_MOTOR);
                    output.accept(ModItems.BIO_GEL);
                    output.accept(ModItems.OPTICS_LENS);

                    output.accept(ModBlocks.IMPLANT_FORGE);
                    output.accept(ModBlocks.INSTALLATION_TABLE);
                    output.accept(ModBlocks.NEON_BLOCK);
                    output.accept(ModBlocks.DARK_METAL_BLOCK);

                    output.accept(ModBlocks.TITANIUM_ORE);
                    output.accept(ModBlocks.DEEPSLATE_TITANIUM_ORE);
                    output.accept(ModBlocks.NEODYMIUM_ORE);
                    output.accept(ModBlocks.DEEPSLATE_NEODYMIUM_ORE);
                    output.accept(ModBlocks.LITHIUM_ORE);
                    output.accept(ModBlocks.DEEPSLATE_LITHIUM_ORE);

                    output.accept(ModBlocks.RAW_TITANIUM_BLOCK);
                    output.accept(ModBlocks.RAW_NEODYMIUM_BLOCK);
                    output.accept(ModBlocks.RAW_LITHIUM_BLOCK);
                    output.accept(ModBlocks.TITANIUM_BLOCK);
                    output.accept(ModBlocks.NEODYMIUM_BLOCK);
                    output.accept(ModBlocks.LITHIUM_BLOCK);
                  })
              .build());

  private ModCreativeTabs() {}

  public static void init() {}
}
