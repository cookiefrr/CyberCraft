package com.cybercraft.registry;

import com.cybercraft.CyberCraft;
import com.cybercraft.block.ImplantForgeBlock;
import com.cybercraft.block.InstallationTableBlock;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

/** Registro di blocchi, minerali e banchi di lavoro. Ogni blocco ottiene il suo BlockItem. */
public final class ModBlocks {

    /** Blocco al neon: emette luce al massimo. */
    public static final Block NEON_BLOCK = register("neon_block", Block::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .strength(1.5f, 6.0f)
                    .sound(SoundType.GLASS)
                    .lightLevel(state -> 15));

    /** Metallo scuro: resistente e serve un piccone. */
    public static final Block DARK_METAL_BLOCK = register("dark_metal_block", Block::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(5.0f, 8.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.NETHERITE_BLOCK));

    // --- Minerali nel mondo ---
    public static final Block TITANIUM_ORE = register("titanium_ore",
            p -> new DropExperienceBlock(UniformInt.of(1, 3), p),
            stoneOre(MapColor.STONE, 3.0f));
    public static final Block DEEPSLATE_TITANIUM_ORE = register("deepslate_titanium_ore",
            p -> new DropExperienceBlock(UniformInt.of(1, 3), p),
            deepslateOre(MapColor.DEEPSLATE, 4.5f));

    public static final Block NEODYMIUM_ORE = register("neodymium_ore",
            p -> new DropExperienceBlock(UniformInt.of(2, 4), p),
            stoneOre(MapColor.STONE, 3.5f));
    public static final Block DEEPSLATE_NEODYMIUM_ORE = register("deepslate_neodymium_ore",
            p -> new DropExperienceBlock(UniformInt.of(2, 4), p),
            deepslateOre(MapColor.DEEPSLATE, 5.0f));

    public static final Block LITHIUM_ORE = register("lithium_ore",
            p -> new DropExperienceBlock(UniformInt.of(1, 2), p),
            stoneOre(MapColor.STONE, 2.5f));
    public static final Block DEEPSLATE_LITHIUM_ORE = register("deepslate_lithium_ore",
            p -> new DropExperienceBlock(UniformInt.of(1, 2), p),
            deepslateOre(MapColor.DEEPSLATE, 4.0f));

    // --- Blocchi di materiale ---
    public static final Block RAW_TITANIUM_BLOCK = register("raw_titanium_block", Block::new,
            rawBlock(MapColor.COLOR_LIGHT_GRAY));
    public static final Block RAW_NEODYMIUM_BLOCK = register("raw_neodymium_block", Block::new,
            rawBlock(MapColor.COLOR_PURPLE));
    public static final Block RAW_LITHIUM_BLOCK = register("raw_lithium_block", Block::new,
            rawBlock(MapColor.COLOR_LIGHT_GREEN));

    public static final Block TITANIUM_BLOCK = register("titanium_block", Block::new,
            metalBlock(MapColor.COLOR_LIGHT_GRAY, SoundType.METAL));
    public static final Block NEODYMIUM_BLOCK = register("neodymium_block", Block::new,
            metalBlock(MapColor.COLOR_PURPLE, SoundType.METAL));
    public static final Block LITHIUM_BLOCK = register("lithium_block", Block::new,
            metalBlock(MapColor.COLOR_LIGHT_GREEN, SoundType.METAL));

    // --- Banchi di lavoro ---
    public static final Block IMPLANT_FORGE = register("implant_forge", ImplantForgeBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(4.0f, 6.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL));
    public static final Block INSTALLATION_TABLE = register("installation_table", InstallationTableBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(4.0f, 6.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL));

    private ModBlocks() {}

    private static BlockBehaviour.Properties stoneOre(MapColor color, float hardness) {
        return BlockBehaviour.Properties.of()
                .mapColor(color)
                .strength(hardness, 3.0f)
                .requiresCorrectToolForDrops()
                .sound(SoundType.STONE);
    }

    private static BlockBehaviour.Properties deepslateOre(MapColor color, float hardness) {
        return BlockBehaviour.Properties.of()
                .mapColor(color)
                .strength(hardness, 3.0f)
                .requiresCorrectToolForDrops()
                .sound(SoundType.DEEPSLATE);
    }

    private static BlockBehaviour.Properties rawBlock(MapColor color) {
        return BlockBehaviour.Properties.of()
                .mapColor(color)
                .strength(5.0f, 6.0f)
                .requiresCorrectToolForDrops()
                .sound(SoundType.STONE);
    }

    private static BlockBehaviour.Properties metalBlock(MapColor color, SoundType sound) {
        return BlockBehaviour.Properties.of()
                .mapColor(color)
                .strength(5.0f, 8.0f)
                .requiresCorrectToolForDrops()
                .sound(sound);
    }

    private static Block register(String name, Function<BlockBehaviour.Properties, Block> factory,
                                  BlockBehaviour.Properties properties) {
        Block block = factory.apply(properties);
        Registry.register(BuiltInRegistries.BLOCK, CyberCraft.id(name), block);
        Registry.register(BuiltInRegistries.ITEM, CyberCraft.id(name),
                new BlockItem(block, new Item.Properties()));
        return block;
    }

    /** Chiamare dall'inizializzazione per forzare il caricamento della classe. */
    public static void init() {}
}
