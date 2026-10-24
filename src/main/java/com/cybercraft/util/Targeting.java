package com.cybercraft.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/** Regole condivise di targeting tra client e server. */
public final class Targeting {

    /** Portata massima assoluta, indipendente dalla render distance del client. */
    public static final double MAX_RANGE = 64.0;

    private Targeting() {}

    /** La portata segue la render distance, ma non supera {@link #MAX_RANGE}. */
    public static double rangeForRenderDistance(int renderDistanceChunks) {
        return Math.min(MAX_RANGE, Math.max(16.0, renderDistanceChunks * 16.0));
    }

    /** Solo entità viventi, vive, non spettatrici e nello stesso livello dell'osservatore. */
    public static boolean isValidTarget(Entity entity, Entity viewer) {
        return entity instanceof LivingEntity living
                && entity != viewer
                && living.isAlive()
                && !living.isSpectator()
                && !living.isRemoved()
                && entity.level() == viewer.level();
    }
}
