package com.cybercraft.equipment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public record ImplantDefinition(
    ResourceLocation item,
    List<ResourceLocation> slots,
    int tier,
    float capacity,
    ResourceLocation behavior,
    ResourceLocation model) {
  public ImplantDefinition {
    slots = List.copyOf(slots);
  }

  public static final Codec<ImplantDefinition> CODEC =
      RecordCodecBuilder.create(
          i ->
              i.group(
                      ResourceLocation.CODEC.fieldOf("item").forGetter(ImplantDefinition::item),
                      ResourceLocation.CODEC
                          .listOf()
                          .fieldOf("slots")
                          .forGetter(ImplantDefinition::slots),
                      Codec.intRange(1, 3).fieldOf("tier").forGetter(ImplantDefinition::tier),
                      Codec.floatRange(0, 1000)
                          .fieldOf("capacity")
                          .forGetter(ImplantDefinition::capacity),
                      ResourceLocation.CODEC
                          .fieldOf("behavior")
                          .forGetter(ImplantDefinition::behavior),
                      ResourceLocation.CODEC.fieldOf("model").forGetter(ImplantDefinition::model))
                  .apply(i, ImplantDefinition::new));
}
