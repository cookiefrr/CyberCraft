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
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;

public final class MobProgression {
  private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
  private static final ModConfigSpec.BooleanValue ENABLED =
      BUILDER.define("mobCyberware.enabled", true);
  private static final ModConfigSpec.IntValue FIRST =
      BUILDER.defineInRange("mobCyberware.firstDay", 5, 0, 100000);
  private static final ModConfigSpec.IntValue CAP =
      BUILDER.defineInRange("mobCyberware.capDay", 50, 1, 100000);
  private static final ModConfigSpec.DoubleValue INITIAL =
      BUILDER.defineInRange("mobCyberware.initialChance", .05, 0, 1);
  private static final ModConfigSpec.DoubleValue CHANCE =
      BUILDER.defineInRange("mobCyberware.maxChance", .35, 0, 1);
  private static final ModConfigSpec.IntValue COUNT =
      BUILDER.defineInRange("mobCyberware.maxImplants", 3, 0, 11);
  private static final ModConfigSpec.IntValue TIER =
      BUILDER.defineInRange("mobCyberware.maxTier", 3, 1, 3);
  public static final ModConfigSpec CONFIG = BUILDER.build();
  public static final TagKey<EntityType<?>> USERS =
      TagKey.create(Registries.ENTITY_TYPE, CyberCraft.id("cyberware_users"));

  public static Progression.Stage stage(long day) {
    return Progression.at(
        day,
        new Progression.Settings(
            FIRST.get(),
            Math.max(FIRST.get() + 1, CAP.get()),
            Math.min(INITIAL.get(), CHANCE.get()),
            CHANCE.get(),
            COUNT.get(),
            TIER.get()));
  }

  public static void spawn(FinalizeSpawnEvent event) {
    var mob = event.getEntity();
    if (!ENABLED.get()
        || event.getSpawnType() != MobSpawnType.NATURAL
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
    if (mob.hasData(com.cybercraft.registry.ModAttachments.CYBERWARE)
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
