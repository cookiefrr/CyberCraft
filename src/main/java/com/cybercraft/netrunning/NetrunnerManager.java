package com.cybercraft.netrunning;

import com.cybercraft.cyberware.*;
import com.cybercraft.network.*;
import com.cybercraft.util.Targeting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.*;

public final class NetrunnerManager {
  public static final double RANGE = 64;
  public static final int COOLDOWN_TICKS = 10;

  public static float ramCost(LivingEntity entity, Quickhack hack) {
    return Math.max(
        1,
        Math.round(
                hack.ramCost()
                    * (CyberwareManager.get(entity).has(Cyberware.BEHAVIORAL_IMPRINT) ? .9f : 1)
                    * 10)
            / 10f);
  }

  public static boolean allowed(LivingEntity hacker, LivingEntity target) {
    if (!Targeting.isValidTarget(target, hacker) || hacker.isAlliedTo(target)) return false;
    if (target instanceof Player p && (p.isCreative() || p.isSpectator())) return false;
    if (hacker instanceof ServerPlayer p
        && target instanceof Player victim
        && (!p.server.isPvpAllowed() || !p.canHarmPlayer(victim))) return false;
    return !(target instanceof TamableAnimal pet && pet.isOwnedBy(hacker));
  }

  public static int cast(
      ServerPlayer player, net.minecraft.resources.ResourceLocation id, HackTarget target) {
    var hack = Quickhack.byId(id);
    if (hack.isEmpty()) return fail(player, 1, "msg.cybercraft.no_target");
    if (!CyberwareManager.canHack(CyberwareManager.get(player))
        || !CyberwareManager.operational(player))
      return fail(player, 4, "msg.cybercraft.no_cyberdeck");
    var hit = Targeting.pick(player, RANGE);
    if (target instanceof HackTarget.Entity e) {
      if (hack.get().blockHack()
          || !(hit instanceof EntityHitResult actual)
          || actual.getEntity().getId() != e.id()
          || !(actual.getEntity() instanceof LivingEntity living)
          || !allowed(player, living)) return fail(player, 1, "msg.cybercraft.no_target");
      return castLiving(player, hack.get(), living);
    }
    if (!hack.get().blockHack()
        || !(hit instanceof BlockHitResult actual)
        || hit.getType() == HitResult.Type.MISS
        || !actual.getBlockPos().equals(((HackTarget.Block) target).pos())
        || !BlockHacks.allowed(player, actual)) return fail(player, 1, "msg.cybercraft.no_target");
    var state = player.level().getBlockState(actual.getBlockPos());
    if (hack.get() == Quickhack.REMOTE_ACCESS
        ? !BlockHacks.remoteSupported(state)
        : !BlockHacks.disruptSupported(state)) return fail(player, 1, "msg.cybercraft.no_target");
    int validation = pay(player, hack.get());
    if (validation != 0) return validation;
    BlockHacks.apply(player, hack.get(), actual);
    success(player, hack.get());
    return 0;
  }

  public static int castLiving(LivingEntity hacker, Quickhack hack, LivingEntity target) {
    if (hack.blockHack()
        || !CyberwareManager.canHack(CyberwareManager.get(hacker))
        || !CyberwareManager.operational(hacker)
        || !allowed(hacker, target)
        || hacker.distanceToSqr(target) > RANGE * RANGE
        || !hacker.hasLineOfSight(target)) return fail(hacker, 1, "msg.cybercraft.no_target");
    int validation = pay(hacker, hack);
    if (validation != 0) return validation;
    hack.apply((ServerLevel) hacker.level(), hacker, target);
    success(hacker, hack);
    return 0;
  }

  private static int pay(LivingEntity entity, Quickhack hack) {
    long now = entity.level().getGameTime();
    if (now < CyberwareManager.get(entity).cooldowns().getOrDefault("quickhack", 0L))
      return fail(entity, 3, "msg.cybercraft.cooldown_short");
    if (!CyberwareManager.spendRam(entity, ramCost(entity, hack)))
      return fail(entity, 2, "msg.cybercraft.no_ram");
    CyberwareManager.ready(entity, "quickhack", COOLDOWN_TICKS);
    return 0;
  }

  private static void success(LivingEntity entity, Quickhack hack) {
    CyberwareManager.set(entity, CyberwareManager.get(entity).withSelectedHack(hack.resource()));
    CyberwareManager.sendSync(entity);
    if (entity instanceof ServerPlayer p) HackResultPayload.send(p, true, 0);
  }

  public static void select(ServerPlayer player, net.minecraft.resources.ResourceLocation id) {
    Quickhack.byId(id)
        .ifPresent(
            h -> {
              CyberwareManager.set(player, CyberwareManager.get(player).withSelectedHack(id));
              CyberwareManager.sendSync(player);
            });
  }

  public static void select(ServerPlayer player, int delta) {
    select(
        player, Quickhack.byIndex(CyberwareManager.get(player).selectedHack() + delta).resource());
  }

  private static int fail(LivingEntity entity, int code, String message) {
    CyberwareManager.say(entity, Component.translatable(message));
    if (entity instanceof ServerPlayer p) HackResultPayload.send(p, false, code);
    return code;
  }

  public static void clear() {}

  private NetrunnerManager() {}
}
