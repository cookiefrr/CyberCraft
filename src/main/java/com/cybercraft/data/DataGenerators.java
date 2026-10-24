package com.cybercraft.data;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = "cybercraft")
public final class DataGenerators {
  @SubscribeEvent
  public static void gather(GatherDataEvent event) {
    var g = event.getGenerator();
    var output = g.getPackOutput();
    g.addProvider(event.includeServer(), new RecipeData(output));
    g.addProvider(event.includeServer(), new GameplayData(output));
    g.addProvider(event.includeClient(), new AssetData(output));
    g.addProvider(event.includeClient(), new EnglishLanguage(output));
  }
}
