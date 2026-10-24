package com.cybercraft.client;

import com.cybercraft.netrunning.*;
import com.cybercraft.network.HackTarget;
import com.cybercraft.util.Targeting;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.*;

public final class ScannerTargets {
  public static HitResult pick(Minecraft client) {
    return client.player == null
        ? null
        : Targeting.pick(
            client.player, Targeting.rangeForRenderDistance(client.options.renderDistance().get()));
  }

  public static BlockHitResult block(Minecraft client) {
    var hit = pick(client);
    if (!(hit instanceof BlockHitResult b)
        || hit.getType() == HitResult.Type.MISS
        || client.level == null) return null;
    var state = client.level.getBlockState(b.getBlockPos());
    return BlockHacks.remoteSupported(state) || BlockHacks.disruptSupported(state) ? b : null;
  }

  public static List<Quickhack> available(Minecraft client) {
    var block = block(client);
    return Arrays.stream(Quickhack.values())
        .filter(
            h ->
                block == null
                    ? !h.blockHack()
                    : h == Quickhack.REMOTE_ACCESS
                        ? BlockHacks.remoteSupported(
                            client.level.getBlockState(block.getBlockPos()))
                        : h == Quickhack.DISRUPTION
                            && BlockHacks.disruptSupported(
                                client.level.getBlockState(block.getBlockPos())))
        .toList();
  }

  public static Quickhack selected(Minecraft client) {
    var list = available(client);
    var id = ClientCyberwareState.get().selectedHackId();
    return list.stream().filter(h -> h.resource().equals(id)).findFirst().orElse(list.getFirst());
  }

  public static void cycle(Minecraft client, int delta) {
    var list = available(client);
    int index = list.indexOf(selected(client));
    var hack = list.get(Math.floorMod(index + delta, list.size()));
    ClientCyberwareState.set(ClientCyberwareState.get().withSelectedHack(hack.resource()));
    net.neoforged.neoforge.network.PacketDistributor.sendToServer(
        new com.cybercraft.network.SelectHackPayload(hack.resource()));
  }

  public static HackTarget target(Minecraft client) {
    var hit = pick(client);
    if (hit instanceof EntityHitResult e) return new HackTarget.Entity(e.getEntity().getId());
    var b = block(client);
    return b == null ? null : new HackTarget.Block(b.getBlockPos());
  }
}
