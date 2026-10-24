package com.cybercraft.cyberware;

import com.cybercraft.CyberCraft;
import com.cybercraft.equipment.InstalledImplant;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record CyberwareData(
    List<InstalledImplant> equipment,
    List<ItemStack> recovery,
    float tolerance,
    float ram,
    boolean overloaded,
    ResourceLocation selectedHackId,
    long sandevistanEnd,
    long sandevistanActiveUntil,
    Map<String, Long> cooldowns,
    boolean spawnRolled) {
  public static final float MAX_TOLERANCE = 100, MAX_RAM = 100;
  public static final CyberwareData DEFAULT =
      new CyberwareData(
          List.of(),
          List.of(),
          100,
          100,
          false,
          CyberCraft.id("short_circuit"),
          0,
          0,
          Map.of(),
          false);
  public static final Codec<CyberwareData> CODEC =
      RecordCodecBuilder.create(
          i ->
              i.group(
                      InstalledImplant.CODEC
                          .listOf()
                          .optionalFieldOf("equipment", List.of())
                          .forGetter(CyberwareData::equipment),
                      ItemStack.OPTIONAL_CODEC
                          .listOf()
                          .optionalFieldOf("recovery", List.of())
                          .forGetter(CyberwareData::recovery),
                      Codec.FLOAT
                          .optionalFieldOf("tolerance", 100f)
                          .forGetter(CyberwareData::tolerance),
                      Codec.FLOAT.optionalFieldOf("ram", 100f).forGetter(CyberwareData::ram),
                      Codec.BOOL
                          .optionalFieldOf("overloaded", false)
                          .forGetter(CyberwareData::overloaded),
                      ResourceLocation.CODEC
                          .optionalFieldOf("selected_hack", CyberCraft.id("short_circuit"))
                          .forGetter(CyberwareData::selectedHackId),
                      Codec.LONG
                          .optionalFieldOf("os_cooldown", 0L)
                          .forGetter(CyberwareData::sandevistanEnd),
                      Codec.LONG
                          .optionalFieldOf("os_active_until", 0L)
                          .forGetter(CyberwareData::sandevistanActiveUntil),
                      Codec.unboundedMap(Codec.STRING, Codec.LONG)
                          .optionalFieldOf("cooldowns", Map.of())
                          .forGetter(CyberwareData::cooldowns),
                      Codec.BOOL
                          .optionalFieldOf("spawn_rolled", false)
                          .forGetter(CyberwareData::spawnRolled))
                  .apply(i, CyberwareData::new));

  public CyberwareData {
    equipment = List.copyOf(equipment);
    recovery = recovery.stream().map(ItemStack::copy).toList();
    cooldowns = Map.copyOf(cooldowns);
    tolerance = finiteClamp(tolerance, 100);
    ram = finiteClamp(ram, 100);
  }

  public static float finiteClamp(float value, float max) {
    return Float.isFinite(value) ? Math.max(0, Math.min(max, value)) : 0;
  }

  public boolean has(Cyberware implant) {
    return hasBehavior(CyberCraft.id(implant.id()));
  }

  public boolean hasBehavior(ResourceLocation behavior) {
    return equipment.stream().anyMatch(e -> e.behavior().equals(behavior));
  }

  public List<String> implants() {
    return equipment.stream()
        .filter(e -> e.behavior().getNamespace().equals(CyberCraft.MOD_ID))
        .map(e -> e.behavior().getPath())
        .toList();
  }

  public float maxRam() {
    return MAX_RAM;
  }

  public int selectedHack() {
    return com.cybercraft.netrunning.Quickhack.indexOf(selectedHackId);
  }

  public CyberwareData withEquipment(List<InstalledImplant> value, List<ItemStack> displaced) {
    return new CyberwareData(
        value,
        displaced,
        tolerance,
        ram,
        overloaded,
        selectedHackId,
        sandevistanEnd,
        sandevistanActiveUntil,
        cooldowns,
        spawnRolled);
  }

  public CyberwareData withTolerance(float value) {
    return new CyberwareData(
        equipment,
        recovery,
        value,
        ram,
        overloaded,
        selectedHackId,
        sandevistanEnd,
        sandevistanActiveUntil,
        cooldowns,
        spawnRolled);
  }

  public CyberwareData withRam(float value) {
    return new CyberwareData(
        equipment,
        recovery,
        tolerance,
        value,
        overloaded,
        selectedHackId,
        sandevistanEnd,
        sandevistanActiveUntil,
        cooldowns,
        spawnRolled);
  }

  public CyberwareData withOverloaded(boolean value) {
    return new CyberwareData(
        equipment,
        recovery,
        tolerance,
        ram,
        value,
        selectedHackId,
        sandevistanEnd,
        sandevistanActiveUntil,
        cooldowns,
        spawnRolled);
  }

  public CyberwareData withSelectedHack(int value) {
    return withSelectedHack(com.cybercraft.netrunning.Quickhack.byIndex(value).resource());
  }

  public CyberwareData withSelectedHack(ResourceLocation value) {
    return new CyberwareData(
        equipment,
        recovery,
        tolerance,
        ram,
        overloaded,
        value,
        sandevistanEnd,
        sandevistanActiveUntil,
        cooldowns,
        spawnRolled);
  }

  public CyberwareData withSandevistan(long active, long cooldown) {
    return new CyberwareData(
        equipment,
        recovery,
        tolerance,
        ram,
        overloaded,
        selectedHackId,
        cooldown,
        active,
        cooldowns,
        spawnRolled);
  }

  public CyberwareData withCooldown(String key, long until) {
    var copy = new HashMap<>(cooldowns);
    copy.put(key, until);
    return new CyberwareData(
        equipment,
        recovery,
        tolerance,
        ram,
        overloaded,
        selectedHackId,
        sandevistanEnd,
        sandevistanActiveUntil,
        copy,
        spawnRolled);
  }

  public CyberwareData withSpawnRolled() {
    return new CyberwareData(
        equipment,
        recovery,
        tolerance,
        ram,
        overloaded,
        selectedHackId,
        sandevistanEnd,
        sandevistanActiveUntil,
        cooldowns,
        true);
  }
}
