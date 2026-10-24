package com.cybercraft.client;

import com.cybercraft.util.Targeting;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

/**
 * Outline rosso delle entità visibili mentre lo scanner è attivo.
 * Il glow vanilla viene colorato da un team client dedicato; lo stato
 * originale di glow e team viene ripristinato alla chiusura.
 */
public final class EntityOutlineManager {

    private static final String TEAM_NAME = "cybercraft_scan";
    private static final int UPDATE_INTERVAL = 5;
    private static final int MAX_OUTLINED = 48;
    private static final double FOV_MARGIN_DEGREES = 8.0;

    private static final Set<Integer> OUTLINED = new HashSet<>();
    private static final Map<Integer, Boolean> ORIGINAL_GLOW = new HashMap<>();
    private static final Map<Integer, String> ORIGINAL_TEAM = new HashMap<>();

    private static PlayerTeam team;
    private static ClientLevel trackedLevel;
    private static int tickCounter;

    private EntityOutlineManager() {}

    public static void tick(Minecraft client) {
        if (client.player == null || client.level == null || !ScannerState.active) {
            clear(client);
            return;
        }
        if (trackedLevel != client.level) {
            reset();
            trackedLevel = client.level;
        }
        if (++tickCounter < UPDATE_INTERVAL) {
            return;
        }
        tickCounter = 0;
        update(client);
    }

    private static void update(Minecraft client) {
        ClientLevel level = client.level;
        if (level == null || client.player == null) {
            clear(client);
            return;
        }

        Scoreboard scoreboard = level.getScoreboard();
        PlayerTeam redTeam = team(scoreboard);
        double range = Targeting.rangeForRenderDistance(client.options.renderDistance().get());
        Vec3 eye = client.player.getEyePosition(1.0f);
        Vec3 look = client.player.getViewVector(1.0f);
        double cosLimit = Math.cos(Math.min(Math.PI / 2.0,
                halfFovRadians(client) + Math.toRadians(FOV_MARGIN_DEGREES)));

        List<LivingEntity> candidates = new ArrayList<>();
        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class,
                client.player.getBoundingBox().inflate(range),
                entity -> Targeting.isValidTarget(entity, client.player))) {
            if (entity.distanceToSqr(eye) > range * range) {
                continue;
            }
            Vec3 toEntity = entity.getBoundingBox().getCenter().subtract(eye);
            if (toEntity.lengthSqr() < 1.0E-6 || look.dot(toEntity.normalize()) < cosLimit) {
                continue;
            }
            candidates.add(entity);
        }
        candidates.sort(Comparator.comparingDouble(entity -> entity.distanceToSqr(eye)));

        Set<Integer> next = new HashSet<>();
        for (LivingEntity entity : candidates) {
            if (next.size() >= MAX_OUTLINED) {
                break;
            }
            if (!client.player.hasLineOfSight(entity)) {
                continue;
            }
            outlineEntity(entity, redTeam, scoreboard, next);
        }

        for (int id : new ArrayList<>(OUTLINED)) {
            if (!next.contains(id)) {
                restoreEntity(level, scoreboard, id);
            }
        }
    }

    private static void outlineEntity(LivingEntity entity, PlayerTeam redTeam, Scoreboard scoreboard,
                                      Set<Integer> next) {
        int id = entity.getId();
        String name = entity.getScoreboardName();
        PlayerTeam current = scoreboard.getPlayersTeam(name);
        if (current != redTeam) {
            if (current != null) {
                ORIGINAL_TEAM.putIfAbsent(id, current.getName());
                scoreboard.removePlayerFromTeam(name);
            }
            scoreboard.addPlayerToTeam(name, redTeam);
        }
        ORIGINAL_GLOW.putIfAbsent(id, entity.hasGlowingTag());
        if (!entity.hasGlowingTag()) {
            entity.setGlowingTag(true);
        }
        OUTLINED.add(id);
        next.add(id);
    }

    private static void restoreEntity(ClientLevel level, Scoreboard scoreboard, int id) {
        Entity entity = level.getEntity(id);
        if (entity != null) {
            String name = entity.getScoreboardName();
            if (!ORIGINAL_GLOW.getOrDefault(id, true)) {
                entity.setGlowingTag(false);
            }
            if (team != null) {
                scoreboard.removePlayerFromTeam(name, team);
            }
            String original = ORIGINAL_TEAM.get(id);
            if (original != null) {
                PlayerTeam originalTeam = scoreboard.getPlayerTeam(original);
                if (originalTeam != null) {
                    scoreboard.addPlayerToTeam(name, originalTeam);
                }
            }
        }
        OUTLINED.remove(id);
        ORIGINAL_GLOW.remove(id);
        ORIGINAL_TEAM.remove(id);
    }

    private static PlayerTeam team(Scoreboard scoreboard) {
        if (team == null) {
            team = scoreboard.getPlayerTeam(TEAM_NAME);
            if (team == null) {
                team = scoreboard.addPlayerTeam(TEAM_NAME);
                team.setColor(ChatFormatting.RED);
            }
        }
        return team;
    }

    private static double halfFovRadians(Minecraft client) {
        double vertical = Math.toRadians(client.options.fov().get());
        double aspect = (double) client.getWindow().getWidth() / Math.max(1, client.getWindow().getHeight());
        double horizontal = 2.0 * Math.atan(Math.tan(vertical / 2.0) * aspect);
        return Math.max(vertical, horizontal) / 2.0;
    }

    public static void clear(Minecraft client) {
        if (client.level != null && trackedLevel == client.level
                && (!OUTLINED.isEmpty() || team != null)) {
            ClientLevel level = client.level;
            Scoreboard scoreboard = level.getScoreboard();
            for (int id : new ArrayList<>(OUTLINED)) {
                restoreEntity(level, scoreboard, id);
            }
            if (team != null) {
                scoreboard.removePlayerTeam(team);
            }
        }
        reset();
    }

    /** Da chiamare al cambio livello o alla disconnessione. */
    public static void reset() {
        OUTLINED.clear();
        ORIGINAL_GLOW.clear();
        ORIGINAL_TEAM.clear();
        team = null;
        trackedLevel = null;
        tickCounter = 0;
    }
}
