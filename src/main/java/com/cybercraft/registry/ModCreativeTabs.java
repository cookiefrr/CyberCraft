package com.cybercraft.registry;

import com.cybercraft.CyberCraft;
import com.cybercraft.cyberware.Cyberware;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

/** Scheda creativa dedicata alla mod. */
public final class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CyberCraft.MOD_ID);
    public static final Supplier<CreativeModeTab> MAIN = TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.cybercraft.main"))
            .icon(() -> new ItemStack(ModItems.CYBERDECK.get()))
            .displayItems((parameters, output) -> {
                output.accept(ModItems.NEURAL_LINK.get());
                output.accept(ModItems.CYBERDECK.get());
                output.accept(ModItems.EDGERUNNERS_BOOK.get());
                output.accept(ModItems.MONOWIRE.get());

                for (Cyberware cyberware : Cyberware.values()) {
                    output.accept(ModItems.implant(cyberware));
                }

                output.accept(ModItems.RAW_TITANIUM.get());
                output.accept(ModItems.TITANIUM_INGOT.get());
                output.accept(ModItems.RAW_NEODYMIUM.get());
                output.accept(ModItems.NEODYMIUM_INGOT.get());
                output.accept(ModItems.RAW_LITHIUM.get());
                output.accept(ModItems.LITHIUM_INGOT.get());
                output.accept(ModItems.CIRCUIT_BOARD.get());
                output.accept(ModItems.SERVO_MOTOR.get());
                output.accept(ModItems.BIO_GEL.get());
                output.accept(ModItems.OPTICS_LENS.get());

                output.accept(ModBlocks.IMPLANT_FORGE.get());
                output.accept(ModBlocks.INSTALLATION_TABLE.get());
                output.accept(ModBlocks.NEON_BLOCK.get());
                output.accept(ModBlocks.DARK_METAL_BLOCK.get());

                output.accept(ModBlocks.TITANIUM_ORE.get());
                output.accept(ModBlocks.DEEPSLATE_TITANIUM_ORE.get());
                output.accept(ModBlocks.NEODYMIUM_ORE.get());
                output.accept(ModBlocks.DEEPSLATE_NEODYMIUM_ORE.get());
                output.accept(ModBlocks.LITHIUM_ORE.get());
                output.accept(ModBlocks.DEEPSLATE_LITHIUM_ORE.get());

                output.accept(ModBlocks.RAW_TITANIUM_BLOCK.get());
                output.accept(ModBlocks.RAW_NEODYMIUM_BLOCK.get());
                output.accept(ModBlocks.RAW_LITHIUM_BLOCK.get());
                output.accept(ModBlocks.TITANIUM_BLOCK.get());
                output.accept(ModBlocks.NEODYMIUM_BLOCK.get());
                output.accept(ModBlocks.LITHIUM_BLOCK.get());
            })
            .build());

    private ModCreativeTabs() {}

    public static void init(net.neoforged.bus.api.IEventBus bus) { TABS.register(bus); }
}
