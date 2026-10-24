package com.cybercraft.cyberware;

import java.util.List;

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
    GLASS
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

  public record Group(Part part, boolean mirror, List<Box> boxes) {
    public Group {
      boxes = List.copyOf(boxes);
      if (part == null || boxes.isEmpty()) throw new IllegalArgumentException("Empty group");
    }
  }

  public record Model(List<Group> groups) {
    public Model {
      groups = List.copyOf(groups);
      if (groups.isEmpty()) throw new IllegalArgumentException("Empty model");
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
          body(
              b(-3.5f, 5, 2.0f, 3.5f, 10, 2.8f, Material.INDUSTRIAL),
              b(-3.0f, 5.4f, 2.8f, 3.0f, 9.6f, 3.05f, Material.DARK_METAL),
              b(-2.6f, 6.0f, 3.05f, 2.6f, 9.0f, 3.2f, Material.CIRCUIT),
              b(-2.2f, 7.4f, 3.2f, -1.4f, 8.0f, 3.35f, Material.CYAN),
              b(-0.9f, 7.4f, 3.2f, -0.1f, 8.0f, 3.35f, Material.CYAN),
              b(0.4f, 7.4f, 3.2f, 1.2f, 8.0f, 3.35f, Material.CYAN),
              b(1.7f, 7.4f, 3.2f, 2.5f, 8.0f, 3.35f, Material.CYAN),
              b(-4.3f, 5.2f, 2.2f, -3.5f, 9.8f, 2.8f, Material.GUNMETAL),
              b(3.5f, 5.2f, 2.2f, 4.3f, 9.8f, 2.8f, Material.GUNMETAL),
              b(-1.2f, 10.0f, 2.1f, 1.2f, 10.8f, 3.0f, Material.INDUSTRIAL),
              b(-2.4f, 5.8f, 1.9f, -1.8f, 6.4f, 2.0f, Material.MUSTARD),
              b(1.8f, 5.8f, 1.9f, 2.4f, 6.4f, 2.0f, Material.MUSTARD));
      case "sandevistan" ->
          body(
              b(-1.0f, 1.0f, 2.0f, 1.0f, 11.0f, 2.7f, Material.BRONZE),
              b(-1.7f, 2.0f, 2.7f, 1.7f, 3.3f, 3.4f, Material.BRONZE),
              b(-0.5f, 2.2f, 3.4f, 0.5f, 3.1f, 3.55f, Material.NEON_ORANGE),
              b(-1.7f, 3.9f, 2.7f, 1.7f, 5.2f, 3.4f, Material.BRONZE),
              b(-0.5f, 4.1f, 3.4f, 0.5f, 5.0f, 3.55f, Material.NEON_ORANGE),
              b(-1.7f, 5.8f, 2.7f, 1.7f, 7.1f, 3.4f, Material.BRONZE),
              b(-0.5f, 6.0f, 3.4f, 0.5f, 6.9f, 3.55f, Material.NEON_ORANGE),
              b(-1.7f, 7.7f, 2.7f, 1.7f, 9.0f, 3.4f, Material.BRONZE),
              b(-0.5f, 7.9f, 3.4f, 0.5f, 8.8f, 3.55f, Material.NEON_ORANGE),
              b(-1.7f, 9.6f, 2.7f, 1.7f, 10.7f, 3.4f, Material.BRONZE),
              b(-0.5f, 9.8f, 3.4f, 0.5f, 10.5f, 3.55f, Material.NEON_ORANGE),
              b(-2.2f, 10.8f, 1.9f, 2.2f, 12.0f, 2.9f, Material.GOLD),
              b(-2.1f, 2.5f, 2.1f, -1.4f, 9.8f, 2.7f, Material.COPPER),
              b(1.4f, 2.5f, 2.1f, 2.1f, 9.8f, 2.7f, Material.COPPER),
              b(-3.0f, 10.4f, 2.0f, -2.2f, 11.6f, 2.8f, Material.BRONZE),
              b(2.2f, 10.4f, 2.0f, 3.0f, 11.6f, 2.8f, Material.BRONZE));
      case "berserk" ->
          body(
              b(-3.5f, 5.0f, 2.0f, 3.5f, 11.0f, 4.0f, Material.DARK_METAL),
              b(-4.5f, 5.5f, 2.2f, -3.5f, 10.5f, 3.8f, Material.BLOOD_RED),
              b(3.5f, 5.5f, 2.2f, 4.5f, 10.5f, 3.8f, Material.BLOOD_RED),
              b(-1.6f, 6.4f, 4.0f, 1.6f, 9.6f, 4.35f, Material.BLOOD_RED),
              b(-1.0f, 7.0f, 4.35f, 1.0f, 9.0f, 4.5f, Material.NEON_RED),
              b(-2.6f, 7.0f, 4.45f, 2.6f, 7.4f, 4.6f, Material.NEON_YELLOW),
              b(-2.6f, 8.1f, 4.45f, 2.6f, 8.5f, 4.6f, Material.NEON_YELLOW),
              b(-2.6f, 9.2f, 4.45f, 2.6f, 9.6f, 4.6f, Material.NEON_YELLOW),
              b(-4.0f, 11.0f, 2.5f, -3.0f, 12.4f, 3.5f, Material.BLOOD_RED),
              b(3.0f, 11.0f, 2.5f, 4.0f, 12.4f, 3.5f, Material.BLOOD_RED),
              b(-2.4f, 3.8f, 2.3f, -1.4f, 5.0f, 3.6f, Material.COPPER),
              b(1.4f, 3.8f, 2.3f, 2.4f, 5.0f, 3.6f, Material.COPPER),
              b(-0.6f, 10.9f, 2.2f, 0.6f, 11.6f, 2.9f, Material.GOLD));
      case "chrome_compressor" ->
          body(
              b(-2.6f, 4.0f, 2.0f, 2.6f, 7.6f, 3.2f, Material.TITANIUM),
              b(-2.2f, 4.4f, 3.2f, 2.2f, 7.2f, 3.35f, Material.INDUSTRIAL),
              b(-1.7f, 7.6f, 2.3f, -0.8f, 9.2f, 3.0f, Material.CHROME),
              b(0.8f, 7.6f, 2.3f, 1.7f, 9.2f, 3.0f, Material.CHROME),
              b(-2.3f, 4.4f, 3.35f, 2.3f, 5.1f, 3.5f, Material.YELLOW),
              b(-2.3f, 5.2f, 3.35f, 2.3f, 5.5f, 3.5f, Material.CARBON),
              b(-2.3f, 5.6f, 3.35f, 2.3f, 5.9f, 3.5f, Material.YELLOW),
              b(-3.1f, 4.5f, 2.2f, -2.6f, 7.0f, 3.0f, Material.CHROME),
              b(2.6f, 4.5f, 2.2f, 3.1f, 7.0f, 3.0f, Material.CHROME),
              b(-1.0f, 7.7f, 3.1f, 1.0f, 8.6f, 3.5f, Material.GUNMETAL));
      case "axolotl" ->
          head(
              b(-4.5f, -5.5f, -2.0f, -3.8f, -2.2f, 1.8f, Material.EMERALD),
              b(-4.8f, -5.0f, -1.4f, -4.5f, -4.2f, -0.6f, Material.ACID),
              b(-4.8f, -4.4f, -0.4f, -4.5f, -3.6f, 0.4f, Material.ACID),
              b(-4.8f, -5.4f, 0.6f, -4.5f, -4.6f, 1.4f, Material.MEDICAL),
              b(-4.9f, -4.8f, -1.0f, -4.8f, -4.4f, -0.8f, Material.CIRCUIT),
              b(-4.6f, -2.6f, -0.5f, -4.0f, -1.4f, 1.5f, Material.EMERALD),
              b(-4.7f, -2.8f, 1.4f, -4.1f, -2.0f, 2.2f, Material.DARK_METAL),
              b(-4.4f, -6.0f, -0.4f, -4.0f, -5.5f, 0.4f, Material.ACID),
              b(-3.9f, -5.2f, -2.1f, -3.4f, -2.5f, -1.7f, Material.TOXIC));
      case "quantum_tuner" ->
          head(
              b(-2.2f, -7.2f, 2.8f, 2.2f, -3.4f, 4.2f, Material.DARK_PURPLE),
              b(-2.8f, -6.8f, 3.1f, -2.2f, -3.8f, 4.0f, Material.CARBON),
              b(2.2f, -6.8f, 3.1f, 2.8f, -3.8f, 4.0f, Material.CARBON),
              b(-0.8f, -5.8f, 4.2f, 0.8f, -4.6f, 4.5f, Material.CYAN),
              b(-3.2f, -6.4f, 3.4f, -2.8f, -3.6f, 4.1f, Material.PURPLE),
              b(2.8f, -6.4f, 3.4f, 3.2f, -3.6f, 4.1f, Material.PURPLE),
              b(-1.6f, -7.6f, 3.4f, 1.6f, -7.2f, 4.0f, Material.PURPLE),
              b(-0.5f, -7.4f, 4.0f, 0.5f, -7.0f, 4.2f, Material.CYAN));
      case "cox2_optimizer" ->
          head(
              b(3.8f, -5.6f, -2.0f, 4.6f, -2.4f, 1.8f, Material.COPPER),
              b(4.6f, -5.2f, -1.6f, 4.72f, -4.4f, -0.8f, Material.NEON_ORANGE),
              b(4.6f, -4.2f, -1.6f, 4.72f, -3.4f, -0.8f, Material.NEON_ORANGE),
              b(4.6f, -5.2f, -0.4f, 4.72f, -4.4f, 0.4f, Material.NEON_ORANGE),
              b(4.6f, -4.2f, -0.4f, 4.72f, -3.4f, 0.4f, Material.NEON_ORANGE),
              b(4.6f, -3.2f, -1.6f, 4.72f, -2.6f, -0.8f, Material.NEON_ORANGE),
              b(4.6f, -3.2f, -0.4f, 4.72f, -2.6f, 0.4f, Material.NEON_ORANGE),
              b(3.4f, -3.0f, 1.4f, 4.4f, -1.6f, 2.6f, Material.COPPER),
              b(3.4f, -2.6f, 2.6f, 4.4f, -1.8f, 2.9f, Material.NEON_ORANGE),
              b(3.4f, -5.8f, 1.8f, 4.2f, -5.2f, 2.6f, Material.DARK_METAL));
      case "self_ice" ->
          head(
              b(-2.4f, -7.2f, -4.6f, 2.4f, -4.4f, -4.1f, Material.BLUE_STEEL),
              b(-1.8f, -6.8f, -4.8f, 1.8f, -4.8f, -4.6f, Material.ELECTRIC_BLUE),
              b(-0.25f, -6.6f, -4.9f, 0.25f, -5.0f, -4.8f, Material.CHROME),
              b(-1.4f, -5.9f, -4.9f, 1.4f, -5.6f, -4.8f, Material.CHROME),
              b(-2.0f, -7.0f, -4.75f, -1.7f, -6.7f, -4.6f, Material.CHROME),
              b(1.7f, -7.0f, -4.75f, 2.0f, -6.7f, -4.6f, Material.CHROME),
              b(-2.0f, -4.9f, -4.75f, -1.7f, -4.6f, -4.6f, Material.CHROME),
              b(1.7f, -4.9f, -4.75f, 2.0f, -4.6f, -4.6f, Material.CHROME),
              b(-0.6f, -7.4f, -4.5f, 0.6f, -7.2f, -4.2f, Material.GUNMETAL));
      case "newton_memory_boost" ->
          head(
              b(-3.6f, -6.6f, 2.6f, -0.4f, -3.4f, 4.1f, Material.BLUE_STEEL),
              b(-3.2f, -6.2f, 4.1f, -0.8f, -3.8f, 4.3f, Material.INDUSTRIAL),
              b(-2.8f, -5.9f, 4.3f, -1.2f, -5.7f, 4.42f, Material.CYAN),
              b(-2.8f, -5.4f, 4.3f, -1.2f, -5.2f, 4.42f, Material.CYAN),
              b(-2.8f, -4.9f, 4.3f, -1.2f, -4.7f, 4.42f, Material.CYAN),
              b(-2.6f, -3.4f, 3.0f, -1.4f, -2.4f, 3.9f, Material.DARK_METAL),
              b(-3.4f, -6.4f, 3.4f, -3.2f, -3.6f, 4.0f, Material.CYAN));
      case "ex_disk_ram_upgrade" ->
          head(
              b(0.8f, -6.6f, 2.8f, 3.8f, -3.4f, 4.2f, Material.CARBON),
              b(1.1f, -6.2f, 4.2f, 3.5f, -3.8f, 4.45f, Material.DARK_PURPLE),
              b(1.4f, -5.9f, 4.45f, 3.2f, -5.5f, 4.6f, Material.PURPLE),
              b(1.4f, -5.2f, 4.45f, 3.2f, -4.8f, 4.6f, Material.PURPLE),
              b(1.4f, -4.5f, 4.45f, 3.2f, -4.1f, 4.6f, Material.PURPLE),
              b(2.4f, -6.0f, 4.6f, 3.0f, -5.4f, 4.72f, Material.VIOLET),
              b(1.5f, -6.0f, 4.6f, 2.1f, -5.4f, 4.72f, Material.VIOLET),
              b(2.0f, -4.4f, 4.6f, 2.8f, -3.9f, 4.72f, Material.DARK_PURPLE),
              b(1.4f, -7.0f, 3.0f, 3.2f, -6.6f, 4.0f, Material.DARK_METAL));
      case "kiroshi_optics" ->
          head(
              b(-3.6f, -5.4f, -4.5f, -0.9f, -3.1f, -3.9f, Material.CARBON),
              b(0.9f, -5.4f, -4.5f, 3.6f, -3.1f, -3.9f, Material.CARBON),
              b(-2.9f, -4.9f, -4.65f, -1.6f, -3.6f, -4.5f, Material.CYAN),
              b(1.6f, -4.9f, -4.65f, 2.9f, -3.6f, -4.5f, Material.CYAN),
              b(-3.7f, -5.6f, -4.4f, -0.8f, -5.3f, -3.9f, Material.CHROME),
              b(0.8f, -5.6f, -4.4f, 3.7f, -5.3f, -3.9f, Material.CHROME),
              b(-3.7f, -3.2f, -4.4f, -0.8f, -2.9f, -3.9f, Material.CHROME),
              b(0.8f, -3.2f, -4.4f, 3.7f, -2.9f, -3.9f, Material.CHROME),
              b(-4.0f, -5.2f, -4.2f, -3.7f, -3.3f, -3.9f, Material.CHROME),
              b(3.7f, -5.2f, -4.2f, 4.0f, -3.3f, -3.9f, Material.CHROME),
              b(-2.3f, -4.75f, -4.75f, -2.0f, -3.9f, -4.65f, Material.CHROME),
              b(2.0f, -4.75f, -4.75f, 2.3f, -3.9f, -4.65f, Material.CHROME),
              b(-0.4f, -6.0f, -4.4f, 0.4f, -5.4f, -3.9f, Material.GUNMETAL));
      case "behavioral_imprint" ->
          head(
              b(-4.3f, -4.4f, -4.6f, -0.7f, -3.9f, -3.9f, Material.INDUSTRIAL),
              b(-4.3f, -3.5f, -4.6f, -0.9f, -3.0f, -3.9f, Material.SMOKE),
              b(-4.3f, -2.6f, -4.6f, -1.2f, -2.1f, -3.9f, Material.INDUSTRIAL),
              b(-0.8f, -4.7f, -4.8f, 0.8f, -3.8f, -4.0f, Material.SMOKE),
              b(-0.5f, -3.7f, -4.9f, 0.5f, -2.6f, -4.2f, Material.CHROME),
              b(-3.8f, -3.7f, -4.65f, -1.0f, -3.55f, -4.55f, Material.CYAN),
              b(-4.4f, -3.6f, -3.9f, -4.2f, -1.8f, -2.0f, Material.GUNMETAL),
              b(-4.0f, -2.1f, -3.9f, -1.4f, -1.9f, -2.6f, Material.DARK_METAL),
              b(0.2f, -5.1f, -4.4f, 0.8f, -4.7f, -3.9f, Material.CYAN));
      case "smart_link" ->
          arms(
              b(-2.6f, 8.3f, -2.3f, 0.6f, 10.7f, -2.05f, Material.DARK_METAL),
              b(-2.3f, 8.7f, -2.4f, 0.3f, 8.95f, -2.3f, Material.NEON_ORANGE),
              b(-2.3f, 9.35f, -2.4f, 0.3f, 9.6f, -2.3f, Material.NEON_ORANGE),
              b(-2.3f, 10.0f, -2.4f, 0.3f, 10.25f, -2.3f, Material.NEON_ORANGE),
              b(-0.5f, 8.6f, -2.4f, 0.0f, 10.4f, -2.3f, Material.NEON_ORANGE),
              b(-2.5f, 7.9f, -2.25f, -1.7f, 8.35f, -1.95f, Material.GOLD),
              b(0.0f, 7.9f, -2.25f, 0.6f, 8.35f, -1.95f, Material.GOLD),
              b(-2.6f, 10.7f, -2.2f, 0.6f, 11.2f, -1.9f, Material.CARBON),
              b(-0.9f, 8.5f, -2.35f, -0.6f, 10.3f, -2.3f, Material.NEON_YELLOW));
      case "ballistic_coprocessor" ->
          arms(
              b(-2.7f, 8.2f, 1.8f, 0.7f, 10.9f, 2.3f, Material.GUNMETAL),
              b(-2.4f, 8.6f, 2.3f, -1.8f, 10.5f, 2.42f, Material.NEON_RED),
              b(-1.4f, 8.6f, 2.3f, -0.8f, 10.5f, 2.42f, Material.NEON_RED),
              b(-0.4f, 8.6f, 2.3f, 0.2f, 10.5f, 2.42f, Material.NEON_RED),
              b(-2.7f, 10.6f, 1.9f, 0.7f, 11.2f, 2.4f, Material.CARBON),
              b(-2.6f, 11.1f, 1.95f, -1.9f, 12.0f, 2.35f, Material.GUNMETAL),
              b(-1.7f, 11.1f, 1.95f, -1.0f, 11.8f, 2.35f, Material.GUNMETAL),
              b(-0.8f, 11.1f, 1.95f, -0.1f, 12.1f, 2.35f, Material.GUNMETAL),
              b(0.1f, 11.1f, 1.95f, 0.6f, 11.7f, 2.35f, Material.GUNMETAL),
              b(-2.6f, 11.6f, 2.35f, -1.9f, 12.0f, 2.42f, Material.NEON_RED),
              b(-0.8f, 11.7f, 2.35f, -0.1f, 12.1f, 2.42f, Material.NEON_RED),
              b(-2.8f, 7.8f, 1.9f, 0.8f, 8.3f, 2.3f, Material.DARK_METAL));
      case "microgenerator" ->
          arms(
              b(-2.8f, 7.0f, -1.6f, 0.8f, 8.5f, 2.2f, Material.COPPER),
              b(-2.3f, 7.2f, -1.9f, 0.3f, 8.3f, -1.6f, Material.BRONZE),
              b(-2.9f, 7.2f, -1.0f, -2.8f, 8.3f, 1.8f, Material.BRONZE),
              b(0.8f, 7.2f, -1.0f, 0.9f, 8.3f, 1.8f, Material.BRONZE),
              b(-2.4f, 8.5f, -0.9f, 0.4f, 8.9f, 1.7f, Material.CHROME),
              b(-1.6f, 8.9f, 0.0f, -0.6f, 9.3f, 0.9f, Material.ELECTRIC_BLUE),
              b(0.0f, 9.0f, 0.3f, 0.5f, 9.25f, 0.8f, Material.CYAN),
              b(-3.0f, 7.6f, 0.2f, -2.9f, 8.0f, 0.8f, Material.COPPER),
              b(-1.2f, 7.3f, -2.0f, -0.4f, 8.0f, -1.9f, Material.COPPER));
      case "handle_wrap" ->
          arms(
              b(-2.9f, 8.0f, -2.4f, 0.9f, 9.3f, 2.4f, Material.RUBBER),
              b(-2.9f, 9.3f, -2.4f, 0.9f, 10.4f, 2.4f, Material.CARBON),
              b(-3.0f, 9.2f, -2.5f, 1.0f, 9.4f, 2.5f, Material.GUNMETAL),
              b(-3.0f, 10.3f, -2.5f, 1.0f, 10.5f, 2.5f, Material.GUNMETAL),
              b(-3.0f, 7.8f, -2.4f, 1.0f, 8.1f, 2.4f, Material.INDUSTRIAL),
              b(-3.0f, 8.4f, -2.45f, -2.9f, 9.9f, -1.7f, Material.RUBBER),
              b(0.9f, 8.4f, -2.45f, 1.0f, 9.9f, -1.7f, Material.RUBBER),
              b(-1.4f, 10.4f, -2.45f, 0.0f, 10.7f, -1.8f, Material.GUNMETAL));
      case "mantis_blades" ->
          rightArm(
              b(-3.0f, 2.6f, -2.2f, 1.0f, 8.4f, 2.2f, Material.CERAMIC),
              b(-2.5f, 3.0f, -2.45f, 0.5f, 7.8f, -2.2f, Material.GUNMETAL),
              b(-2.1f, 3.3f, -2.55f, 0.1f, 4.0f, -2.45f, Material.NEON_ORANGE),
              b(-2.1f, 4.3f, -2.55f, 0.1f, 5.0f, -2.45f, Material.NEON_ORANGE),
              b(-2.1f, 5.3f, -2.55f, 0.1f, 6.0f, -2.45f, Material.NEON_ORANGE),
              b(-3.05f, 3.0f, -2.1f, -2.95f, 8.0f, 1.8f, Material.CHROME),
              b(0.95f, 3.0f, -2.1f, 1.05f, 8.0f, 1.8f, Material.CHROME),
              b(-3.05f, 2.4f, -2.2f, 1.05f, 2.9f, 2.2f, Material.DARK_METAL),
              b(-0.4f, 8.4f, -0.7f, 0.4f, 15.6f, 0.7f, Material.CERAMIC),
              b(0.4f, 8.4f, -0.7f, 0.6f, 15.6f, 0.1f, Material.CHROME),
              b(-0.4f, 8.4f, -0.75f, 0.4f, 15.4f, -0.65f, Material.NEON_ORANGE),
              b(-0.55f, 10.0f, -0.5f, -0.4f, 10.6f, 0.5f, Material.CHROME),
              b(-0.55f, 11.4f, -0.5f, -0.4f, 12.0f, 0.5f, Material.CHROME),
              b(-0.55f, 12.8f, -0.5f, -0.4f, 13.4f, 0.5f, Material.CHROME),
              b(-0.8f, 8.8f, 0.7f, 0.8f, 9.4f, 0.9f, Material.NEON_ORANGE));
      case "gorilla_arms" ->
          arms(
              b(-3.4f, 1.8f, -2.3f, 1.2f, 7.2f, 2.3f, Material.CARBON),
              b(-3.8f, 1.4f, -2.0f, -3.4f, 7.6f, 2.0f, Material.GUNMETAL),
              b(-3.2f, 6.8f, -2.2f, 1.0f, 9.4f, 2.2f, Material.CARBON),
              b(-3.4f, 9.2f, -2.2f, -2.2f, 10.4f, 2.2f, Material.BRASS),
              b(-2.0f, 9.2f, -2.2f, -0.8f, 10.6f, 2.2f, Material.BRASS),
              b(-0.6f, 9.2f, -2.2f, 0.6f, 10.8f, 2.2f, Material.BRASS),
              b(0.8f, 9.2f, -2.2f, 1.2f, 10.2f, 2.2f, Material.GOLD),
              b(-3.5f, 2.4f, -1.9f, -3.3f, 6.8f, -0.7f, Material.BLUE_STEEL),
              b(-3.5f, 2.4f, 0.7f, -3.3f, 6.8f, 1.9f, Material.BLUE_STEEL),
              b(-3.6f, 3.6f, -1.9f, -3.5f, 4.0f, -0.7f, Material.NEON_RED),
              b(-3.6f, 5.2f, 0.7f, -3.5f, 5.6f, 1.9f, Material.NEON_RED),
              b(-2.8f, -1.4f, -2.1f, 0.8f, 1.8f, 2.1f, Material.DARK_METAL),
              b(-2.2f, -2.0f, -1.6f, 0.2f, -1.4f, 1.6f, Material.GUNMETAL),
              b(-2.6f, -1.0f, -2.2f, -2.0f, -0.2f, -2.0f, Material.NEON_RED),
              b(0.0f, -1.0f, -2.2f, 0.6f, -0.2f, -2.0f, Material.NEON_RED));
      case "projectile_launch_system" ->
          rightArm(
              b(-3.2f, 2.8f, 1.7f, 0.8f, 7.4f, 3.6f, Material.OLIVE),
              b(-2.8f, 3.2f, 3.6f, 0.4f, 7.0f, 3.9f, Material.GUNMETAL),
              b(-3.0f, 3.6f, -2.2f, 0.6f, 7.0f, 1.7f, Material.OLIVE),
              b(-1.8f, 7.4f, 2.1f, -0.6f, 9.2f, 3.3f, Material.GUNMETAL),
              b(-2.1f, 9.2f, 1.8f, -0.3f, 9.7f, 3.6f, Material.BLOOD_RED),
              b(-2.05f, 9.7f, 1.85f, -0.35f, 9.85f, 3.55f, Material.NEON_YELLOW),
              b(-2.6f, 4.0f, 3.9f, 0.2f, 4.5f, 4.05f, Material.YELLOW),
              b(-2.6f, 5.0f, 3.9f, 0.2f, 5.25f, 4.05f, Material.CARBON),
              b(-3.0f, 3.2f, -2.3f, -2.7f, 6.6f, -2.1f, Material.DARK_METAL),
              b(0.3f, 3.2f, -2.3f, 0.6f, 6.6f, -2.1f, Material.DARK_METAL),
              b(-2.4f, 6.8f, 3.0f, -1.8f, 7.6f, 3.6f, Material.NEON_ORANGE));
      case "kerenzikov" ->
          body(
              b(-4.6f, 7.0f, 2.0f, -1.6f, 10.4f, 2.6f, Material.CHROME),
              b(1.6f, 7.0f, 2.0f, 4.6f, 10.4f, 2.6f, Material.CHROME),
              b(-3.9f, 7.6f, 2.6f, -3.1f, 8.6f, 3.2f, Material.CYAN),
              b(-3.3f, 7.6f, 2.6f, -2.5f, 8.6f, 3.2f, Material.CYAN),
              b(-2.7f, 7.6f, 2.6f, -1.9f, 8.6f, 3.2f, Material.CYAN),
              b(1.9f, 7.6f, 2.6f, 2.7f, 8.6f, 3.2f, Material.CYAN),
              b(2.5f, 7.6f, 2.6f, 3.3f, 8.6f, 3.2f, Material.CYAN),
              b(3.1f, 7.6f, 2.6f, 3.9f, 8.6f, 3.2f, Material.CYAN),
              b(-4.2f, 8.8f, 2.6f, -1.8f, 9.2f, 3.0f, Material.ELECTRIC_BLUE),
              b(1.8f, 8.8f, 2.6f, 4.2f, 9.2f, 3.0f, Material.ELECTRIC_BLUE),
              b(-3.2f, 10.4f, 2.1f, -2.6f, 11.6f, 2.6f, Material.CHROME),
              b(2.6f, 10.4f, 2.1f, 3.2f, 11.6f, 2.6f, Material.CHROME),
              b(-3.0f, 10.2f, 2.6f, -2.8f, 11.2f, 2.8f, Material.CYAN),
              b(2.8f, 10.2f, 2.6f, 3.0f, 11.2f, 2.8f, Material.CYAN));
      case "reflex_synaptic_accelerator" ->
          body(
              b(-1.5f, 10.0f, 1.2f, -1.0f, 12.8f, 1.8f, Material.NEON_YELLOW),
              b(-0.3f, 10.2f, 1.4f, 0.3f, 13.0f, 1.9f, Material.NEON_YELLOW),
              b(1.0f, 10.0f, 1.2f, 1.5f, 12.8f, 1.8f, Material.NEON_YELLOW),
              b(-1.1f, 10.8f, 1.0f, -0.7f, 11.4f, 1.6f, Material.CHROME),
              b(0.7f, 10.8f, 1.0f, 1.1f, 11.4f, 1.6f, Material.CHROME),
              b(-1.8f, 9.6f, 1.0f, 1.8f, 10.3f, 1.9f, Material.DARK_METAL),
              b(-0.8f, 10.0f, 1.9f, 0.8f, 10.6f, 2.1f, Material.VIOLET),
              b(-2.2f, 10.6f, -0.4f, -1.5f, 12.4f, 1.2f, Material.NEON_YELLOW),
              b(1.5f, 10.6f, -0.4f, 2.2f, 12.4f, 1.2f, Material.NEON_YELLOW));
      case "maneuvering_system" ->
          body(
              b(-5.3f, 3.0f, -1.2f, -4.1f, 5.8f, 1.5f, Material.SMOKE),
              b(4.1f, 3.0f, -1.2f, 5.3f, 5.8f, 1.5f, Material.SMOKE),
              b(-4.5f, 5.8f, -0.9f, -4.1f, 6.5f, 1.2f, Material.INDUSTRIAL),
              b(4.1f, 5.8f, -0.9f, 4.5f, 6.5f, 1.2f, Material.INDUSTRIAL),
              b(-5.1f, 2.2f, 0.3f, -4.3f, 3.0f, 1.5f, Material.GUNMETAL),
              b(4.3f, 2.2f, 0.3f, 5.1f, 3.0f, 1.5f, Material.GUNMETAL),
              b(-5.0f, 2.0f, 0.5f, -4.4f, 2.2f, 1.3f, Material.NEON_ORANGE),
              b(4.4f, 2.0f, 0.5f, 5.0f, 2.2f, 1.3f, Material.NEON_ORANGE),
              b(-5.2f, 3.4f, 1.5f, -4.2f, 5.0f, 1.8f, Material.GUNMETAL),
              b(4.2f, 3.4f, 1.5f, 5.2f, 5.0f, 1.8f, Material.GUNMETAL),
              b(-4.9f, 3.4f, 1.8f, -4.5f, 4.8f, 2.0f, Material.NEON_ORANGE),
              b(4.5f, 3.4f, 1.8f, 4.9f, 4.8f, 2.0f, Material.NEON_ORANGE));
      case "adrenaline_converter" ->
          body(
              b(-1.5f, 9.6f, -2.6f, 1.5f, 12.4f, -1.4f, Material.CHROME),
              b(-1.1f, 9.9f, -2.75f, 1.1f, 12.1f, -2.5f, Material.TOXIC),
              b(-1.6f, 9.4f, -2.7f, 1.6f, 9.9f, -1.3f, Material.GUNMETAL),
              b(-1.6f, 12.1f, -2.7f, 1.6f, 12.7f, -1.3f, Material.GUNMETAL),
              b(-0.4f, 10.2f, -2.85f, 0.4f, 11.9f, -2.75f, Material.ACID),
              b(-2.5f, 10.4f, -2.4f, -1.5f, 11.0f, -1.7f, Material.RUBBER),
              b(1.5f, 10.4f, -2.4f, 2.5f, 11.0f, -1.7f, Material.RUBBER),
              b(-1.9f, 10.7f, -2.1f, -1.5f, 11.0f, -1.9f, Material.TOXIC),
              b(1.5f, 10.7f, -2.1f, 1.9f, 11.0f, -1.9f, Material.TOXIC));
      case "second_heart" ->
          body(
              b(-2.8f, 4.8f, -3.5f, -0.2f, 8.4f, -2.0f, Material.BLOOD_RED),
              b(-2.2f, 3.4f, -3.2f, -0.6f, 4.8f, -2.2f, Material.BLOOD_RED),
              b(-1.8f, 5.9f, -3.7f, -0.8f, 7.4f, -3.5f, Material.GOLD),
              b(-1.6f, 6.1f, -3.8f, -1.0f, 7.2f, -3.7f, Material.AMBER),
              b(-3.2f, 5.2f, -3.0f, -2.8f, 8.0f, -2.2f, Material.BLUE_STEEL),
              b(-0.2f, 5.2f, -3.0f, 0.3f, 8.0f, -2.2f, Material.BLUE_STEEL),
              b(-2.8f, 8.4f, -3.2f, -0.2f, 8.9f, -2.4f, Material.BLUE_STEEL),
              b(-2.4f, 5.0f, -2.0f, -1.8f, 8.6f, -1.9f, Material.DARK_METAL),
              b(-1.0f, 4.0f, -2.9f, -0.6f, 4.8f, -2.3f, Material.CHROME),
              b(-2.6f, 7.8f, -3.55f, -2.2f, 8.2f, -3.1f, Material.NEON_RED));
      case "biomonitor" ->
          rightArm(
              b(-2.6f, 7.7f, -2.45f, 0.6f, 9.7f, -2.1f, Material.CARBON),
              b(-2.2f, 8.0f, -2.6f, 0.2f, 9.4f, -2.45f, Material.CIRCUIT),
              b(-2.0f, 8.3f, -2.68f, -1.8f, 9.1f, -2.6f, Material.EMERALD),
              b(-1.5f, 8.3f, -2.68f, -0.1f, 8.5f, -2.6f, Material.EMERALD),
              b(-1.5f, 8.8f, -2.68f, -0.4f, 9.0f, -2.6f, Material.EMERALD),
              b(-1.0f, 9.1f, -2.68f, -0.2f, 9.3f, -2.6f, Material.EMERALD),
              b(-2.7f, 7.5f, -2.5f, 0.7f, 7.8f, -2.0f, Material.RUBBER),
              b(-2.7f, 9.6f, -2.5f, 0.7f, 10.0f, -2.0f, Material.RUBBER),
              b(0.3f, 9.5f, -2.55f, 0.6f, 9.7f, -2.4f, Material.NEON_RED));
      case "blood_pump" ->
          body(
              b(1.0f, 4.2f, -3.3f, 3.8f, 7.6f, -2.1f, Material.GUNMETAL),
              b(1.4f, 6.8f, -3.1f, 2.2f, 8.4f, -2.3f, Material.CHROME),
              b(2.6f, 6.8f, -3.1f, 3.4f, 8.4f, -2.3f, Material.CHROME),
              b(1.9f, 5.2f, -3.5f, 2.9f, 6.4f, -3.3f, Material.BLOOD_RED),
              b(1.2f, 3.4f, -2.9f, 1.8f, 4.2f, -2.3f, Material.NEON_RED),
              b(3.0f, 3.4f, -2.9f, 3.6f, 4.2f, -2.3f, Material.NEON_ORANGE),
              b(3.8f, 4.8f, -3.0f, 4.2f, 7.0f, -2.2f, Material.CHROME),
              b(2.0f, 8.4f, -3.0f, 2.8f, 8.9f, -2.4f, Material.NEON_ORANGE),
              b(1.3f, 5.0f, -2.1f, 3.5f, 5.5f, -1.9f, Material.DARK_METAL));
      case "heal_on_kill" ->
          body(
              b(2.2f, 5.8f, -3.1f, 3.6f, 9.0f, -2.1f, Material.MEDICAL),
              b(2.5f, 6.2f, -3.3f, 3.3f, 8.6f, -3.1f, Material.TOXIC),
              b(2.7f, 6.4f, -3.42f, 3.1f, 8.4f, -3.3f, Material.EMERALD),
              b(2.4f, 7.2f, -3.45f, 3.4f, 7.7f, -3.35f, Material.MEDICAL),
              b(2.75f, 6.8f, -3.45f, 3.05f, 8.1f, -3.35f, Material.MEDICAL),
              b(2.8f, 9.0f, -2.9f, 3.0f, 10.0f, -2.5f, Material.CHROME),
              b(2.2f, 5.4f, -2.8f, 3.6f, 5.8f, -2.1f, Material.DARK_METAL),
              b(3.0f, 8.7f, -2.8f, 3.4f, 9.1f, -2.4f, Material.CYAN));
      case "syn_lungs_bioplastic" ->
          body(
              b(-3.8f, 5.4f, -2.9f, -0.6f, 9.8f, -1.9f, Material.CERAMIC),
              b(0.6f, 5.4f, -2.9f, 3.8f, 9.8f, -1.9f, Material.CERAMIC),
              b(-3.4f, 5.8f, -3.1f, -1.0f, 9.4f, -2.9f, Material.PINK),
              b(1.0f, 5.8f, -3.1f, 3.4f, 9.4f, -2.9f, Material.PINK),
              b(-0.8f, 9.0f, -2.7f, 0.8f, 11.2f, -2.0f, Material.CHROME),
              b(-3.2f, 6.4f, -3.2f, -3.0f, 8.8f, -3.1f, Material.ELECTRIC_BLUE),
              b(-3.0f, 8.6f, -3.2f, -2.0f, 8.8f, -3.1f, Material.ELECTRIC_BLUE),
              b(2.0f, 8.6f, -3.2f, 3.0f, 8.8f, -3.1f, Material.ELECTRIC_BLUE),
              b(3.0f, 6.4f, -3.2f, 3.2f, 8.8f, -3.1f, Material.ELECTRIC_BLUE),
              b(-2.6f, 5.8f, -3.2f, -2.4f, 7.2f, -3.1f, Material.CYAN),
              b(2.4f, 5.8f, -3.2f, 2.6f, 7.2f, -3.1f, Material.CYAN),
              b(-0.4f, 10.8f, -2.5f, 0.4f, 11.6f, -2.1f, Material.GUNMETAL));
      case "subdermal_armor" ->
          body(
              b(-3.8f, 7.8f, -2.7f, -0.4f, 10.0f, -2.0f, Material.CARBON),
              b(0.4f, 7.8f, -2.7f, 3.8f, 10.0f, -2.0f, Material.CARBON),
              b(-3.6f, 6.4f, -2.6f, -0.6f, 7.6f, -2.0f, Material.CARBON),
              b(0.6f, 6.4f, -2.6f, 3.6f, 7.6f, -2.0f, Material.CARBON),
              b(-3.0f, 5.2f, -2.5f, -0.8f, 6.2f, -2.0f, Material.CARBON),
              b(0.8f, 5.2f, -2.5f, 3.0f, 6.2f, -2.0f, Material.CARBON),
              b(-0.3f, 5.0f, -2.6f, 0.3f, 10.2f, -2.0f, Material.GUNMETAL),
              b(-5.0f, 9.0f, -2.0f, -3.8f, 11.2f, 2.0f, Material.CARBON),
              b(3.8f, 9.0f, -2.0f, 5.0f, 11.2f, 2.0f, Material.CARBON),
              b(-3.4f, 5.4f, -2.55f, -1.0f, 5.9f, -2.1f, Material.OLIVE),
              b(1.0f, 5.4f, -2.55f, 3.4f, 5.9f, -2.1f, Material.OLIVE),
              b(-4.8f, 9.2f, -2.1f, -3.9f, 9.6f, 2.1f, Material.GUNMETAL),
              b(3.9f, 9.2f, -2.1f, 4.8f, 9.6f, 2.1f, Material.GUNMETAL),
              b(-3.6f, 6.6f, 2.0f, 3.6f, 9.8f, 2.6f, Material.CARBON));
      case "optical_camo" ->
          body(
              b(-4.4f, 5.0f, -2.8f, 4.4f, 11.0f, -2.1f, Material.GLASS),
              b(-5.2f, 8.6f, -2.2f, -3.8f, 11.4f, 2.2f, Material.GLASS),
              b(3.8f, 8.6f, -2.2f, 5.2f, 11.4f, 2.2f, Material.GLASS),
              b(-4.4f, 5.0f, 2.1f, 4.4f, 11.0f, 2.8f, Material.GLASS),
              b(-2.0f, 11.0f, -2.4f, 2.0f, 11.7f, 2.4f, Material.GLASS),
              b(-2.0f, 5.6f, -2.9f, -1.8f, 10.2f, -2.8f, Material.CYAN),
              b(1.8f, 5.6f, -2.9f, 2.0f, 10.2f, -2.8f, Material.CYAN),
              b(-3.6f, 7.8f, -2.9f, 3.6f, 8.0f, -2.8f, Material.CYAN),
              b(-4.6f, 7.4f, -2.3f, -3.9f, 7.6f, 2.3f, Material.CYAN),
              b(3.9f, 7.4f, -2.3f, 4.6f, 7.6f, 2.3f, Material.CYAN),
              b(-0.8f, 11.4f, -2.2f, 0.8f, 11.9f, 2.2f, Material.CHROME));
      case "pain_editor" ->
          body(
              b(-0.9f, 3.0f, 1.9f, 0.9f, 10.4f, 2.6f, Material.RUST),
              b(-1.7f, 4.0f, 2.6f, 1.7f, 5.2f, 3.1f, Material.MUSTARD),
              b(-1.7f, 5.8f, 2.6f, 1.7f, 7.0f, 3.1f, Material.MUSTARD),
              b(-1.7f, 7.6f, 2.6f, 1.7f, 8.8f, 3.1f, Material.MUSTARD),
              b(-1.7f, 9.4f, 2.6f, 1.7f, 10.2f, 3.1f, Material.MUSTARD),
              b(-2.6f, 4.4f, 2.0f, -1.7f, 8.4f, 2.7f, Material.RUST),
              b(1.7f, 4.4f, 2.0f, 2.6f, 8.4f, 2.7f, Material.RUST),
              b(-1.2f, 4.6f, 3.1f, -0.4f, 4.9f, 3.2f, Material.DARK_METAL),
              b(0.4f, 6.4f, 3.1f, 1.2f, 6.7f, 3.2f, Material.DARK_METAL),
              b(-1.0f, 8.2f, 3.1f, -0.2f, 8.5f, 3.2f, Material.DARK_METAL),
              b(-2.4f, 8.4f, 2.1f, -1.8f, 9.2f, 2.6f, Material.GUNMETAL),
              b(1.8f, 8.4f, 2.1f, 2.4f, 9.2f, 2.6f, Material.GUNMETAL));
      case "shock_n_awe" ->
          body(
              b(-3.8f, 6.0f, -2.8f, -0.6f, 9.6f, -2.0f, Material.BLUE_STEEL),
              b(0.6f, 6.0f, -2.8f, 3.8f, 9.6f, -2.0f, Material.BLUE_STEEL),
              b(-3.0f, 9.6f, -2.7f, -2.2f, 11.1f, -2.0f, Material.CYAN),
              b(-2.0f, 9.6f, -2.7f, -1.2f, 11.1f, -2.0f, Material.CYAN),
              b(1.2f, 9.6f, -2.7f, 2.0f, 11.1f, -2.0f, Material.CYAN),
              b(2.2f, 9.6f, -2.7f, 3.0f, 11.1f, -2.0f, Material.CYAN),
              b(-3.0f, 11.1f, -2.6f, -2.2f, 11.5f, -2.1f, Material.YELLOW),
              b(-2.0f, 11.1f, -2.6f, -1.2f, 11.5f, -2.1f, Material.YELLOW),
              b(1.2f, 11.1f, -2.6f, 2.0f, 11.5f, -2.1f, Material.YELLOW),
              b(2.2f, 11.1f, -2.6f, 3.0f, 11.5f, -2.1f, Material.YELLOW),
              b(-0.4f, 5.4f, -3.0f, 0.4f, 9.8f, -2.4f, Material.CHROME),
              b(-0.6f, 6.6f, -3.1f, 0.6f, 6.9f, -3.0f, Material.YELLOW),
              b(-0.6f, 8.0f, -3.1f, 0.6f, 8.3f, -3.0f, Material.YELLOW),
              b(-0.3f, 7.2f, -3.15f, 0.3f, 7.8f, -3.1f, Material.CYAN),
              b(-3.2f, 9.4f, -2.9f, -2.0f, 9.7f, -2.7f, Material.NEON_YELLOW),
              b(2.0f, 9.4f, -2.9f, 3.2f, 9.7f, -2.7f, Material.NEON_YELLOW));
      case "fireproof_grounding" ->
          body(
              b(-3.8f, 5.0f, -2.7f, 3.8f, 9.6f, -2.0f, Material.BLOOD_RED),
              b(-2.8f, 4.4f, -2.8f, 2.8f, 7.0f, -2.5f, Material.GUNMETAL),
              b(-4.2f, 4.2f, -2.5f, 4.2f, 4.7f, -2.1f, Material.RUBBER),
              b(-4.0f, 9.4f, -2.4f, 4.0f, 9.9f, -2.0f, Material.RUBBER),
              b(-3.0f, 7.4f, -2.8f, 3.0f, 7.6f, -2.7f, Material.CARBON),
              b(-3.0f, 8.1f, -2.8f, 3.0f, 8.3f, -2.7f, Material.CARBON),
              b(-3.0f, 8.8f, -2.8f, 3.0f, 9.0f, -2.7f, Material.CARBON),
              b(-1.8f, 4.6f, -2.9f, -1.2f, 5.2f, -2.8f, Material.CHROME),
              b(1.2f, 4.6f, -2.9f, 1.8f, 5.2f, -2.8f, Material.CHROME),
              b(-2.0f, 9.9f, -2.3f, -1.4f, 10.3f, -2.1f, Material.RUST),
              b(1.4f, 9.9f, -2.3f, 2.0f, 10.3f, -2.1f, Material.RUST));
      case "titanium_bones" ->
          mirroredArmsAndLegs(
              List.of(
                  b(-3.3f, 1.2f, -1.4f, -2.6f, 8.2f, 1.4f, Material.TITANIUM),
                  b(0.2f, 1.2f, -1.4f, 0.9f, 8.2f, 1.4f, Material.TITANIUM),
                  b(-3.5f, 0.4f, -2.0f, -2.8f, 1.6f, 2.0f, Material.CHROME),
                  b(-3.4f, 1.8f, -1.5f, -3.2f, 7.8f, 1.5f, Material.CHROME),
                  b(-3.3f, 3.0f, -1.5f, -2.9f, 3.4f, 1.5f, Material.GUNMETAL),
                  b(-3.3f, 6.0f, -1.5f, -2.9f, 6.4f, 1.5f, Material.GUNMETAL),
                  b(-1.2f, 9.2f, -1.5f, 0.2f, 10.2f, 1.5f, Material.TITANIUM),
                  b(-0.8f, 9.6f, -1.6f, -0.2f, 10.0f, 1.6f, Material.CHROME)),
              List.of(
                  b(-2.7f, 3.0f, -1.5f, -1.9f, 10.2f, 1.5f, Material.TITANIUM),
                  b(1.9f, 3.0f, -1.5f, 2.7f, 10.2f, 1.5f, Material.TITANIUM),
                  b(-2.9f, 2.0f, -2.2f, -2.2f, 3.4f, 2.2f, Material.CHROME),
                  b(-2.8f, 4.0f, -1.6f, -2.6f, 9.8f, 1.6f, Material.CHROME),
                  b(-2.7f, 6.0f, -1.6f, -2.3f, 6.4f, 1.6f, Material.GUNMETAL),
                  b(-2.7f, 8.0f, -1.6f, -2.3f, 8.4f, 1.6f, Material.GUNMETAL),
                  b(-1.8f, 9.8f, -1.3f, -1.2f, 10.6f, 1.3f, Material.TITANIUM),
                  b(1.2f, 9.8f, -1.3f, 1.8f, 10.6f, 1.3f, Material.TITANIUM)));
      case "dense_marrow" ->
          mirroredArmsAndLegs(
              List.of(
                  b(-3.5f, 0.0f, -1.6f, -2.9f, 3.4f, 0.6f, Material.GUNMETAL),
                  b(-3.55f, 0.6f, -1.4f, -3.45f, 2.6f, 0.4f, Material.CARBON),
                  b(-3.6f, 3.2f, -1.8f, -2.8f, 4.2f, 0.8f, Material.RUST),
                  b(0.3f, 0.6f, -0.9f, 0.9f, 3.0f, 0.3f, Material.GUNMETAL),
                  b(0.35f, 1.0f, -0.7f, 0.45f, 2.4f, -0.1f, Material.CARBON),
                  b(-3.8f, 1.2f, -1.2f, -3.5f, 2.0f, 0.2f, Material.CHROME)),
              List.of(
                  b(-3.0f, 1.0f, 1.5f, -2.2f, 4.4f, 2.5f, Material.GUNMETAL),
                  b(-2.9f, 1.4f, 2.5f, -2.3f, 4.0f, 2.7f, Material.CARBON),
                  b(-3.1f, 4.2f, 1.3f, -2.1f, 5.2f, 2.7f, Material.RUST),
                  b(2.2f, 1.0f, 1.5f, 3.0f, 4.4f, 2.5f, Material.GUNMETAL),
                  b(2.3f, 1.4f, 2.5f, 2.9f, 4.0f, 2.7f, Material.CARBON),
                  b(-2.8f, 4.4f, 2.5f, -2.4f, 5.0f, 2.8f, Material.CHROME)));
      case "epimorphic_skeleton" ->
          body(
              b(-0.8f, 4.0f, -3.1f, 0.8f, 11.0f, -2.3f, Material.BRONZE),
              b(-1.2f, 3.5f, 2.0f, 1.2f, 11.0f, 2.7f, Material.BRONZE),
              b(-4.2f, 10.6f, -2.9f, -0.8f, 11.2f, -2.3f, Material.GOLD),
              b(0.8f, 10.6f, -2.9f, 4.2f, 11.2f, -2.3f, Material.GOLD),
              b(-3.8f, 9.4f, -3.0f, -0.8f, 9.85f, -2.4f, Material.GOLD),
              b(-4.2f, 9.4f, -2.8f, -3.8f, 9.85f, -0.3f, Material.BRONZE),
              b(0.8f, 9.4f, -3.0f, 3.8f, 9.85f, -2.4f, Material.GOLD),
              b(3.8f, 9.4f, -2.8f, 4.2f, 9.85f, -0.3f, Material.BRONZE),
              b(-3.8f, 8.1f, -3.0f, -0.8f, 8.55f, -2.4f, Material.GOLD),
              b(-4.2f, 8.1f, -2.8f, -3.8f, 8.55f, -0.3f, Material.BRONZE),
              b(0.8f, 8.1f, -3.0f, 3.8f, 8.55f, -2.4f, Material.GOLD),
              b(3.8f, 8.1f, -2.8f, 4.2f, 8.55f, -0.3f, Material.BRONZE),
              b(-3.8f, 6.8f, -3.0f, -0.8f, 7.25f, -2.4f, Material.GOLD),
              b(-4.2f, 6.8f, -2.8f, -3.8f, 7.25f, -0.3f, Material.BRONZE),
              b(0.8f, 6.8f, -3.0f, 3.8f, 7.25f, -2.4f, Material.GOLD),
              b(3.8f, 6.8f, -2.8f, 4.2f, 7.25f, -0.3f, Material.BRONZE),
              b(-3.8f, 5.5f, -3.0f, -0.8f, 5.95f, -2.4f, Material.GOLD),
              b(-4.2f, 5.5f, -2.8f, -3.8f, 5.95f, -0.3f, Material.BRONZE),
              b(0.8f, 5.5f, -3.0f, 3.8f, 5.95f, -2.4f, Material.GOLD),
              b(3.8f, 5.5f, -2.8f, 4.2f, 5.95f, -0.3f, Material.BRONZE),
              b(-0.4f, 10.2f, -3.2f, 0.4f, 10.8f, -3.0f, Material.CHROME),
              b(-0.4f, 7.4f, -3.2f, 0.4f, 8.0f, -3.0f, Material.CHROME),
              b(-0.4f, 4.8f, -3.2f, 0.4f, 5.4f, -3.0f, Material.CHROME));
      case "bionic_joints" ->
          mirroredArmsAndLegs(
              List.of(
                  b(-3.5f, 0.4f, -2.3f, 1.2f, 2.9f, 2.3f, Material.GUNMETAL),
                  b(-3.55f, 0.8f, -2.35f, 1.25f, 1.3f, 2.35f, Material.RUBBER),
                  b(-3.9f, 1.0f, -1.2f, -3.5f, 2.6f, 1.2f, Material.CHROME),
                  b(1.2f, 1.0f, -1.2f, 1.6f, 2.6f, 1.2f, Material.CHROME),
                  b(-1.2f, -0.2f, -2.4f, 0.4f, 0.6f, 2.4f, Material.DARK_METAL),
                  b(-3.0f, 2.9f, -2.1f, 0.8f, 3.4f, 2.1f, Material.CHROME)),
              List.of(
                  b(-2.5f, 1.2f, -2.3f, 2.5f, 4.0f, 2.3f, Material.GUNMETAL),
                  b(-2.55f, 1.6f, -2.35f, 2.55f, 2.1f, 2.35f, Material.RUBBER),
                  b(-3.0f, 1.4f, -1.2f, -2.5f, 3.4f, 1.2f, Material.CHROME),
                  b(2.5f, 1.4f, -1.2f, 3.0f, 3.4f, 1.2f, Material.CHROME),
                  b(-1.6f, 1.4f, -2.8f, 1.6f, 3.6f, -2.3f, Material.CHROME),
                  b(-2.2f, 4.0f, -2.1f, 2.2f, 4.5f, 2.1f, Material.DARK_METAL)));
      case "spring_joints" ->
          legs(
              b(-1.6f, 4.6f, 1.6f, 1.6f, 5.2f, 2.8f, Material.GUNMETAL),
              b(-1.2f, 5.2f, 1.8f, 1.2f, 5.6f, 2.6f, Material.YELLOW),
              b(-1.5f, 5.6f, 1.7f, 1.5f, 6.0f, 2.7f, Material.CHROME),
              b(-1.2f, 6.0f, 1.8f, 1.2f, 6.4f, 2.6f, Material.YELLOW),
              b(-1.5f, 6.4f, 1.7f, 1.5f, 6.8f, 2.7f, Material.CHROME),
              b(-1.2f, 6.8f, 1.8f, 1.2f, 7.2f, 2.6f, Material.YELLOW),
              b(-1.5f, 7.2f, 1.7f, 1.5f, 7.6f, 2.7f, Material.CHROME),
              b(-1.2f, 7.6f, 1.8f, 1.2f, 8.0f, 2.6f, Material.YELLOW),
              b(-0.3f, 4.6f, 1.9f, 0.3f, 9.4f, 2.5f, Material.CHROME),
              b(-1.8f, 9.2f, 1.5f, 1.8f, 9.8f, 2.9f, Material.GUNMETAL),
              b(-1.0f, 9.8f, 1.7f, 1.0f, 10.4f, 2.7f, Material.YELLOW),
              b(-1.3f, 10.4f, 1.6f, 1.3f, 10.8f, 2.8f, Material.CHROME),
              b(-0.2f, 10.8f, 1.8f, 0.2f, 11.6f, 2.6f, Material.CHROME),
              b(-1.6f, 11.4f, 1.5f, 1.6f, 11.9f, 2.9f, Material.GUNMETAL));
      case "cyber_legs" ->
          legs(
              b(-2.4f, 0.8f, -2.4f, 2.4f, 5.6f, 2.4f, Material.CHROME),
              b(-2.2f, 5.4f, -2.3f, 2.2f, 11.6f, 2.3f, Material.TITANIUM),
              b(-2.6f, 5.2f, -2.6f, 2.6f, 6.8f, 2.6f, Material.DARK_METAL),
              b(-2.7f, 5.7f, -1.2f, -2.6f, 6.3f, 1.2f, Material.CHROME),
              b(2.6f, 5.7f, -1.2f, 2.7f, 6.3f, 1.2f, Material.CHROME),
              b(-2.5f, 1.2f, -2.5f, -2.3f, 5.4f, -1.0f, Material.BLUE_STEEL),
              b(2.3f, 1.2f, -2.5f, 2.5f, 5.4f, -1.0f, Material.BLUE_STEEL),
              b(-2.4f, 2.0f, -2.5f, -1.6f, 2.4f, -2.4f, Material.CYAN),
              b(1.6f, 2.0f, -2.5f, 2.4f, 2.4f, -2.4f, Material.CYAN),
              b(-2.2f, 6.6f, -2.4f, -1.6f, 9.8f, -2.3f, Material.BLUE_STEEL),
              b(1.6f, 6.6f, -2.4f, 2.2f, 9.8f, -2.3f, Material.BLUE_STEEL),
              b(-2.3f, 10.8f, -2.4f, 2.3f, 11.8f, 2.4f, Material.GUNMETAL),
              b(-2.0f, 11.6f, -2.2f, 2.0f, 12.2f, 2.2f, Material.CHROME));
      case "reinforced_tendons" ->
          legs(
              b(-1.3f, 6.4f, 2.1f, -0.7f, 11.2f, 2.8f, Material.CHROME),
              b(0.7f, 6.4f, 2.1f, 1.3f, 11.2f, 2.8f, Material.CHROME),
              b(-0.25f, 6.8f, 2.3f, 0.25f, 11.0f, 2.9f, Material.NEON_RED),
              b(-1.8f, 6.0f, 1.8f, 1.8f, 6.8f, 3.0f, Material.GUNMETAL),
              b(-1.9f, 10.8f, 1.7f, 1.9f, 11.8f, 3.0f, Material.GUNMETAL),
              b(-1.6f, 7.2f, 2.8f, -1.2f, 7.8f, 3.0f, Material.GOLD),
              b(1.2f, 7.2f, 2.8f, 1.6f, 7.8f, 3.0f, Material.GOLD),
              b(-1.6f, 9.8f, 2.8f, -1.2f, 10.4f, 3.0f, Material.GOLD),
              b(1.2f, 9.8f, 2.8f, 1.6f, 10.4f, 3.0f, Material.GOLD));
      case "fortified_ankles" ->
          legs(
              b(-2.8f, 9.4f, -2.8f, 2.8f, 11.6f, 2.8f, Material.GUNMETAL),
              b(-3.2f, 11.2f, -4.2f, 3.2f, 12.2f, 2.4f, Material.DARK_METAL),
              b(-2.8f, 11.3f, -4.7f, 2.8f, 12.0f, -4.1f, Material.GOLD),
              b(-3.3f, 10.0f, -2.0f, -2.8f, 11.5f, 2.0f, Material.MUSTARD),
              b(2.8f, 10.0f, -2.0f, 3.3f, 11.5f, 2.0f, Material.MUSTARD),
              b(-2.1f, 9.6f, -1.0f, -1.3f, 11.2f, 0.8f, Material.CHROME),
              b(1.3f, 9.6f, -1.0f, 2.1f, 11.2f, 0.8f, Material.CHROME),
              b(-2.1f, 10.4f, -1.1f, -1.3f, 10.7f, 0.9f, Material.GOLD),
              b(1.3f, 10.4f, -1.1f, 2.1f, 10.7f, 0.9f, Material.GOLD),
              b(-2.6f, 11.8f, -4.0f, 2.6f, 12.3f, 2.2f, Material.GUNMETAL),
              b(-2.8f, 11.4f, 1.8f, 2.8f, 12.2f, 2.8f, Material.CHROME));
      case "lynx_paws" ->
          legs(
              b(-2.6f, 11.3f, -4.4f, 2.6f, 12.3f, 2.2f, Material.CARBON),
              b(-2.4f, 11.4f, -4.8f, -1.2f, 12.1f, -4.3f, Material.CARBON),
              b(-0.6f, 11.4f, -4.8f, 0.6f, 12.1f, -4.3f, Material.CARBON),
              b(1.2f, 11.4f, -4.8f, 2.4f, 12.1f, -4.3f, Material.CARBON),
              b(-2.4f, 12.0f, -4.9f, -1.2f, 12.2f, -4.4f, Material.DARK_PURPLE),
              b(-0.6f, 12.0f, -4.9f, 0.6f, 12.2f, -4.4f, Material.DARK_PURPLE),
              b(1.2f, 12.0f, -4.9f, 2.4f, 12.2f, -4.4f, Material.DARK_PURPLE),
              b(-2.3f, 9.8f, -2.0f, 2.3f, 11.5f, 2.0f, Material.RUBBER),
              b(-2.7f, 11.3f, -3.2f, -2.6f, 12.2f, 1.0f, Material.DARK_PURPLE),
              b(2.6f, 11.3f, -3.2f, 2.7f, 12.2f, 1.0f, Material.DARK_PURPLE),
              b(-1.8f, 12.2f, -2.0f, 1.8f, 12.5f, 0.8f, Material.CARBON),
              b(-0.4f, 12.3f, 2.0f, 0.4f, 12.6f, 2.6f, Material.DARK_PURPLE));
      case "jenkins_tendons" ->
          legs(
              b(-3.1f, 2.0f, -1.8f, -2.4f, 6.2f, 2.0f, Material.CHROME),
              b(-3.6f, 2.6f, -0.6f, -3.1f, 5.4f, 1.8f, Material.BLUE_STEEL),
              b(-3.15f, 2.4f, -1.9f, -2.35f, 3.2f, -1.8f, Material.MEDICAL),
              b(-3.15f, 5.4f, -1.9f, -2.35f, 6.2f, -1.8f, Material.NEON_RED),
              b(-3.0f, 6.0f, -1.6f, -2.3f, 10.0f, 1.8f, Material.TITANIUM),
              b(-3.5f, 6.6f, -0.4f, -3.0f, 9.4f, 1.6f, Material.BLUE_STEEL),
              b(-3.05f, 7.0f, -1.7f, -2.25f, 9.4f, -1.6f, Material.CYAN),
              b(-2.8f, 6.2f, 1.6f, -2.4f, 9.8f, 2.0f, Material.CHROME),
              b(-2.6f, 1.6f, -1.0f, -2.2f, 2.2f, 1.2f, Material.GUNMETAL),
              b(-2.7f, 9.8f, -1.0f, -2.3f, 10.4f, 1.2f, Material.GUNMETAL));
      case "monowire" ->
          rightArm(
              b(-2.8f, 8.0f, -2.2f, 0.8f, 9.4f, 2.2f, Material.CARBON),
              b(-2.4f, 9.4f, -2.0f, 0.4f, 10.6f, 2.0f, Material.GUNMETAL),
              b(-1.8f, 10.6f, -1.4f, -0.2f, 11.4f, 1.4f, Material.NEON_ORANGE),
              b(-0.6f, 11.4f, -0.2f, 0.2f, 15.0f, 0.6f, Material.NEON_ORANGE),
              b(-0.65f, 15.0f, -0.25f, 0.25f, 15.6f, 0.65f, Material.NEON_RED),
              b(-2.0f, 8.4f, -2.3f, -1.2f, 9.2f, -2.2f, Material.CYAN));
      default -> null;
    };
  }

  public static boolean isLeft(Part part) {
    return part == Part.LEFT_ARM || part == Part.LEFT_LEG;
  }

  private static Model body(Box... boxes) {
    return new Model(List.of(new Group(Part.BODY, false, List.of(boxes))));
  }

  private static Model head(Box... boxes) {
    return new Model(List.of(new Group(Part.HEAD, false, List.of(boxes))));
  }

  private static Model rightArm(Box... boxes) {
    return new Model(List.of(new Group(Part.RIGHT_ARM, false, List.of(boxes))));
  }

  private static Model arms(Box... boxes) {
    return new Model(
        List.of(
            new Group(Part.RIGHT_ARM, false, List.of(boxes)),
            new Group(Part.LEFT_ARM, true, List.of(boxes))));
  }

  private static Model legs(Box... boxes) {
    return new Model(
        List.of(
            new Group(Part.RIGHT_LEG, false, List.of(boxes)),
            new Group(Part.LEFT_LEG, true, List.of(boxes))));
  }

  private static Model mirroredArmsAndLegs(List<Box> arms, List<Box> legs) {
    return new Model(
        List.of(
            new Group(Part.RIGHT_ARM, false, arms),
            new Group(Part.LEFT_ARM, true, arms),
            new Group(Part.RIGHT_LEG, false, legs),
            new Group(Part.LEFT_LEG, true, legs)));
  }

  private static Box b(
      float x0, float y0, float z0, float x1, float y1, float z1, Material material) {
    return new Box(x0, y0, z0, x1, y1, z1, material);
  }

  private ImplantModels() {}
}
