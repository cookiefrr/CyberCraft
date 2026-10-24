package com.cybercraft.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;

public sealed interface HackTarget permits HackTarget.Entity, HackTarget.Block {
  record Entity(int id) implements HackTarget {}

  record Block(BlockPos pos) implements HackTarget {
    public Block {
      pos = pos.immutable();
    }
  }

  static void write(RegistryFriendlyByteBuf buf, HackTarget target) {
    buf.writeBoolean(target instanceof Entity);
    if (target instanceof Entity e) buf.writeVarInt(e.id());
    else buf.writeBlockPos(((Block) target).pos());
  }

  static HackTarget read(RegistryFriendlyByteBuf buf) {
    return buf.readBoolean() ? new Entity(buf.readVarInt()) : new Block(buf.readBlockPos());
  }
}
