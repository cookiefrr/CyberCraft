package com.cybercraft.data;

import net.fabricmc.fabric.api.datagen.v1.*;

public final class DataGenerators implements DataGeneratorEntrypoint {
  public void onInitializeDataGenerator(FabricDataGenerator generator) {
    var pack = generator.createPack();
    pack.addProvider((net.minecraft.data.DataProvider.Factory<RecipeData>) RecipeData::new);
    pack.addProvider((net.minecraft.data.DataProvider.Factory<GameplayData>) GameplayData::new);
    pack.addProvider((net.minecraft.data.DataProvider.Factory<AssetData>) AssetData::new);
    pack.addProvider(
        (net.minecraft.data.DataProvider.Factory<EnglishLanguage>) EnglishLanguage::new);
  }
}
