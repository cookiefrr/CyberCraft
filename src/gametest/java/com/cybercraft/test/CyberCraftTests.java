package com.cybercraft.test;

import com.cybercraft.CyberCraft;
import com.cybercraft.cyberware.*;
import com.cybercraft.equipment.*;
import com.cybercraft.menu.CyberwareMenu;
import com.cybercraft.netrunning.*;
import com.cybercraft.registry.*;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.*;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.*;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.*;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.gametest.*;

@GameTestHolder("cybercraft_tests")
@PrefixGameTestTemplate(false)
public final class CyberCraftTests {
  private static Zombie mob(GameTestHelper h) {
    var mob = h.spawnWithNoFreeWill(EntityType.ZOMBIE, 2, 1, 2);
    mob.setInvulnerable(false);
    mob.getAttribute(Attributes.ARMOR).setBaseValue(0);
    return mob;
  }

  private static void install(LivingEntity entity, Cyberware implant) {
    var stack = new ItemStack(ModItems.implant(implant));
    if (!CyberwareManager.canEquip(entity, implant.slot().resource(), 0, stack))
      throw new AssertionError("Cannot equip " + implant);
    CyberwareManager.equip(entity, implant.slot().resource(), 0, stack);
  }

  @GameTest(template = "empty")
  public static void accessRequiresInstallation(GameTestHelper h) {
    var entity = mob(h);
    h.assertFalse(CyberwareManager.canScan(CyberwareManager.get(entity)), "Bare mob can scan");
    install(entity, Cyberware.KIROSHI_OPTICS);
    h.assertTrue(
        CyberwareManager.canScan(CyberwareManager.get(entity)), "Optics do not unlock scanning");
    h.assertFalse(CyberwareManager.canHack(CyberwareManager.get(entity)), "Optics alone can hack");
    install(entity, Cyberware.CYBERDECK);
    h.assertTrue(
        CyberwareManager.canHack(CyberwareManager.get(entity)), "Deck does not unlock hacking");
    CyberwareManager.set(entity, CyberwareManager.get(entity).withOverloaded(true));
    h.assertFalse(
        CyberwareManager.canHack(CyberwareManager.get(entity)), "Overload permits hacking");
    h.succeed();
  }

  @GameTest(template = "empty")
  public static void componentsSurviveCodecAndRemoval(GameTestHelper h) {
    var entity = mob(h);
    var stack = new ItemStack(ModItems.implant(Cyberware.KIROSHI_OPTICS));
    stack.set(DataComponents.CUSTOM_NAME, Component.literal("Named optics"));
    CyberwareManager.equip(entity, Cyberware.KIROSHI_OPTICS.slot().resource(), 0, stack);
    var ops = RegistryOps.create(NbtOps.INSTANCE, h.getLevel().registryAccess());
    var data =
        CyberwareData.CODEC
            .parse(
                ops,
                CyberwareData.CODEC.encodeStart(ops, CyberwareManager.get(entity)).getOrThrow())
            .getOrThrow();
    h.assertTrue(
        data.equipment().getFirst().stack().getHoverName().getString().equals("Named optics"),
        "Codec dropped components");
    CyberwareManager.set(entity, data);
    h.assertTrue(
        CyberwareManager.at(data, Cyberware.KIROSHI_OPTICS.slot().resource(), 0)
                .orElseThrow()
                .stack()
                .getCount()
            == 1,
        "Incorrect installed count");
    h.succeed();
  }

  @GameTest(template = "empty")
  public static void cooldownSurvivesReinstall(GameTestHelper h) {
    var entity = mob(h);
    install(entity, Cyberware.SANDEVISTAN);
    SandevistanManager.activate(entity);
    long end = CyberwareManager.get(entity).sandevistanEnd();
    h.assertTrue(end > entity.level().getGameTime(), "Activation did not set cooldown");
    CyberwareManager.equip(entity, Cyberware.SANDEVISTAN.slot().resource(), 0, ItemStack.EMPTY);
    install(entity, Cyberware.SANDEVISTAN);
    h.assertTrue(CyberwareManager.get(entity).sandevistanEnd() == end, "Reinstall reset cooldown");
    h.assertTrue(!entity.hasEffect(MobEffects.MOVEMENT_SPEED), "Unequipped OS left speed active");
    h.succeed();
  }

  @GameTest(template = "empty")
  public static void potionSurvivesUnequip(GameTestHelper h) {
    var entity = mob(h);
    entity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 1000, 2));
    install(entity, Cyberware.KIROSHI_OPTICS);
    CyberwareManager.equip(entity, Cyberware.KIROSHI_OPTICS.slot().resource(), 0, ItemStack.EMPTY);
    h.assertTrue(
        entity.hasEffect(MobEffects.NIGHT_VISION)
            && entity.getEffect(MobEffects.NIGHT_VISION).getAmplifier() == 2,
        "Uninstall removed an unrelated potion");
    h.succeed();
  }

  @GameTest(template = "empty")
  public static void ownEffectsEndOnOverload(GameTestHelper h) {
    var entity = mob(h);
    install(entity, Cyberware.KIROSHI_OPTICS);
    h.assertTrue(entity.hasEffect(MobEffects.NIGHT_VISION), "No implant effect");
    install(entity, Cyberware.AXOLOTL);
    entity.setHealth(entity.getMaxHealth());
    CyberwareManager.set(entity, CyberwareManager.get(entity).withOverloaded(true));
    ImplantEffects.reconcile(entity);
    h.assertFalse(entity.hasEffect(MobEffects.NIGHT_VISION), "Overload left implant effect active");
    h.assertTrue(
        entity.getHealth() <= entity.getMaxHealth(),
        "Overload retained implant health above the new maximum");
    h.succeed();
  }

  @GameTest(template = "empty")
  public static void meleeBonusUsesOriginalHit(GameTestHelper h) {
    var attacker = mob(h);
    var victim = mob(h);
    install(attacker, Cyberware.MANTIS_BLADES);
    victim.hurt(h.getLevel().damageSources().mobAttack(attacker), 2);
    h.assertTrue(
        Math.abs(victim.getHealth() - 12) < .01,
        "Bonus was lost to invulnerability frames: " + victim.getHealth());
    h.succeed();
  }

  @GameTest(template = "empty")
  public static void mantisBladesOnlyHitWhenExtended(GameTestHelper h) {
    var player =
        FakePlayerFactory.get(h.getLevel(), new GameProfile(UUID.randomUUID(), "mantis_state"));
    var victim = mob(h);
    install(player, Cyberware.MANTIS_BLADES);
    float tolerance = CyberwareManager.get(player).tolerance();
    h.assertFalse(MantisBladeManager.active(player), "Mantis blades started extended");
    h.assertTrue(
        Math.abs(ImplantEffects.meleeBonus(player, victim)) < .001f, "Retracted blade hit");
    h.assertTrue(
        Math.abs(tolerance - CyberwareManager.get(player).tolerance()) < .001f,
        "Retracted blade spent tolerance");
    h.assertTrue(MantisBladeManager.toggle(player), "Toggle to extended was rejected");
    h.assertTrue(MantisBladeManager.active(player), "Blades did not extend");
    h.assertTrue(
        Math.abs(6f - ImplantEffects.meleeBonus(player, victim)) < .001f,
        "Extended blade missed");
    h.assertTrue(MantisBladeManager.toggle(player), "Toggle to retracted was rejected");
    h.assertFalse(MantisBladeManager.active(player), "Blades did not retract");
    h.succeed();
  }

  @GameTest(template = "empty")
  public static void mantisToggleRequiresImplantAndResetsOnRemoval(GameTestHelper h) {
    var player =
        FakePlayerFactory.get(h.getLevel(), new GameProfile(UUID.randomUUID(), "mantis_toggle"));
    h.assertFalse(MantisBladeManager.toggle(player), "Toggle accepted without the implant");
    install(player, Cyberware.MANTIS_BLADES);
    h.assertTrue(MantisBladeManager.toggle(player), "Toggle rejected with the implant");
    h.assertTrue(MantisBladeManager.active(player), "Blades did not extend");
    CyberwareManager.equip(player, Cyberware.MANTIS_BLADES.slot().resource(), 0, ItemStack.EMPTY);
    install(player, Cyberware.MANTIS_BLADES);
    h.assertFalse(MantisBladeManager.active(player), "Reinstall restored a stale extended state");
    h.succeed();
  }

  @GameTest(template = "empty")
  public static void retaliationDoesNotRecurse(GameTestHelper h) {
    var a = mob(h);
    var b = mob(h);
    install(a, Cyberware.SHOCK_N_AWE);
    install(b, Cyberware.SHOCK_N_AWE);
    b.hurt(h.getLevel().damageSources().mobAttack(a), 2);
    h.assertTrue(a.getHealth() > 0 && b.getHealth() > 0, "Retaliation killed both mobs");
    h.assertTrue(Math.abs(a.getHealth() - 17.5) < .01, "Retaliation repeated: " + a.getHealth());
    h.succeed();
  }

  @GameTest(template = "empty")
  public static void dataPackAddsSlotsAndImplants(GameTestHelper h) {
    var entity = mob(h);
    var slot = ResourceLocation.fromNamespaceAndPath("cybercraft_tests", "auxiliary");
    var stack = new ItemStack(Items.AMETHYST_SHARD);
    h.assertTrue(
        CyberwareManager.canEquip(entity, slot, 1, stack),
        "Data-pack slot or non-implant item rejected");
    CyberwareManager.equip(entity, slot, 1, stack);
    h.assertFalse(CyberwareManager.canEquip(entity, slot, 0, stack), "Duplicate implant allowed");
    h.succeed();
  }

  @GameTest(template = "empty")
  public static void microgeneratorDamageJoinsOriginalHit(GameTestHelper h) {
    var attacker = mob(h);
    var target = mob(h);
    install(attacker, Cyberware.MICROGENERATOR);
    long seed = 0;
    while (net.minecraft.util.RandomSource.create(seed).nextFloat() >= .15f) seed++;
    attacker.getRandom().setSeed(seed);
    float bonus = ImplantEffects.meleeBonus(attacker, target);
    h.assertTrue(
        bonus == 2.5f && target.getHealth() == 20, "Microgenerator applied a separate damage call");
    attacker.getRandom().setSeed(seed);
    target.hurt(h.getLevel().damageSources().mobAttack(attacker), 2);
    h.assertTrue(
        Math.abs(target.getHealth() - 15.5f) < .01,
        "Original hit did not contain the microgenerator bonus: " + target.getHealth());
    h.succeed();
  }

  @GameTest(template = "empty")
  public static void missingDefinitionsRecoverStacks(GameTestHelper h) {
    var entity = mob(h);
    var stack = new ItemStack(Items.DIAMOND);
    stack.set(DataComponents.CUSTOM_NAME, Component.literal("Recover me"));
    var entry =
        new InstalledImplant(
            CyberCraft.id("missing"),
            0,
            CyberCraft.id("missing"),
            CyberCraft.id("smart_link"),
            stack);
    CyberwareManager.set(
        entity, CyberwareManager.get(entity).withEquipment(List.of(entry), List.of()));
    CyberwareManager.reconcile(entity);
    h.assertTrue(
        CyberwareManager.get(entity).equipment().isEmpty(), "Missing definition remained equipped");
    h.assertTrue(
        CyberwareManager.get(entity).recovery().size() == 1
            && CyberwareManager.get(entity)
                .recovery()
                .getFirst()
                .getHoverName()
                .getString()
                .equals("Recover me"),
        "Recovery lost the original stack");
    h.succeed();
  }

  @GameTest(template = "empty", timeoutTicks = 150)
  public static void disruptionExpiresWithoutRestoringState(GameTestHelper h) {
    var pos = h.absolutePos(new BlockPos(2, 1, 2));
    h.getLevel().setBlockAndUpdate(pos, Blocks.REDSTONE_BLOCK.defaultBlockState());
    var player =
        FakePlayerFactory.get(h.getLevel(), new GameProfile(UUID.randomUUID(), "disruption_test"));
    var hit = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);
    BlockHacks.apply(player, Quickhack.DISRUPTION, hit);
    h.assertTrue(h.getLevel().getSignal(pos, Direction.UP) == 0, "Weak signal not suppressed");
    h.assertTrue(
        h.getLevel().getControlInputSignal(pos, Direction.UP, false) == 0,
        "Comparator input bypassed disruption");
    h.runAfterDelay(
        105,
        () -> {
          h.assertTrue(h.getLevel().getSignal(pos, Direction.UP) == 15, "Output did not recover");
          h.succeed();
        });
  }

  @GameTest(template = "empty")
  public static void ironDoorHalvesStayTogether(GameTestHelper h) {
    var pos = h.absolutePos(new BlockPos(2, 1, 2));
    var state = Blocks.IRON_DOOR.defaultBlockState();
    h.getLevel().setBlock(pos, state, 2);
    h.getLevel()
        .setBlock(
            pos.above(),
            state.setValue(
                DoorBlock.HALF,
                net.minecraft.world.level.block.state.properties.DoubleBlockHalf.UPPER),
            2);
    var player =
        FakePlayerFactory.get(h.getLevel(), new GameProfile(UUID.randomUUID(), "door_test"));
    BlockHacks.apply(
        player,
        Quickhack.REMOTE_ACCESS,
        new BlockHitResult(Vec3.atCenterOf(pos), Direction.NORTH, pos, false));
    h.assertTrue(
        h.getLevel().getBlockState(pos).getValue(DoorBlock.OPEN)
            && h.getLevel().getBlockState(pos.above()).getValue(DoorBlock.OPEN),
        "Door halves diverged");
    h.succeed();
  }

  @GameTest(template = "empty")
  public static void permissionsCanDenyRemoteAccess(GameTestHelper h) {
    var player =
        FakePlayerFactory.get(h.getLevel(), new GameProfile(UUID.randomUUID(), "permission_test"));
    var pos = h.absolutePos(new BlockPos(2, 1, 2));
    java.util.function.Consumer<
            net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock>
        deny = e -> e.setCanceled(true);
    net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(deny);
    try {
      h.assertFalse(
          BlockHacks.allowed(
              player, new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false)),
          "Canceled interaction was allowed");
    } finally {
      net.neoforged.neoforge.common.NeoForge.EVENT_BUS.unregister(deny);
    }
    h.succeed();
  }

  @GameTest(template = "empty")
  public static void inventoryTransferPreservesComponents(GameTestHelper h) {
    var player =
        FakePlayerFactory.get(h.getLevel(), new GameProfile(UUID.randomUUID(), "inventory_test"));
    player.experienceLevel = 20;
    var stack = new ItemStack(ModItems.implant(Cyberware.KIROSHI_OPTICS));
    stack.set(DataComponents.CUSTOM_NAME, Component.literal("Transfer optics"));
    player.getInventory().setItem(0, stack);
    var menu = new CyberwareMenu(7, player.getInventory());
    menu.quickMoveStack(player, menu.equipmentSlots + 27);
    h.assertTrue(
        player.getInventory().getItem(0).isEmpty(), "Shift-click did not consume inventory item");
    var pos = Cyberware.KIROSHI_OPTICS.slot().resource();
    var installed = CyberwareManager.at(CyberwareManager.get(player), pos, 0).orElseThrow();
    h.assertTrue(
        installed.stack().getHoverName().getString().equals("Transfer optics"),
        "Installation lost components");
    int slot = 0;
    while (!menu.positions.get(slot).type().equals(pos)) slot++;
    menu.quickMoveStack(player, slot);
    h.assertTrue(
        CyberwareManager.get(player).equipment().isEmpty(), "Shift-click did not remove implant");
    h.assertTrue(
        player.getInventory().items.stream()
            .anyMatch(
                st -> !st.isEmpty() && st.getHoverName().getString().equals("Transfer optics")),
        "Removal lost components");
    h.succeed();
  }

  @GameTest(template = "empty")
  public static void fullInventoryDoesNotLoseImplant(GameTestHelper h) {
    var player =
        FakePlayerFactory.get(h.getLevel(), new GameProfile(UUID.randomUUID(), "full_inventory"));
    install(player, Cyberware.KIROSHI_OPTICS);
    for (int i = 0; i < 36; i++) player.getInventory().setItem(i, new ItemStack(Items.STONE, 64));
    var menu = new CyberwareMenu(8, player.getInventory());
    int slot = 0;
    while (!menu.positions.get(slot).type().equals(Cyberware.KIROSHI_OPTICS.slot().resource()))
      slot++;
    menu.quickMoveStack(player, slot);
    h.assertTrue(
        CyberwareManager.get(player).has(Cyberware.KIROSHI_OPTICS),
        "Full inventory deleted implant");
    h.succeed();
  }

  @GameTest(template = "empty")
  public static void capacityAndOccupiedSlotsAreValidated(GameTestHelper h) {
    var player =
        FakePlayerFactory.get(h.getLevel(), new GameProfile(UUID.randomUUID(), "capacity_test"));
    player.experienceLevel = 0;
    install(player, Cyberware.SANDEVISTAN);
    install(player, Cyberware.MANTIS_BLADES);
    h.assertFalse(
        CyberwareManager.canEquip(
            player,
            Cyberware.KIROSHI_OPTICS.slot().resource(),
            0,
            new ItemStack(ModItems.implant(Cyberware.KIROSHI_OPTICS))),
        "Capacity limit bypassed");
    h.assertFalse(
        CyberwareManager.canEquip(
            player,
            Cyberware.KIROSHI_OPTICS.slot().resource(),
            0,
            new ItemStack(ModItems.implant(Cyberware.MANTIS_BLADES))),
        "Wrong slot accepted");
    h.succeed();
  }

  @GameTest(template = "empty")
  public static void deathCloneKeepsEquipmentAndCooldown(GameTestHelper h) {
    var original =
        FakePlayerFactory.get(h.getLevel(), new GameProfile(UUID.randomUUID(), "before_death"));
    install(original, Cyberware.KIROSHI_OPTICS);
    CyberwareManager.set(
        original, CyberwareManager.get(original).withCooldown("second_heart", 10000));
    var replacement =
        FakePlayerFactory.get(h.getLevel(), new GameProfile(UUID.randomUUID(), "after_death"));
    replacement.restoreFrom(original, false);
    h.assertTrue(
        CyberwareManager.get(replacement).has(Cyberware.KIROSHI_OPTICS),
        "Death discarded equipment");
    h.assertTrue(
        CyberwareManager.get(replacement).cooldowns().getOrDefault("second_heart", 0L) == 10000,
        "Death reset cooldown");
    h.succeed();
  }

  @GameTest(template = "empty")
  public static void secondHeartRevivesOnce(GameTestHelper h) {
    var entity = mob(h);
    install(entity, Cyberware.SECOND_HEART);
    entity.hurt(h.getLevel().damageSources().genericKill(), 100);
    h.assertTrue(entity.isAlive() && entity.getHealth() > 0, "Second Heart failed to revive");
    h.assertTrue(
        CyberwareManager.get(entity).cooldowns().getOrDefault("second_heart", 0L)
            > entity.level().getGameTime(),
        "Revival did not set a persistent cooldown");
    h.succeed();
  }

  @GameTest(template = "empty")
  public static void wallsBlockScannerTargets(GameTestHelper h) {
    var player =
        FakePlayerFactory.get(h.getLevel(), new GameProfile(UUID.randomUUID(), "wall_test"));
    var start = h.absolutePos(new BlockPos(2, 1, 1));
    player.setPos(start.getX() + .5, start.getY(), start.getZ() + .5);
    player.setYRot(0);
    player.setXRot(0);
    var target = h.spawnWithNoFreeWill(EntityType.COW, 2, 1, 5);
    var wall = h.absolutePos(new BlockPos(2, 2, 3));
    h.getLevel().setBlockAndUpdate(wall, Blocks.STONE.defaultBlockState());
    var hit = com.cybercraft.util.Targeting.pick(player, 64);
    h.assertTrue(
        hit instanceof BlockHitResult b && b.getBlockPos().equals(wall),
        "Scanner selected an entity through a wall");
    h.succeed();
  }

  @GameTest(template = "empty")
  public static void unknownHackDoesNotSpendRam(GameTestHelper h) {
    var player =
        FakePlayerFactory.get(h.getLevel(), new GameProfile(UUID.randomUUID(), "invalid_hack"));
    install(player, Cyberware.KIROSHI_OPTICS);
    install(player, Cyberware.CYBERDECK);
    int result =
        NetrunnerManager.cast(
            player, CyberCraft.id("invalid"), new com.cybercraft.network.HackTarget.Entity(-999));
    h.assertTrue(
        result != 0 && CyberwareManager.get(player).ram() == 100, "Invalid payload consumed RAM");
    h.succeed();
  }

  @GameTest(template = "empty")
  public static void friendlyFireAndCreativeAreRejected(GameTestHelper h) {
    var a = FakePlayerFactory.get(h.getLevel(), new GameProfile(UUID.randomUUID(), "team_a"));
    var b = FakePlayerFactory.get(h.getLevel(), new GameProfile(UUID.randomUUID(), "team_b"));
    var scoreboard = h.getLevel().getScoreboard();
    var team = scoreboard.addPlayerTeam("cyber_test_" + Math.abs(a.getId()));
    team.setAllowFriendlyFire(false);
    scoreboard.addPlayerToTeam(a.getScoreboardName(), team);
    scoreboard.addPlayerToTeam(b.getScoreboardName(), team);
    h.assertFalse(NetrunnerManager.allowed(a, b), "Friendly fire bypassed");
    scoreboard.removePlayerTeam(team);
    b.setGameMode(net.minecraft.world.level.GameType.CREATIVE);
    h.assertFalse(NetrunnerManager.allowed(a, b), "Creative player can be hacked");
    h.succeed();
  }

  @GameTest(template = "empty")
  public static void mobLoadoutsRespectTierAndCap(GameTestHelper h) {
    for (int i = 0; i < 30; i++) {
      var entity = mob(h);
      com.cybercraft.mob.MobProgression.enhance(
          entity, new com.cybercraft.mob.Progression.Stage(1, 3, 2));
      var data = CyberwareManager.get(entity);
      h.assertTrue(data.equipment().size() <= 3, "Too many mob implants");
      h.assertTrue(CyberwareManager.usedCapacity(entity) <= 60, "Mob capacity exceeded");
      for (var e : data.equipment())
        h.assertTrue(
            EquipmentRegistry.definition(entity.level(), e.definition()).tier() <= 2,
            "Mob tier exceeded");
      var saved = new net.minecraft.nbt.CompoundTag();
      entity.saveWithoutId(saved);
      var reloaded = new Zombie(EntityType.ZOMBIE, h.getLevel());
      reloaded.load(saved);
      h.assertTrue(
          CyberwareManager.get(reloaded).equipment().size() == data.equipment().size(),
          "Reload changed equipment");
      entity.discard();
    }
    h.succeed();
  }

  @GameTest(template = "empty")
  public static void disruptedBlockReplacementIsNotOverwritten(GameTestHelper h) {
    var pos = h.absolutePos(new BlockPos(2, 1, 2));
    h.getLevel().setBlockAndUpdate(pos, Blocks.REDSTONE_BLOCK.defaultBlockState());
    var player =
        FakePlayerFactory.get(h.getLevel(), new GameProfile(UUID.randomUUID(), "replace_test"));
    BlockHacks.apply(
        player,
        Quickhack.DISRUPTION,
        new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false));
    h.getLevel().setBlockAndUpdate(pos, Blocks.GOLD_BLOCK.defaultBlockState());
    BlockHacks.tick(h.getLevel());
    h.assertTrue(
        h.getLevel().getBlockState(pos).is(Blocks.GOLD_BLOCK)
            && !BlockHacks.disrupted(h.getLevel(), pos),
        "Disruption restored an old block");
    h.succeed();
  }

  @GameTest(template = "empty", timeoutTicks = 150)
  public static void wireDisruptionCutsPropagationAndRecovers(GameTestHelper h) {
    var level = h.getLevel();
    var source = h.absolutePos(new BlockPos(1, 1, 2));
    var middle = source.east();
    var end = middle.east();
    for (var support : java.util.List.of(source, middle, end))
      level.setBlockAndUpdate(support.below(), Blocks.STONE.defaultBlockState());
    level.setBlockAndUpdate(source, Blocks.REDSTONE_BLOCK.defaultBlockState());
    level.setBlockAndUpdate(middle, Blocks.REDSTONE_WIRE.defaultBlockState());
    level.setBlockAndUpdate(end, Blocks.REDSTONE_WIRE.defaultBlockState());
    var player = FakePlayerFactory.get(level, new GameProfile(UUID.randomUUID(), "wire_test"));
    h.runAfterDelay(
        3,
        () -> {
          h.assertTrue(
              level.getBlockState(end).getValue(RedStoneWireBlock.POWER) > 0,
              "Fixture wire not powered");
          BlockHacks.apply(
              player,
              Quickhack.DISRUPTION,
              new BlockHitResult(Vec3.atCenterOf(middle), Direction.UP, middle, false));
          h.assertTrue(
              level.getBlockState(end).getValue(RedStoneWireBlock.POWER) == 0,
              "Wire propagation bypassed disruption");
        });
    h.runAfterDelay(
        110,
        () -> {
          h.assertTrue(
              level.getBlockState(end).getValue(RedStoneWireBlock.POWER) > 0,
              "Wire did not recover");
          h.succeed();
        });
  }

  @GameTest(template = "empty")
  public static void animationPayloadRoundTrips(GameTestHelper h) {
    var payload =
        new com.cybercraft.network.ImplantAnimationPayload(42, "mantis_blades", "slash_r");
    var buffer =
        new net.minecraft.network.RegistryFriendlyByteBuf(
            io.netty.buffer.Unpooled.buffer(), h.getLevel().registryAccess());
    com.cybercraft.network.ImplantAnimationPayload.CODEC.encode(buffer, payload);
    var decoded = com.cybercraft.network.ImplantAnimationPayload.CODEC.decode(buffer);
    h.assertTrue(decoded.equals(payload), "Payload round-trip mismatch: " + decoded);
    h.succeed();
  }

  @GameTest(template = "empty")
  public static void triggerDispatchToleratesMissingClips(GameTestHelper h) {
    var bare = mob(h);
    com.cybercraft.cyberware.ImplantTriggers.play(
        bare, com.cybercraft.cyberware.animation.Trigger.DAMAGE);
    var entity = mob(h);
    install(entity, Cyberware.BLOOD_PUMP);
    com.cybercraft.cyberware.ImplantTriggers.play(
        entity, com.cybercraft.cyberware.animation.Trigger.DAMAGE);
    com.cybercraft.cyberware.ImplantTriggers.play(
        entity, com.cybercraft.cyberware.animation.Trigger.LAND);
    h.succeed();
  }

  @GameTest(template = "empty")
  public static void selfIceBlocksHostileEffectsOncePerCooldown(GameTestHelper h) {
    var entity = h.spawnWithNoFreeWill(EntityType.COW, 2, 1, 2);
    var poison = new MobEffectInstance(MobEffects.POISON, 100);
    var before =
        new net.neoforged.neoforge.event.entity.living.MobEffectEvent.Applicable(
            entity, poison, null);
    SelfIceGuard.applicable(before);
    h.assertTrue(before.getApplicationResult(), "Self-ICE blocked without the implant");
    install(entity, Cyberware.SELF_ICE);
    var blocked =
        new net.neoforged.neoforge.event.entity.living.MobEffectEvent.Applicable(
            entity, poison, null);
    SelfIceGuard.applicable(blocked);
    h.assertFalse(blocked.getApplicationResult(), "Self-ICE did not block a hostile effect");
    h.assertTrue(
        CyberwareManager.get(entity).cooldowns().getOrDefault("self_ice", 0L)
            > entity.level().getGameTime(),
        "Self-ICE block did not set a cooldown");
    var cooling =
        new net.neoforged.neoforge.event.entity.living.MobEffectEvent.Applicable(
            entity, poison, null);
    SelfIceGuard.applicable(cooling);
    h.assertTrue(cooling.getApplicationResult(), "Self-ICE blocked while cooling down");
    var helpful =
        new net.neoforged.neoforge.event.entity.living.MobEffectEvent.Applicable(
            entity, new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100), null);
    SelfIceGuard.applicable(helpful);
    h.assertTrue(helpful.getApplicationResult(), "Self-ICE blocked a beneficial effect");
    h.succeed();
  }
}
