package com.cybercraft.data;

import static org.junit.jupiter.api.Assertions.*;

import com.cybercraft.cyberware.ImplantModels;
import com.google.gson.*;
import java.nio.file.*;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;

class ResourceTest {
  private static final Path ROOT = Path.of(System.getProperty("cybercraft.projectDir"));
  private static final Path GENERATED = ROOT.resolve("src/generated/resources");

  private static JsonObject read(Path path) throws Exception {
    return JsonParser.parseString(Files.readString(path)).getAsJsonObject();
  }

  @Test
  void everyImplantHasASlotModelAndEnglishDescription() throws Exception {
    var language = read(GENERATED.resolve("assets/cybercraft/lang/en_us.json"));
    try (var files = Files.list(GENERATED.resolve("data/cybercraft/cybercraft/implant"))) {
      var definitions = files.filter(p -> p.toString().endsWith(".json")).toList();
      assertEquals(43, definitions.size());
      for (var path : definitions) {
        var def = read(path);
        String id = path.getFileName().toString().replace(".json", "");
        assertTrue(language.has("book.cybercraft.desc." + id), id + " description missing");
        for (var slot : def.getAsJsonArray("slots"))
          assertTrue(
              Files.exists(
                  GENERATED.resolve(
                      "data/cybercraft/cybercraft/slot_type/"
                          + slot.getAsString().split(":")[1]
                          + ".json")));
        var model = read(GENERATED.resolve("assets/cybercraft/models/item/" + id + ".json"));
        assertEquals("neoforge:separate_transforms", model.get("loader").getAsString());
        assertEquals(
            "builtin/entity",
            model
                .getAsJsonObject("perspectives")
                .getAsJsonObject("gui")
                .get("parent")
                .getAsString());
        var icon = read(GENERATED.resolve("assets/cybercraft/models/item/icons/" + id + ".json"));
        assertEquals("minecraft:item/generated", icon.get("parent").getAsString());
        var model3d = read(GENERATED.resolve("assets/cybercraft/models/item/" + id + "_3d.json"));
        assertFalse(model3d.getAsJsonArray("elements").isEmpty());
        assertEquals(
            "cybercraft:item/cyberware_palette",
            model3d.getAsJsonObject("textures").get("palette").getAsString());
        var spec = ImplantModels.get(id);
        assertNotNull(spec, id);
        assertFalse(
            Files.exists(
                GENERATED.resolve("assets/cybercraft/models/item/" + id + "_worn_0.json")),
            id + " still generates worn models");
        assertTrue(
            Files.exists(
                ROOT.resolve("src/main/resources/assets/cybercraft/textures/item/" + id + ".png")));
      }
    }
  }

  @Test
  void paletteAndEmissiveAtlasesExistAtFullSize() throws Exception {
    var palette =
        ImageIO.read(
            ROOT.resolve("src/main/resources/assets/cybercraft/textures/item/cyberware_palette.png")
                .toFile());
    assertNotNull(palette);
    assertEquals(256, palette.getWidth());
    assertEquals(256, palette.getHeight());
    var emissive =
        ImageIO.read(
            ROOT.resolve(
                    "src/main/resources/assets/cybercraft/textures/item/cyberware_palette_e.png")
                .toFile());
    assertNotNull(emissive);
    assertEquals(256, emissive.getWidth());
    assertEquals(256, emissive.getHeight());
    int swatches =
        ImplantModels.Material.values().length * ImplantModels.Tone.values().length;
    assertTrue(swatches <= 256, "palette swatches " + swatches);
  }

  @Test
  void monowireHasAThreeDimensionalItemModel() throws Exception {
    var model = read(GENERATED.resolve("assets/cybercraft/models/item/monowire.json"));
    assertEquals("neoforge:separate_transforms", model.get("loader").getAsString());
    var model3d = read(GENERATED.resolve("assets/cybercraft/models/item/monowire_3d.json"));
    assertFalse(model3d.getAsJsonArray("elements").isEmpty());
    assertTrue(
        Files.exists(
            ROOT.resolve("src/main/resources/assets/cybercraft/textures/item/monowire.png")));
  }

  @Test
  void jsonIsValidAndHasNoDuplicateSourceCopies() throws Exception {
    try (var files = Files.walk(GENERATED)) {
      for (var path : files.filter(p -> p.toString().endsWith(".json")).toList()) {
        read(path);
        assertFalse(
            Files.exists(ROOT.resolve("src/main/resources").resolve(GENERATED.relativize(path))),
            "Duplicate source: " + path);
      }
    }
  }

  @Test
  void legacyRecipesAndWorldgenRemainPresent() throws Exception {
    try (var recipes = Files.list(GENERATED.resolve("data/cybercraft/recipe"))) {
      assertEquals(70, recipes.count());
    }
    for (String ore : new String[] {"titanium", "neodymium", "lithium"}) {
      assertTrue(
          Files.exists(
              GENERATED.resolve("data/cybercraft/worldgen/placed_feature/" + ore + "_ore.json")));
      assertTrue(
          Files.exists(
              GENERATED.resolve("data/cybercraft/neoforge/biome_modifier/" + ore + "_ore.json")));
    }
  }
}
