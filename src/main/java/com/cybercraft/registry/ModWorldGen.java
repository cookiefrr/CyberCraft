package com.cybercraft.registry;

import com.cybercraft.CyberCraft;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

/**
 * Inserisce i minerali della mod nella generazione del mondo.
 * ConfiguredFeature e PlacedFeature sono registry datapack in 1.21.1:
 * i JSON stanno in data/cybercraft/worldgen/, qui si aggiungono ai biomi.
 */
public final class ModWorldGen {

    private ModWorldGen() {}

    public static void init() {
        add("titanium_ore");
        add("neodymium_ore");
        add("lithium_ore");
    }

    private static void add(String name) {
        ResourceKey<PlacedFeature> key =
                ResourceKey.create(Registries.PLACED_FEATURE, CyberCraft.id(name));
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(),
                GenerationStep.Decoration.UNDERGROUND_ORES, key);
    }
}
