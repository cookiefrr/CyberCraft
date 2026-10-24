package com.cybercraft.equipment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record SlotDefinition(
    String name, ResourceLocation icon, int order, int count, String anchor) {
  public static final Codec<SlotDefinition> CODEC =
      RecordCodecBuilder.create(
          i ->
              i.group(
                      Codec.STRING.fieldOf("name").forGetter(SlotDefinition::name),
                      ResourceLocation.CODEC.fieldOf("icon").forGetter(SlotDefinition::icon),
                      Codec.INT.optionalFieldOf("order", 0).forGetter(SlotDefinition::order),
                      Codec.intRange(1, 32)
                          .optionalFieldOf("count", 1)
                          .forGetter(SlotDefinition::count),
                      Codec.STRING
                          .optionalFieldOf("anchor", "body")
                          .forGetter(SlotDefinition::anchor))
                  .apply(i, SlotDefinition::new));
}
