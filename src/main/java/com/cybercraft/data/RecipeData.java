package com.cybercraft.data;

import com.google.gson.*;
import net.minecraft.data.PackOutput;

public final class RecipeData extends JsonDataProvider {
  public RecipeData(PackOutput output) {
    super(output);
  }

  @Override
  protected void generate() {
    shaped(
        "adrenaline_converter",
        "cybercraft:adrenaline_converter",
        1,
        new String[] {"CRC", " E ", " F "},
        new String[] {
          "C",
          "cybercraft:circuit_board",
          "R",
          "minecraft:redstone",
          "E",
          "cybercraft:titanium_ingot",
          "F",
          "minecraft:gold_ingot"
        });
    shaped(
        "axolotl",
        "cybercraft:axolotl",
        1,
        new String[] {"C C", " E ", " F "},
        new String[] {
          "C",
          "cybercraft:circuit_board",
          "E",
          "cybercraft:titanium_ingot",
          "F",
          "minecraft:gold_ingot"
        });
    shaped(
        "ballistic_coprocessor",
        "cybercraft:ballistic_coprocessor",
        1,
        new String[] {"C C", "SES", " F "},
        new String[] {
          "C",
          "cybercraft:circuit_board",
          "S",
          "cybercraft:servo_motor",
          "E",
          "cybercraft:titanium_ingot",
          "F",
          "minecraft:gold_ingot"
        });
    shaped(
        "behavioral_imprint",
        "cybercraft:behavioral_imprint",
        1,
        new String[] {" D ", "LEL", " F "},
        new String[] {
          "D",
          "cybercraft:optics_lens",
          "L",
          "minecraft:glass",
          "E",
          "cybercraft:titanium_ingot",
          "F",
          "minecraft:gold_ingot"
        });
    shaped(
        "berserk",
        "cybercraft:berserk",
        1,
        new String[] {" C ", "CEC", " F "},
        new String[] {
          "C",
          "cybercraft:circuit_board",
          "E",
          "cybercraft:neodymium_ingot",
          "F",
          "minecraft:diamond"
        });
    shaped(
        "bio_gel",
        "cybercraft:bio_gel",
        2,
        new String[] {" S ", "LGL", " S "},
        new String[] {
          "S",
          "minecraft:slime_ball",
          "L",
          "cybercraft:lithium_ingot",
          "G",
          "minecraft:glowstone_dust"
        });
    shaped(
        "biomonitor",
        "cybercraft:biomonitor",
        1,
        new String[] {"B B", " E ", " F "},
        new String[] {
          "B", "cybercraft:bio_gel", "E", "cybercraft:titanium_ingot", "F", "minecraft:gold_ingot"
        });
    shaped(
        "bionic_joints",
        "cybercraft:bionic_joints",
        1,
        new String[] {"G G", "HEH", " F "},
        new String[] {
          "G",
          "cybercraft:titanium_ingot",
          "H",
          "cybercraft:neodymium_ingot",
          "E",
          "cybercraft:titanium_ingot",
          "F",
          "minecraft:gold_ingot"
        });
    shaped(
        "blood_pump",
        "cybercraft:blood_pump",
        1,
        new String[] {"B B", " E ", " F "},
        new String[] {
          "B", "cybercraft:bio_gel", "E", "cybercraft:titanium_ingot", "F", "minecraft:gold_ingot"
        });
    shaped(
        "chrome_compressor",
        "cybercraft:chrome_compressor",
        1,
        new String[] {" C ", "CEC", " F "},
        new String[] {
          "C",
          "cybercraft:circuit_board",
          "E",
          "cybercraft:neodymium_ingot",
          "F",
          "minecraft:diamond"
        });
    shaped(
        "circuit_board",
        "cybercraft:circuit_board",
        2,
        new String[] {" R ", "RGR", " R "},
        new String[] {"R", "minecraft:redstone", "G", "minecraft:gold_ingot"});
    shaped(
        "cox2_optimizer",
        "cybercraft:cox2_optimizer",
        1,
        new String[] {"C C", " E ", " F "},
        new String[] {
          "C",
          "cybercraft:circuit_board",
          "E",
          "cybercraft:titanium_ingot",
          "F",
          "minecraft:gold_ingot"
        });
    shaped(
        "cyber_legs",
        "cybercraft:cyber_legs",
        1,
        new String[] {"G G", " E ", " S "},
        new String[] {
          "G", "minecraft:iron_ingot", "E", "minecraft:iron_ingot", "S", "cybercraft:servo_motor"
        });
    shaped(
        "cyberdeck",
        "cybercraft:cyberdeck",
        1,
        new String[] {"CGC", "RER", "III"},
        new String[] {
          "C",
          "cybercraft:circuit_board",
          "G",
          "minecraft:glass",
          "R",
          "minecraft:redstone",
          "E",
          "cybercraft:neodymium_ingot",
          "I",
          "minecraft:iron_ingot"
        });
    shaped(
        "dense_marrow",
        "cybercraft:dense_marrow",
        1,
        new String[] {"G G", "HEH", " F "},
        new String[] {
          "G",
          "cybercraft:titanium_ingot",
          "H",
          "cybercraft:neodymium_ingot",
          "E",
          "cybercraft:titanium_ingot",
          "F",
          "minecraft:gold_ingot"
        });
    shaped(
        "edgerunners_book",
        "cybercraft:edgerunners_book",
        1,
        new String[] {" L ", "LBL", " R "},
        new String[] {"L", "minecraft:leather", "B", "minecraft:book", "R", "minecraft:redstone"});
    shaped(
        "epimorphic_skeleton",
        "cybercraft:epimorphic_skeleton",
        1,
        new String[] {"G G", "HEH", " F "},
        new String[] {
          "G",
          "cybercraft:titanium_ingot",
          "H",
          "cybercraft:neodymium_ingot",
          "E",
          "cybercraft:neodymium_ingot",
          "F",
          "minecraft:diamond"
        });
    shaped(
        "ex_disk_ram_upgrade",
        "cybercraft:ex_disk_ram_upgrade",
        1,
        new String[] {"C C", " E ", " F "},
        new String[] {
          "C",
          "cybercraft:circuit_board",
          "E",
          "cybercraft:neodymium_ingot",
          "F",
          "minecraft:diamond"
        });
    shaped(
        "fireproof_grounding",
        "cybercraft:fireproof_grounding",
        1,
        new String[] {"G G", " E ", " F "},
        new String[] {
          "G",
          "cybercraft:titanium_ingot",
          "E",
          "cybercraft:titanium_ingot",
          "F",
          "minecraft:gold_ingot"
        });
    shaped(
        "fortified_ankles",
        "cybercraft:fortified_ankles",
        1,
        new String[] {"G G", " E ", " S "},
        new String[] {
          "G",
          "cybercraft:titanium_ingot",
          "E",
          "cybercraft:titanium_ingot",
          "S",
          "cybercraft:servo_motor"
        });
    shaped(
        "gorilla_arms",
        "cybercraft:gorilla_arms",
        1,
        new String[] {"G G", " E ", "SFS"},
        new String[] {
          "G",
          "cybercraft:titanium_ingot",
          "E",
          "cybercraft:titanium_ingot",
          "S",
          "cybercraft:servo_motor",
          "F",
          "minecraft:gold_ingot"
        });
    shaped(
        "handle_wrap",
        "cybercraft:handle_wrap",
        1,
        new String[] {"C C", "SES", " F "},
        new String[] {
          "C",
          "cybercraft:circuit_board",
          "S",
          "cybercraft:servo_motor",
          "E",
          "minecraft:iron_ingot",
          "F",
          "minecraft:copper_ingot"
        });
    shaped(
        "heal_on_kill",
        "cybercraft:heal_on_kill",
        1,
        new String[] {"B B", " E ", " F "},
        new String[] {
          "B", "cybercraft:bio_gel", "E", "cybercraft:titanium_ingot", "F", "minecraft:gold_ingot"
        });
    shaped(
        "implant_forge",
        "cybercraft:implant_forge",
        1,
        new String[] {"III", "ICI", "RRR"},
        new String[] {
          "I", "minecraft:iron_ingot", "C", "cybercraft:circuit_board", "R", "minecraft:redstone"
        });
    shaped(
        "installation_table",
        "cybercraft:installation_table",
        1,
        new String[] {"IGI", "CCC", "RRR"},
        new String[] {
          "I",
          "minecraft:iron_ingot",
          "G",
          "minecraft:glass",
          "C",
          "cybercraft:circuit_board",
          "R",
          "minecraft:redstone"
        });
    shaped(
        "jenkins_tendons",
        "cybercraft:jenkins_tendons",
        1,
        new String[] {"G G", " E ", " S "},
        new String[] {
          "G",
          "cybercraft:titanium_ingot",
          "E",
          "cybercraft:titanium_ingot",
          "S",
          "cybercraft:servo_motor"
        });
    shaped(
        "kerenzikov",
        "cybercraft:kerenzikov",
        1,
        new String[] {"CRC", " E ", " F "},
        new String[] {
          "C",
          "cybercraft:circuit_board",
          "R",
          "minecraft:redstone",
          "E",
          "cybercraft:titanium_ingot",
          "F",
          "minecraft:gold_ingot"
        });
    shaped(
        "kiroshi_optics",
        "cybercraft:kiroshi_optics",
        1,
        new String[] {" D ", "LEL", " F "},
        new String[] {
          "D",
          "cybercraft:optics_lens",
          "L",
          "minecraft:glass",
          "E",
          "cybercraft:titanium_ingot",
          "F",
          "minecraft:gold_ingot"
        });
    shaped(
        "lithium_block",
        "cybercraft:lithium_block",
        1,
        new String[] {"III", "III", "III"},
        new String[] {"I", "cybercraft:lithium_ingot"});
    cooking(
        "lithium_ingot_from_blasting",
        "blasting",
        "cybercraft:lithium_ingot",
        0.7f,
        100,
        new String[] {"cybercraft:raw_lithium"});
    shapeless(
        "lithium_ingot_from_block",
        "cybercraft:lithium_ingot",
        9,
        new String[] {"cybercraft:lithium_block"});
    cooking(
        "lithium_ingot_from_smelting",
        "smelting",
        "cybercraft:lithium_ingot",
        0.7f,
        200,
        new String[] {"cybercraft:raw_lithium"});
    shaped(
        "lynx_paws",
        "cybercraft:lynx_paws",
        1,
        new String[] {"G G", " E ", " S "},
        new String[] {
          "G", "minecraft:iron_ingot", "E", "minecraft:iron_ingot", "S", "cybercraft:servo_motor"
        });
    shaped(
        "maneuvering_system",
        "cybercraft:maneuvering_system",
        1,
        new String[] {"CRC", " E ", " F "},
        new String[] {
          "C",
          "cybercraft:circuit_board",
          "R",
          "minecraft:redstone",
          "E",
          "cybercraft:titanium_ingot",
          "F",
          "minecraft:gold_ingot"
        });
    shaped(
        "mantis_blades",
        "cybercraft:mantis_blades",
        1,
        new String[] {"G G", " E ", "SFS"},
        new String[] {
          "G",
          "cybercraft:titanium_ingot",
          "E",
          "cybercraft:titanium_ingot",
          "S",
          "cybercraft:servo_motor",
          "F",
          "minecraft:gold_ingot"
        });
    shaped(
        "microgenerator",
        "cybercraft:microgenerator",
        1,
        new String[] {"C C", "SES", " F "},
        new String[] {
          "C",
          "cybercraft:circuit_board",
          "S",
          "cybercraft:servo_motor",
          "E",
          "cybercraft:titanium_ingot",
          "F",
          "minecraft:gold_ingot"
        });
    shaped(
        "monowire",
        "cybercraft:monowire",
        1,
        new String[] {" N ", "SDS", " N "},
        new String[] {
          "N", "cybercraft:neodymium_ingot", "S", "minecraft:string", "D", "minecraft:diamond"
        });
    shaped(
        "neodymium_block",
        "cybercraft:neodymium_block",
        1,
        new String[] {"III", "III", "III"},
        new String[] {"I", "cybercraft:neodymium_ingot"});
    cooking(
        "neodymium_ingot_from_blasting",
        "blasting",
        "cybercraft:neodymium_ingot",
        0.7f,
        100,
        new String[] {"cybercraft:raw_neodymium"});
    shapeless(
        "neodymium_ingot_from_block",
        "cybercraft:neodymium_ingot",
        9,
        new String[] {"cybercraft:neodymium_block"});
    cooking(
        "neodymium_ingot_from_smelting",
        "smelting",
        "cybercraft:neodymium_ingot",
        0.7f,
        200,
        new String[] {"cybercraft:raw_neodymium"});
    shaped(
        "neural_link",
        "cybercraft:neural_link",
        1,
        new String[] {" C ", "RGR", " I "},
        new String[] {
          "C",
          "cybercraft:circuit_board",
          "R",
          "minecraft:redstone",
          "G",
          "minecraft:gold_ingot",
          "I",
          "minecraft:iron_ingot"
        });
    shaped(
        "newton_memory_boost",
        "cybercraft:newton_memory_boost",
        1,
        new String[] {"C C", " E ", " F "},
        new String[] {
          "C",
          "cybercraft:circuit_board",
          "E",
          "cybercraft:titanium_ingot",
          "F",
          "minecraft:gold_ingot"
        });
    shaped(
        "optical_camo",
        "cybercraft:optical_camo",
        1,
        new String[] {"G G", " E ", " F "},
        new String[] {
          "G",
          "cybercraft:titanium_ingot",
          "E",
          "cybercraft:neodymium_ingot",
          "F",
          "minecraft:diamond"
        });
    shaped(
        "optics_lens",
        "cybercraft:optics_lens",
        2,
        new String[] {" G ", "GQG", " G "},
        new String[] {"G", "minecraft:glass", "Q", "minecraft:quartz"});
    shaped(
        "pain_editor",
        "cybercraft:pain_editor",
        1,
        new String[] {"G G", " E ", " F "},
        new String[] {
          "G",
          "cybercraft:titanium_ingot",
          "E",
          "cybercraft:titanium_ingot",
          "F",
          "minecraft:gold_ingot"
        });
    shaped(
        "projectile_launch_system",
        "cybercraft:projectile_launch_system",
        1,
        new String[] {"G G", " E ", "SFS"},
        new String[] {
          "G",
          "cybercraft:titanium_ingot",
          "E",
          "cybercraft:neodymium_ingot",
          "S",
          "cybercraft:servo_motor",
          "F",
          "minecraft:diamond"
        });
    shaped(
        "quantum_tuner",
        "cybercraft:quantum_tuner",
        1,
        new String[] {"C C", " E ", " F "},
        new String[] {
          "C",
          "cybercraft:circuit_board",
          "E",
          "cybercraft:neodymium_ingot",
          "F",
          "minecraft:diamond"
        });
    shaped(
        "raw_lithium_block",
        "cybercraft:raw_lithium_block",
        1,
        new String[] {"RRR", "RRR", "RRR"},
        new String[] {"R", "cybercraft:raw_lithium"});
    shapeless(
        "raw_lithium_from_block",
        "cybercraft:raw_lithium",
        9,
        new String[] {"cybercraft:raw_lithium_block"});
    shaped(
        "raw_neodymium_block",
        "cybercraft:raw_neodymium_block",
        1,
        new String[] {"RRR", "RRR", "RRR"},
        new String[] {"R", "cybercraft:raw_neodymium"});
    shapeless(
        "raw_neodymium_from_block",
        "cybercraft:raw_neodymium",
        9,
        new String[] {"cybercraft:raw_neodymium_block"});
    shaped(
        "raw_titanium_block",
        "cybercraft:raw_titanium_block",
        1,
        new String[] {"RRR", "RRR", "RRR"},
        new String[] {"R", "cybercraft:raw_titanium"});
    shapeless(
        "raw_titanium_from_block",
        "cybercraft:raw_titanium",
        9,
        new String[] {"cybercraft:raw_titanium_block"});
    shaped(
        "reflex_synaptic_accelerator",
        "cybercraft:reflex_synaptic_accelerator",
        1,
        new String[] {"CRC", " E ", " F "},
        new String[] {
          "C",
          "cybercraft:circuit_board",
          "R",
          "minecraft:redstone",
          "E",
          "cybercraft:neodymium_ingot",
          "F",
          "minecraft:diamond"
        });
    shaped(
        "reinforced_tendons",
        "cybercraft:reinforced_tendons",
        1,
        new String[] {"G G", " E ", " S "},
        new String[] {
          "G",
          "cybercraft:titanium_ingot",
          "E",
          "cybercraft:titanium_ingot",
          "S",
          "cybercraft:servo_motor"
        });
    shaped(
        "sandevistan",
        "cybercraft:sandevistan",
        1,
        new String[] {" C ", "CEC", " F "},
        new String[] {
          "C",
          "cybercraft:circuit_board",
          "E",
          "cybercraft:neodymium_ingot",
          "F",
          "minecraft:diamond"
        });
    shaped(
        "second_heart",
        "cybercraft:second_heart",
        1,
        new String[] {"B B", " E ", " F "},
        new String[] {
          "B", "cybercraft:bio_gel", "E", "cybercraft:neodymium_ingot", "F", "minecraft:diamond"
        });
    shaped(
        "self_ice",
        "cybercraft:self_ice",
        1,
        new String[] {"C C", " E ", " F "},
        new String[] {
          "C",
          "cybercraft:circuit_board",
          "E",
          "cybercraft:titanium_ingot",
          "F",
          "minecraft:gold_ingot"
        });
    shaped(
        "servo_motor",
        "cybercraft:servo_motor",
        2,
        new String[] {" I ", "CRC", " I "},
        new String[] {
          "I", "minecraft:iron_ingot", "C", "minecraft:copper_ingot", "R", "minecraft:redstone"
        });
    shaped(
        "shock_n_awe",
        "cybercraft:shock_n_awe",
        1,
        new String[] {"G G", " E ", " F "},
        new String[] {
          "G",
          "cybercraft:titanium_ingot",
          "E",
          "cybercraft:titanium_ingot",
          "F",
          "minecraft:gold_ingot"
        });
    shaped(
        "smart_link",
        "cybercraft:smart_link",
        1,
        new String[] {"C C", "SES", " F "},
        new String[] {
          "C",
          "cybercraft:circuit_board",
          "S",
          "cybercraft:servo_motor",
          "E",
          "cybercraft:titanium_ingot",
          "F",
          "minecraft:gold_ingot"
        });
    shaped(
        "spring_joints",
        "cybercraft:spring_joints",
        1,
        new String[] {"G G", "HEH", " F "},
        new String[] {
          "G",
          "minecraft:iron_ingot",
          "H",
          "minecraft:copper_ingot",
          "E",
          "minecraft:iron_ingot",
          "F",
          "minecraft:copper_ingot"
        });
    shaped(
        "subdermal_armor",
        "cybercraft:subdermal_armor",
        1,
        new String[] {"G G", " E ", " F "},
        new String[] {
          "G",
          "cybercraft:titanium_ingot",
          "E",
          "cybercraft:titanium_ingot",
          "F",
          "minecraft:gold_ingot"
        });
    shaped(
        "syn_lungs_bioplastic",
        "cybercraft:syn_lungs_bioplastic",
        1,
        new String[] {"B B", " E ", " F "},
        new String[] {
          "B", "cybercraft:bio_gel", "E", "cybercraft:titanium_ingot", "F", "minecraft:gold_ingot"
        });
    shaped(
        "titanium_block",
        "cybercraft:titanium_block",
        1,
        new String[] {"III", "III", "III"},
        new String[] {"I", "cybercraft:titanium_ingot"});
    shaped(
        "titanium_bones",
        "cybercraft:titanium_bones",
        1,
        new String[] {"G G", "HEH", " F "},
        new String[] {
          "G",
          "cybercraft:titanium_ingot",
          "H",
          "cybercraft:neodymium_ingot",
          "E",
          "cybercraft:titanium_ingot",
          "F",
          "minecraft:gold_ingot"
        });
    cooking(
        "titanium_ingot_from_blasting",
        "blasting",
        "cybercraft:titanium_ingot",
        0.7f,
        100,
        new String[] {"cybercraft:raw_titanium"});
    shapeless(
        "titanium_ingot_from_block",
        "cybercraft:titanium_ingot",
        9,
        new String[] {"cybercraft:titanium_block"});
    cooking(
        "titanium_ingot_from_smelting",
        "smelting",
        "cybercraft:titanium_ingot",
        0.7f,
        200,
        new String[] {"cybercraft:raw_titanium"});
  }

  private static String convention(String id) {
    for (String metal : new String[] {"titanium", "neodymium", "lithium"}) {
      if (id.equals("cybercraft:" + metal + "_ingot")) return "#c:ingots/" + metal;
      if (id.equals("cybercraft:raw_" + metal)) return "#c:raw_materials/" + metal;
    }
    return id;
  }

  private JsonObject ingredient(String id) {
    id = convention(id);
    return id.startsWith("#") ? obj("tag", id.substring(1)) : obj("item", id);
  }

  private void shaped(String name, String result, int count, String[] pattern, String[] keys) {
    var key = new JsonObject();
    for (int i = 0; i < keys.length; i += 2) key.add(keys[i], ingredient(keys[i + 1]));
    add(
        "data/cybercraft/recipe/" + name + ".json",
        obj(
            "type",
            "minecraft:crafting_shaped",
            "category",
            "misc",
            "pattern",
            pattern,
            "key",
            key,
            "result",
            obj("id", result, "count", count)));
  }

  private void shapeless(String name, String result, int count, String[] items) {
    var list = new JsonArray();
    for (String item : items) list.add(ingredient(item));
    add(
        "data/cybercraft/recipe/" + name + ".json",
        obj(
            "type",
            "minecraft:crafting_shapeless",
            "category",
            "misc",
            "ingredients",
            list,
            "result",
            obj("id", result, "count", count)));
  }

  private void cooking(
      String name, String type, String result, float experience, int time, String[] items) {
    var list = new JsonArray();
    for (String item : items) list.add(ingredient(item));
    add(
        "data/cybercraft/recipe/" + name + ".json",
        obj(
            "type",
            "minecraft:" + type,
            "category",
            "misc",
            "ingredient",
            items.length == 1 ? list.get(0) : list,
            "result",
            obj("id", result),
            "experience",
            experience,
            "cookingtime",
            time));
  }
}
