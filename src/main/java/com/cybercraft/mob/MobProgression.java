package com.cybercraft.mob;

import com.cybercraft.CyberCraft;
import com.cybercraft.cyberware.*;
import com.cybercraft.equipment.*;
import com.cybercraft.netrunning.*;
import java.util.*;
import net.minecraft.core.registries.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.ItemStack;

public final class MobProgression {
  public record Config(
      boolean enabled,
      int firstDay,
      int capDay,
      double initialChance,
      double maxChance,
      int maxImplants,
      int maxTier) {
    public Config {
      if (firstDay < 0
          || capDay <= firstDay
          || !Double.isFinite(initialChance)
          || !Double.isFinite(maxChance)
          || initialChance < 0
          || maxChance < initialChance
          || maxChance > 1
          || maxImplants < 0
          || maxImplants > 11
          || maxTier < 1
          || maxTier > 3) throw new IllegalArgumentException("Invalid mob cyberware configuration");
    }
  }

  private static Config config = new Config(true, 5, 50, .05, .35, 3, 3);

  public static void load(net.minecraft.server.MinecraftServer server) {
    var path =
        server
            .getWorldPath(net.minecraft.world.level.storage.LevelResource.ROOT)
            .resolve("serverconfig/cybercraft-server.json");
    var gson = new com.google.gson.GsonBuilder().setPrettyPrinting().create();
    try {
      if (java.nio.file.Files.exists(path))
        config =
            java.util.Objects.requireNonNull(
                gson.fromJson(java.nio.file.Files.readString(path), Config.class));
      else {
        config = new Config(true, 5, 50, .05, .35, 3, 3);
        java.nio.file.Files.createDirectories(path.getParent());
        java.nio.file.Files.writeString(path, gson.toJson(config));
      }
    } catch (Exception e) {
      throw new IllegalStateException("Cannot load cyberware configuration: " + path, e);
    }
  }

  public static final TagKey<EntityType<?>> USERS =
      TagKey.create(Registries.ENTITY_TYPE, CyberCraft.id("cyberware_users"));

  public static Progression.Stage stage(long day) {
    return Progression.at(
        day,
        new Progression.Settings(
            config.firstDay(),
            Math.max(config.firstDay() + 1, config.capDay()),
            Math.min(config.initialChance(), config.maxChance()),
            config.maxChance(),
            config.maxImplants(),
            config.maxTier()));
  }

  public static void spawn(Mob mob, MobSpawnType reason) {
    if (!config.enabled()
        || reason != MobSpawnType.NATURAL
        || !(mob instanceof Enemy)
        || !mob.getType().is(USERS)
        || CyberwareManager.get(mob).spawnRolled()) return;
    CyberwareManager.set(mob, CyberwareManager.get(mob).withSpawnRolled());
    var level = (ServerLevel) mob.level();
    var stage = stage(level.getServer().overworld().getGameTime() / 24000);
    if (mob.getRandom().nextDouble() >= stage.chance()) return;
    enhance(mob, stage);
  }

  public static void enhance(Mob mob, Progression.Stage stage) {
    var level = mob.level();
    var pool =
        new ArrayList<>(
            level.registryAccess().registryOrThrow(EquipmentRegistry.IMPLANTS).entrySet().stream()
                .filter(e -> e.getValue().tier() <= stage.tier())
                .toList());

    boolean netrunner =
        stage.implants() >= 3 && stage.tier() >= 2 && mob.getRandom().nextFloat() < .25;
    if (netrunner) {
      pool.sort(
          Comparator.comparingInt(
              e ->
                  switch (e.getKey().location().getPath()) {
                    case "kiroshi_optics" -> 0;
                    case "cyberdeck" -> 1;
                    case "cox2_optimizer" -> 2;
                    default -> 3;
                  }));
    } else Collections.shuffle(pool, new Random(mob.getRandom().nextLong()));
    for (var def : pool) {
      if (CyberwareManager.get(mob).equipment().size() >= stage.implants()) break;
      ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.get(def.getValue().item()));
      for (var pos : EquipmentRegistry.positions(level))
        if (CyberwareManager.at(CyberwareManager.get(mob), pos.type(), pos.index()).isEmpty()
            && CyberwareManager.canEquip(mob, pos.type(), pos.index(), stack)) {
          CyberwareManager.equip(mob, pos.type(), pos.index(), stack);
          break;
        }
    }
  }

  public static void join(Mob mob) {
    if (mob.hasAttached(com.cybercraft.registry.ModAttachments.CYBERWARE)
        && !CyberwareManager.get(mob).equipment().isEmpty()) {
      CyberwareManager.reconcile(mob);
      mob.goalSelector.addGoal(0, new CyberwareGoal(mob));
      SandevistanManager.track(mob);
    }
  }

  private static final class CyberwareGoal extends Goal {
    private final Mob mob;

    CyberwareGoal(Mob mob) {
      this.mob = mob;
    }

    @Override
    public boolean canUse() {
      return mob.getTarget() != null
          && mob.getTarget().isAlive()
          && CyberwareManager.operational(mob);
    }

    @Override
    public boolean canContinueToUse() {
      return canUse();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
      return true;
    }

    @Override
    public void tick() {
      DoubleJumpHandler.tickEntity(mob);
      var data = CyberwareManager.get(mob);
      var target = mob.getTarget();
      if (mob.horizontalCollision && !mob.onGround() && mob.getDeltaMovement().y < 0)
        DoubleJumpHandler.tryDoubleJump(mob);
      if (mob.tickCount % 20 != 0 || target == null) return;
      if (data.has(Cyberware.SANDEVISTAN) || data.has(Cyberware.BERSERK))
        SandevistanManager.activate(mob);
      if (CyberwareManager.canHack(data)) {
        var hacks = Arrays.stream(Quickhack.values()).filter(h -> !h.blockHack()).toList();
        NetrunnerManager.castLiving(mob, hacks.get(mob.getRandom().nextInt(hacks.size())), target);
      }
    }
  }

  private MobProgression() {}
}
