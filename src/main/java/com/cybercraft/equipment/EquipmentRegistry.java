package com.cybercraft.equipment;

import com.cybercraft.CyberCraft;
import java.util.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

public final class EquipmentRegistry {
  public static final ResourceKey<Registry<SlotDefinition>> SLOTS =
      ResourceKey.createRegistryKey(CyberCraft.id("slot_type"));
  public static final ResourceKey<Registry<ImplantDefinition>> IMPLANTS =
      ResourceKey.createRegistryKey(CyberCraft.id("implant"));

  public record Position(ResourceLocation type, int index, SlotDefinition definition) {}

  public static void register(DataPackRegistryEvent.NewRegistry event) {
    event.dataPackRegistry(SLOTS, SlotDefinition.CODEC, SlotDefinition.CODEC);
    event.dataPackRegistry(IMPLANTS, ImplantDefinition.CODEC, ImplantDefinition.CODEC);
  }

  public static List<Position> positions(Level level) {
    List<Position> result = new ArrayList<>();
    level.registryAccess().registryOrThrow(SLOTS).entrySet().stream()
        .sorted(
            Comparator.<Map.Entry<ResourceKey<SlotDefinition>, SlotDefinition>>comparingInt(
                    e -> e.getValue().order())
                .thenComparing(e -> e.getKey().location().toString()))
        .forEach(
            e -> {
              for (int n = 0; n < e.getValue().count(); n++)
                result.add(new Position(e.getKey().location(), n, e.getValue()));
            });
    return List.copyOf(result);
  }

  public static Optional<Map.Entry<ResourceKey<ImplantDefinition>, ImplantDefinition>> find(
      Level level, ItemStack stack) {
    if (stack.isEmpty()) return Optional.empty();
    var id = BuiltInRegistries.ITEM.getKey(stack.getItem());
    return level.registryAccess().registryOrThrow(IMPLANTS).entrySet().stream()
        .filter(e -> e.getValue().item().equals(id))
        .findFirst();
  }

  public static ImplantDefinition definition(Level level, ResourceLocation id) {
    return level.registryAccess().registryOrThrow(IMPLANTS).get(id);
  }

  public static void validate(Level level) {
    Set<ResourceLocation> items = new HashSet<>();
    var slots = level.registryAccess().registryOrThrow(SLOTS);
    for (var entry : level.registryAccess().registryOrThrow(IMPLANTS).entrySet()) {
      var d = entry.getValue();
      if (!items.add(d.item())
          || !BuiltInRegistries.ITEM.containsKey(d.item())
          || d.slots().isEmpty()
          || d.slots().stream().anyMatch(s -> !slots.containsKey(s))
          || !Float.isFinite(d.capacity())
          || !ImplantBehaviors.contains(d.behavior()))
        throw new IllegalStateException("Invalid implant definition: " + entry.getKey().location());
    }
  }

  private EquipmentRegistry() {}
}
