package com.cybercraft.test;

import com.cybercraft.netrunning.*;
import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.core.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.*;

public class DisruptionPersistenceTests {
  @GameTest(template = "cybercraft_tests:empty")
  public static void savedDisruptionsRoundTrip(GameTestHelper h) {
    var level = h.getLevel();
    var pos = h.absolutePos(new BlockPos(2, 1, 2));
    level.setBlockAndUpdate(pos, Blocks.REDSTONE_BLOCK.defaultBlockState());
    var player = TestPlayers.get(level, new GameProfile(UUID.randomUUID(), "persistence_test"));
    BlockHacks.apply(
        player,
        Quickhack.DISRUPTION,
        new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false));
    var saved = BlockHacks.get(level).save(new CompoundTag(), level.registryAccess());
    var loaded = BlockHacks.load(saved, level.registryAccess());
    h.assertTrue(
        saved.equals(loaded.save(new CompoundTag(), level.registryAccess())),
        "Restart codec changed positions, blocks, or expiry ticks");
    try {
      var folder =
          java.nio.file.Files.createTempDirectory(java.nio.file.Path.of("."), "disruption-test-");
      var storage =
          new net.minecraft.world.level.storage.DimensionDataStorage(
              folder.toFile(), level.getServer().getFixerUpper(), level.registryAccess());
      loaded.setDirty();
      storage.set("disruptions", loaded);
      storage.save();

      var reopened =
          new net.minecraft.world.level.storage.DimensionDataStorage(
              folder.toFile(), level.getServer().getFixerUpper(), level.registryAccess());
      var fromDisk =
          reopened.get(
              new net.minecraft.world.level.saveddata.SavedData.Factory<>(
                  BlockHacks::new, BlockHacks::load, null),
              "disruptions");
      h.assertTrue(
          fromDisk != null
              && saved.equals(fromDisk.save(new CompoundTag(), level.registryAccess())),
          "Saved disruption could not be reopened from disk");

      java.nio.file.Files.delete(folder.resolve("disruptions.dat"));
      java.nio.file.Files.delete(folder);
    } catch (java.io.IOException e) {
      throw new AssertionError("Disruption disk persistence failed", e);
    }
    h.succeed();
  }

  @GameTest(template = "cybercraft_tests:empty", timeoutTicks = 200)
  public static void unloadedExpiryDoesNotForceChunksOrRestoreBlocks(GameTestHelper h) {
    var level = h.getLevel();
    var pos = new BlockPos(10000000, 64, 10000000);
    h.assertFalse(level.hasChunkAt(pos), "Fixture chunk was already loaded");
    var saved = BlockHacks.get(level).save(new CompoundTag(), level.registryAccess());
    var entries = saved.getList("entries", net.minecraft.nbt.Tag.TAG_COMPOUND);
    var entry = new CompoundTag();
    entry.putLong("pos", pos.asLong());
    entry.putString("block", "minecraft:redstone_block");
    entry.putLong("until", level.getGameTime() + 1);
    entries.add(entry);
    level
        .getDataStorage()
        .set("cybercraft_disruptions", BlockHacks.load(saved, level.registryAccess()));
    h.runAfterDelay(
        5,
        () -> {
          BlockHacks.tick(level);
          h.assertFalse(level.hasChunkAt(pos), "Expiry forced an unloaded chunk");
          level.setBlockAndUpdate(pos.below(), Blocks.STONE.defaultBlockState());
          level.setBlockAndUpdate(pos, Blocks.LEVER.defaultBlockState());
          BlockHacks.tick(level);
          h.assertTrue(
              level.getBlockState(pos).is(Blocks.LEVER), "Reload restored a stale source snapshot");
          var remaining =
              BlockHacks.get(level)
                  .save(new CompoundTag(), level.registryAccess())
                  .getList("entries", net.minecraft.nbt.Tag.TAG_COMPOUND);
          for (int i = 0; i < remaining.size(); i++)
            h.assertTrue(
                remaining.getCompound(i).getLong("pos") != pos.asLong(),
                "Expired entry survived chunk loading");
          level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
          level.setBlockAndUpdate(pos.below(), Blocks.AIR.defaultBlockState());
          h.succeed();
        });
  }
}
