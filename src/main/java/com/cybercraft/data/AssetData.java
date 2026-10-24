package com.cybercraft.data;

import com.cybercraft.cyberware.ImplantModels;
import com.cybercraft.cyberware.ImplantModels.Box;
import com.cybercraft.cyberware.ImplantModels.Group;
import com.cybercraft.cyberware.ImplantModels.Material;
import com.cybercraft.cyberware.ImplantModels.Model;
import com.cybercraft.cyberware.ImplantModels.Tone;
import com.cybercraft.registry.*;
import com.google.gson.*;
import java.util.ArrayList;
import java.util.List;
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
      Model spec = ImplantModels.get(id);
      if (spec == null) {
        add(
            "assets/cybercraft/models/item/" + id + ".json",
            obj(
                "parent",
                "minecraft:item/generated",
                "textures",
                obj("layer0", "cybercraft:item/" + id)));
        continue;
      }
      add("assets/cybercraft/models/item/" + id + "_3d.json", itemModel(spec));
      add(
          "assets/cybercraft/models/item/icons/" + id + ".json",
          obj(
              "parent",
              "minecraft:item/generated",
              "textures",
              obj("layer0", "cybercraft:item/" + id)));
      for (int i = 0; i < spec.groups().size(); i++) {
        Group group = spec.groups().get(i);
        add(
            "assets/cybercraft/models/item/" + id + "_worn_" + i + ".json",
            wornModel(group.boxes()));
        if (group.mirror())
          add(
              "assets/cybercraft/models/item/" + id + "_worn_" + i + "_left.json",
              wornModel(mirrored(group.boxes())));
      }
      add(
          "assets/cybercraft/models/item/" + id + ".json",
          obj(
              "loader",
              "neoforge:separate_transforms",
              "gui_light",
              "front",
              "base",
              obj("parent", "cybercraft:item/" + id + "_3d"),
              "perspectives",
              obj("gui", obj("parent", "builtin/entity"))));
    }
  }

  private JsonObject paletteFace(Material material, Tone tone) {
    int swatch = ImplantModels.swatch(material, tone);
    return obj(
        "texture",
        "#palette",
        "uv",
        array(
            ImplantModels.u0(swatch),
            ImplantModels.v0(swatch),
            ImplantModels.u1(swatch),
            ImplantModels.v1(swatch)));
  }

  private JsonObject paletteCube(Box box) {
    return paletteCube(
        box.x0(), box.y0(), box.z0(), box.x1(), box.y1(), box.z1(), box.material());
  }

  private JsonObject paletteCube(
      float x0, float y0, float z0, float x1, float y1, float z1, Material material) {
    var faces = new JsonObject();
    faces.add("down", paletteFace(material, Tone.DARK));
    faces.add("up", paletteFace(material, Tone.LIGHT));
    for (String side : new String[] {"north", "south", "west", "east"})
      faces.add(side, paletteFace(material, Tone.BASE));
    return obj(
        "from", array(f(x0), f(y0), f(z0)), "to", array(f(x1), f(y1), f(z1)), "faces", faces);
  }

  private JsonArray paletteElements(List<Box> boxes) {
    var elements = new JsonArray();
    for (Box box : boxes) elements.add(paletteCube(box));
    return elements;
  }

  private JsonObject wornModel(List<Box> boxes) {
    JsonObject model =
        obj(
            "parent",
            "minecraft:block/block",
            "textures",
            obj(
                "particle",
                "cybercraft:item/cyberware_palette",
                "palette",
                "cybercraft:item/cyberware_palette"),
            "display",
            obj(),
            "elements",
            paletteElements(boxes));
    for (Box box : boxes)
      if (box.material() == Material.GLASS) {
        model.addProperty("render_type", "minecraft:translucent");
        break;
      }
    return model;
  }

  private JsonObject itemModel(Model spec) {
    List<Box> boxes = spec.groups().get(0).boxes();
    float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, minZ = Float.MAX_VALUE;
    float maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE, maxZ = -Float.MAX_VALUE;
    for (Box box : boxes) {
      minX = Math.min(minX, box.x0());
      minY = Math.min(minY, box.y0());
      minZ = Math.min(minZ, box.z0());
      maxX = Math.max(maxX, box.x1());
      maxY = Math.max(maxY, box.y1());
      maxZ = Math.max(maxZ, box.z1());
    }
    float cx = (minX + maxX) / 2f, cy = (minY + maxY) / 2f, cz = (minZ + maxZ) / 2f;
    float largest = Math.max(maxX - minX, Math.max(maxY - minY, maxZ - minZ));
    float scale = 13f / Math.max(largest, .001f);
    var elements = new JsonArray();
    for (Box box : boxes)
      elements.add(
          paletteCube(
              8 + (box.x0() - cx) * scale,
              8 + (box.y0() - cy) * scale,
              8 + (box.z0() - cz) * scale,
              8 + (box.x1() - cx) * scale,
              8 + (box.y1() - cy) * scale,
              8 + (box.z1() - cz) * scale,
              box.material()));
    return obj(
        "parent",
        "minecraft:block/block",
        "gui_light",
        "front",
        "textures",
        obj(
            "particle",
            "cybercraft:item/cyberware_palette",
            "palette",
            "cybercraft:item/cyberware_palette"),
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

  private static List<Box> mirrored(List<Box> boxes) {
    var result = new ArrayList<Box>(boxes.size());
    for (Box box : boxes)
      result.add(
          new Box(-box.x1(), box.y0(), box.z0(), -box.x0(), box.y1(), box.z1(), box.material()));
    return result;
  }

  private static float f(float value) {
    return Math.round(value * 1000f) / 1000f;
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
