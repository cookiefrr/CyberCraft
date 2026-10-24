package com.cybercraft;

import com.cybercraft.cyberware.*;
import com.cybercraft.equipment.EquipmentRegistry;
import com.cybercraft.mob.MobProgression;
import com.cybercraft.network.*;
import com.cybercraft.registry.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.*;
import net.fabricmc.fabric.api.entity.event.v1.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import org.slf4j.*;

public final class CyberCraft implements ModInitializer {
  public static final String MOD_ID = "cybercraft";
  public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

  public static ResourceLocation id(String path) {
    return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
  }

  public void onInitialize() {
    ModAttachments.init();
    OwnedEffects.init();
    ModItems.init();
    ModBlocks.init();
    ModMenus.init();
    ModCreativeTabs.init();
    EquipmentRegistry.register();
    ModNetworking.init();
    for (String metal : new String[] {"titanium", "neodymium", "lithium"})
      BiomeModifications.addFeature(
          BiomeSelectors.foundInOverworld(),
          GenerationStep.Decoration.UNDERGROUND_ORES,
          ResourceKey.create(Registries.PLACED_FEATURE, id(metal + "_ore")));
    ServerLifecycleEvents.SERVER_STARTED.register(
        server -> {
          MobProgression.load(server);
          EquipmentRegistry.validate(server.overworld());
        });
    ServerLifecycleEvents.SERVER_STOPPED.register(
        server -> {
          DoubleJumpHandler.clear();
          SandevistanManager.clear();
          ImplantEffects.clear();
          MantisBladeManager.clear();
        });
    ServerTickEvents.END_SERVER_TICK.register(
        server -> {
          DoubleJumpHandler.tick(server);
          SandevistanManager.tick(server);
          for (var level : server.getAllLevels()) com.cybercraft.netrunning.BlockHacks.tick(level);
        });
    ServerEntityEvents.ENTITY_LOAD.register(
        (e, level) -> {
          if (e instanceof Mob mob) MobProgression.join(mob);
        });
    ServerEntityEvents.ENTITY_UNLOAD.register((e, level) -> DoubleJumpHandler.forget(e));
    ServerLivingEntityEvents.AFTER_DAMAGE.register(
        (e, source, base, taken, blocked) -> {
          if (blocked || taken <= 0) return;
          if (e.hasAttached(ModAttachments.CYBERWARE)) CyberwareManager.onDamaged(e, source);
          if (source.getEntity() instanceof LivingEntity attacker
              && source.getDirectEntity() == attacker
              && (source.is(net.minecraft.world.damagesource.DamageTypes.PLAYER_ATTACK)
                  || source.is(net.minecraft.world.damagesource.DamageTypes.MOB_ATTACK)))
            ImplantEffects.onMeleeHit(attacker, e);
        });
    ServerLivingEntityEvents.ALLOW_DEATH.register(
        (e, source, amount) ->
            !e.hasAttached(ModAttachments.CYBERWARE) || CyberwareManager.allowDeath(e));
    ServerLivingEntityEvents.AFTER_DEATH.register(
        (e, source) -> {
          MantisBladeManager.reset(e);
          if (source.getEntity() instanceof LivingEntity attacker)
            CyberwareManager.onKill(attacker, e);
        });
    ServerPlayConnectionEvents.DISCONNECT.register(
        (handler, server) -> MantisBladeManager.reset(handler.player));
    ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> sync(handler.player));
    ServerPlayerEvents.COPY_FROM.register(
        (old, p, alive) -> CyberwareManager.set(p, CyberwareManager.get(old)));
    ServerPlayerEvents.AFTER_RESPAWN.register((old, p, alive) -> sync(p));
    ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((p, from, to) -> sync(p));
    EntityTrackingEvents.START_TRACKING.register(
        (e, p) -> {
          if (e instanceof LivingEntity living)
            ServerPlayNetworking.send(
                p, new CyberwareSyncPayload(e.getId(), CyberwareManager.get(living)));
          if (e instanceof net.minecraft.world.entity.player.Player living
              && MantisBladeManager.extended(living))
            ServerPlayNetworking.send(p, new MantisBladeStatePayload(e.getId(), true, false));
        });
    LOGGER.info("CyberCraft loaded on Fabric.");
  }

  public static void tickEntity(LivingEntity e) {
    if (e.level().isClientSide
        || !(e instanceof ServerPlayer)
            && (!e.hasAttached(ModAttachments.CYBERWARE)
                || CyberwareManager.get(e).equipment().isEmpty())) return;
    OwnedEffects.reconcile(e);
    if (e.tickCount % 20 == 0) CyberwareManager.tick(e);
  }

  private static void sync(ServerPlayer p) {
    CyberwareManager.reconcile(p);
    SandevistanManager.track(p);
    CyberwareManager.sendSync(p);
    ImplantEffects.reconcile(p);
  }
}
