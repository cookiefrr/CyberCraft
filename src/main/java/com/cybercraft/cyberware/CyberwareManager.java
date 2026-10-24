package com.cybercraft.cyberware;

import com.cybercraft.CyberCraft;
import com.cybercraft.equipment.*;
import com.cybercraft.menu.CyberwareMenu;
import com.cybercraft.network.CyberwareSyncPayload;
import com.cybercraft.registry.ModAttachments;
import java.util.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class CyberwareManager {
  public static final float BASE_REGEN = 3,
      OVERLOAD_REGEN_FACTOR = .5f,
      OVERLOAD_EXIT = 25,
      OVERLOAD_DAMAGE = 2,
      RAM_REGEN = .34f;
  public static final float BASE_CAPACITY = 20,
      CAPACITY_PER_LEVEL = 2,
      MAX_CAPACITY = 60,
      MANTIS_BONUS_DAMAGE = 6;

  public enum InstallResult {
    INSTALLED,
    ALREADY_INSTALLED,
    SLOT_OCCUPIED,
    NOT_ENOUGH_CAPACITY,
    BLOCKED
  }

  public static CyberwareData get(LivingEntity entity) {
    return entity.getData(ModAttachments.CYBERWARE);
  }

  public static void set(LivingEntity entity, CyberwareData data) {
    entity.setData(ModAttachments.CYBERWARE, data);
  }

  public static boolean hasInstalled(LivingEntity entity, ResourceLocation id) {
    return get(entity).equipment().stream().anyMatch(e -> e.definition().equals(id));
  }

  public static float maxCapacity(LivingEntity entity) {
    float base =
        entity instanceof Player p
            ? Math.min(MAX_CAPACITY, BASE_CAPACITY + p.experienceLevel * CAPACITY_PER_LEVEL)
            : MAX_CAPACITY;
    return base
        + (entity instanceof Player && get(entity).has(Cyberware.CHROME_COMPRESSOR) ? 20 : 0);
  }

  public static float usedCapacity(LivingEntity entity) {
    return usedCapacity(entity, get(entity));
  }

  public static float usedCapacity(LivingEntity entity, CyberwareData data) {
    float sum = 0;
    for (var entry : data.equipment()) {
      var d = EquipmentRegistry.definition(entity.level(), entry.definition());
      if (d != null) sum += d.capacity();
    }
    return sum;
  }

  public static boolean operationalForDeath(LivingEntity entity) {
    return !get(entity).overloaded() && usedCapacity(entity) <= maxCapacity(entity);
  }

  public static boolean operational(LivingEntity entity) {
    return entity.isAlive()
        && !get(entity).overloaded()
        && usedCapacity(entity) <= maxCapacity(entity);
  }

  public static boolean canScan(CyberwareData data) {
    return !data.overloaded() && data.has(Cyberware.KIROSHI_OPTICS);
  }

  public static boolean canHack(CyberwareData data) {
    return canScan(data) && data.has(Cyberware.CYBERDECK);
  }

  public static void sendSync(LivingEntity entity) {
    CyberwareSyncPayload.send(entity, get(entity));
  }

  public static void openMenu(ServerPlayer player) {
    if (player.containerMenu instanceof CyberwareMenu) return;
    reconcile(player);
    player.openMenu(
        new SimpleMenuProvider(
            (id, inv, p) -> new CyberwareMenu(id, inv),
            Component.translatable("screen.cybercraft.cyberware")),
        buf -> buf.writeVarInt(get(player).recovery().size()));
    sendSync(player);
  }

  public static boolean canEquip(
      LivingEntity entity, ResourceLocation slot, int index, ItemStack stack) {
    if (stack.isEmpty()) return true;
    if (!entity.isAlive() || get(entity).overloaded()) return false;
    var position =
        EquipmentRegistry.positions(entity.level()).stream()
            .filter(p -> p.type().equals(slot) && p.index() == index)
            .findFirst();
    var definition = EquipmentRegistry.find(entity.level(), stack);
    if (position.isEmpty()
        || definition.isEmpty()
        || !definition.get().getValue().slots().contains(slot)) return false;
    var data = get(entity);
    var id = definition.get().getKey().location();
    if (data.equipment().stream()
        .anyMatch(e -> e.definition().equals(id) && !(e.slot().equals(slot) && e.index() == index)))
      return false;
    float used = usedCapacity(entity);
    var old = at(data, slot, index);
    if (old.isPresent()) {
      var d = EquipmentRegistry.definition(entity.level(), old.get().definition());
      if (d != null) used -= d.capacity();
    }
    float max = maxCapacity(entity);
    if (entity instanceof Player
        && old.isPresent()
        && old.get().behavior().equals(CyberCraft.id("chrome_compressor"))) max -= 20;
    if (entity instanceof Player
        && definition.get().getValue().behavior().equals(CyberCraft.id("chrome_compressor")))
      max += 20;
    return used + definition.get().getValue().capacity() <= max;
  }

  public static Optional<InstalledImplant> at(
      CyberwareData data, ResourceLocation slot, int index) {
    return data.equipment().stream()
        .filter(e -> e.slot().equals(slot) && e.index() == index)
        .findFirst();
  }

  public static void equip(LivingEntity entity, ResourceLocation slot, int index, ItemStack stack) {
    if (!stack.isEmpty() && !canEquip(entity, slot, index, stack))
      throw new IllegalArgumentException("Invalid implant transaction");
    var data = get(entity);
    var previous = at(data, slot, index);
    var list = new ArrayList<>(data.equipment());
    list.removeIf(e -> e.slot().equals(slot) && e.index() == index);
    if (!stack.isEmpty()) {
      var def = EquipmentRegistry.find(entity.level(), stack).orElseThrow();
      list.add(
          new InstalledImplant(
              slot, index, def.getKey().location(), def.getValue().behavior(), stack));
    }
    set(entity, data.withEquipment(list, data.recovery()));
    previous.ifPresent(e -> ImplantBehaviors.changed(entity, e, false));
    at(get(entity), slot, index).ifPresent(e -> ImplantBehaviors.changed(entity, e, true));
    if (previous.isPresent()
        && (previous.get().behavior().equals(CyberCraft.id("sandevistan"))
            || previous.get().behavior().equals(CyberCraft.id("berserk"))))
      set(entity, get(entity).withSandevistan(0, get(entity).sandevistanEnd()));
    ImplantEffects.reconcile(entity);
    sendSync(entity);
  }

  public static InstallResult tryInstall(ServerPlayer player, ItemStack source) {
    var def = EquipmentRegistry.find(player.level(), source);
    if (def.isEmpty()) return InstallResult.BLOCKED;
    if (hasInstalled(player, def.get().getKey().location())) return InstallResult.ALREADY_INSTALLED;
    for (var pos : EquipmentRegistry.positions(player.level())) {
      if (at(get(player), pos.type(), pos.index()).isEmpty()
          && canEquip(player, pos.type(), pos.index(), source)) {
        Component name = source.getHoverName();
        equip(player, pos.type(), pos.index(), source.copyWithCount(1));
        if (!player.getAbilities().instabuild) source.shrink(1);
        say(player, Component.translatable("msg.cybercraft.installed", name));
        return InstallResult.INSTALLED;
      }
    }
    say(player, Component.translatable("msg.cybercraft.install_failed"));
    return InstallResult.BLOCKED;
  }

  public static void reconcile(LivingEntity entity) {
    var data = get(entity);
    var keep = new ArrayList<InstalledImplant>();
    var recovery = new ArrayList<>(data.recovery().stream().filter(s -> !s.isEmpty()).toList());
    var seen = new HashSet<ResourceLocation>();
    var positions = EquipmentRegistry.positions(entity.level());
    for (var entry : data.equipment()) {
      var def = EquipmentRegistry.definition(entity.level(), entry.definition());
      boolean valid =
          def != null
              && def.item()
                  .equals(
                      net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(
                          entry.stack().getItem()))
              && def.slots().contains(entry.slot())
              && positions.stream()
                  .anyMatch(p -> p.type().equals(entry.slot()) && p.index() == entry.index())
              && seen.add(entry.definition())
              && keep.stream()
                  .noneMatch(e -> e.slot().equals(entry.slot()) && e.index() == entry.index());
      if (valid)
        keep.add(
            new InstalledImplant(
                entry.slot(), entry.index(), entry.definition(), def.behavior(), entry.stack()));
      else recovery.add(entry.stack().copy());
    }
    set(entity, data.withEquipment(keep, recovery));
  }

  public static void tick(LivingEntity entity) {
    var data = get(entity);
    float drain =
        data.overloaded()
            ? 0
            : (float)
                data.equipment().stream()
                    .filter(e -> e.behavior().getNamespace().equals(CyberCraft.MOD_ID))
                    .mapToDouble(
                        e ->
                            Cyberware.byId(e.behavior().getPath())
                                .map(Cyberware::passiveDrain)
                                .orElse(0f))
                    .sum();
    var next =
        data.withTolerance(
                data.tolerance()
                    + BASE_REGEN * (data.overloaded() ? OVERLOAD_REGEN_FACTOR : 1)
                    - drain)
            .withRam(data.ram() + ramRegen(data));
    if (!next.overloaded() && next.tolerance() <= 0) next = next.withOverloaded(true);
    else if (next.overloaded() && next.tolerance() >= OVERLOAD_EXIT)
      next = next.withOverloaded(false);
    set(entity, next);
    if (next.overloaded()) {
      entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 140, 0, false, false));
      if (entity.tickCount % 60 == 0) entity.hurt(entity.damageSources().magic(), OVERLOAD_DAMAGE);
    }
    ImplantEffects.reconcile(entity);
    if (operational(entity))
      for (var entry : next.equipment()) ImplantBehaviors.tick(entity, entry);
    sendSync(entity);
  }

  public static float ramRegen(CyberwareData data) {
    return RAM_REGEN
        + (data.has(Cyberware.EX_DISK_RAM_UPGRADE) ? .10f : 0)
        + (data.has(Cyberware.NEWTON_MEMORY_BOOST) ? .08f : 0)
        + (data.has(Cyberware.COX2_OPTIMIZER) ? .04f : 0);
  }

  public static boolean spendTolerance(LivingEntity entity, float cost) {
    if (!operational(entity) || !Float.isFinite(cost) || cost < 0) return false;
    var data = get(entity).withTolerance(get(entity).tolerance() - cost);
    if (data.tolerance() <= 0) data = data.withOverloaded(true);
    set(entity, data);
    if (data.overloaded()) ImplantEffects.reconcile(entity);
    sendSync(entity);
    return true;
  }

  public static boolean spendRam(LivingEntity entity, float cost) {
    var data = get(entity);
    if (!operational(entity) || !Float.isFinite(cost) || cost < 0 || data.ram() < cost)
      return false;
    set(entity, data.withRam(data.ram() - cost));
    return true;
  }

  public static boolean ready(LivingEntity entity, String key, long cooldown) {
    long now = entity.level().getGameTime();
    var data = get(entity);
    if (now < data.cooldowns().getOrDefault(key, 0L)) return false;
    set(entity, data.withCooldown(key, now + cooldown));
    return true;
  }

  public static void onDamaged(
      LivingEntity entity, net.minecraft.world.damagesource.DamageSource source) {
    ImplantEffects.onDamaged(entity, source);
  }

  public static void onKill(LivingEntity entity, LivingEntity victim) {
    ImplantEffects.onKill(entity, victim);
  }

  public static boolean allowDeath(LivingEntity entity) {
    return ImplantEffects.allowDeath(entity, get(entity));
  }

  public static void say(LivingEntity entity, Component message) {
    if (entity instanceof ServerPlayer p) p.displayClientMessage(message, true);
  }

  public static long secondsLeft(long until, long now) {
    return Math.max(0, (until - now + 19) / 20);
  }

  public static String bar(float value, float max, int length) {
    int n = Math.max(0, Math.min(length, Math.round(length * value / max)));
    return "|".repeat(n) + ".".repeat(length - n);
  }

  private CyberwareManager() {}
}
