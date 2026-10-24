package com.cybercraft.cyberware;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

/**
 * Reinforced Tendons: doppio salto.
 *
 * Il client invia un payload quando il giocatore preme salto in aria; il server
 * verifica impianto, stato e numero di salti già usati. La decisione resta
 * quindi autoritativa e non si può barare modificando il client.
 */
public final class DoubleJumpHandler {

    /** Spinta verticale del secondo salto (il salto vanilla è circa 0.42). */
    public static final double JUMP_POWER = 0.55;

    /** Giocatori che hanno già usato il doppio salto in questo "volo". */
    private static final Set<UUID> USED = new HashSet<>();

    private DoubleJumpHandler() {}

    /** Da chiamare ogni tick: azzera il contatore appena il giocatore tocca terra o esce dallo stato di volo. */
    public static void tick(MinecraftServer server) {
        Set<UUID> online = new HashSet<>();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            online.add(player.getUUID());
            if (canReset(player)) {
                USED.remove(player.getUUID());
            }
        }
        USED.retainAll(online); // dimentica chi è uscito dal server
    }

    public static void tryDoubleJump(ServerPlayer player) {
        if (canReset(player)) {
            return;
        }
        CyberwareData data = CyberwareManager.get(player);
        if (!data.has(Cyberware.REINFORCED_TENDONS) || data.overloaded()) {
            return;
        }
        if (!USED.add(player.getUUID())) {
            return; // un solo doppio salto per ogni permanenza in aria
        }

        Vec3 movement = player.getDeltaMovement();
        player.setDeltaMovement(movement.x, JUMP_POWER, movement.z);
        player.resetFallDistance(); // il secondo salto annulla la caduta accumulata
        player.hasImpulse = true;
        player.hurtMarked = true;
        // Il movimento delle entità controllate dal giocatore va inviato esplicitamente al client.
        player.connection.send(new ClientboundSetEntityMotionPacket(player));

        ServerLevel level = (ServerLevel) player.level();
        level.sendParticles(ParticleTypes.CLOUD, player.getX(), player.getY(), player.getZ(),
                10, 0.25, 0.1, 0.25, 0.05);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.SLIME_JUMP, SoundSource.PLAYERS, 0.7f, 1.5f);
    }

    /** Il contatore si azzera quando il giocatore è di nuovo in una condizione "a terra". */
    private static boolean canReset(ServerPlayer player) {
        return player.onGround()
                || player.isInWater()
                || player.isInLava()
                || player.isFallFlying()
                || player.getAbilities().flying
                || player.isPassenger()
                || player.isSpectator();
    }

    /** Pulizia alla chiusura del server (utile in singleplayer quando si cambia mondo). */
    public static void clear() {
        USED.clear();
    }
}
