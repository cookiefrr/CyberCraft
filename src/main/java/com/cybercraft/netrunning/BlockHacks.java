package com.cybercraft.netrunning;

import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.BlockHitResult;

public final class BlockHacks extends SavedData {
  public record Disruption(ResourceLocation block, long until) {}

  private final Map<BlockPos, Disruption> entries = new HashMap<>();
  private static final Factory<BlockHacks> FACTORY =
      new Factory<>(BlockHacks::new, BlockHacks::load, null);

  public static BlockHacks get(ServerLevel level) {
    return level.getDataStorage().computeIfAbsent(FACTORY, "cybercraft_disruptions");
  }

  public static boolean remoteSupported(BlockState state) {
    return state.getBlock() instanceof DoorBlock
        || state.getBlock() instanceof TrapDoorBlock
        || state.getBlock() instanceof FenceGateBlock
        || state.getBlock() instanceof LeverBlock;
  }

  public static boolean disruptSupported(BlockState state) {
    return state.isSignalSource()
        && BuiltInRegistries.BLOCK.getKey(state.getBlock()).getNamespace().equals("minecraft");
  }

  public static boolean disrupted(Level level, BlockPos pos) {
    if (!(level instanceof ServerLevel server)) return false;
    var entry = get(server).entries.get(pos);
    return entry != null
        && entry.until() > level.getGameTime()
        && entry
            .block()
            .equals(BuiltInRegistries.BLOCK.getKey(level.getBlockState(pos).getBlock()));
  }

  public static boolean allowed(ServerPlayer player, BlockHitResult hit) {
    var level = player.serverLevel();
    var pos = hit.getBlockPos();
    if (!level.hasChunkAt(pos)
        || !player.mayBuild()
        || !level.mayInteract(player, pos)
        || player.server.isUnderSpawnProtection(level, pos, player)) return false;
    return net.fabricmc.fabric.api.event.player.UseBlockCallback.EVENT
            .invoker()
            .interact(player, level, InteractionHand.MAIN_HAND, hit)
        != net.minecraft.world.InteractionResult.FAIL;
  }

  public static boolean apply(ServerPlayer player, Quickhack hack, BlockHitResult hit) {
    var level = player.serverLevel();
    var pos = hit.getBlockPos();
    var state = level.getBlockState(pos);
    if (hack == Quickhack.DISRUPTION) {
      if (!disruptSupported(state)) return false;
      var data = get(level);
      data.entries.put(
          pos.immutable(),
          new Disruption(
              BuiltInRegistries.BLOCK.getKey(state.getBlock()), level.getGameTime() + 100));
      data.setDirty();
      notify(level, pos, state.getBlock());
      return true;
    }
    if (!remoteSupported(state)) return false;
    if (state.getBlock() instanceof DoorBlock door)
      door.setOpen(player, level, state, pos, !state.getValue(DoorBlock.OPEN));
    else if (state.getBlock() instanceof TrapDoorBlock) {
      level.setBlock(pos, state.cycle(TrapDoorBlock.OPEN), 3);
      level.gameEvent(player, net.minecraft.world.level.gameevent.GameEvent.BLOCK_OPEN, pos);
    } else if (state.getBlock() instanceof FenceGateBlock)
      level.setBlock(pos, state.cycle(FenceGateBlock.OPEN), 3);
    else if (state.getBlock() instanceof LeverBlock lever) lever.pull(state, level, pos, player);
    return true;
  }

  public static void tick(ServerLevel level) {
    var data = get(level);
    if (data.entries.isEmpty()) return;
    for (var entry : new ArrayList<>(data.entries.entrySet())) {
      BlockPos pos = entry.getKey();
      if (!level.hasChunkAt(pos)) continue;
      var state = level.getBlockState(pos);
      if (entry.getValue().until() <= level.getGameTime()
          || !entry.getValue().block().equals(BuiltInRegistries.BLOCK.getKey(state.getBlock()))) {
        data.entries.remove(pos);
        data.setDirty();
        notify(level, pos, state.getBlock());
      }
    }
  }

  private static void notify(ServerLevel level, BlockPos pos, Block block) {
    level.neighborChanged(pos, block, pos);
    level.updateNeighborsAt(pos, block);
    for (Direction d : Direction.values()) level.updateNeighborsAt(pos.relative(d), block);
  }

  public static BlockHacks load(CompoundTag tag, HolderLookup.Provider lookup) {
    var data = new BlockHacks();
    var list = tag.getList("entries", Tag.TAG_COMPOUND);
    for (int i = 0; i < list.size(); i++) {
      var e = list.getCompound(i);
      var id = ResourceLocation.tryParse(e.getString("block"));
      if (id != null)
        data.entries.put(BlockPos.of(e.getLong("pos")), new Disruption(id, e.getLong("until")));
    }
    return data;
  }

  @Override
  public CompoundTag save(CompoundTag tag, HolderLookup.Provider lookup) {
    var list = new ListTag();
    entries.forEach(
        (pos, entry) -> {
          var e = new CompoundTag();
          e.putLong("pos", pos.asLong());
          e.putString("block", entry.block().toString());
          e.putLong("until", entry.until());
          list.add(e);
        });
    tag.put("entries", list);
    return tag;
  }
}
