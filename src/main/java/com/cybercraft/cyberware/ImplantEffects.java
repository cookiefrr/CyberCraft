package com.cybercraft.cyberware;

import com.cybercraft.CyberCraft;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.ItemStack;

/**
 * Effetti concreti dei 43 impianti.
 *
 * Gli attributi sono transient: se il giocatore muore o cambia dimensione
 * vengono ricreati dal server, quindi il metodo tick li riapplica una volta
 * al secondo. In sovraccarico tutti i modificatori vengono rimossi.
 */
public final class ImplantEffects {

    private record Modifier(ResourceLocation id, Holder<Attribute> attribute,
                            double amount, AttributeModifier.Operation operation) {}

    private static final Map<Cyberware, List<Modifier>> MODIFIERS = new EnumMap<>(Cyberware.class);

    private static final Map<UUID, Long> BIOMONITOR_READY = new HashMap<>();
    private static final Map<UUID, Long> SECOND_HEART_READY = new HashMap<>();
    private static final Map<UUID, Long> ADRENALINE_READY = new HashMap<>();
    private static final Map<UUID, Long> PROJECTILE_READY = new HashMap<>();

    private static final ResourceLocation FALL_ID = CyberCraft.id("implant_fall_damage");
    private static final ResourceLocation JUMP_ID = CyberCraft.id("implant_jump_strength");
    private static final ResourceLocation SPEED_ID = CyberCraft.id("implant_movement_speed");
    private static final ResourceLocation ARMOR_ID = CyberCraft.id("implant_armor");
    private static final ResourceLocation TOUGHNESS_ID = CyberCraft.id("implant_toughness");
    private static final ResourceLocation HEALTH_ID = CyberCraft.id("implant_max_health");
    private static final ResourceLocation ATTACK_ID = CyberCraft.id("implant_attack_damage");
    private static final ResourceLocation ATTACK_SPEED_ID = CyberCraft.id("implant_attack_speed");
    private static final ResourceLocation KNOCKBACK_ID = CyberCraft.id("implant_knockback_resistance");
    private static final ResourceLocation WATER_ID = CyberCraft.id("implant_water_movement");
    private static final ResourceLocation SNEAK_ID = CyberCraft.id("implant_sneaking_speed");
    private static final ResourceLocation LUCK_ID = CyberCraft.id("implant_luck");

    static {
        // Ogni impianto usa id di modificatore propri: due impianti sullo stesso
        // attributo si sommano senza sovrascriversi.
        add(Cyberware.AXOLOTL, HEALTH_ID, Attributes.MAX_HEALTH, 2.0, AttributeModifier.Operation.ADD_VALUE);
        add(Cyberware.SELF_ICE, ARMOR_ID, Attributes.ARMOR, 2.0, AttributeModifier.Operation.ADD_VALUE);
        add(Cyberware.BEHAVIORAL_IMPRINT, LUCK_ID, Attributes.LUCK, 1.0, AttributeModifier.Operation.ADD_VALUE);

        add(Cyberware.SMART_LINK, ATTACK_ID, Attributes.ATTACK_DAMAGE, 2.0, AttributeModifier.Operation.ADD_VALUE);
        add(Cyberware.BALLISTIC_COPROCESSOR, ATTACK_ID, Attributes.ATTACK_DAMAGE, 1.5, AttributeModifier.Operation.ADD_VALUE);
        add(Cyberware.BALLISTIC_COPROCESSOR, ATTACK_SPEED_ID, Attributes.ATTACK_SPEED, 0.5, AttributeModifier.Operation.ADD_VALUE);
        add(Cyberware.MICROGENERATOR, ATTACK_ID, Attributes.ATTACK_DAMAGE, 1.0, AttributeModifier.Operation.ADD_VALUE);
        add(Cyberware.HANDLE_WRAP, ATTACK_SPEED_ID, Attributes.ATTACK_SPEED, 0.5, AttributeModifier.Operation.ADD_VALUE);
        add(Cyberware.GORILLA_ARMS, ATTACK_ID, Attributes.ATTACK_DAMAGE, 3.0, AttributeModifier.Operation.ADD_VALUE);
        add(Cyberware.GORILLA_ARMS, KNOCKBACK_ID, Attributes.KNOCKBACK_RESISTANCE, 0.2, AttributeModifier.Operation.ADD_VALUE);

        add(Cyberware.KERENZIKOV, SPEED_ID, Attributes.MOVEMENT_SPEED, 0.02, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        add(Cyberware.REFLEX_SYNAPTIC, SPEED_ID, Attributes.MOVEMENT_SPEED, 0.02, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        add(Cyberware.REFLEX_SYNAPTIC, ATTACK_SPEED_ID, Attributes.ATTACK_SPEED, 1.0, AttributeModifier.Operation.ADD_VALUE);
        add(Cyberware.MANEUVERING_SYSTEM, JUMP_ID, Attributes.JUMP_STRENGTH, 0.08, AttributeModifier.Operation.ADD_VALUE);
        add(Cyberware.MANEUVERING_SYSTEM, FALL_ID, Attributes.FALL_DAMAGE_MULTIPLIER, -0.15, AttributeModifier.Operation.ADD_VALUE);

        add(Cyberware.BLOOD_PUMP, HEALTH_ID, Attributes.MAX_HEALTH, 2.0, AttributeModifier.Operation.ADD_VALUE);
        add(Cyberware.SYN_LUNGS_BIOPLASTIC, WATER_ID, Attributes.WATER_MOVEMENT_EFFICIENCY, 0.5, AttributeModifier.Operation.ADD_VALUE);

        add(Cyberware.SUBDERMAL_ARMOR, ARMOR_ID, Attributes.ARMOR, 4.0, AttributeModifier.Operation.ADD_VALUE);
        add(Cyberware.PAIN_EDITOR, ARMOR_ID, Attributes.ARMOR, 2.0, AttributeModifier.Operation.ADD_VALUE);
        add(Cyberware.PAIN_EDITOR, TOUGHNESS_ID, Attributes.ARMOR_TOUGHNESS, 2.0, AttributeModifier.Operation.ADD_VALUE);
        add(Cyberware.OPTICAL_CAMO, SNEAK_ID, Attributes.SNEAKING_SPEED, 0.1, AttributeModifier.Operation.ADD_VALUE);

        add(Cyberware.TITANIUM_BONES, TOUGHNESS_ID, Attributes.ARMOR_TOUGHNESS, 4.0, AttributeModifier.Operation.ADD_VALUE);
        add(Cyberware.DENSE_MARROW, HEALTH_ID, Attributes.MAX_HEALTH, 4.0, AttributeModifier.Operation.ADD_VALUE);
        add(Cyberware.EPIMORPHIC_SKELETON, HEALTH_ID, Attributes.MAX_HEALTH, 6.0, AttributeModifier.Operation.ADD_VALUE);
        add(Cyberware.BIONIC_JOINTS, SPEED_ID, Attributes.MOVEMENT_SPEED, 0.03, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        add(Cyberware.SPRING_JOINTS, JUMP_ID, Attributes.JUMP_STRENGTH, 0.15, AttributeModifier.Operation.ADD_VALUE);
        add(Cyberware.SPRING_JOINTS, FALL_ID, Attributes.FALL_DAMAGE_MULTIPLIER, -0.3, AttributeModifier.Operation.ADD_VALUE);

        add(Cyberware.FORTIFIED_ANKLES, FALL_ID, Attributes.FALL_DAMAGE_MULTIPLIER, -0.5, AttributeModifier.Operation.ADD_VALUE);
        add(Cyberware.LYNX_PAWS, SNEAK_ID, Attributes.SNEAKING_SPEED, 0.3, AttributeModifier.Operation.ADD_VALUE);
        add(Cyberware.JENKINS_TENDONS, SPEED_ID, Attributes.MOVEMENT_SPEED, 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    private ImplantEffects() {}

    private static void add(Cyberware cyberware, ResourceLocation suffix, Holder<Attribute> attribute,
                            double amount, AttributeModifier.Operation operation) {
        ResourceLocation id = CyberCraft.id("implant_" + cyberware.id() + "_" + suffix.getPath());
        MODIFIERS.computeIfAbsent(cyberware, key -> new ArrayList<>())
                .add(new Modifier(id, attribute, amount, operation));
    }

    // ------------------------------------------------------------------
    // Tick passivo
    // ------------------------------------------------------------------

    public static void tick(ServerPlayer player, CyberwareData data) {
        if (data.overloaded()) {
            clearModifiers(player, data);
            return;
        }
        for (String id : data.implants()) {
            Cyberware.byId(id).ifPresent(cyberware -> {
                applyModifiers(player, cyberware);
                applyPassiveEffect(player, cyberware);
            });
        }
    }

    private static void applyModifiers(ServerPlayer player, Cyberware cyberware) {
        for (Modifier modifier : MODIFIERS.getOrDefault(cyberware, List.of())) {
            AttributeInstance instance = player.getAttribute(modifier.attribute());
            if (instance == null) {
                continue;
            }
            instance.addOrUpdateTransientModifier(new AttributeModifier(
                    modifier.id(), modifier.amount(), modifier.operation()));
        }
    }

    /** Rimuove subito i modificatori di un impianto appena disinstallato. */
    public static void removeModifiers(ServerPlayer player, Cyberware cyberware) {
        for (Modifier modifier : MODIFIERS.getOrDefault(cyberware, List.of())) {
            AttributeInstance instance = player.getAttribute(modifier.attribute());
            if (instance != null) {
                instance.removeModifier(modifier.id());
            }
        }
    }

    private static void clearModifiers(ServerPlayer player, CyberwareData data) {
        for (String id : data.implants()) {
            Cyberware.byId(id).ifPresent(cyberware -> {
                for (Modifier modifier : MODIFIERS.getOrDefault(cyberware, List.of())) {
                    AttributeInstance instance = player.getAttribute(modifier.attribute());
                    if (instance != null) {
                        instance.removeModifier(modifier.id());
                    }
                }
            });
        }
    }

    /** Effetti con durata breve, rinnovati ogni secondo. */
    private static void applyPassiveEffect(ServerPlayer player, Cyberware cyberware) {
        switch (cyberware) {
            case CYBER_LEGS ->
                    player.addEffect(new MobEffectInstance(MobEffects.JUMP, 60, 2, true, false, false));
            case KIROSHI_OPTICS ->
                    player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 300, 0, true, false, false));
            case SYN_LUNGS_BIOPLASTIC ->
                    player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 300, 0, true, false, false));
            case FIREPROOF_GROUNDING ->
                    player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 300, 0, true, false, false));
            case EPIMORPHIC_SKELETON -> {
                if (player.getHealth() < player.getMaxHealth() * 0.5f) {
                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 0, true, false, false));
                }
            }
            case OPTICAL_CAMO -> {
                if (player.isShiftKeyDown() && player.getDeltaMovement().horizontalDistanceSqr() < 0.0025) {
                    player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 40, 0, true, false, false));
                }
            }
            default -> {
            }
        }
    }

    // ------------------------------------------------------------------
    // Eventi di combattimento
    // ------------------------------------------------------------------

    /** Bonus da mischia. Chiamato da CyberwareManager.onMeleeAttack. */
    public static void onMeleeHit(ServerPlayer player, LivingEntity target) {
        CyberwareData data = CyberwareManager.get(player);
        if (data.overloaded()) {
            return;
        }
        ServerLevel level = (ServerLevel) player.level();
        ItemStack main = player.getMainHandItem();

        if (main.isEmpty()) {
            if (data.has(Cyberware.MANTIS_BLADES)) {
                extraDamage(player, level, target, Cyberware.MANTIS_BLADES, CyberwareManager.MANTIS_BONUS_DAMAGE);
            }
            if (data.has(Cyberware.GORILLA_ARMS) && CyberwareManager.spendTolerance(player, 2.0f)) {
                extraDamage(player, level, target, null, 3.0f);
                target.knockback(0.8, player.getX() - target.getX(), player.getZ() - target.getZ());
            }
            if (data.has(Cyberware.PROJECTILE_LAUNCHER)
                    && ready(PROJECTILE_READY, player, 100L)
                    && CyberwareManager.spendTolerance(player, Cyberware.PROJECTILE_LAUNCHER.activationCost())) {
                level.explode(player, target.getX(), target.getY(0.5), target.getZ(), 1.6f,
                        net.minecraft.world.level.Level.ExplosionInteraction.NONE);
                level.sendParticles(ParticleTypes.EXPLOSION, target.getX(), target.getY(0.5), target.getZ(),
                        6, 0.4, 0.3, 0.4, 0.02);
            }
        }

        if (data.has(Cyberware.MICROGENERATOR) && player.level().getRandom().nextFloat() < 0.15f) {
            target.hurt(level.damageSources().lightningBolt(), 2.5f);
            level.sendParticles(ParticleTypes.ELECTRIC_SPARK, target.getX(), target.getY(0.6), target.getZ(),
                    12, 0.3, 0.3, 0.3, 0.15);
        }
    }

    private static void extraDamage(ServerPlayer player, ServerLevel level, LivingEntity target,
                                    Cyberware source, float amount) {
        if (source != null && !CyberwareManager.spendTolerance(player, source.activationCost())) {
            return;
        }
        target.hurt(level.damageSources().playerAttack(player), amount);
        level.sendParticles(ParticleTypes.CRIT, target.getX(), target.getY(0.5), target.getZ(),
                12, 0.3, 0.3, 0.3, 0.2);
        level.playSound(null, target.getX(), target.getY(), target.getZ(),
                SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0f, 1.4f);
    }

    public static void onKill(ServerPlayer player, LivingEntity victim) {
        CyberwareData data = CyberwareManager.get(player);
        if (data.overloaded()) {
            return;
        }
        if (data.has(Cyberware.HEAL_ON_KILL)) {
            player.heal(4.0f);
            ServerLevel level = (ServerLevel) player.level();
            level.sendParticles(ParticleTypes.HEART, player.getX(), player.getY() + 1.2, player.getZ(),
                    6, 0.3, 0.3, 0.3, 0.02);
        }
        if (data.has(Cyberware.BLOOD_PUMP)) {
            player.heal(1.0f);
        }
    }

    /** Danno subito: Biomonitor, Adrenalina, Shock-n-Awe. */
    public static void onDamaged(ServerPlayer player, net.minecraft.world.damagesource.DamageSource source) {
        CyberwareData data = CyberwareManager.get(player);
        if (data.overloaded() || !player.isAlive()) {
            return;
        }
        ServerLevel level = (ServerLevel) player.level();

        if (data.has(Cyberware.BIOMONITOR)
                && player.getHealth() <= player.getMaxHealth() * 0.25f
                && ready(BIOMONITOR_READY, player, 1200L)) {
            player.heal(6.0f);
            player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 200, 1, true, false, true));
            CyberwareManager.say(player, Component.translatable("msg.cybercraft.biomonitor")
                    .withStyle(net.minecraft.ChatFormatting.GREEN));
            level.sendParticles(ParticleTypes.HEART, player.getX(), player.getY() + 1.0, player.getZ(),
                    10, 0.4, 0.4, 0.4, 0.03);
        }

        if (data.has(Cyberware.ADRENALINE_CONVERTER)
                && player.getHealth() <= player.getMaxHealth() * 0.3f
                && ready(ADRENALINE_READY, player, 1200L)) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 120, 1, true, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 120, 1, true, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 120, 0, true, false, true));
            CyberwareManager.say(player, Component.translatable("msg.cybercraft.adrenaline")
                    .withStyle(net.minecraft.ChatFormatting.GOLD));
        }

        if (data.has(Cyberware.SHOCK_N_AWE)
                && source.getEntity() instanceof LivingEntity attacker
                && attacker != player
                && attacker.distanceToSqr(player) <= 16.0) {
            attacker.hurt(level.damageSources().thorns(player), 2.5f);
            level.sendParticles(ParticleTypes.ELECTRIC_SPARK, attacker.getX(), attacker.getY(0.6), attacker.getZ(),
                    16, 0.35, 0.35, 0.35, 0.2);
        }
    }

    /** Secondo Cuore: annulla la morte una volta ogni minuto. */
    public static boolean allowDeath(ServerPlayer player, CyberwareData data) {
        if (data.overloaded() || !data.has(Cyberware.SECOND_HEART)) {
            return true;
        }
        long now = player.level().getGameTime();
        long readyAt = SECOND_HEART_READY.getOrDefault(player.getUUID(), 0L);
        if (now < readyAt) {
            return true;
        }
        SECOND_HEART_READY.put(player.getUUID(), now + 1200L);
        player.setHealth(Math.max(6.0f, player.getMaxHealth() * 0.3f));
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 300, 2, true, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 1, true, false, true));
        player.removeEffect(MobEffects.CONFUSION);
        ServerLevel level = (ServerLevel) player.level();
        level.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, player.getX(), player.getY() + 0.8, player.getZ(),
                40, 0.5, 0.8, 0.5, 0.4);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0f, 1.1f);
        CyberwareManager.say(player, Component.translatable("msg.cybercraft.second_heart")
                .withStyle(net.minecraft.ChatFormatting.RED));
        return false;
    }

    private static boolean ready(Map<UUID, Long> map, ServerPlayer player, long cooldown) {
        long now = player.level().getGameTime();
        long readyAt = map.getOrDefault(player.getUUID(), 0L);
        if (now < readyAt) {
            return false;
        }
        map.put(player.getUUID(), now + cooldown);
        return true;
    }

    public static void clear() {
        BIOMONITOR_READY.clear();
        SECOND_HEART_READY.clear();
        ADRENALINE_READY.clear();
        PROJECTILE_READY.clear();
    }
}
