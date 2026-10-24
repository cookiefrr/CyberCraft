package com.cybercraft.data;

import com.cybercraft.cyberware.*;
import com.cybercraft.registry.*;
import com.google.gson.*;
import java.util.*;
import net.minecraft.data.PackOutput;

public final class GameplayData extends JsonDataProvider {
  public GameplayData(PackOutput output) {
    super(output);
  }

  @Override
  protected void generate() {
    String[] slots = {
      "operating_system",
      "frontal_cortex",
      "ocular",
      "hands",
      "arms",
      "nervous",
      "circulatory",
      "integumentary",
      "skeleton",
      "legs",
      "cyberdeck"
    };
    for (int i = 0; i < slots.length; i++) {
      String name = slots[i];
      String anchor =
          switch (name) {
            case "ocular", "frontal_cortex" -> "head";
            case "arms", "hands" -> "arms";
            case "legs" -> "legs";
            default -> "body";
          };
      add(
          "data/cybercraft/cybercraft/slot_type/" + name + ".json",
          obj(
              "name",
              "slot.cybercraft." + name,
              "icon",
              "cybercraft:neural_link",
              "order",
              i,
              "count",
              1,
              "anchor",
              anchor));
    }
    for (var implant : Cyberware.values())
      add(
          "data/cybercraft/cybercraft/implant/" + implant.id() + ".json",
          obj(
              "item",
              "cybercraft:" + implant.id(),
              "slots",
              array("cybercraft:" + implant.slot().id()),
              "tier",
              implant.tier(),
              "capacity",
              implant.capacityCost(),
              "behavior",
              "cybercraft:" + implant.id(),
              "model",
              "cybercraft:" + implant.id()));
    for (var block : ModBlocks.BLOCKS) {
      String id = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block).getPath();
      boolean ore = id.endsWith("_ore");
      JsonObject entry = obj("type", "minecraft:item", "name", "cybercraft:" + id);
      if (ore) {
        String metal = id.replace("deepslate_", "").replace("_ore", "");
        var silk =
            obj(
                "type",
                "minecraft:item",
                "name",
                "cybercraft:" + id,
                "conditions",
                array(
                    obj(
                        "condition",
                        "minecraft:match_tool",
                        "predicate",
                        obj(
                            "predicates",
                            obj(
                                "minecraft:enchantments",
                                array(
                                    obj(
                                        "enchantments",
                                        "minecraft:silk_touch",
                                        "levels",
                                        obj("min", 1))))))));
        var raw =
            obj(
                "type",
                "minecraft:item",
                "name",
                "cybercraft:raw_" + metal,
                "functions",
                array(
                    obj(
                        "function",
                        "minecraft:apply_bonus",
                        "enchantment",
                        "minecraft:fortune",
                        "formula",
                        "minecraft:ore_drops"),
                    obj("function", "minecraft:explosion_decay")));
        entry = obj("type", "minecraft:alternatives", "children", array(silk, raw));
      }
      var pool = obj("rolls", 1, "entries", array(entry));
      if (!ore) pool.add("conditions", array(obj("condition", "minecraft:survives_explosion")));
      add(
          "data/cybercraft/loot_table/blocks/" + id + ".json",
          obj("type", "minecraft:block", "pools", array(pool)));
    }
    ore("titanium", 6, 10, -64, 32);
    ore("neodymium", 5, 7, -64, 16);
    ore("lithium", 7, 12, -32, 64);
    for (String metal : new String[] {"titanium", "neodymium", "lithium"}) {
      tag("item", "c", "ingots/" + metal, "cybercraft:" + metal + "_ingot");
      tag("item", "c", "raw_materials/" + metal, "cybercraft:raw_" + metal);
      tag("item", "c", "storage_blocks/" + metal, "cybercraft:" + metal + "_block");
      tag(
          "block",
          "c",
          "ores/" + metal,
          "cybercraft:" + metal + "_ore",
          "cybercraft:deepslate_" + metal + "_ore");
      tag(
          "item",
          "c",
          "ores/" + metal,
          "cybercraft:" + metal + "_ore",
          "cybercraft:deepslate_" + metal + "_ore");
    }
    tag("item", "c", "ingots", "#c:ingots/titanium", "#c:ingots/neodymium", "#c:ingots/lithium");
    tag(
        "item",
        "c",
        "raw_materials",
        "#c:raw_materials/titanium",
        "#c:raw_materials/neodymium",
        "#c:raw_materials/lithium");
    tag(
        "entity_type",
        "cybercraft",
        "cyberware_users",
        "minecraft:zombie",
        "minecraft:husk",
        "minecraft:drowned",
        "minecraft:skeleton",
        "minecraft:stray",
        "minecraft:wither_skeleton",
        "minecraft:pillager",
        "minecraft:vindicator",
        "minecraft:evoker",
        "minecraft:piglin_brute");
    add(
        "data/minecraft/tags/block/needs_iron_tool.json",
        obj(
            "replace",
            false,
            "values",
            array(
                "cybercraft:titanium_ore",
                "cybercraft:deepslate_titanium_ore",
                "cybercraft:neodymium_ore",
                "cybercraft:deepslate_neodymium_ore",
                "cybercraft:titanium_block",
                "cybercraft:neodymium_block",
                "cybercraft:lithium_block")));
    add(
        "data/minecraft/tags/block/needs_stone_tool.json",
        obj(
            "replace",
            false,
            "values",
            array(
                "cybercraft:dark_metal_block",
                "cybercraft:lithium_ore",
                "cybercraft:deepslate_lithium_ore",
                "cybercraft:raw_titanium_block",
                "cybercraft:raw_neodymium_block",
                "cybercraft:raw_lithium_block",
                "cybercraft:implant_forge",
                "cybercraft:installation_table")));
    add(
        "data/minecraft/tags/block/mineable/pickaxe.json",
        obj(
            "replace",
            false,
            "values",
            array(
                "cybercraft:neon_block",
                "cybercraft:dark_metal_block",
                "cybercraft:titanium_ore",
                "cybercraft:deepslate_titanium_ore",
                "cybercraft:neodymium_ore",
                "cybercraft:deepslate_neodymium_ore",
                "cybercraft:lithium_ore",
                "cybercraft:deepslate_lithium_ore",
                "cybercraft:raw_titanium_block",
                "cybercraft:raw_neodymium_block",
                "cybercraft:raw_lithium_block",
                "cybercraft:titanium_block",
                "cybercraft:neodymium_block",
                "cybercraft:lithium_block",
                "cybercraft:implant_forge",
                "cybercraft:installation_table")));
  }

  private void tag(String type, String ns, String name, String... values) {
    add(
        "data/" + ns + "/tags/" + type + "/" + name + ".json",
        obj("replace", false, "values", values));
  }

  private void ore(String metal, int size, int count, int min, int max) {
    String name = metal + "_ore";
    add(
        "data/cybercraft/worldgen/configured_feature/" + name + ".json",
        obj(
            "type",
            "minecraft:ore",
            "config",
            obj(
                "size",
                size,
                "discard_chance_on_air_exposure",
                0,
                "targets",
                array(
                    obj(
                        "target",
                        obj(
                            "predicate_type",
                            "minecraft:tag_match",
                            "tag",
                            "minecraft:stone_ore_replaceables"),
                        "state",
                        obj("Name", "cybercraft:" + name)),
                    obj(
                        "target",
                        obj(
                            "predicate_type",
                            "minecraft:tag_match",
                            "tag",
                            "minecraft:deepslate_ore_replaceables"),
                        "state",
                        obj("Name", "cybercraft:deepslate_" + name))))));
    add(
        "data/cybercraft/worldgen/placed_feature/" + name + ".json",
        obj(
            "feature",
            "cybercraft:" + name,
            "placement",
            array(
                obj("type", "minecraft:count", "count", count),
                obj("type", "minecraft:in_square"),
                obj(
                    "type",
                    "minecraft:height_range",
                    "height",
                    obj(
                        "type",
                        "minecraft:uniform",
                        "min_inclusive",
                        obj("absolute", min),
                        "max_inclusive",
                        obj("absolute", max))),
                obj("type", "minecraft:biome"))));
  }
}
