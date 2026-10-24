package com.cybercraft;

import com.cybercraft.cyberware.*;
import com.cybercraft.cyberware.animation.Trigger;
import com.cybercraft.netrunning.NetrunnerManager;
import com.cybercraft.network.ModNetworking;
import com.cybercraft.registry.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(CyberCraft.MOD_ID)
public final class CyberCraft {
  public static final String MOD_ID = "cybercraft";
  public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

  public static ResourceLocation id(String path) {
    return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
  }

  public CyberCraft(IEventBus bus, net.neoforged.fml.ModContainer container) {
    container.registerConfig(
        net.neoforged.fml.config.ModConfig.Type.SERVER, com.cybercraft.mob.MobProgression.CONFIG);
    ModAttachments.init(bus);
    OwnedEffects.init(bus);
    ModMenus.init(bus);
    bus.addListener(com.cybercraft.equipment.EquipmentRegistry::register);
    ModItems.init(bus);
    ModBlocks.init(bus);
    ModCreativeTabs.init(bus);
    bus.addListener(ModNetworking::register);
    NeoForge.EVENT_BUS.addListener(CyberCraft::tick);
    NeoForge.EVENT_BUS.addListener(
        (net.neoforged.neoforge.event.tick.EntityTickEvent.Post e) -> {
          if (!e.getEntity().level().isClientSide
              && e.getEntity() instanceof net.minecraft.world.entity.LivingEntity living
              && (living instanceof ServerPlayer
                  || living.hasData(ModAttachments.CYBERWARE)
                      && !CyberwareManager.get(living).equipment().isEmpty())) {
            OwnedEffects.reconcile(living);
            LandingTracker.tick(living);
            if (living.tickCount % 20 == 0) CyberwareManager.tick(living);
          }
        });
    NeoForge.EVENT_BUS.addListener(
        (net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent e) -> {
          DoubleJumpHandler.forget(e.getEntity());
          LandingTracker.forget(e.getEntity());
        });
    NeoForge.EVENT_BUS.addListener(com.cybercraft.mob.MobProgression::spawn);
    NeoForge.EVENT_BUS.addListener(
        (net.neoforged.neoforge.event.entity.living.MobEffectEvent.Applicable e) ->
            SelfIceGuard.applicable(e));
    NeoForge.EVENT_BUS.addListener(
        (net.neoforged.neoforge.event.server.ServerStartedEvent e) ->
            com.cybercraft.equipment.EquipmentRegistry.validate(e.getServer().overworld()));
    NeoForge.EVENT_BUS.addListener(
        (net.neoforged.neoforge.event.entity.EntityJoinLevelEvent e) -> {
          if (!e.getLevel().isClientSide
              && e.getEntity() instanceof net.minecraft.world.entity.Mob mob)
            com.cybercraft.mob.MobProgression.join(mob);
        });
    NeoForge.EVENT_BUS.addListener(
        (PlayerEvent.StartTracking e) -> {
          if (e.getEntity() instanceof ServerPlayer player
              && e.getTarget() instanceof net.minecraft.world.entity.LivingEntity target) {
            net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(
                player,
                new com.cybercraft.network.CyberwareSyncPayload(
                    target.getId(), CyberwareManager.get(target)));
            if (target instanceof net.minecraft.world.entity.player.Player
                && MantisBladeManager.extended(target))
              net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(
                  player,
                  new com.cybercraft.network.MantisBladeStatePayload(
                      target.getId(), true, false));
          }
        });
    NeoForge.EVENT_BUS.addListener(CyberCraft::damage);
    NeoForge.EVENT_BUS.addListener(
        (LivingIncomingDamageEvent e) -> {
          if (e.getSource().getEntity() instanceof net.minecraft.world.entity.LivingEntity attacker
              && e.getSource().getDirectEntity() == attacker
              && attacker.hasData(ModAttachments.CYBERWARE)
              && (e.getSource().is(net.minecraft.world.damagesource.DamageTypes.PLAYER_ATTACK)
                  || e.getSource().is(net.minecraft.world.damagesource.DamageTypes.MOB_ATTACK)))
            e.setAmount(e.getAmount() + ImplantEffects.meleeBonus(attacker, e.getEntity()));
        });
    NeoForge.EVENT_BUS.addListener(CyberCraft::death);
    NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent e) -> sync(e));
    NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerRespawnEvent e) -> sync(e));
    NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerChangedDimensionEvent e) -> sync(e));
    NeoForge.EVENT_BUS.addListener(
        (PlayerEvent.PlayerLoggedOutEvent e) -> MantisBladeManager.reset(e.getEntity()));
    NeoForge.EVENT_BUS.addListener(
        (ServerStoppedEvent e) -> {
          DoubleJumpHandler.clear();
          LandingTracker.clear();
          SandevistanManager.clear();
          ImplantEffects.clear();
          NetrunnerManager.clear();
          MantisBladeManager.clear();
        });
    LOGGER.info("CyberCraft loaded. Welcome to Night City.");
  }

  private static void tick(ServerTickEvent.Post event) {
    var server = event.getServer();
    DoubleJumpHandler.tick(server);
    SandevistanManager.tick(server);
    for (var level : server.getAllLevels()) com.cybercraft.netrunning.BlockHacks.tick(level);
  }

  private static void damage(LivingDamageEvent.Post event) {
    if (event.getNewDamage() <= 0) return;
    if (event.getEntity().hasData(ModAttachments.CYBERWARE)) {
      CyberwareManager.onDamaged(event.getEntity(), event.getSource());
      ImplantTriggers.play(event.getEntity(), Trigger.DAMAGE);
      if (event.getSource().is(net.minecraft.tags.DamageTypeTags.IS_FIRE))
        ImplantTriggers.play(event.getEntity(), Trigger.GROUND);
    }
    if (event.getSource().getEntity() instanceof net.minecraft.world.entity.LivingEntity player
        && event.getSource().getDirectEntity() == player
        && (event.getSource().is(net.minecraft.world.damagesource.DamageTypes.PLAYER_ATTACK)
            || event.getSource().is(net.minecraft.world.damagesource.DamageTypes.MOB_ATTACK)))
      ImplantEffects.onMeleeHit(player, event.getEntity());
  }

  private static void death(LivingDeathEvent event) {
    if (event.getEntity().hasData(ModAttachments.CYBERWARE)
        && !CyberwareManager.allowDeath(event.getEntity())) {
      event.setCanceled(true);
      return;
    }
    MantisBladeManager.reset(event.getEntity());
    if (event.getSource().getEntity() instanceof net.minecraft.world.entity.LivingEntity player) {
      CyberwareManager.onKill(player, event.getEntity());
      ImplantTriggers.play(player, Trigger.KILL);
    }
  }

  private static void sync(PlayerEvent event) {
    if (event.getEntity() instanceof ServerPlayer player) {
      CyberwareManager.reconcile(player);
      SandevistanManager.track(player);
      CyberwareManager.sendSync(player);
      ImplantEffects.tick(player, CyberwareManager.get(player));
    }
  }
}
