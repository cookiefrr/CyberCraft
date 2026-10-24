package com.cybercraft.netrunning;

import com.cybercraft.cyberware.Cyberware;
import com.cybercraft.cyberware.CyberwareData;
import com.cybercraft.cyberware.CyberwareManager;
import com.cybercraft.network.CyberwareSyncPayload;
import com.cybercraft.network.HackResultPayload;
import com.cybercraft.registry.ModItems;
import com.cybercraft.util.Combat;
import com.cybercraft.util.Targeting;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Logica condivisa del Cyberdeck: usata sia dal tasto destro dell'item
 * sia dallo scanner HUD. Tutte le verifiche restano sul server.
 */
public final class NetrunnerManager {

    public static final double RANGE = Targeting.MAX_RANGE;
    public static final double AIM_TOLERANCE = 0.985; // circa 10 gradi
    public static final int COOLDOWN_TICKS = 10;

    private static final Map<UUID, Long> LAST_CAST = new HashMap<>();

    private NetrunnerManager() {}

    public static float ramCost(ServerPlayer player, Quickhack hack) {
        CyberwareData data = CyberwareManager.get(player);
        float cost = hack.ramCost();
        if (data.has(Cyberware.BEHAVIORAL_IMPRINT)) {
            cost *= 0.9f;
        }
        return Math.max(1.0f, Math.round(cost * 10.0f) / 10.0f);
    }

    public static boolean hasCyberdeck(ServerPlayer player) {
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(ModItems.CYBERDECK)) {
                return true;
            }
        }
        for (ItemStack stack : player.getInventory().offhand) {
            if (stack.is(ModItems.CYBERDECK)) {
                return true;
            }
        }
        return false;
    }

    /** Esegue un Quickhack. Restituisce il codice di esito usato dall'HUD. */
    public static int cast(ServerPlayer player, int index) {
        return cast(player, index, -1);
    }

    /**
     * Esegue un Quickhack sul bersaglio indicato dal client.
     * Con {@code targetId < 0} il server cerca il bersaglio lungo la mira (percorso item).
     */
    public static int cast(ServerPlayer player, int index, int targetId) {
        CyberwareData data = CyberwareManager.get(player);
        if (!hasCyberdeck(player)) {
            return fail(player, 4, "msg.cybercraft.no_cyberdeck");
        }
        if (data.overloaded()) {
            return fail(player, 5, "msg.cybercraft.overloaded");
        }
        long now = player.level().getGameTime();
        if (now < LAST_CAST.getOrDefault(player.getUUID(), 0L)) {
            return fail(player, 3, "msg.cybercraft.cooldown_short");
        }

        Quickhack hack = Quickhack.byIndex(index);
        Optional<LivingEntity> target = targetId >= 0
                ? findExplicitTarget(player, targetId)
                : Combat.findTarget(player, RANGE, AIM_TOLERANCE);
        if (target.isEmpty()) {
            return fail(player, 1, "msg.cybercraft.no_target");
        }

        float cost = ramCost(player, hack);
        if (!CyberwareManager.spendRam(player, cost)) {
            return fail(player, 2, "msg.cybercraft.no_ram");
        }

        ServerLevel level = (ServerLevel) player.level();
        hack.apply(level, player, target.get());
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.PLAYERS, 1.0f, 1.8f);

        LAST_CAST.put(player.getUUID(), now + COOLDOWN_TICKS);
        CyberwareManager.set(player, CyberwareManager.get(player).withSelectedHack(index));
        CyberwareSyncPayload.send(player, CyberwareManager.get(player));
        HackResultPayload.send(player, true, 0);
        return 0;
    }

    /**
     * Rivalida il bersaglio inviato dal client: deve esistere, essere vivo,
     * essere entro la portata, avere linea di vista libera ed essere quello
     * effettivamente colpito dal raggio del mirino.
     */
    private static Optional<LivingEntity> findExplicitTarget(ServerPlayer player, int targetId) {
        Entity entity = player.level().getEntity(targetId);
        if (entity == null || !Targeting.isValidTarget(entity, player)) {
            return Optional.empty();
        }
        LivingEntity living = (LivingEntity) entity;
        if (player.distanceTo(living) > RANGE || !player.hasLineOfSight(living)) {
            return Optional.empty();
        }

        Vec3 eye = player.getEyePosition();
        Vec3 end = eye.add(player.getLookAngle().scale(RANGE));
        AABB search = new AABB(eye, end).inflate(1.0);
        EntityHitResult hit = ProjectileUtil.getEntityHitResult(player, eye, end, search,
                candidate -> Targeting.isValidTarget(candidate, player), RANGE * RANGE);
        if (hit == null || hit.getEntity() != living) {
            return Optional.empty();
        }
        return Optional.of(living);
    }

    /** Cambia il Quickhack selezionato di delta posizioni. */
    public static void select(ServerPlayer player, int delta) {
        CyberwareData data = CyberwareManager.get(player);
        int next = Math.floorMod(data.selectedHack() + delta, Quickhack.values().length);
        CyberwareManager.set(player, data.withSelectedHack(next));

        Quickhack hack = Quickhack.byIndex(next);
        CyberwareManager.say(player, Component.translatable("msg.cybercraft.hack_selected",
                Component.translatable(hack.translationKey()),
                Math.round(ramCost(player, hack))).withStyle(ChatFormatting.LIGHT_PURPLE));
        CyberwareSyncPayload.send(player, CyberwareManager.get(player));
    }

    private static int fail(ServerPlayer player, int code, String messageKey) {
        CyberwareManager.say(player, Component.translatable(messageKey).withStyle(ChatFormatting.RED));
        HackResultPayload.send(player, false, code);
        return code;
    }

    public static void clear() {
        LAST_CAST.clear();
    }
}
