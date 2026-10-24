package com.cybercraft.cyberware;

import com.cybercraft.CyberCraft;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.*;
import java.util.function.Supplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class OwnedEffects {
  public record Lease(
      String owner,
      MobEffectInstance granted,
      Optional<MobEffectInstance> previous,
      long captured,
      long refreshed) {
    public static final Codec<Lease> CODEC =
        RecordCodecBuilder.create(
            i ->
                i.group(
                        Codec.STRING.fieldOf("owner").forGetter(Lease::owner),
                        MobEffectInstance.CODEC.fieldOf("granted").forGetter(Lease::granted),
                        MobEffectInstance.CODEC
                            .optionalFieldOf("previous")
                            .forGetter(Lease::previous),
                        Codec.LONG.fieldOf("captured").forGetter(Lease::captured),
                        Codec.LONG.fieldOf("refreshed").forGetter(Lease::refreshed))
                    .apply(i, Lease::new));
  }

  private static final DeferredRegister<AttachmentType<?>> TYPES =
      DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, CyberCraft.MOD_ID);
  private static final Supplier<AttachmentType<Map<String, Lease>>> LEDGER =
      TYPES.register(
          "effect_leases",
          () ->
              AttachmentType.<Map<String, Lease>>builder(() -> new HashMap<String, Lease>())
                  .serialize(Codec.unboundedMap(Codec.STRING, Lease.CODEC))
                  .build());

  public static void init(net.neoforged.bus.api.IEventBus bus) {
    TYPES.register(bus);
  }

  private static boolean matches(MobEffectInstance current, Lease lease, long now) {
    if (current == null) return false;
    var own = lease.granted();
    long remaining = own.isInfiniteDuration() ? -1 : own.getDuration() - (now - lease.refreshed());
    return current.getAmplifier() == own.getAmplifier()
        && current.isAmbient() == own.isAmbient()
        && current.isVisible() == own.isVisible()
        && (current.isInfiniteDuration() && remaining == -1
            || Math.abs(current.getDuration() - remaining) <= 2);
  }

  public static void grant(LivingEntity entity, String owner, MobEffectInstance effect) {
    long now = entity.level().getGameTime();
    String key = BuiltInRegistries.MOB_EFFECT.getKey(effect.getEffect().value()).toString();
    var ledger = new HashMap<>(entity.getData(LEDGER));
    var lease = ledger.get(key);
    var current = entity.getEffect(effect.getEffect());
    if (lease != null && !matches(current, lease, now)) {
      ledger.remove(key);
      lease = null;
    }
    if (lease == null
        && current != null
        && (current.getAmplifier() > effect.getAmplifier()
            || current.getAmplifier() == effect.getAmplifier()
                && (current.isInfiniteDuration()
                    || current.getDuration() >= effect.getDuration()))) {
      entity.setData(LEDGER, ledger);
      return;
    }
    Optional<MobEffectInstance> previous =
        lease == null ? Optional.ofNullable(current).map(MobEffectInstance::new) : lease.previous();
    long captured = lease == null ? now : lease.captured();
    entity.addEffect(effect);
    ledger.put(key, new Lease(owner, new MobEffectInstance(effect), previous, captured, now));
    entity.setData(LEDGER, ledger);
  }

  public static void reconcile(LivingEntity entity) {
    var ledger = new HashMap<>(entity.getData(LEDGER));
    if (ledger.isEmpty()) return;
    long now = entity.level().getGameTime();
    var data = CyberwareManager.get(entity);
    boolean operational = CyberwareManager.operational(entity);
    for (var entry : new ArrayList<>(ledger.entrySet())) {
      var lease = entry.getValue();
      boolean equipped = data.hasBehavior(CyberCraft.id(lease.owner()));
      boolean os = lease.owner().equals("sandevistan") || lease.owner().equals("berserk");
      if (operational && equipped && (!os || data.sandevistanActiveUntil() > now)) continue;
      var current = entity.getEffect(lease.granted().getEffect());
      if (matches(current, lease, now)) {
        entity.removeEffect(lease.granted().getEffect());
        lease
            .previous()
            .ifPresent(
                previous -> {
                  int duration =
                      previous.isInfiniteDuration()
                          ? -1
                          : Math.max(
                              0,
                              previous.getDuration()
                                  - (int) Math.min(Integer.MAX_VALUE, now - lease.captured()));
                  if (duration != 0)
                    entity.addEffect(
                        new MobEffectInstance(
                            previous.getEffect(),
                            duration,
                            previous.getAmplifier(),
                            previous.isAmbient(),
                            previous.isVisible(),
                            previous.showIcon()));
                });
      }
      ledger.remove(entry.getKey());
    }
    entity.setData(LEDGER, ledger);
  }

  private OwnedEffects() {}
}
