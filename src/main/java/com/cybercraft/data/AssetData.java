package com.cybercraft.data;

import com.cybercraft.cyberware.Cyberware;
import com.cybercraft.registry.*;
import com.google.gson.*;
import net.minecraft.data.PackOutput;

public final class AssetData extends JsonDataProvider {
  public AssetData(PackOutput output) {
    super(output);
  }

  @Override
  protected void generate() {
    for (var holder : ModBlocks.BLOCKS.getEntries()) {
      String id = holder.getId().getPath();
      add(
          "assets/cybercraft/blockstates/" + id + ".json",
          obj("variants", obj("", obj("model", "cybercraft:block/" + id))));
      JsonObject model;
      if (id.equals("implant_forge") || id.equals("installation_table"))
        model =
            obj(
                "parent",
                "minecraft:block/cube_bottom_top",
                "textures",
                obj(
                    "top",
                    "cybercraft:block/" + id + "_top",
                    "bottom",
                    "cybercraft:block/dark_metal_block",
                    "side",
                    "cybercraft:block/" + id + "_side"));
      else if (id.equals("dark_metal_block"))
        model =
            obj(
                "parent",
                "minecraft:block/cube_column",
                "textures",
                obj(
                    "end",
                    "cybercraft:block/dark_metal_block_top",
                    "side",
                    "cybercraft:block/dark_metal_block"));
      else
        model =
            obj(
                "parent",
                "minecraft:block/cube_all",
                "textures",
                obj("all", "cybercraft:block/" + id));
      add("assets/cybercraft/models/block/" + id + ".json", model);
      add("assets/cybercraft/models/item/" + id + ".json", obj("parent", "cybercraft:block/" + id));
    }
    for (var holder : ModItems.ITEMS.getEntries()) {
      String id = holder.getId().getPath();
      var implant = Cyberware.byId(id);
      JsonObject model =
          implant.isPresent()
              ? implantModel(implant.get())
              : obj(
                  "parent",
                  "minecraft:item/generated",
                  "textures",
                  obj("layer0", "cybercraft:item/" + id));
      if (implant.isPresent()) {
        add("assets/cybercraft/models/item/" + id + "_3d.json", model);
        add(
            "assets/cybercraft/models/item/icons/" + id + ".json",
            obj(
                "parent",
                "minecraft:item/generated",
                "textures",
                obj("layer0", "cybercraft:item/" + id)));
        model =
            obj(
                "loader",
                "neoforge:separate_transforms",
                "gui_light",
                "front",
                "base",
                obj("parent", "cybercraft:item/" + id + "_3d"),
                "perspectives",
                obj("gui", obj("parent", "builtin/entity")));
      }
      add("assets/cybercraft/models/item/" + id + ".json", model);
    }
  }

  private JsonObject cube(float x, float y, float z, float xx, float yy, float zz, String texture) {
    var faces = new JsonObject();
    for (String side : new String[] {"down", "up", "north", "south", "west", "east"})
      faces.add(side, obj("texture", "#" + texture, "uv", array(0, 0, 16, 16)));
    return obj("from", array(x, y, z), "to", array(xx, yy, zz), "faces", faces);
  }

  private JsonObject implantModel(Cyberware implant) {
    String id = implant.id(), slot = implant.slot().id();
    int variant = id.hashCode() & 0x7fffffff;
    var elements = new JsonArray();
    switch (slot) {
      case "ocular" -> {
        elements.add(cube(1, 6, 6, 15, 10, 9, "metal"));
        elements.add(cube(2, 6.5f, 5.5f, 6, 9.5f, 6.1f, "accent"));
        elements.add(cube(10, 6.5f, 5.5f, 14, 9.5f, 6.1f, "badge"));
      }
      case "arms", "hands" -> {
        elements.add(cube(4, 2, 5, 12, 13, 11, "metal"));
        elements.add(cube(3, 3, 4, 5, 11, 12, "accent"));
        elements.add(cube(5, 3, 4.6f, 11, 9, 5.1f, "badge"));
        if (id.equals("mantis_blades")) {
          elements.add(cube(6, 3, 3.4f, 10, 14, 4.6f, "metal"));
          elements.add(cube(9.5f, 4, 3.1f, 10.3f, 13, 3.6f, "metal"));
          elements.add(cube(6.3f, 3.5f, 3.1f, 9.7f, 4.6f, 3.5f, "accent"));
          elements.add(cube(6.2f, 1.8f, 3.5f, 10, 3.0f, 4.6f, "metal"));
          for (int rib = 0; rib < 3; rib++)
            elements.add(cube(6.8f, 6 + rib * 3, 3.0f, 9.4f, 6.8f + rib * 3, 3.4f, "accent"));
        }
        if (id.equals("projectile_launch_system")) {
          elements.add(cube(5, 7, 2, 11, 13, 5, "metal"));
          elements.add(cube(6, 8, 1.8f, 10, 12, 2.1f, "accent"));
        }
      }
      case "legs" -> {
        elements.add(cube(5, 1, 5, 11, 15, 11, "metal"));
        elements.add(cube(4, 2, 4, 6, 14, 6, "accent"));
        elements.add(cube(5.5f, 6, 4.5f, 10.5f, 11, 5.1f, "badge"));
      }
      case "cyberdeck" -> {
        elements.add(cube(2, 2, 6, 14, 14, 10, "metal"));
        elements.add(cube(3, 5, 5.7f, 13, 13, 6.1f, "badge"));
        elements.add(cube(3, 2.5f, 5.7f, 13, 4, 6.2f, "accent"));
      }
      default -> {
        elements.add(cube(3, 3, 5, 13, 13, 11, "metal"));
        elements.add(cube(4, 4, 4.6f, 12, 12, 5.1f, "badge"));
        for (int k = 0; k < 2 + variant % 3; k++) {
          float x = 3 + k * 2.5f;
          elements.add(cube(x, 2, 6, x + 1, 3, 10, "accent"));
        }
        elements.add(cube(2, 4 + variant % 4, 6, 3, 10 + variant % 3, 9, "accent"));
      }
    }
    return obj(
        "parent",
        "minecraft:block/block",
        "textures",
        obj(
            "particle",
            "cybercraft:block/dark_metal_block",
            "metal",
            "cybercraft:block/dark_metal_block",
            "accent",
            "cybercraft:block/neon_block",
            "badge",
            "cybercraft:item/" + id),
        "elements",
        elements,
        "display",
        obj(
            "gui",
            transform(25, 225, 0, 0, 0, 0, .75f),
            "ground",
            transform(0, 0, 0, 0, 2, 0, .4f),
            "fixed",
            transform(0, 0, 180, 0, 0, 0, 1),
            "thirdperson_righthand",
            transform(75, 45, 0, 0, 2, 1, .5f),
            "firstperson_righthand",
            transform(0, 45, 0, 1, 2, 0, .55f)));
  }

  private JsonObject transform(int x, int y, int z, int tx, int ty, int tz, float scale) {
    return obj(
        "rotation",
        array(x, y, z),
        "translation",
        array(tx, ty, tz),
        "scale",
        array(scale, scale, scale));
  }
}
