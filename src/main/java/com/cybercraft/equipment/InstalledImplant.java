package com.cybercraft.equipment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record InstalledImplant(
    ResourceLocation slot,
    int index,
    ResourceLocation definition,
    ResourceLocation behavior,
    ItemStack stack) {
  public static final Codec<InstalledImplant> CODEC =
      RecordCodecBuilder.create(
          i ->
              i.group(
                      ResourceLocation.CODEC.fieldOf("slot").forGetter(InstalledImplant::slot),
                      Codec.intRange(0, 31).fieldOf("index").forGetter(InstalledImplant::index),
                      ResourceLocation.CODEC
                          .fieldOf("definition")
                          .forGetter(InstalledImplant::definition),
                      ResourceLocation.CODEC
                          .fieldOf("behavior")
                          .forGetter(InstalledImplant::behavior),
                      ItemStack.CODEC.fieldOf("stack").forGetter(InstalledImplant::stack))
                  .apply(i, InstalledImplant::new));

  public InstalledImplant {
    stack = stack.copyWithCount(1);
  }
}
