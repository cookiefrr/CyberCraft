package com.cybercraft.cyberware;

import java.util.HashSet;
import java.util.List;
import net.minecraft.world.phys.Vec3;

public final class ImplantModels {
  public enum Part {
    HEAD,
    BODY,
    RIGHT_ARM,
    LEFT_ARM,
    RIGHT_LEG,
    LEFT_LEG
  }

  public enum Material {
    INDUSTRIAL,
    DARK_METAL,
    TITANIUM,
    CHROME,
    GUNMETAL,
    CARBON,
    RUBBER,
    GOLD,
    BRONZE,
    COPPER,
    BRASS,
    RUST,
    CONCRETE,
    SMOKE,
    OLIVE,
    MUSTARD,
    YELLOW,
    BLOOD_RED,
    NEON_RED,
    NEON_ORANGE,
    NEON_YELLOW,
    ACID,
    EMERALD,
    CIRCUIT,
    CYAN,
    ELECTRIC_BLUE,
    BLUE_STEEL,
    DARK_BLUE,
    NAVY,
    PURPLE,
    DARK_PURPLE,
    VIOLET,
    PINK,
    MEDICAL,
    CERAMIC,
    BONE,
    AMBER,
    TOXIC,
    GLASS,
    HOLO,
    FLASH
  }

  public enum Tone {
    LIGHT,
    BASE,
    DARK
  }

  public record Box(float x0, float y0, float z0, float x1, float y1, float z1, Material material) {
    public Box {
      if (x0 >= x1 || y0 >= y1 || z0 >= z1)
        throw new IllegalArgumentException(
            "Invalid box " + x0 + "," + y0 + "," + z0 + " -> " + x1 + "," + y1 + "," + z1);
      if (material == null) throw new IllegalArgumentException("Missing material");
    }
  }

  public record Group(
      Part part, boolean mirror, String bone, Vec3 pivot, List<Box> boxes, boolean hidden) {
    public Group {
      boxes = List.copyOf(boxes);
      if (part == null || boxes.isEmpty()) throw new IllegalArgumentException("Empty group");
      if (bone == null || bone.isBlank()) throw new IllegalArgumentException("Missing bone");
      if (pivot == null
          || !Double.isFinite(pivot.x())
          || !Double.isFinite(pivot.y())
          || !Double.isFinite(pivot.z()))
        throw new IllegalArgumentException("Invalid pivot for " + bone);
    }

    public Group(Part part, boolean mirror, String bone, Vec3 pivot, List<Box> boxes) {
      this(part, mirror, bone, pivot, boxes, false);
    }
  }

  public record Model(List<Group> groups) {
    public Model {
      groups = List.copyOf(groups);
      if (groups.isEmpty()) throw new IllegalArgumentException("Empty model");
      var bones = new HashSet<String>();
      for (Group group : groups)
        if (!bones.add(group.bone()))
          throw new IllegalArgumentException("Duplicate bone " + group.bone());
    }

    public List<Group> itemGroups() {
      return groups.stream().filter(group -> !group.mirror() && !group.hidden()).toList();
    }

    public List<Box> itemBoxes() {
      return itemGroups().stream().flatMap(group -> group.boxes().stream()).toList();
    }
  }

  public static final int SWATCHES_PER_ROW = 16;
  public static final int SWATCH_PIXELS = 16;
  public static final int TEXTURE_PIXELS = 256;

  public static int swatch(Material material, Tone tone) {
    return material.ordinal() * 3 + tone.ordinal();
  }

  public static float u0(int swatch) {
    return (swatch % SWATCHES_PER_ROW) + .04f;
  }

  public static float v0(int swatch) {
    return (swatch / SWATCHES_PER_ROW) + .04f;
  }

  public static float u1(int swatch) {
    return (swatch % SWATCHES_PER_ROW) + .96f;
  }

  public static float v1(int swatch) {
    return (swatch / SWATCHES_PER_ROW) + .96f;
  }

  public static Model get(String id) {
    return switch (id) {
      case "cyberdeck" ->
          m(
              hg(
                  "housing",
                  0, -4.5f, 4.5f,
                  b(-3.2f, -6.8f, 4.1f, 3.2f, -2.2f, 4.55f, Material.INDUSTRIAL),
                  b(-2.8f, -6.5f, 4.55f, 2.8f, -2.5f, 4.7f, Material.DARK_METAL),
                  b(-2.4f, -6.3f, 4.7f, 2.4f, -2.8f, 4.78f, Material.GUNMETAL)),
              hg(
                  "circuit_a",
                  -1.5f, -4.5f, 4.7f,
                  b(-2.8f, -6.2f, 4.78f, -0.2f, -2.9f, 4.84f, Material.CIRCUIT)),
              hg(
                  "circuit_b",
                  1.5f, -4.5f, 4.7f,
                  b(0.2f, -6.2f, 4.78f, 2.8f, -2.9f, 4.84f, Material.CIRCUIT)),
              hg(
                  "led_a",
                  -1.6f, -3.3f, 4.84f,
                  b(-2.4f, -3.6f, 4.84f, -1.8f, -3.0f, 4.94f, Material.CYAN),
                  b(-1.4f, -3.6f, 4.84f, -0.8f, -3.0f, 4.94f, Material.CYAN)),
              hg(
                  "led_b",
                  1.6f, -3.3f, 4.84f,
                  b(0.8f, -3.6f, 4.84f, 1.4f, -3.0f, 4.94f, Material.CYAN),
                  b(1.8f, -3.6f, 4.84f, 2.4f, -3.0f, 4.94f, Material.CYAN)),
              hg(
                  "slot",
                  0, -2.5f, 4.88f,
                  b(-2.6f, -2.8f, 4.7f, -1.8f, -2.3f, 5.06f, Material.GOLD),
                  b(-0.8f, -2.8f, 4.7f, 0f, -2.3f, 5.06f, Material.GOLD),
                  b(0.8f, -2.8f, 4.7f, 1.8f, -2.3f, 5.06f, Material.GOLD),
                  b(2.4f, -2.8f, 4.7f, 3.2f, -2.3f, 5.06f, Material.GOLD)));
      case "sandevistan" ->
          m(
              hg(
                  "neck",
                  0, -1.5f, 4.3f,
                  b(-1.4f, -2.4f, 3.9f, 1.4f, -0.6f, 4.7f, Material.BRONZE),
                  b(-0.6f, -2.2f, 4.7f, 0.6f, -0.8f, 4.85f, Material.NEON_ORANGE)),
              bg(
                  "root",
                  0, 0, 2.5f,
                  b(-1.0f, 1.0f, 2.0f, 1.0f, 11.0f, 2.7f, Material.BRONZE),
                  b(-2.2f, 10.8f, 1.9f, 2.2f, 12.0f, 2.9f, Material.GOLD),
                  b(-3.0f, 10.4f, 2.0f, -2.2f, 11.6f, 2.8f, Material.BRONZE),
                  b(2.2f, 10.4f, 2.0f, 3.0f, 11.6f, 2.8f, Material.BRONZE)),
              bg(
                  "module_1",
                  0, 3.3f, 2.7f,
                  b(-1.7f, 2.0f, 2.7f, 1.7f, 3.3f, 3.4f, Material.BRONZE),
                  b(-0.5f, 2.2f, 3.4f, 0.5f, 3.1f, 3.55f, Material.NEON_ORANGE)),
              bg(
                  "spine_1",
                  0, 3.3f, 2.7f,
                  b(-2.1f, 1.6f, 2.1f, -1.4f, 2.6f, 2.7f, Material.COPPER),
                  b(1.4f, 1.6f, 2.1f, 2.1f, 2.6f, 2.7f, Material.COPPER)),
              bg(
                  "module_2",
                  0, 5.2f, 2.7f,
                  b(-1.7f, 3.9f, 2.7f, 1.7f, 5.2f, 3.4f, Material.BRONZE),
                  b(-0.5f, 4.1f, 3.4f, 0.5f, 5.0f, 3.55f, Material.NEON_ORANGE)),
              bg(
                  "spine_2",
                  0, 5.2f, 2.7f,
                  b(-2.1f, 3.3f, 2.1f, -1.4f, 3.9f, 2.7f, Material.COPPER),
                  b(1.4f, 3.3f, 2.1f, 2.1f, 3.9f, 2.7f, Material.COPPER)),
              bg(
                  "module_3",
                  0, 7.1f, 2.7f,
                  b(-1.7f, 5.8f, 2.7f, 1.7f, 7.1f, 3.4f, Material.BRONZE),
                  b(-0.5f, 6.0f, 3.4f, 0.5f, 6.9f, 3.55f, Material.NEON_ORANGE)),
              bg(
                  "spine_3",
                  0, 7.1f, 2.7f,
                  b(-2.1f, 5.2f, 2.1f, -1.4f, 5.8f, 2.7f, Material.COPPER),
                  b(1.4f, 5.2f, 2.1f, 2.1f, 5.8f, 2.7f, Material.COPPER)),
              bg(
                  "module_4",
                  0, 9.0f, 2.7f,
                  b(-1.7f, 7.7f, 2.7f, 1.7f, 9.0f, 3.4f, Material.BRONZE),
                  b(-0.5f, 7.9f, 3.4f, 0.5f, 8.8f, 3.55f, Material.NEON_ORANGE)),
              bg(
                  "spine_4",
                  0, 9.0f, 2.7f,
                  b(-2.1f, 7.1f, 2.1f, -1.4f, 7.7f, 2.7f, Material.COPPER),
                  b(1.4f, 7.1f, 2.1f, 2.1f, 7.7f, 2.7f, Material.COPPER)),
              bg(
                  "module_5",
                  0, 10.7f, 2.7f,
                  b(-1.7f, 9.6f, 2.7f, 1.7f, 10.7f, 3.4f, Material.BRONZE),
                  b(-0.5f, 9.8f, 3.4f, 0.5f, 10.5f, 3.55f, Material.NEON_ORANGE)),
              bg(
                  "spine_5",
                  0, 10.7f, 2.7f,
                  b(-2.1f, 9.0f, 2.1f, -1.4f, 9.6f, 2.7f, Material.COPPER),
                  b(1.4f, 9.0f, 2.1f, 2.1f, 9.6f, 2.7f, Material.COPPER)));
      case "berserk" ->
          m(
              bg(
                  "housing",
                  0, 8.0f, 3.0f,
                  b(-3.5f, 5.0f, 2.0f, 3.5f, 11.0f, 4.0f, Material.DARK_METAL),
                  b(-4.0f, 11.0f, 2.5f, -3.0f, 12.4f, 3.5f, Material.BLOOD_RED),
                  b(3.0f, 11.0f, 2.5f, 4.0f, 12.4f, 3.5f, Material.BLOOD_RED),
                  b(-2.4f, 3.8f, 2.3f, -1.4f, 5.0f, 3.6f, Material.COPPER),
                  b(1.4f, 3.8f, 2.3f, 2.4f, 5.0f, 3.6f, Material.COPPER),
                  b(-0.6f, 10.9f, 2.2f, 0.6f, 11.6f, 2.9f, Material.GOLD),
                  b(-2.6f, 7.0f, 4.45f, 2.6f, 7.4f, 4.6f, Material.NEON_YELLOW),
                  b(-2.6f, 8.1f, 4.45f, 2.6f, 8.5f, 4.6f, Material.NEON_YELLOW),
                  b(-2.6f, 9.2f, 4.45f, 2.6f, 9.6f, 4.6f, Material.NEON_YELLOW)),
              bg(
                  "core",
                  0, 8.0f, 4.0f,
                  b(-1.6f, 6.4f, 4.0f, 1.6f, 9.6f, 4.35f, Material.BLOOD_RED),
                  b(-1.0f, 7.0f, 4.35f, 1.0f, 9.0f, 4.5f, Material.NEON_RED)),
              bg(
                  "vent_l",
                  -3.5f, 8.0f, 3.0f,
                  b(-4.5f, 5.5f, 2.2f, -3.5f, 10.5f, 3.8f, Material.BLOOD_RED)),
              bg(
                  "vent_r",
                  3.5f, 8.0f, 3.0f,
                  b(3.5f, 5.5f, 2.2f, 4.5f, 10.5f, 3.8f, Material.BLOOD_RED)),
              bh(
                  "vent_flap_l",
                  -3.5f, 8.0f, 3.8f,
                  b(-4.6f, 6.0f, 3.8f, -3.5f, 10.0f, 3.9f, Material.NEON_YELLOW)),
              bh(
                  "vent_flap_r",
                  3.5f, 8.0f, 3.8f,
                  b(3.5f, 6.0f, 3.8f, 4.6f, 10.0f, 3.9f, Material.NEON_YELLOW)));
      case "chrome_compressor" ->
          m(
              bg(
                  "housing",
                  0, 5.8f, 2.6f,
                  b(-2.6f, 4.0f, 2.0f, 2.6f, 7.6f, 3.2f, Material.TITANIUM),
                  b(-2.2f, 4.4f, 3.2f, 2.2f, 7.2f, 3.35f, Material.INDUSTRIAL),
                  b(-2.3f, 4.4f, 3.35f, 2.3f, 5.1f, 3.5f, Material.YELLOW),
                  b(-2.3f, 5.2f, 3.35f, 2.3f, 5.5f, 3.5f, Material.CARBON),
                  b(-2.3f, 5.6f, 3.35f, 2.3f, 5.9f, 3.5f, Material.YELLOW),
                  b(-1.7f, 7.6f, 2.3f, -0.8f, 9.2f, 3.0f, Material.CHROME),
                  b(0.8f, 7.6f, 2.3f, 1.7f, 9.2f, 3.0f, Material.CHROME),
                  b(-1.0f, 7.7f, 3.1f, 1.0f, 8.6f, 3.5f, Material.GUNMETAL)),
              bg(
                  "fan_1",
                  -1.2f, 6.0f, 3.35f,
                  b(-1.75f, 5.85f, 3.35f, -0.65f, 6.15f, 3.5f, Material.CHROME),
                  b(-1.35f, 5.5f, 3.35f, -1.05f, 6.5f, 3.5f, Material.CHROME)),
              bg(
                  "fan_2",
                  1.2f, 6.0f, 3.35f,
                  b(0.65f, 5.85f, 3.35f, 1.75f, 6.15f, 3.5f, Material.CHROME),
                  b(1.05f, 5.5f, 3.35f, 1.35f, 6.5f, 3.5f, Material.CHROME)),
              bg(
                  "exhaust_l",
                  -2.6f, 5.75f, 2.6f,
                  b(-3.1f, 4.5f, 2.2f, -2.6f, 7.0f, 3.0f, Material.CHROME)),
              bg(
                  "exhaust_r",
                  2.6f, 5.75f, 2.6f,
                  b(2.6f, 4.5f, 2.2f, 3.1f, 7.0f, 3.0f, Material.CHROME)),
              bg(
                  "status",
                  0, 7.6f, 3.35f,
                  b(-0.4f, 7.4f, 3.35f, 0.4f, 7.8f, 3.45f, Material.ELECTRIC_BLUE)));
      case "axolotl" ->
          m(
              hg(
                  "chip",
                  -3.75f, -6.7f, -4.3f,
                  b(-4.45f, -7.4f, -4.75f, -3.05f, -6.0f, -3.9f, Material.DARK_METAL),
                  b(-4.25f, -7.2f, -4.75f, -3.25f, -6.2f, -3.95f, Material.EMERALD),
                  b(-4.45f, -7.4f, -4.85f, -3.05f, -7.2f, -4.75f, Material.GUNMETAL),
                  b(-4.45f, -6.2f, -4.85f, -3.05f, -6.0f, -4.75f, Material.GUNMETAL)),
              hg(
                  "glow",
                  -3.45f, -6.7f, -4.85f,
                  b(-4.15f, -7.0f, -4.9f, -3.35f, -6.4f, -4.85f, Material.ACID)),
              hg(
                  "insert",
                  -3.75f, -6.7f, -4.86f,
                  b(-3.95f, -6.9f, -4.92f, -3.55f, -6.5f, -4.86f, Material.CHROME)));
      case "quantum_tuner" ->
          m(
              hg(
                  "chip",
                  -2.25f, -6.7f, -4.3f,
                  b(-2.95f, -7.4f, -4.75f, -1.55f, -6.0f, -3.9f, Material.DARK_PURPLE),
                  b(-2.95f, -7.4f, -4.85f, -1.55f, -7.15f, -4.75f, Material.CARBON),
                  b(-2.95f, -6.25f, -4.85f, -1.55f, -6.0f, -4.75f, Material.CARBON),
                  b(-2.95f, -7.15f, -4.85f, -2.75f, -6.25f, -4.75f, Material.PURPLE),
                  b(-1.75f, -7.15f, -4.85f, -1.55f, -6.25f, -4.75f, Material.PURPLE)),
              hg(
                  "core",
                  -2.25f, -6.7f, -4.85f,
                  b(-2.45f, -6.9f, -4.91f, -2.05f, -6.5f, -4.86f, Material.CYAN)),
              hg(
                  "ring",
                  -2.25f, -6.7f, -4.87f,
                  b(-2.6f, -7.05f, -4.9f, -1.9f, -6.95f, -4.85f, Material.PURPLE),
                  b(-2.6f, -6.45f, -4.9f, -1.9f, -6.35f, -4.85f, Material.PURPLE)));
      case "cox2_optimizer" ->
          m(
              hg(
                  "chip",
                  -0.75f, -6.7f, -4.3f,
                  b(-1.45f, -7.4f, -4.75f, -0.05f, -6.0f, -3.9f, Material.COPPER),
                  b(-1.35f, -7.3f, -4.75f, -0.15f, -6.1f, -3.95f, Material.DARK_METAL)),
              hg(
                  "copper_frame",
                  -0.75f, -6.7f, -4.8f,
                  b(-1.45f, -7.4f, -4.85f, -0.05f, -7.25f, -4.75f, Material.COPPER),
                  b(-1.45f, -6.15f, -4.85f, -0.05f, -6.0f, -4.75f, Material.COPPER),
                  b(-1.45f, -7.25f, -4.85f, -1.3f, -6.15f, -4.75f, Material.COPPER),
                  b(-0.2f, -7.25f, -4.85f, -0.05f, -6.15f, -4.75f, Material.COPPER)),
              hg("grid_1", -1.05f, -6.98f, -4.89f, b(-1.2f, -7.1f, -4.91f, -0.9f, -6.86f, -4.87f, Material.NEON_ORANGE)),
              hg("grid_2", -0.45f, -6.98f, -4.89f, b(-0.6f, -7.1f, -4.91f, -0.3f, -6.86f, -4.87f, Material.NEON_ORANGE)),
              hg("grid_3", -1.05f, -6.62f, -4.89f, b(-1.2f, -6.74f, -4.91f, -0.9f, -6.5f, -4.87f, Material.NEON_ORANGE)),
              hg("grid_4", -0.45f, -6.62f, -4.89f, b(-0.6f, -6.74f, -4.91f, -0.3f, -6.5f, -4.87f, Material.NEON_ORANGE)),
              hg("grid_5", -1.05f, -6.26f, -4.89f, b(-1.2f, -6.38f, -4.91f, -0.9f, -6.14f, -4.87f, Material.NEON_ORANGE)),
              hg("grid_6", -0.45f, -6.26f, -4.89f, b(-0.6f, -6.38f, -4.91f, -0.3f, -6.14f, -4.87f, Material.NEON_ORANGE)));
      case "self_ice" ->
          m(
              hg(
                  "plate",
                  0.75f, -6.7f, -4.3f,
                  b(0.05f, -7.4f, -4.75f, 1.45f, -6.0f, -3.9f, Material.BLUE_STEEL),
                  b(0.15f, -7.3f, -4.85f, 1.35f, -7.2f, -4.75f, Material.CHROME),
                  b(0.15f, -6.2f, -4.85f, 1.35f, -6.1f, -4.75f, Material.CHROME),
                  b(0.15f, -7.2f, -4.85f, 0.25f, -6.2f, -4.75f, Material.CHROME),
                  b(1.25f, -7.2f, -4.85f, 1.35f, -6.2f, -4.75f, Material.CHROME),
                  b(0.45f, -7.45f, -4.5f, 1.05f, -7.3f, -4.2f, Material.GUNMETAL)),
              hg(
                  "node",
                  0.75f, -6.7f, -4.86f,
                  b(0.55f, -6.95f, -4.92f, 0.95f, -6.55f, -4.86f, Material.ELECTRIC_BLUE)),
              hh(
                  "barrier",
                  0, -4f, 0f,
                  b(-4.4f, -8.4f, -5.2f, 4.4f, -1.6f, -5.0f, Material.HOLO),
                  b(-4.4f, -8.4f, 4.0f, 4.4f, -1.6f, 4.2f, Material.HOLO),
                  b(-4.6f, -8.4f, -5.0f, -4.4f, -1.6f, 4.0f, Material.HOLO),
                  b(4.4f, -8.4f, -5.0f, 4.6f, -1.6f, 4.0f, Material.HOLO),
                  b(-4.4f, -8.6f, -5.0f, 4.4f, -8.4f, 4.0f, Material.HOLO),
                  b(-4.4f, -1.6f, -5.0f, 4.4f, -1.4f, 4.0f, Material.HOLO)));
      case "newton_memory_boost" ->
          m(
              hg(
                  "chip",
                  2.25f, -6.7f, -4.3f,
                  b(1.55f, -7.4f, -4.75f, 2.95f, -6.0f, -3.9f, Material.BLUE_STEEL),
                  b(1.65f, -7.3f, -4.75f, 2.85f, -6.1f, -3.95f, Material.INDUSTRIAL),
                  b(1.75f, -7.15f, -4.82f, 2.75f, -7.05f, -4.75f, Material.CYAN),
                  b(1.75f, -6.55f, -4.82f, 2.75f, -6.45f, -4.75f, Material.CYAN),
                  b(2.05f, -7.4f, -4.5f, 2.45f, -7.2f, -4.2f, Material.DARK_METAL)),
              hh(
                  "flash_blue",
                  2.25f, -6.7f, -4.83f,
                  b(1.65f, -7.3f, -4.88f, 2.85f, -6.1f, -4.78f, Material.ELECTRIC_BLUE)),
              hh(
                  "flash_green",
                  2.25f, -6.7f, -4.83f,
                  b(1.65f, -7.3f, -4.88f, 2.85f, -6.1f, -4.78f, Material.EMERALD)));
      case "ex_disk_ram_upgrade" ->
          m(
              hg(
                  "card",
                  3.75f, -6.7f, -4.3f,
                  b(3.05f, -7.4f, -4.75f, 4.45f, -6.0f, -3.9f, Material.CARBON),
                  b(3.15f, -7.3f, -4.82f, 4.35f, -6.1f, -4.75f, Material.DARK_PURPLE),
                  b(3.35f, -7.4f, -4.5f, 4.15f, -7.3f, -4.2f, Material.DARK_METAL)),
              hg("edge_1", 3.75f, -7.32f, -4.845f, b(3.15f, -7.35f, -4.87f, 4.35f, -7.3f, -4.82f, Material.VIOLET)),
              hg("edge_2", 3.75f, -6.08f, -4.845f, b(3.15f, -6.1f, -4.87f, 4.35f, -6.05f, -4.82f, Material.VIOLET)),
              hg("edge_3", 3.125f, -6.7f, -4.845f, b(3.1f, -7.3f, -4.87f, 3.15f, -6.1f, -4.82f, Material.VIOLET)),
              hg("edge_4", 4.375f, -6.7f, -4.845f, b(4.35f, -7.3f, -4.87f, 4.4f, -6.1f, -4.82f, Material.VIOLET)),
              hg("data_1", 3.6f, -7.31f, -4.89f, b(3.5f, -7.34f, -4.92f, 3.7f, -7.28f, -4.86f, Material.PURPLE)),
              hg("data_2", 4.33f, -6.6f, -4.89f, b(4.3f, -6.7f, -4.92f, 4.36f, -6.5f, -4.86f, Material.PURPLE)),
              hg("data_3", 4f, -6.05f, -4.89f, b(3.9f, -6.08f, -4.92f, 4.1f, -6.02f, -4.86f, Material.PURPLE)),
              hg("data_4", 3.11f, -6.8f, -4.89f, b(3.08f, -6.9f, -4.92f, 3.14f, -6.7f, -4.86f, Material.PURPLE)));
      case "kiroshi_optics" ->
          m(
              hg(
                  "root",
                  0, -4.25f, -4.2f,
                  b(-3.6f, -5.4f, -4.5f, -0.9f, -3.1f, -3.9f, Material.CARBON),
                  b(0.9f, -5.4f, -4.5f, 3.6f, -3.1f, -3.9f, Material.CARBON),
                  b(-3.7f, -5.6f, -4.4f, -0.8f, -5.3f, -3.9f, Material.CHROME),
                  b(0.8f, -5.6f, -4.4f, 3.7f, -5.3f, -3.9f, Material.CHROME),
                  b(-3.7f, -3.2f, -4.4f, -0.8f, -2.9f, -3.9f, Material.CHROME),
                  b(0.8f, -3.2f, -4.4f, 3.7f, -2.9f, -3.9f, Material.CHROME),
                  b(-4.0f, -5.2f, -4.2f, -3.7f, -3.3f, -3.9f, Material.CHROME),
                  b(3.7f, -5.2f, -4.2f, 4.0f, -3.3f, -3.9f, Material.CHROME),
                  b(-0.4f, -6.0f, -4.4f, 0.4f, -5.4f, -3.9f, Material.GUNMETAL)),
              hg(
                  "lens_l",
                  -2.25f, -4.25f, -4.65f,
                  b(-2.9f, -4.9f, -4.65f, -1.6f, -3.6f, -4.5f, Material.CYAN)),
              hg(
                  "lens_r",
                  2.25f, -4.25f, -4.65f,
                  b(1.6f, -4.9f, -4.65f, 2.9f, -3.6f, -4.5f, Material.CYAN)),
              hg(
                  "iris_l",
                  -2.15f, -4.32f, -4.7f,
                  b(-2.3f, -4.75f, -4.75f, -2.0f, -3.9f, -4.65f, Material.CHROME)),
              hg(
                  "iris_r",
                  2.15f, -4.32f, -4.7f,
                  b(2.0f, -4.75f, -4.75f, 2.3f, -3.9f, -4.65f, Material.CHROME)),
              hh(
                  "cone",
                  0, -4.25f, -4.8f,
                  b(-0.35f, -4.6f, -5.3f, 0.35f, -3.9f, -4.8f, Material.HOLO)));
      case "behavioral_imprint" ->
          m(
              hg(
                  "root",
                  -1.8f, -3.5f, -4.0f,
                  b(-0.8f, -4.7f, -4.8f, 0.8f, -3.8f, -4.0f, Material.SMOKE),
                  b(-0.5f, -3.7f, -4.9f, 0.5f, -2.6f, -4.2f, Material.CHROME),
                  b(-4.4f, -3.6f, -3.9f, -4.2f, -1.8f, -2.0f, Material.GUNMETAL),
                  b(-4.0f, -2.1f, -3.9f, -1.4f, -1.9f, -2.6f, Material.DARK_METAL),
                  b(0.2f, -5.1f, -4.4f, 0.8f, -4.7f, -3.9f, Material.CYAN)),
              hg(
                  "seg_1",
                  -0.7f, -4.15f, -4.25f,
                  b(-4.3f, -4.4f, -4.6f, -0.7f, -3.9f, -3.9f, Material.INDUSTRIAL)),
              hg(
                  "seg_2",
                  -0.9f, -3.25f, -4.25f,
                  b(-4.3f, -3.5f, -4.6f, -0.9f, -3.0f, -3.9f, Material.SMOKE)),
              hg(
                  "seg_3",
                  -1.2f, -2.35f, -4.25f,
                  b(-4.3f, -2.6f, -4.6f, -1.2f, -2.1f, -3.9f, Material.INDUSTRIAL)),
              hh(
                  "holo",
                  -2.4f, -3.3f, -4.65f,
                  b(-3.8f, -4.35f, -4.7f, -1.0f, -2.25f, -4.6f, Material.CYAN)));
      case "smart_link" ->
          m(
              both(
                  ra(
                      "root_r",
                      0, 9.4f, -2.15f,
                      b(-2.6f, 8.3f, -2.3f, 0.6f, 10.7f, -2.05f, Material.DARK_METAL),
                      b(-2.6f, 10.7f, -2.2f, 0.6f, 11.2f, -1.9f, Material.CARBON),
                      b(-2.5f, 7.9f, -2.25f, -1.7f, 8.35f, -1.95f, Material.GOLD),
                      b(0.0f, 7.9f, -2.25f, 0.6f, 8.35f, -1.95f, Material.GOLD)),
                  ra(
                      "palm_r",
                      0, 9.5f, -2.3f,
                      b(-2.3f, 8.7f, -2.4f, 0.3f, 8.95f, -2.3f, Material.NEON_ORANGE),
                      b(-2.3f, 9.35f, -2.4f, 0.3f, 9.6f, -2.3f, Material.NEON_ORANGE),
                      b(-2.3f, 10.0f, -2.4f, 0.3f, 10.25f, -2.3f, Material.NEON_ORANGE)),
                  ra(
                      "reticle_r",
                      -0.75f, 9.4f, -2.45f,
                      b(-1.0f, 8.6f, -2.5f, -0.7f, 10.2f, -2.4f, Material.NEON_YELLOW),
                      b(-1.35f, 9.25f, -2.5f, -0.35f, 9.55f, -2.4f, Material.NEON_ORANGE)),
                  ra(
                      "port_r",
                      -0.75f, 9.4f, -2.35f,
                      b(-0.9f, 8.5f, -2.35f, -0.6f, 10.3f, -2.3f, Material.NEON_YELLOW))));
      case "ballistic_coprocessor" ->
          m(
              both(
                  ra(
                      "plate_r",
                      0, 9.95f, 2.05f,
                      b(-2.7f, 8.2f, 1.8f, 0.7f, 10.9f, 2.3f, Material.GUNMETAL),
                      b(-2.7f, 10.6f, 1.9f, 0.7f, 11.2f, 2.4f, Material.CARBON),
                      b(-2.6f, 11.1f, 1.95f, -1.9f, 12.0f, 2.35f, Material.GUNMETAL),
                      b(-1.7f, 11.1f, 1.95f, -1.0f, 11.8f, 2.35f, Material.GUNMETAL),
                      b(-0.8f, 11.1f, 1.95f, -0.1f, 12.1f, 2.35f, Material.GUNMETAL),
                      b(0.1f, 11.1f, 1.95f, 0.6f, 11.7f, 2.35f, Material.GUNMETAL),
                      b(-2.6f, 11.6f, 2.35f, -1.9f, 12.0f, 2.42f, Material.NEON_RED),
                      b(-0.8f, 11.7f, 2.35f, -0.1f, 12.1f, 2.42f, Material.NEON_RED),
                      b(-2.8f, 7.8f, 1.9f, 0.8f, 8.3f, 2.3f, Material.DARK_METAL)),
                  ra("line_1_r", -2.1f, 9.55f, 2.36f,
                      b(-2.4f, 8.6f, 2.3f, -1.8f, 10.5f, 2.42f, Material.NEON_RED)),
                  ra("line_2_r", -1.1f, 9.55f, 2.36f,
                      b(-1.4f, 8.6f, 2.3f, -0.8f, 10.5f, 2.42f, Material.NEON_RED)),
                  ra("line_3_r", -0.1f, 9.55f, 2.36f,
                      b(-0.4f, 8.6f, 2.3f, 0.2f, 10.5f, 2.42f, Material.NEON_RED))));
      case "microgenerator" ->
          m(
              both(
                  ra(
                      "coil_r",
                      -1.0f, 8.0f, 0.3f,
                      b(-2.8f, 7.0f, -1.6f, 0.8f, 8.5f, 2.2f, Material.COPPER),
                      b(-2.3f, 7.2f, -1.9f, 0.3f, 8.3f, -1.6f, Material.BRONZE),
                      b(-2.9f, 7.2f, -1.0f, -2.8f, 8.3f, 1.8f, Material.BRONZE),
                      b(0.8f, 7.2f, -1.0f, 0.9f, 8.3f, 1.8f, Material.BRONZE),
                      b(-2.4f, 8.5f, -0.9f, 0.4f, 8.9f, 1.7f, Material.CHROME),
                      b(-1.6f, 8.9f, 0.0f, -0.6f, 9.3f, 0.9f, Material.ELECTRIC_BLUE),
                      b(0.0f, 9.0f, 0.3f, 0.5f, 9.25f, 0.8f, Material.CYAN),
                      b(-3.0f, 7.6f, 0.2f, -2.9f, 8.0f, 0.8f, Material.COPPER),
                      b(-1.2f, 7.3f, -2.0f, -0.4f, 8.0f, -1.9f, Material.COPPER)),
                  rah("charge_1_r", -1.8f, 7.4f, 2.25f,
                      b(-2.0f, 7.2f, 2.2f, -1.6f, 7.6f, 2.3f, Material.ELECTRIC_BLUE)),
                  rah("charge_2_r", -0.8f, 7.4f, 2.25f,
                      b(-1.0f, 7.2f, 2.2f, -0.6f, 7.6f, 2.3f, Material.ELECTRIC_BLUE)),
                  rah("charge_3_r", 0.2f, 7.4f, 2.25f,
                      b(0.0f, 7.2f, 2.2f, 0.4f, 7.6f, 2.3f, Material.ELECTRIC_BLUE)),
                  rah("charge_4_r", -1.3f, 8.2f, 2.25f,
                      b(-1.5f, 8.0f, 2.2f, -1.1f, 8.4f, 2.3f, Material.CYAN)),
                  rah(
                      "ring_r",
                      -1.0f, 7.6f, 0f,
                      b(-3.0f, 7.0f, -1.0f, -2.9f, 8.2f, 1.0f, Material.HOLO),
                      b(0.9f, 7.0f, -1.0f, 1.0f, 8.2f, 1.0f, Material.HOLO),
                      b(-2.9f, 7.0f, -1.2f, 0.9f, 8.2f, -1.1f, Material.HOLO),
                      b(-2.9f, 7.0f, 1.1f, 0.9f, 8.2f, 1.2f, Material.HOLO))));
      case "handle_wrap" ->
          m(
              both(
                  ra(
                      "wrap_r",
                      0, 9.1f, 0f,
                      b(-2.9f, 8.0f, -2.4f, 0.9f, 9.3f, 2.4f, Material.RUBBER),
                      b(-2.9f, 9.3f, -2.4f, 0.9f, 10.4f, 2.4f, Material.CARBON),
                      b(-3.0f, 7.8f, -2.4f, 1.0f, 8.1f, 2.4f, Material.INDUSTRIAL),
                      b(-3.0f, 8.4f, -2.45f, -2.9f, 9.9f, -1.7f, Material.RUBBER),
                      b(0.9f, 8.4f, -2.45f, 1.0f, 9.9f, -1.7f, Material.RUBBER)),
                  ra(
                      "grip_r",
                      0, 9.7f, 0f,
                      b(-3.0f, 9.2f, -2.5f, 1.0f, 9.4f, 2.5f, Material.GUNMETAL),
                      b(-3.0f, 10.3f, -2.5f, 1.0f, 10.5f, 2.5f, Material.GUNMETAL),
                      b(-1.4f, 10.4f, -2.45f, 0.0f, 10.7f, -1.8f, Material.GUNMETAL)),
                  ra(
                      "plate_r",
                      0, 9.4f, 2.55f,
                      b(-3.1f, 8.6f, 2.4f, 1.1f, 10.2f, 2.7f, Material.GUNMETAL))));
      case "mantis_blades" ->
          m(
              ra(
                  "plate",
                  0, 8.4f, 0f,
                  b(-3.0f, 2.6f, -2.2f, 1.0f, 8.4f, 2.2f, Material.CERAMIC),
                  b(-2.5f, 3.0f, -2.45f, 0.5f, 7.8f, -2.2f, Material.GUNMETAL),
                  b(-3.05f, 3.0f, -2.1f, -2.95f, 8.0f, 1.8f, Material.CHROME),
                  b(0.95f, 3.0f, -2.1f, 1.05f, 8.0f, 1.8f, Material.CHROME),
                  b(-3.05f, 2.4f, -2.2f, 1.05f, 2.9f, 2.2f, Material.DARK_METAL),
                  b(-0.8f, 8.8f, 0.7f, 0.8f, 9.4f, 0.9f, Material.NEON_ORANGE)),
              ra(
                  "blade",
                  0, 8.4f, 0f,
                  b(-0.4f, 8.4f, -0.7f, 0.4f, 15.6f, 0.7f, Material.CERAMIC),
                  b(0.4f, 8.4f, -0.7f, 0.6f, 15.6f, 0.1f, Material.CHROME),
                  b(-0.4f, 8.4f, -0.75f, 0.4f, 15.4f, -0.65f, Material.NEON_ORANGE),
                  b(-0.55f, 10.0f, -0.5f, -0.4f, 10.6f, 0.5f, Material.CHROME),
                  b(-0.55f, 11.4f, -0.5f, -0.4f, 12.0f, 0.5f, Material.CHROME),
                  b(-0.55f, 12.8f, -0.5f, -0.4f, 13.4f, 0.5f, Material.CHROME)),
              ra(
                  "edge",
                  0, 4.6f, -2.5f,
                  b(-2.1f, 3.3f, -2.55f, 0.1f, 4.0f, -2.45f, Material.NEON_ORANGE),
                  b(-2.1f, 4.3f, -2.55f, 0.1f, 5.0f, -2.45f, Material.NEON_ORANGE),
                  b(-2.1f, 5.3f, -2.55f, 0.1f, 6.0f, -2.45f, Material.NEON_ORANGE)));
      case "gorilla_arms" ->
          m(
              both(
                  ra(
                      "forearm_r",
                      0, 5.6f, 0f,
                      b(-3.4f, 1.8f, -2.3f, 1.2f, 7.2f, 2.3f, Material.CARBON),
                      b(-3.8f, 1.4f, -2.0f, -3.4f, 7.6f, 2.0f, Material.GUNMETAL),
                      b(-3.2f, 6.8f, -2.2f, 1.0f, 9.4f, 2.2f, Material.CARBON),
                      b(-3.4f, 9.2f, -2.2f, -2.2f, 10.4f, 2.2f, Material.BRASS),
                      b(-2.0f, 9.2f, -2.2f, -0.8f, 10.6f, 2.2f, Material.BRASS),
                      b(-0.6f, 9.2f, -2.2f, 0.6f, 10.8f, 2.2f, Material.BRASS),
                      b(0.8f, 9.2f, -2.2f, 1.2f, 10.2f, 2.2f, Material.GOLD)),
                  ra(
                      "fist_r",
                      0, 0.2f, 0f,
                      b(-2.8f, -1.4f, -2.1f, 0.8f, 1.8f, 2.1f, Material.DARK_METAL),
                      b(-2.2f, -2.0f, -1.6f, 0.2f, -1.4f, 1.6f, Material.GUNMETAL)),
                  ra(
                      "piston_a_r",
                      -3.4f, 4.6f, -1.3f,
                      b(-3.5f, 2.4f, -1.9f, -3.3f, 6.8f, -0.7f, Material.BLUE_STEEL),
                      b(-3.6f, 3.6f, -1.9f, -3.5f, 4.0f, -0.7f, Material.NEON_RED)),
                  ra(
                      "piston_b_r",
                      -3.4f, 4.6f, 1.3f,
                      b(-3.5f, 2.4f, 0.7f, -3.3f, 6.8f, 1.9f, Material.BLUE_STEEL),
                      b(-3.6f, 5.2f, 0.7f, -3.5f, 5.6f, 1.9f, Material.NEON_RED)),
                  ra(
                      "knuckle_a_r",
                      -2.3f, -0.6f, -2.1f,
                      b(-2.6f, -1.0f, -2.2f, -2.0f, -0.2f, -2.0f, Material.NEON_RED)),
                  ra(
                      "knuckle_b_r",
                      0.3f, -0.6f, -2.1f,
                      b(0.0f, -1.0f, -2.2f, 0.6f, -0.2f, -2.0f, Material.NEON_RED))));
      case "projectile_launch_system" ->
          m(
              ra(
                  "root",
                  0, 5.1f, 2.65f,
                  b(-3.0f, 3.6f, -2.2f, 0.6f, 7.0f, 1.7f, Material.OLIVE),
                  b(-3.0f, 3.2f, -2.3f, -2.7f, 6.6f, -2.1f, Material.DARK_METAL),
                  b(0.3f, 3.2f, -2.3f, 0.6f, 6.6f, -2.1f, Material.DARK_METAL)),
              ra(
                  "hatch",
                  0, 5.1f, 2.65f,
                  b(-3.2f, 2.8f, 1.7f, 0.8f, 7.4f, 3.6f, Material.OLIVE),
                  b(-2.8f, 3.2f, 3.6f, 0.4f, 7.0f, 3.9f, Material.GUNMETAL),
                  b(-2.6f, 4.0f, 3.9f, 0.2f, 4.5f, 4.05f, Material.YELLOW),
                  b(-2.6f, 5.0f, 3.9f, 0.2f, 5.25f, 4.05f, Material.CARBON)),
              ra(
                  "muzzle",
                  -1.2f, 8.3f, 2.7f,
                  b(-1.8f, 7.4f, 2.1f, -0.6f, 9.2f, 3.3f, Material.GUNMETAL),
                  b(-2.1f, 9.2f, 1.8f, -0.3f, 9.7f, 3.6f, Material.BLOOD_RED),
                  b(-2.05f, 9.7f, 1.85f, -0.35f, 9.85f, 3.55f, Material.NEON_YELLOW),
                  b(-2.4f, 6.8f, 3.0f, -1.8f, 7.6f, 3.6f, Material.NEON_ORANGE)),
              rah(
                  "rocket",
                  -0.6f, 10.1f, 2.7f,
                  b(-1.4f, 9.85f, 2.2f, 0.2f, 10.4f, 3.2f, Material.GUNMETAL),
                  b(-1.2f, 10.4f, 2.4f, 0.0f, 10.7f, 3.0f, Material.NEON_RED)),
              rah(
                  "flame_1",
                  -0.6f, 10.9f, 2.45f,
                  b(-1.3f, 10.7f, 2.3f, -0.1f, 11.1f, 2.65f, Material.NEON_ORANGE)),
              rah(
                  "flame_2",
                  -0.6f, 10.9f, 2.75f,
                  b(-1.3f, 10.7f, 2.6f, -0.1f, 11.1f, 2.95f, Material.NEON_ORANGE)),
              rah(
                  "flame_3",
                  -0.6f, 10.9f, 3.05f,
                  b(-1.3f, 10.7f, 2.9f, -0.1f, 11.1f, 3.25f, Material.NEON_ORANGE)));
      case "kerenzikov" ->
          m(
              bg(
                  "node_l",
                  -3.1f, 8.7f, 2.6f,
                  b(-4.6f, 7.0f, 2.0f, -1.6f, 10.4f, 2.6f, Material.CHROME),
                  b(-3.9f, 7.6f, 2.6f, -3.1f, 8.6f, 3.2f, Material.CYAN),
                  b(-3.3f, 7.6f, 2.6f, -2.5f, 8.6f, 3.2f, Material.CYAN),
                  b(-2.7f, 7.6f, 2.6f, -1.9f, 8.6f, 3.2f, Material.CYAN),
                  b(-4.2f, 8.8f, 2.6f, -1.8f, 9.2f, 3.0f, Material.ELECTRIC_BLUE),
                  b(-3.2f, 10.4f, 2.1f, -2.6f, 11.6f, 2.6f, Material.CHROME),
                  b(-3.0f, 10.2f, 2.6f, -2.8f, 11.2f, 2.8f, Material.CYAN)),
              bg(
                  "node_r",
                  3.1f, 8.7f, 2.6f,
                  b(1.6f, 7.0f, 2.0f, 4.6f, 10.4f, 2.6f, Material.CHROME),
                  b(1.9f, 7.6f, 2.6f, 2.7f, 8.6f, 3.2f, Material.CYAN),
                  b(2.5f, 7.6f, 2.6f, 3.3f, 8.6f, 3.2f, Material.CYAN),
                  b(3.1f, 7.6f, 2.6f, 3.9f, 8.6f, 3.2f, Material.CYAN),
                  b(1.8f, 8.8f, 2.6f, 4.2f, 9.2f, 3.0f, Material.ELECTRIC_BLUE),
                  b(2.6f, 10.4f, 2.1f, 3.2f, 11.6f, 2.6f, Material.CHROME),
                  b(2.8f, 10.2f, 2.6f, 3.0f, 11.2f, 2.8f, Material.CYAN)),
              bh(
                  "ring_l",
                  -3.1f, 8.7f, 2.6f,
                  b(-4.8f, 7.2f, 2.2f, -4.6f, 10.2f, 3.0f, Material.HOLO),
                  b(-1.8f, 7.2f, 2.2f, -1.6f, 10.2f, 3.0f, Material.HOLO),
                  b(-4.6f, 7.2f, 2.2f, -1.8f, 7.4f, 3.0f, Material.HOLO),
                  b(-4.6f, 10.0f, 2.2f, -1.8f, 10.2f, 3.0f, Material.HOLO)),
              bh(
                  "ring_r",
                  3.1f, 8.7f, 2.6f,
                  b(1.6f, 7.2f, 2.2f, 1.8f, 10.2f, 3.0f, Material.HOLO),
                  b(4.6f, 7.2f, 2.2f, 4.8f, 10.2f, 3.0f, Material.HOLO),
                  b(1.8f, 7.2f, 2.2f, 4.6f, 7.4f, 3.0f, Material.HOLO),
                  b(1.8f, 10.0f, 2.2f, 4.6f, 10.2f, 3.0f, Material.HOLO)),
              bg(
                  "spine",
                  0, 8.2f, 2.3f,
                  b(-1.4f, 8.8f, 2.0f, 1.4f, 9.6f, 2.6f, Material.CHROME),
                  b(-1.2f, 7.4f, 2.2f, 1.2f, 8.0f, 2.6f, Material.GUNMETAL)));
      case "reflex_synaptic_accelerator" ->
          m(
              bg("filament_1", -1.25f, 10.0f, 1.5f,
                  b(-1.5f, 10.0f, 1.2f, -1.0f, 12.8f, 1.8f, Material.NEON_YELLOW)),
              bg("filament_2", 0f, 10.2f, 1.65f,
                  b(-0.3f, 10.2f, 1.4f, 0.3f, 13.0f, 1.9f, Material.NEON_YELLOW)),
              bg("filament_3", 1.25f, 10.0f, 1.5f,
                  b(1.0f, 10.0f, 1.2f, 1.5f, 12.8f, 1.8f, Material.NEON_YELLOW)),
              bg(
                  "node",
                  0, 10.6f, 1.2f,
                  b(-1.1f, 10.8f, 1.0f, -0.7f, 11.4f, 1.6f, Material.CHROME),
                  b(0.7f, 10.8f, 1.0f, 1.1f, 11.4f, 1.6f, Material.CHROME),
                  b(-1.8f, 9.6f, 1.0f, 1.8f, 10.3f, 1.9f, Material.DARK_METAL),
                  b(-0.8f, 10.0f, 1.9f, 0.8f, 10.6f, 2.1f, Material.VIOLET),
                  b(-2.2f, 10.6f, -0.4f, -1.5f, 12.4f, 1.2f, Material.NEON_YELLOW),
                  b(1.5f, 10.6f, -0.4f, 2.2f, 12.4f, 1.2f, Material.NEON_YELLOW)),
              bh(
                  "ring",
                  0, 9.55f, 0.6f,
                  b(-2.4f, 9.4f, -0.6f, 2.4f, 9.7f, 0.0f, Material.HOLO),
                  b(-2.4f, 9.4f, 1.2f, 2.4f, 9.7f, 1.8f, Material.HOLO),
                  b(-2.4f, 9.4f, 0.0f, -1.8f, 9.7f, 1.2f, Material.HOLO),
                  b(1.8f, 9.4f, 0.0f, 2.4f, 9.7f, 1.2f, Material.HOLO)));
      case "maneuvering_system" ->
          m(
              bg(
                  "thruster_l",
                  -5.3f, 6.5f, 0.15f,
                  b(-5.3f, 3.0f, -1.2f, -4.1f, 5.8f, 1.5f, Material.SMOKE),
                  b(-4.5f, 5.8f, -0.9f, -4.1f, 6.5f, 1.2f, Material.INDUSTRIAL),
                  b(-5.1f, 2.2f, 0.3f, -4.3f, 3.0f, 1.5f, Material.GUNMETAL),
                  b(-5.0f, 2.0f, 0.5f, -4.4f, 2.2f, 1.3f, Material.NEON_ORANGE),
                  b(-5.2f, 3.4f, 1.5f, -4.2f, 5.0f, 1.8f, Material.GUNMETAL),
                  b(-4.9f, 3.4f, 1.8f, -4.5f, 4.8f, 2.0f, Material.NEON_ORANGE)),
              bg(
                  "thruster_r",
                  5.3f, 6.5f, 0.15f,
                  b(4.1f, 3.0f, -1.2f, 5.3f, 5.8f, 1.5f, Material.SMOKE),
                  b(4.1f, 5.8f, -0.9f, 4.5f, 6.5f, 1.2f, Material.INDUSTRIAL),
                  b(4.3f, 2.2f, 0.3f, 5.1f, 3.0f, 1.5f, Material.GUNMETAL),
                  b(4.4f, 2.0f, 0.5f, 5.0f, 2.2f, 1.3f, Material.NEON_ORANGE),
                  b(4.2f, 3.4f, 1.5f, 5.2f, 5.0f, 1.8f, Material.GUNMETAL),
                  b(4.5f, 3.4f, 1.8f, 4.9f, 4.8f, 2.0f, Material.NEON_ORANGE)),
              bh(
                  "flame_l",
                  -4.7f, 2.0f, 0.9f,
                  b(-4.9f, 1.2f, 0.4f, -4.5f, 2.0f, 1.4f, Material.NEON_ORANGE)),
              bh(
                  "flame_r",
                  4.7f, 2.0f, 0.9f,
                  b(4.5f, 1.2f, 0.4f, 4.9f, 2.0f, 1.4f, Material.NEON_ORANGE)));
      case "adrenaline_converter" ->
          m(
              bg(
                  "vial",
                  0, 11.0f, -2.0f,
                  b(-1.5f, 9.6f, -2.6f, 1.5f, 12.4f, -1.4f, Material.CHROME),
                  b(-1.6f, 9.4f, -2.7f, 1.6f, 9.9f, -1.3f, Material.GUNMETAL),
                  b(-2.5f, 10.4f, -2.4f, -1.5f, 11.0f, -1.7f, Material.RUBBER),
                  b(1.5f, 10.4f, -2.4f, 2.5f, 11.0f, -1.7f, Material.RUBBER),
                  b(-1.9f, 10.7f, -2.1f, -1.5f, 11.0f, -1.9f, Material.TOXIC),
                  b(1.5f, 10.7f, -2.1f, 1.9f, 11.0f, -1.9f, Material.TOXIC)),
              bg(
                  "cap",
                  0, 12.5f, -2.0f,
                  b(-1.6f, 12.1f, -2.7f, 1.6f, 12.7f, -1.3f, Material.GUNMETAL),
                  b(-1.1f, 12.7f, -2.4f, 1.1f, 12.9f, -1.6f, Material.CHROME)),
              bg("liquid_1", 0, 10.175f, -2.635f, b(-1.1f, 9.9f, -2.72f, 1.1f, 10.45f, -2.55f, Material.TOXIC)),
              bg("liquid_2", 0, 10.725f, -2.635f, b(-1.1f, 10.45f, -2.72f, 1.1f, 11.0f, -2.55f, Material.TOXIC)),
              bg("liquid_3", 0, 11.275f, -2.635f, b(-1.1f, 11.0f, -2.72f, 1.1f, 11.55f, -2.55f, Material.ACID)),
              bg("liquid_4", 0, 11.825f, -2.635f, b(-1.1f, 11.55f, -2.72f, 1.1f, 12.1f, -2.55f, Material.TOXIC)));
      case "second_heart" ->
          m(
              bg(
                  "heart",
                  -1.5f, 6.6f, -2.75f,
                  b(-2.8f, 4.8f, -3.5f, -0.2f, 8.4f, -2.0f, Material.BLOOD_RED),
                  b(-2.2f, 3.4f, -3.2f, -0.6f, 4.8f, -2.2f, Material.BLOOD_RED),
                  b(-2.8f, 8.4f, -3.2f, -0.2f, 8.9f, -2.4f, Material.BLUE_STEEL),
                  b(-2.4f, 5.0f, -2.0f, -1.8f, 8.6f, -1.9f, Material.DARK_METAL),
                  b(-2.6f, 7.8f, -3.55f, -2.2f, 8.2f, -3.1f, Material.NEON_RED)),
              bg(
                  "core",
                  -1.3f, 6.65f, -3.75f,
                  b(-1.8f, 5.9f, -3.7f, -0.8f, 7.4f, -3.5f, Material.GOLD),
                  b(-1.6f, 6.1f, -3.8f, -1.0f, 7.2f, -3.7f, Material.AMBER)),
              bg(
                  "tube_l",
                  -3.0f, 6.6f, -2.6f,
                  b(-3.2f, 5.2f, -3.0f, -2.8f, 8.0f, -2.2f, Material.BLUE_STEEL)),
              bg(
                  "tube_r",
                  0.05f, 6.6f, -2.6f,
                  b(-0.2f, 5.2f, -3.0f, 0.3f, 8.0f, -2.2f, Material.BLUE_STEEL),
                  b(-1.0f, 4.0f, -2.9f, -0.6f, 4.8f, -2.3f, Material.CHROME)),
              bh(
                  "flash",
                  -1.5f, 6.6f, -2.75f,
                  b(-2.9f, 4.6f, -3.6f, 0.3f, 8.6f, -2.1f, Material.FLASH)));
      case "biomonitor" ->
          m(
              g(
                  Part.LEFT_ARM,
                  "screen",
                  -1.0f, 8.7f, -2.3f,
                  b(-2.6f, 7.7f, -2.45f, 0.6f, 9.7f, -2.1f, Material.CARBON),
                  b(-2.2f, 8.0f, -2.6f, 0.2f, 9.4f, -2.45f, Material.CIRCUIT),
                  b(-2.7f, 7.5f, -2.5f, 0.7f, 7.8f, -2.0f, Material.RUBBER),
                  b(-2.7f, 9.6f, -2.5f, 0.7f, 10.0f, -2.0f, Material.RUBBER),
                  b(0.3f, 9.5f, -2.55f, 0.6f, 9.7f, -2.4f, Material.NEON_RED)),
              g(
                  Part.LEFT_ARM,
                  "bar_1",
                  -1.9f, 8.7f, -2.64f,
                  b(-2.0f, 8.3f, -2.68f, -1.8f, 9.1f, -2.6f, Material.EMERALD)),
              g(
                  Part.LEFT_ARM,
                  "bar_2",
                  -0.8f, 8.4f, -2.64f,
                  b(-1.5f, 8.3f, -2.68f, -0.1f, 8.5f, -2.6f, Material.EMERALD)),
              g(
                  Part.LEFT_ARM,
                  "bar_3",
                  -0.95f, 8.9f, -2.64f,
                  b(-1.5f, 8.8f, -2.68f, -0.4f, 9.0f, -2.6f, Material.EMERALD)),
              g(
                  Part.LEFT_ARM,
                  "bar_4",
                  -0.6f, 9.2f, -2.64f,
                  b(-1.0f, 9.1f, -2.68f, -0.2f, 9.3f, -2.6f, Material.EMERALD)),
              g(
                  Part.LEFT_ARM,
                  "cross",
                  -0.95f, 8.55f, -2.7f,
                  b(-1.4f, 8.4f, -2.75f, -0.5f, 8.6f, -2.67f, Material.NEON_RED),
                  b(-1.05f, 8.1f, -2.75f, -0.85f, 9.0f, -2.67f, Material.NEON_RED)),
              g(
                  Part.LEFT_ARM,
                  "heal",
                  -0.95f, 8.55f, -2.7f,
                  b(-1.4f, 8.4f, -2.75f, -0.5f, 8.6f, -2.67f, Material.EMERALD),
                  b(-1.05f, 8.1f, -2.75f, -0.85f, 9.0f, -2.67f, Material.EMERALD)));
      case "blood_pump" ->
          m(
              bg(
                  "pump",
                  2.4f, 5.9f, -2.7f,
                  b(1.0f, 4.2f, -3.3f, 3.8f, 7.6f, -2.1f, Material.GUNMETAL),
                  b(3.8f, 4.8f, -3.0f, 4.2f, 7.0f, -2.2f, Material.CHROME),
                  b(2.0f, 8.4f, -3.0f, 2.8f, 8.9f, -2.4f, Material.NEON_ORANGE),
                  b(1.3f, 5.0f, -2.1f, 3.5f, 5.5f, -1.9f, Material.DARK_METAL),
                  b(1.2f, 3.4f, -2.9f, 1.8f, 4.2f, -2.3f, Material.NEON_RED),
                  b(3.0f, 3.4f, -2.9f, 3.6f, 4.2f, -2.3f, Material.NEON_ORANGE)),
              bg("tube_1", 1.8f, 7.6f, -2.7f,
                  b(1.4f, 6.8f, -3.1f, 2.2f, 8.4f, -2.3f, Material.CHROME)),
              bg("tube_2", 3.0f, 7.6f, -2.7f,
                  b(2.6f, 6.8f, -3.1f, 3.4f, 8.4f, -2.3f, Material.CHROME)),
              bg("flow_1", 1.75f, 7.15f, -3.12f,
                  b(1.6f, 7.0f, -3.16f, 1.9f, 7.3f, -3.08f, Material.BLOOD_RED)),
              bg("flow_2", 2.05f, 7.75f, -3.12f,
                  b(1.9f, 7.6f, -3.16f, 2.2f, 7.9f, -3.08f, Material.BLOOD_RED)),
              bg("flow_3", 2.95f, 7.15f, -3.12f,
                  b(2.8f, 7.0f, -3.16f, 3.1f, 7.3f, -3.08f, Material.BLOOD_RED)),
              bg("flow_4", 2.65f, 7.75f, -3.12f,
                  b(2.5f, 7.6f, -3.16f, 2.8f, 7.9f, -3.08f, Material.BLOOD_RED)),
              bg("pulse", 2.4f, 5.8f, -3.4f,
                  b(1.9f, 5.2f, -3.5f, 2.9f, 6.4f, -3.3f, Material.NEON_RED)));
      case "heal_on_kill" ->
          m(
              bg(
                  "canister",
                  3.5f, 9.4f, -2.6f,
                  b(2.8f, 8.4f, -3.1f, 4.2f, 10.4f, -2.1f, Material.MEDICAL),
                  b(3.0f, 8.6f, -3.3f, 4.0f, 10.2f, -3.1f, Material.TOXIC),
                  b(3.2f, 8.8f, -3.42f, 3.8f, 10.0f, -3.3f, Material.EMERALD),
                  b(2.9f, 9.6f, -3.45f, 4.1f, 9.9f, -3.35f, Material.MEDICAL),
                  b(3.25f, 9.2f, -3.45f, 3.75f, 10.1f, -3.35f, Material.MEDICAL)),
              bg(
                  "injector",
                  3.3f, 10.6f, -2.6f,
                  b(3.0f, 10.4f, -2.9f, 3.6f, 10.9f, -2.4f, Material.CHROME),
                  b(2.8f, 10.6f, -2.7f, 3.8f, 11.0f, -2.3f, Material.DARK_METAL),
                  b(3.2f, 10.9f, -2.6f, 3.4f, 11.1f, -2.4f, Material.CYAN)),
              bg(
                  "needle",
                  3.3f, 10.9f, -2.5f,
                  b(3.2f, 10.9f, -2.55f, 3.4f, 11.3f, -2.45f, Material.CHROME)),
              bh(
                  "glow",
                  3.5f, 9.4f, -3.45f,
                  b(2.85f, 8.5f, -3.5f, 4.15f, 10.3f, -3.4f, Material.EMERALD)));
      case "syn_lungs_bioplastic" ->
          m(
              bg(
                  "root",
                  0, 9.6f, -2.3f,
                  b(-0.8f, 9.0f, -2.7f, 0.8f, 11.2f, -2.0f, Material.CHROME),
                  b(-0.4f, 10.8f, -2.5f, 0.4f, 11.6f, -2.1f, Material.GUNMETAL)),
              bg(
                  "lung_l",
                  -0.6f, 8.4f, -2.4f,
                  b(-3.8f, 5.4f, -2.9f, -0.6f, 9.8f, -1.9f, Material.CERAMIC),
                  b(-3.4f, 5.8f, -3.1f, -1.0f, 9.4f, -2.9f, Material.PINK)),
              bg(
                  "lung_r",
                  0.6f, 8.4f, -2.4f,
                  b(0.6f, 5.4f, -2.9f, 3.8f, 9.8f, -1.9f, Material.CERAMIC),
                  b(1.0f, 5.8f, -3.1f, 3.4f, 9.4f, -2.9f, Material.PINK)),
              bg(
                  "vein_l",
                  -2.6f, 7.3f, -3.15f,
                  b(-3.2f, 6.4f, -3.2f, -3.0f, 8.8f, -3.1f, Material.ELECTRIC_BLUE),
                  b(-3.0f, 8.6f, -3.2f, -2.0f, 8.8f, -3.1f, Material.ELECTRIC_BLUE),
                  b(-2.6f, 5.8f, -3.2f, -2.4f, 7.2f, -3.1f, Material.CYAN)),
              bg(
                  "vein_r",
                  2.6f, 7.3f, -3.15f,
                  b(2.0f, 8.6f, -3.2f, 3.0f, 8.8f, -3.1f, Material.ELECTRIC_BLUE),
                  b(3.0f, 6.4f, -3.2f, 3.2f, 8.8f, -3.1f, Material.ELECTRIC_BLUE),
                  b(2.4f, 5.8f, -3.2f, 2.6f, 7.2f, -3.1f, Material.CYAN)),
              bh(
                  "glow",
                  0, 7.6f, -3.2f,
                  b(-3.6f, 5.6f, -3.25f, 3.6f, 9.6f, -3.15f, Material.CYAN)));
      case "subdermal_armor" ->
          m(
              bg(
                  "plate_chest",
                  0, 7.6f, -2.3f,
                  b(-3.8f, 7.8f, -2.7f, -0.4f, 10.0f, -2.0f, Material.CARBON),
                  b(0.4f, 7.8f, -2.7f, 3.8f, 10.0f, -2.0f, Material.CARBON),
                  b(-3.6f, 6.4f, -2.6f, -0.6f, 7.6f, -2.0f, Material.CARBON),
                  b(0.6f, 6.4f, -2.6f, 3.6f, 7.6f, -2.0f, Material.CARBON),
                  b(-3.0f, 5.2f, -2.5f, -0.8f, 6.2f, -2.0f, Material.CARBON),
                  b(0.8f, 5.2f, -2.5f, 3.0f, 6.2f, -2.0f, Material.CARBON),
                  b(-0.3f, 5.0f, -2.6f, 0.3f, 10.2f, -2.0f, Material.GUNMETAL),
                  b(-3.4f, 5.4f, -2.55f, -1.0f, 5.9f, -2.1f, Material.OLIVE),
                  b(1.0f, 5.4f, -2.55f, 3.4f, 5.9f, -2.1f, Material.OLIVE)),
              bg(
                  "plate_back",
                  0, 8.2f, 2.3f,
                  b(-3.6f, 6.6f, 2.0f, 3.6f, 9.8f, 2.6f, Material.CARBON)),
              bg(
                  "plate_shoulder_l",
                  -3.8f, 10.1f, 0f,
                  b(-5.0f, 9.0f, -2.0f, -3.8f, 11.2f, 2.0f, Material.CARBON),
                  b(-4.8f, 9.2f, -2.1f, -3.9f, 9.6f, 2.1f, Material.GUNMETAL)),
              bg(
                  "plate_shoulder_r",
                  3.8f, 10.1f, 0f,
                  b(3.8f, 9.0f, -2.0f, 5.0f, 11.2f, 2.0f, Material.CARBON),
                  b(3.9f, 9.2f, -2.1f, 4.8f, 9.6f, 2.1f, Material.GUNMETAL)),
              ra(
                  "plate_arm_r",
                  0, 1.4f, 0f,
                  b(-3.2f, 1.4f, -2.3f, 0.8f, 4.2f, 2.3f, Material.CARBON),
                  b(-3.3f, 3.6f, -2.0f, -3.1f, 4.0f, 2.0f, Material.GUNMETAL)),
              ra(
                  "plate_arm_l",
                  0, 1.4f, 0f,
                  b(-3.2f, 1.4f, -2.3f, 0.8f, 4.2f, 2.3f, Material.CARBON),
                  b(-3.3f, 3.6f, -2.0f, -3.1f, 4.0f, 2.0f, Material.GUNMETAL)),
              rl(
                  "plate_leg_r",
                  0, 2.0f, 0f,
                  b(-2.6f, 2.0f, -2.4f, 2.6f, 6.0f, 2.4f, Material.CARBON),
                  b(-2.7f, 5.6f, -2.0f, -2.5f, 5.9f, 2.0f, Material.GUNMETAL)),
              rl(
                  "plate_leg_l",
                  0, 2.0f, 0f,
                  b(-2.6f, 2.0f, -2.4f, 2.6f, 6.0f, 2.4f, Material.CARBON),
                  b(-2.7f, 5.6f, -2.0f, -2.5f, 5.9f, 2.0f, Material.GUNMETAL)));
      case "optical_camo" ->
          m(
              bg(
                  "emitter",
                  0f, 8.0f, -2.86f,
                  b(-0.9f, 7.6f, -3.1f, 0.9f, 8.4f, -2.7f, Material.CHROME),
                  b(-0.4f, 7.8f, -3.16f, 0.4f, 8.2f, -3.08f, Material.HOLO)),
              hh(
                  "shell_head",
                  0, -4f, 0f,
                  b(-4.3f, -8.3f, -4.3f, 4.3f, 0.1f, -4.1f, Material.HOLO),
                  b(-4.3f, -8.3f, 4.1f, 4.3f, 0.1f, 4.3f, Material.HOLO),
                  b(-4.3f, -8.3f, -4.1f, -4.1f, 0.1f, 4.1f, Material.HOLO),
                  b(4.1f, -8.3f, -4.1f, 4.3f, 0.1f, 4.1f, Material.HOLO),
                  b(-4.1f, -8.3f, -4.1f, 4.1f, -8.1f, 4.1f, Material.HOLO),
                  b(-4.1f, -8.3f, -4.1f, 4.1f, -8.2f, -3.9f, Material.CYAN)),
              bh(
                  "shell_body",
                  0, 6f, 0f,
                  b(-4.4f, 0.2f, -2.9f, 4.4f, 12.0f, -2.7f, Material.HOLO),
                  b(-4.4f, 0.2f, 2.7f, 4.4f, 12.0f, 2.9f, Material.HOLO),
                  b(-4.6f, 0.2f, -2.7f, -4.4f, 12.0f, 2.7f, Material.HOLO),
                  b(4.4f, 0.2f, -2.7f, 4.6f, 12.0f, 2.7f, Material.HOLO),
                  b(-4.4f, 0.0f, -2.7f, 4.4f, 0.2f, 2.7f, Material.HOLO),
                  b(-4.4f, 0.1f, -2.9f, 4.4f, 0.3f, -2.8f, Material.CYAN)),
              rah(
                  "shell_arm_r",
                  0, 4f, 0f,
                  b(-3.1f, -2.1f, -2.4f, 1.1f, 10.1f, -2.25f, Material.HOLO),
                  b(-3.1f, -2.1f, 2.25f, 1.1f, 10.1f, 2.4f, Material.HOLO),
                  b(-3.2f, -2.1f, -2.25f, -3.05f, 10.1f, 2.25f, Material.HOLO),
                  b(1.05f, -2.1f, -2.25f, 1.2f, 10.1f, 2.25f, Material.HOLO),
                  b(-3.1f, -2.1f, -2.3f, 1.1f, -2.0f, -2.2f, Material.CYAN)),
              lah(
                  "shell_arm_l",
                  0, 4f, 0f,
                  b(-3.1f, -2.1f, -2.4f, 1.1f, 10.1f, -2.25f, Material.HOLO),
                  b(-3.1f, -2.1f, 2.25f, 1.1f, 10.1f, 2.4f, Material.HOLO),
                  b(-3.2f, -2.1f, -2.25f, -3.05f, 10.1f, 2.25f, Material.HOLO),
                  b(1.05f, -2.1f, -2.25f, 1.2f, 10.1f, 2.25f, Material.HOLO),
                  b(-3.1f, -2.1f, -2.3f, 1.1f, -2.0f, -2.2f, Material.CYAN)),
              rlh(
                  "shell_leg_r",
                  0, 6f, 0f,
                  b(-2.2f, 0.0f, -2.4f, 2.2f, 12.2f, -2.25f, Material.HOLO),
                  b(-2.2f, 0.0f, 2.25f, 2.2f, 12.2f, 2.4f, Material.HOLO),
                  b(-2.3f, 0.0f, -2.25f, -2.2f, 12.2f, 2.25f, Material.HOLO),
                  b(2.2f, 0.0f, -2.25f, 2.3f, 12.2f, 2.25f, Material.HOLO),
                  b(-2.2f, 0.0f, -2.3f, 2.2f, 0.1f, -2.2f, Material.CYAN)),
              rlh(
                  "shell_leg_l",
                  0, 6f, 0f,
                  b(-2.2f, 0.0f, -2.4f, 2.2f, 12.2f, -2.25f, Material.HOLO),
                  b(-2.2f, 0.0f, 2.25f, 2.2f, 12.2f, 2.4f, Material.HOLO),
                  b(-2.3f, 0.0f, -2.25f, -2.2f, 12.2f, 2.25f, Material.HOLO),
                  b(2.2f, 0.0f, -2.25f, 2.3f, 12.2f, 2.25f, Material.HOLO),
                  b(-2.2f, 0.0f, -2.3f, 2.2f, 0.1f, -2.2f, Material.CYAN)));
      case "pain_editor" ->
          m(
              bg("chip_1", 0, 3.0f, 2.25f, b(-0.9f, 3.0f, 1.9f, 0.9f, 5.2f, 2.6f, Material.RUST)),
              bg("chip_2", 0, 5.2f, 2.25f, b(-0.9f, 5.2f, 1.9f, 0.9f, 7.4f, 2.6f, Material.RUST)),
              bg("chip_3", 0, 7.4f, 2.25f, b(-0.9f, 7.4f, 1.9f, 0.9f, 10.4f, 2.6f, Material.RUST)),
              bg(
                  "glow",
                  0, 7.1f, 2.85f,
                  b(-1.7f, 4.0f, 2.6f, 1.7f, 5.2f, 3.1f, Material.MUSTARD),
                  b(-1.7f, 5.8f, 2.6f, 1.7f, 7.0f, 3.1f, Material.MUSTARD),
                  b(-1.7f, 7.6f, 2.6f, 1.7f, 8.8f, 3.1f, Material.MUSTARD),
                  b(-1.7f, 9.4f, 2.6f, 1.7f, 10.2f, 3.1f, Material.MUSTARD),
                  b(-1.2f, 4.6f, 3.1f, -0.4f, 4.9f, 3.2f, Material.DARK_METAL),
                  b(0.4f, 6.4f, 3.1f, 1.2f, 6.7f, 3.2f, Material.DARK_METAL),
                  b(-1.0f, 8.2f, 3.1f, -0.2f, 8.5f, 3.2f, Material.DARK_METAL)),
              bg(
                  "clamp_1",
                  0, 5.0f, 2.35f,
                  b(-2.6f, 4.4f, 2.0f, -1.7f, 5.6f, 2.7f, Material.RUST),
                  b(1.7f, 4.4f, 2.0f, 2.6f, 5.6f, 2.7f, Material.RUST)),
              bg(
                  "clamp_2",
                  0, 7.0f, 2.35f,
                  b(-2.6f, 6.4f, 2.0f, -1.7f, 7.6f, 2.7f, Material.RUST),
                  b(1.7f, 6.4f, 2.0f, 2.6f, 7.6f, 2.7f, Material.RUST)),
              bg(
                  "clamp_3",
                  0, 8.8f, 2.35f,
                  b(-2.4f, 8.4f, 2.1f, -1.8f, 9.2f, 2.6f, Material.GUNMETAL),
                  b(1.8f, 8.4f, 2.1f, 2.4f, 9.2f, 2.6f, Material.GUNMETAL)));
      case "shock_n_awe" ->
          m(
              bg(
                  "root",
                  0, 7.6f, -2.8f,
                  b(-0.4f, 5.4f, -3.0f, 0.4f, 9.8f, -2.4f, Material.CHROME),
                  b(-0.6f, 6.6f, -3.1f, 0.6f, 6.9f, -3.0f, Material.YELLOW),
                  b(-0.6f, 8.0f, -3.1f, 0.6f, 8.3f, -3.0f, Material.YELLOW),
                  b(-0.3f, 7.2f, -3.15f, 0.3f, 7.8f, -3.1f, Material.CYAN)),
              bg(
                  "plate_l",
                  -0.6f, 7.8f, -2.4f,
                  b(-3.8f, 6.0f, -2.8f, -0.6f, 9.6f, -2.0f, Material.BLUE_STEEL)),
              bg(
                  "plate_r",
                  0.6f, 7.8f, -2.4f,
                  b(0.6f, 6.0f, -2.8f, 3.8f, 9.6f, -2.0f, Material.BLUE_STEEL)),
              bg(
                  "coil_l",
                  -2.1f, 10.5f, -2.4f,
                  b(-3.0f, 9.6f, -2.7f, -2.2f, 11.1f, -2.0f, Material.CYAN),
                  b(-2.0f, 9.6f, -2.7f, -1.2f, 11.1f, -2.0f, Material.CYAN),
                  b(-3.0f, 11.1f, -2.6f, -2.2f, 11.5f, -2.1f, Material.YELLOW),
                  b(-2.0f, 11.1f, -2.6f, -1.2f, 11.5f, -2.1f, Material.YELLOW),
                  b(-3.2f, 9.4f, -2.9f, -2.0f, 9.7f, -2.7f, Material.NEON_YELLOW)),
              bg(
                  "coil_r",
                  2.1f, 10.5f, -2.4f,
                  b(1.2f, 9.6f, -2.7f, 2.0f, 11.1f, -2.0f, Material.CYAN),
                  b(2.2f, 9.6f, -2.7f, 3.0f, 11.1f, -2.0f, Material.CYAN),
                  b(1.2f, 11.1f, -2.6f, 2.0f, 11.5f, -2.1f, Material.YELLOW),
                  b(2.2f, 11.1f, -2.6f, 3.0f, 11.5f, -2.1f, Material.YELLOW),
                  b(2.0f, 9.4f, -2.9f, 3.2f, 9.7f, -2.7f, Material.NEON_YELLOW)),
              bh("arc_1", -1.1f, 9.5f, -2.9f, b(-1.8f, 9.4f, -3.0f, -0.4f, 9.6f, -2.8f, Material.ELECTRIC_BLUE)),
              bh("arc_2", 1.1f, 9.5f, -2.9f, b(0.4f, 9.4f, -3.0f, 1.8f, 9.6f, -2.8f, Material.ELECTRIC_BLUE)),
              bh("arc_3", -0.5f, 10.3f, -2.95f, b(-1.0f, 10.2f, -3.05f, 0.0f, 10.4f, -2.85f, Material.ELECTRIC_BLUE)),
              bh("arc_4", 0.5f, 11.1f, -2.95f, b(0.0f, 11.0f, -3.05f, 1.0f, 11.2f, -2.85f, Material.ELECTRIC_BLUE)));
      case "fireproof_grounding" ->
          m(
              bg(
                  "coat",
                  0, 7.0f, -2.4f,
                  b(-3.8f, 5.0f, -2.7f, 3.8f, 9.6f, -2.0f, Material.BLOOD_RED),
                  b(-2.8f, 4.4f, -2.8f, 2.8f, 7.0f, -2.5f, Material.GUNMETAL),
                  b(-4.2f, 4.2f, -2.5f, 4.2f, 4.7f, -2.1f, Material.RUBBER),
                  b(-4.0f, 9.4f, -2.4f, 4.0f, 9.9f, -2.0f, Material.RUBBER),
                  b(-3.0f, 7.4f, -2.8f, 3.0f, 7.6f, -2.7f, Material.CARBON),
                  b(-3.0f, 8.1f, -2.8f, 3.0f, 8.3f, -2.7f, Material.CARBON),
                  b(-3.0f, 8.8f, -2.8f, 3.0f, 9.0f, -2.7f, Material.CARBON),
                  b(-2.0f, 9.9f, -2.3f, -1.4f, 10.3f, -2.1f, Material.RUST),
                  b(1.4f, 9.9f, -2.3f, 2.0f, 10.3f, -2.1f, Material.RUST)),
              bg("ground_l", -1.5f, 4.9f, -2.85f,
                  b(-1.8f, 4.6f, -2.9f, -1.2f, 5.2f, -2.8f, Material.CHROME)),
              bg("ground_r", 1.5f, 4.9f, -2.85f,
                  b(1.2f, 4.6f, -2.9f, 1.8f, 5.2f, -2.8f, Material.CHROME)),
              bh("heat_1", -1.0f, 6.15f, -2.89f,
                  b(-1.2f, 6.0f, -2.92f, -0.8f, 6.3f, -2.86f, Material.NEON_ORANGE)),
              bh("heat_2", 0.4f, 6.95f, -2.89f,
                  b(0.2f, 6.8f, -2.92f, 0.6f, 7.1f, -2.86f, Material.NEON_ORANGE)),
              bh("heat_3", -0.4f, 7.75f, -2.89f,
                  b(-0.6f, 7.6f, -2.92f, -0.2f, 7.9f, -2.86f, Material.NEON_ORANGE)));
      case "titanium_bones" ->
          m(
              both(
                  ra(
                      "strip_arm_r",
                      -1.2f, 4.5f, 0f,
                      b(-3.3f, 1.2f, -1.4f, -2.6f, 8.2f, 1.4f, Material.TITANIUM),
                      b(0.2f, 1.2f, -1.4f, 0.9f, 8.2f, 1.4f, Material.TITANIUM),
                      b(-3.5f, 0.4f, -2.0f, -2.8f, 1.6f, 2.0f, Material.CHROME),
                      b(-3.4f, 1.8f, -1.5f, -3.2f, 7.8f, 1.5f, Material.CHROME),
                      b(-3.3f, 3.0f, -1.5f, -2.9f, 3.4f, 1.5f, Material.GUNMETAL),
                      b(-3.3f, 6.0f, -1.5f, -2.9f, 6.4f, 1.5f, Material.GUNMETAL),
                      b(-1.2f, 9.2f, -1.5f, 0.2f, 10.2f, 1.5f, Material.TITANIUM),
                      b(-0.8f, 9.6f, -1.6f, -0.2f, 10.0f, 1.6f, Material.CHROME)),
                  rah(
                      "glint_arm_r",
                      -3.39f, 5.9f, 0f,
                      b(-3.42f, 5.6f, -1.5f, -3.36f, 6.2f, 1.5f, Material.FLASH)),
                  rl(
                      "strip_leg_r",
                      0f, 6.9f, 0f,
                      b(-2.7f, 3.0f, -1.5f, -1.9f, 10.2f, 1.5f, Material.TITANIUM),
                      b(1.9f, 3.0f, -1.5f, 2.7f, 10.2f, 1.5f, Material.TITANIUM),
                      b(-2.9f, 2.0f, -2.2f, -2.2f, 3.4f, 2.2f, Material.CHROME),
                      b(-2.8f, 4.0f, -1.6f, -2.6f, 9.8f, 1.6f, Material.CHROME),
                      b(-2.7f, 6.0f, -1.6f, -2.3f, 6.4f, 1.6f, Material.GUNMETAL),
                      b(-2.7f, 8.0f, -1.6f, -2.3f, 8.4f, 1.6f, Material.GUNMETAL),
                      b(-1.8f, 9.8f, -1.3f, -1.2f, 10.6f, 1.3f, Material.TITANIUM),
                      b(1.2f, 9.8f, -1.3f, 1.8f, 10.6f, 1.3f, Material.TITANIUM)),
                  rlh(
                      "glint_leg_r",
                      -2.87f, 6.9f, 0f,
                      b(-2.9f, 6.6f, -1.5f, -2.84f, 7.2f, 1.5f, Material.FLASH))));
      case "dense_marrow" ->
          m(
              both(
                  ra(
                      "cyl_arm_r",
                      -1.2f, 0.8f, -0.5f,
                      b(-3.5f, 0.0f, -1.6f, -2.9f, 3.4f, 0.6f, Material.GUNMETAL),
                      b(-3.55f, 0.6f, -1.4f, -3.45f, 2.6f, 0.4f, Material.CARBON),
                      b(-3.6f, 3.2f, -1.8f, -2.8f, 4.2f, 0.8f, Material.RUST),
                      b(0.3f, 0.6f, -0.9f, 0.9f, 3.0f, 0.3f, Material.GUNMETAL),
                      b(0.35f, 1.0f, -0.7f, 0.45f, 2.4f, -0.1f, Material.CARBON),
                      b(-3.8f, 1.2f, -1.2f, -3.5f, 2.0f, 0.2f, Material.CHROME)),
                  rah(
                      "fluid_arm_r",
                      -3.25f, 1.75f, -0.5f,
                      b(-3.5f, 1.4f, -1.3f, -3.0f, 2.1f, 0.3f, Material.SMOKE)),
                  rl(
                      "cyl_leg_r",
                      0f, 2.8f, 2.1f,
                      b(-3.0f, 1.0f, 1.5f, -2.2f, 4.4f, 2.5f, Material.GUNMETAL),
                      b(-2.9f, 1.4f, 2.5f, -2.3f, 4.0f, 2.7f, Material.CARBON),
                      b(-3.1f, 4.2f, 1.3f, -2.1f, 5.2f, 2.7f, Material.RUST),
                      b(2.2f, 1.0f, 1.5f, 3.0f, 4.4f, 2.5f, Material.GUNMETAL),
                      b(2.3f, 1.4f, 2.5f, 2.9f, 4.0f, 2.7f, Material.CARBON),
                      b(-2.8f, 4.4f, 2.5f, -2.4f, 5.0f, 2.8f, Material.CHROME)),
                  rlh(
                      "fluid_leg_r",
                      -2.65f, 2.25f, 2.1f,
                      b(-2.95f, 1.8f, 1.4f, -2.35f, 2.7f, 2.6f, Material.SMOKE))));
      case "epimorphic_skeleton" ->
          m(
              bg(
                  "sternum",
                  0f, 7.6f, -2.7f,
                  b(-0.8f, 4.0f, -3.1f, 0.8f, 11.0f, -2.3f, Material.BRONZE),
                  b(-1.2f, 3.5f, 2.0f, 1.2f, 11.0f, 2.7f, Material.BRONZE),
                  b(-0.4f, 10.2f, -3.2f, 0.4f, 10.8f, -3.0f, Material.CHROME),
                  b(-0.4f, 7.4f, -3.2f, 0.4f, 8.0f, -3.0f, Material.CHROME),
                  b(-0.4f, 4.8f, -3.2f, 0.4f, 5.4f, -3.0f, Material.CHROME)),
              bg(
                  "rib_r_1",
                  0f, 10.1f, 2.35f,
                  b(0.8f, 10.6f, -2.9f, 4.2f, 11.2f, -2.3f, Material.GOLD),
                  b(3.8f, 10.6f, -2.8f, 4.2f, 11.2f, -0.3f, Material.BRONZE),
                  b(0.8f, 9.4f, -3.0f, 3.8f, 9.85f, -2.4f, Material.GOLD),
                  b(3.8f, 9.4f, -2.8f, 4.2f, 9.85f, -0.3f, Material.BRONZE)),
              bg(
                  "rib_r_2",
                  0f, 8.4f, 2.35f,
                  b(0.8f, 8.1f, -3.0f, 3.8f, 8.55f, -2.4f, Material.GOLD),
                  b(3.8f, 8.1f, -2.8f, 4.2f, 8.55f, -0.3f, Material.BRONZE),
                  b(0.8f, 6.8f, -3.0f, 3.8f, 7.25f, -2.4f, Material.GOLD),
                  b(3.8f, 6.8f, -2.8f, 4.2f, 7.25f, -0.3f, Material.BRONZE)),
              bg(
                  "rib_r_3",
                  0f, 5.7f, 2.35f,
                  b(0.8f, 5.5f, -3.0f, 3.8f, 5.95f, -2.4f, Material.GOLD),
                  b(3.8f, 5.5f, -2.8f, 4.2f, 5.95f, -0.3f, Material.BRONZE)),
              bg(
                  "rib_l_1",
                  0f, 10.1f, 2.35f,
                  b(-4.2f, 10.6f, -2.9f, -0.8f, 11.2f, -2.3f, Material.GOLD),
                  b(-4.2f, 10.6f, -2.8f, -3.8f, 11.2f, -0.3f, Material.BRONZE),
                  b(-3.8f, 9.4f, -3.0f, -0.8f, 9.85f, -2.4f, Material.GOLD),
                  b(-4.2f, 9.4f, -2.8f, -3.8f, 9.85f, -0.3f, Material.BRONZE)),
              bg(
                  "rib_l_2",
                  0f, 8.4f, 2.35f,
                  b(-3.8f, 8.1f, -3.0f, -0.8f, 8.55f, -2.4f, Material.GOLD),
                  b(-4.2f, 8.1f, -2.8f, -3.8f, 8.55f, -0.3f, Material.BRONZE),
                  b(-3.8f, 6.8f, -3.0f, -0.8f, 7.25f, -2.4f, Material.GOLD),
                  b(-4.2f, 6.8f, -2.8f, -3.8f, 7.25f, -0.3f, Material.BRONZE)),
              bg(
                  "rib_l_3",
                  0f, 5.7f, 2.35f,
                  b(-3.8f, 5.5f, -3.0f, -0.8f, 5.95f, -2.4f, Material.GOLD),
                  b(-4.2f, 5.5f, -2.8f, -3.8f, 5.95f, -0.3f, Material.BRONZE)),
              bh(
                  "glow",
                  0f, 7.6f, -3.28f,
                  b(-0.9f, 4.2f, -3.34f, 0.9f, 11.0f, -3.12f, Material.GOLD)));
      case "bionic_joints" ->
          m(
              both(
                  ra(
                      "joint_arm_r",
                      -1.2f, 1.4f, 0f,
                      b(-3.5f, 0.4f, -2.3f, 1.2f, 2.9f, 2.3f, Material.GUNMETAL),
                      b(-3.55f, 0.8f, -2.35f, 1.25f, 1.3f, 2.35f, Material.RUBBER),
                      b(-3.9f, 1.0f, -1.2f, -3.5f, 2.6f, 1.2f, Material.CHROME),
                      b(1.2f, 1.0f, -1.2f, 1.6f, 2.6f, 1.2f, Material.CHROME),
                      b(-1.2f, -0.2f, -2.4f, 0.4f, 0.6f, 2.4f, Material.DARK_METAL),
                      b(-3.0f, 2.9f, -2.1f, 0.8f, 3.4f, 2.1f, Material.CHROME)),
                  rl(
                      "joint_leg_r",
                      0f, 2.6f, 0f,
                      b(-2.5f, 1.2f, -2.3f, 2.5f, 4.0f, 2.3f, Material.GUNMETAL),
                      b(-2.55f, 1.6f, -2.35f, 2.55f, 2.1f, 2.35f, Material.RUBBER),
                      b(-3.0f, 1.4f, -1.2f, -2.5f, 3.4f, 1.2f, Material.CHROME),
                      b(2.5f, 1.4f, -1.2f, 3.0f, 3.4f, 1.2f, Material.CHROME),
                      b(-1.6f, 1.4f, -2.8f, 1.6f, 3.6f, -2.3f, Material.CHROME),
                      b(-2.2f, 4.0f, -2.1f, 2.2f, 4.5f, 2.1f, Material.DARK_METAL))));
      case "spring_joints" ->
          m(
              both(
                  rl(
                      "spring_r",
                      0f, 11.9f, 2.2f,
                      b(-1.6f, 4.6f, 1.6f, 1.6f, 5.2f, 2.8f, Material.GUNMETAL),
                      b(-0.3f, 4.6f, 1.9f, 0.3f, 9.4f, 2.5f, Material.CHROME),
                      b(-1.8f, 9.2f, 1.5f, 1.8f, 9.8f, 2.9f, Material.GUNMETAL),
                      b(-0.2f, 10.8f, 1.8f, 0.2f, 11.6f, 2.6f, Material.CHROME),
                      b(-1.6f, 11.4f, 1.5f, 1.6f, 11.9f, 2.9f, Material.GUNMETAL)),
                  rl(
                      "coil_r",
                      0f, 11.9f, 2.2f,
                      b(-1.2f, 5.2f, 1.8f, 1.2f, 5.6f, 2.6f, Material.YELLOW),
                      b(-1.5f, 5.6f, 1.7f, 1.5f, 6.0f, 2.7f, Material.CHROME),
                      b(-1.2f, 6.0f, 1.8f, 1.2f, 6.4f, 2.6f, Material.YELLOW),
                      b(-1.5f, 6.4f, 1.7f, 1.5f, 6.8f, 2.7f, Material.CHROME),
                      b(-1.2f, 6.8f, 1.8f, 1.2f, 7.2f, 2.6f, Material.YELLOW),
                      b(-1.5f, 7.2f, 1.7f, 1.5f, 7.6f, 2.7f, Material.CHROME),
                      b(-1.2f, 7.6f, 1.8f, 1.2f, 8.0f, 2.6f, Material.YELLOW),
                      b(-1.0f, 9.8f, 1.7f, 1.0f, 10.4f, 2.7f, Material.YELLOW),
                      b(-1.3f, 10.4f, 1.6f, 1.3f, 10.8f, 2.8f, Material.CHROME)),
                  rl(
                      "accent_r",
                      1.45f, 8.4f, 1.62f,
                      b(1.3f, 8.2f, 1.55f, 1.6f, 8.6f, 1.7f, Material.YELLOW))));
      case "cyber_legs" ->
          m(
              both(
                  rl(
                      "thigh_r",
                      0f, 0.8f, 0f,
                      b(-2.4f, 0.8f, -2.4f, 2.4f, 5.6f, 2.4f, Material.CHROME),
                      b(-2.5f, 1.2f, -2.5f, -2.3f, 5.4f, -1.0f, Material.BLUE_STEEL),
                      b(2.3f, 1.2f, -2.5f, 2.5f, 5.4f, -1.0f, Material.BLUE_STEEL),
                      b(-2.4f, 2.0f, -2.5f, -1.6f, 2.4f, -2.4f, Material.CYAN),
                      b(1.6f, 2.0f, -2.5f, 2.4f, 2.4f, -2.4f, Material.CYAN)),
                  rl(
                      "knee_r",
                      0f, 6.0f, 0f,
                      b(-2.6f, 5.2f, -2.6f, 2.6f, 6.8f, 2.6f, Material.DARK_METAL),
                      b(-2.7f, 5.7f, -1.2f, -2.6f, 6.3f, 1.2f, Material.CHROME),
                      b(2.6f, 5.7f, -1.2f, 2.7f, 6.3f, 1.2f, Material.CHROME)),
                  rl(
                      "shin_r",
                      0f, 5.4f, 0f,
                      b(-2.2f, 5.4f, -2.3f, 2.2f, 11.6f, 2.3f, Material.TITANIUM),
                      b(-2.2f, 6.6f, -2.4f, -1.6f, 9.8f, -2.3f, Material.BLUE_STEEL),
                      b(1.6f, 6.6f, -2.4f, 2.2f, 9.8f, -2.3f, Material.BLUE_STEEL)),
                  rl(
                      "foot_r",
                      0f, 11.6f, 0f,
                      b(-2.3f, 10.8f, -2.4f, 2.3f, 11.8f, 2.4f, Material.GUNMETAL),
                      b(-2.0f, 11.6f, -2.2f, 2.0f, 12.2f, 2.2f, Material.CHROME)),
                  rl(
                      "piston_r",
                      0f, 6.8f, 2.5f,
                      b(-1.4f, 6.8f, 2.3f, 0.6f, 10.6f, 2.7f, Material.CHROME))));
      case "reinforced_tendons" ->
          m(
              both(
                  rl(
                      "tendon_r",
                      0f, 11.3f, 2.4f,
                      b(-1.3f, 6.4f, 2.1f, -0.7f, 11.2f, 2.8f, Material.CHROME),
                      b(0.7f, 6.4f, 2.1f, 1.3f, 11.2f, 2.8f, Material.CHROME),
                      b(-1.8f, 6.0f, 1.8f, 1.8f, 6.8f, 3.0f, Material.GUNMETAL),
                      b(-1.9f, 10.8f, 1.7f, 1.9f, 11.8f, 3.0f, Material.GUNMETAL)),
                  rl(
                      "string_r",
                      0f, 11.0f, 2.6f,
                      b(-0.25f, 6.8f, 2.3f, 0.25f, 11.0f, 2.9f, Material.NEON_RED)),
                  rl(
                      "glow_r",
                      0f, 9.1f, 2.9f,
                      b(-1.6f, 7.2f, 2.8f, -1.2f, 7.8f, 3.0f, Material.GOLD),
                      b(1.2f, 7.2f, 2.8f, 1.6f, 7.8f, 3.0f, Material.GOLD),
                      b(-1.6f, 9.8f, 2.8f, -1.2f, 10.4f, 3.0f, Material.GOLD),
                      b(1.2f, 9.8f, 2.8f, 1.6f, 10.4f, 3.0f, Material.GOLD))));
      case "fortified_ankles" ->
          m(
              both(
                  rl(
                      "ankle_r",
                      0f, 9.4f, 0f,
                      b(-2.8f, 9.4f, -2.8f, 2.8f, 11.6f, 2.8f, Material.GUNMETAL),
                      b(-3.2f, 11.2f, -4.2f, 3.2f, 12.2f, 2.4f, Material.DARK_METAL),
                      b(-2.8f, 11.3f, -4.7f, 2.8f, 12.0f, -4.1f, Material.GOLD)),
                  rl(
                      "piston_r",
                      0f, 11.4f, 0f,
                      b(-3.3f, 10.0f, -2.0f, -2.8f, 11.5f, 2.0f, Material.MUSTARD),
                      b(2.8f, 10.0f, -2.0f, 3.3f, 11.5f, 2.0f, Material.MUSTARD),
                      b(-2.1f, 9.6f, -1.0f, -1.3f, 11.2f, 0.8f, Material.CHROME),
                      b(1.3f, 9.6f, -1.0f, 2.1f, 11.2f, 0.8f, Material.CHROME),
                      b(-2.1f, 10.4f, -1.1f, -1.3f, 10.7f, 0.9f, Material.GOLD),
                      b(1.3f, 10.4f, -1.1f, 2.1f, 10.7f, 0.9f, Material.GOLD)),
                  rl(
                      "sole_r",
                      0f, 11.8f, 0f,
                      b(-2.6f, 11.8f, -4.0f, 2.6f, 12.3f, 2.2f, Material.GUNMETAL),
                      b(-2.8f, 11.4f, 1.8f, 2.8f, 12.2f, 2.8f, Material.CHROME))));
      case "lynx_paws" ->
          m(
              both(
                  rl(
                      "paw_r",
                      0f, 11.3f, 0f,
                      b(-2.6f, 11.3f, -4.4f, 2.6f, 12.3f, 2.2f, Material.CARBON),
                      b(-2.3f, 9.8f, -2.0f, 2.3f, 11.5f, 2.0f, Material.RUBBER),
                      b(-2.7f, 11.3f, -3.2f, -2.6f, 12.2f, 1.0f, Material.DARK_PURPLE),
                      b(2.6f, 11.3f, -3.2f, 2.7f, 12.2f, 1.0f, Material.DARK_PURPLE),
                      b(-1.8f, 12.2f, -2.0f, 1.8f, 12.5f, 0.8f, Material.CARBON),
                      b(-0.4f, 12.3f, 2.0f, 0.4f, 12.6f, 2.6f, Material.DARK_PURPLE)),
                  rl(
                      "toe_1_r",
                      -1.8f, 11.4f, -4.3f,
                      b(-2.4f, 11.4f, -4.8f, -1.2f, 12.1f, -4.3f, Material.CARBON),
                      b(-2.4f, 12.0f, -4.9f, -1.2f, 12.2f, -4.4f, Material.DARK_PURPLE)),
                  rl(
                      "toe_2_r",
                      0f, 11.4f, -4.3f,
                      b(-0.6f, 11.4f, -4.8f, 0.6f, 12.1f, -4.3f, Material.CARBON),
                      b(-0.6f, 12.0f, -4.9f, 0.6f, 12.2f, -4.4f, Material.DARK_PURPLE)),
                  rl(
                      "toe_3_r",
                      1.8f, 11.4f, -4.3f,
                      b(1.2f, 11.4f, -4.8f, 2.4f, 12.1f, -4.3f, Material.CARBON),
                      b(1.2f, 12.0f, -4.9f, 2.4f, 12.2f, -4.4f, Material.DARK_PURPLE)),
                  rl(
                      "pad_r",
                      0f, 11.2f, -3.0f,
                      b(-2.2f, 11.1f, -3.9f, 2.2f, 11.3f, -2.1f, Material.DARK_PURPLE))));
      case "jenkins_tendons" ->
          m(
              both(
                  rl(
                      "tendon_r",
                      -2.75f, 6.0f, 0f,
                      b(-3.1f, 2.0f, -1.8f, -2.4f, 6.2f, 2.0f, Material.CHROME),
                      b(-3.6f, 2.6f, -0.6f, -3.1f, 5.4f, 1.8f, Material.BLUE_STEEL),
                      b(-3.0f, 6.0f, -1.6f, -2.3f, 10.0f, 1.8f, Material.TITANIUM),
                      b(-3.5f, 6.6f, -0.4f, -3.0f, 9.4f, 1.6f, Material.BLUE_STEEL),
                      b(-3.05f, 7.0f, -1.7f, -2.25f, 9.4f, -1.6f, Material.CYAN),
                      b(-2.8f, 6.2f, 1.6f, -2.4f, 9.8f, 2.0f, Material.CHROME)),
                  rl(
                      "pulse_1_r",
                      -2.75f, 2.8f, -1.85f,
                      b(-3.16f, 2.4f, -1.9f, -2.35f, 3.2f, -1.8f, Material.MEDICAL)),
                  rl(
                      "pulse_2_r",
                      -2.65f, 4.6f, -1.75f,
                      b(-3.06f, 4.2f, -1.8f, -2.25f, 5.0f, -1.7f, Material.CYAN)),
                  rl(
                      "pulse_3_r",
                      -2.75f, 5.8f, -1.85f,
                      b(-3.15f, 5.4f, -1.9f, -2.35f, 6.2f, -1.8f, Material.NEON_RED)),
                  rl(
                      "pulse_4_r",
                      -2.65f, 7.8f, -1.75f,
                      b(-3.06f, 7.4f, -1.8f, -2.25f, 8.2f, -1.7f, Material.CYAN)),
                  rl(
                      "anchor_r",
                      -2.45f, 1.9f, 0.1f,
                      b(-2.6f, 1.6f, -1.0f, -2.2f, 2.2f, 1.2f, Material.GUNMETAL),
                      b(-2.7f, 9.8f, -1.0f, -2.3f, 10.4f, 1.2f, Material.GUNMETAL))));
      case "monowire" ->
          m(
              ra(
                  "housing",
                  -1.6f, 10.9f, 0f,
                  b(-2.8f, 8.0f, -2.2f, 0.8f, 9.4f, 2.2f, Material.CARBON),
                  b(-2.4f, 9.4f, -2.0f, 0.4f, 10.6f, 2.0f, Material.GUNMETAL),
                  b(-1.8f, 10.6f, -1.4f, -0.2f, 11.4f, 1.4f, Material.NEON_ORANGE),
                  b(-2.0f, 8.4f, -2.3f, -1.2f, 9.2f, -2.2f, Material.CYAN)),
              ra(
                  "wire_1",
                  -0.2f, 11.4f, 0.2f,
                  b(-0.6f, 11.4f, -0.2f, 0.2f, 11.925f, 0.6f, Material.NEON_ORANGE)),
              ra(
                  "wire_2",
                  -0.2f, 11.925f, 0.2f,
                  b(-0.6f, 11.925f, -0.2f, 0.2f, 12.45f, 0.6f, Material.NEON_ORANGE)),
              ra(
                  "wire_3",
                  -0.2f, 12.45f, 0.2f,
                  b(-0.6f, 12.45f, -0.2f, 0.2f, 12.975f, 0.6f, Material.NEON_ORANGE)),
              ra(
                  "wire_4",
                  -0.2f, 12.975f, 0.2f,
                  b(-0.6f, 12.975f, -0.2f, 0.2f, 13.5f, 0.6f, Material.NEON_ORANGE)),
              ra(
                  "wire_5",
                  -0.2f, 13.5f, 0.2f,
                  b(-0.6f, 13.5f, -0.2f, 0.2f, 14.025f, 0.6f, Material.NEON_ORANGE)),
              ra(
                  "wire_6",
                  -0.2f, 14.025f, 0.2f,
                  b(-0.6f, 14.025f, -0.2f, 0.2f, 14.55f, 0.6f, Material.NEON_ORANGE)),
              ra(
                  "wire_7",
                  -0.2f, 14.55f, 0.2f,
                  b(-0.6f, 14.55f, -0.2f, 0.2f, 15.075f, 0.6f, Material.NEON_ORANGE)),
              ra(
                  "wire_8",
                  -0.2f, 15.075f, 0.2f,
                  b(-0.6f, 15.075f, -0.2f, 0.2f, 15.6f, 0.6f, Material.NEON_ORANGE),
                  b(-0.65f, 15.2f, -0.25f, 0.25f, 15.6f, 0.65f, Material.NEON_RED)));
      default -> null;
    };
  }

  public static boolean isLeft(Part part) {
    return part == Part.LEFT_ARM || part == Part.LEFT_LEG;
  }

  private static Model m(Group... groups) {
    return new Model(List.of(groups));
  }

  private static Group g(Part part, String bone, float x, float y, float z, Box... boxes) {
    return new Group(part, false, bone, new Vec3(x, y, z), List.of(boxes));
  }

  private static Group bg(String bone, float x, float y, float z, Box... boxes) {
    return new Group(Part.BODY, false, bone, new Vec3(x, y, z), List.of(boxes));
  }

  private static Group bh(String bone, float x, float y, float z, Box... boxes) {
    return new Group(Part.BODY, false, bone, new Vec3(x, y, z), List.of(boxes), true);
  }

  private static Group hg(String bone, float x, float y, float z, Box... boxes) {
    return new Group(Part.HEAD, false, bone, new Vec3(x, y, z), List.of(boxes));
  }

  private static Group hh(String bone, float x, float y, float z, Box... boxes) {
    return new Group(Part.HEAD, false, bone, new Vec3(x, y, z), List.of(boxes), true);
  }

  private static Group ra(String bone, float x, float y, float z, Box... boxes) {
    return new Group(Part.RIGHT_ARM, false, bone, new Vec3(x, y, z), List.of(boxes));
  }

  private static Group rah(String bone, float x, float y, float z, Box... boxes) {
    return new Group(Part.RIGHT_ARM, false, bone, new Vec3(x, y, z), List.of(boxes), true);
  }

  private static Group la(String bone, float x, float y, float z, Box... boxes) {
    return new Group(Part.LEFT_ARM, true, bone, new Vec3(x, y, z), List.of(boxes));
  }

  private static Group lah(String bone, float x, float y, float z, Box... boxes) {
    return new Group(Part.LEFT_ARM, true, bone, new Vec3(x, y, z), List.of(boxes), true);
  }

  private static Group rl(String bone, float x, float y, float z, Box... boxes) {
    return new Group(Part.RIGHT_LEG, false, bone, new Vec3(x, y, z), List.of(boxes));
  }

  private static Group rlh(String bone, float x, float y, float z, Box... boxes) {
    return new Group(Part.RIGHT_LEG, false, bone, new Vec3(x, y, z), List.of(boxes), true);
  }

  private static Group[] both(Group... right) {
    Group[] all = new Group[right.length * 2];
    for (int i = 0; i < right.length; i++) {
      Group group = right[i];
      all[i] = group;
      Part left = group.part() == Part.RIGHT_ARM ? Part.LEFT_ARM : Part.LEFT_LEG;
      String bone =
          group.bone().endsWith("_r")
              ? group.bone().substring(0, group.bone().length() - 2) + "_l"
              : group.bone() + "_l";
      all[right.length + i] =
          new Group(left, true, bone, group.pivot(), group.boxes(), group.hidden());
    }
    return all;
  }

  private static Box b(
      float x0, float y0, float z0, float x1, float y1, float z1, Material material) {
    return new Box(x0, y0, z0, x1, y1, z1, material);
  }

  private ImplantModels() {}
}
