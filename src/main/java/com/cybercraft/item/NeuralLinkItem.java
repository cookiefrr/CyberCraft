package com.cybercraft.item;

import com.cybercraft.cyberware.SandevistanManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Collegamento Neurale: l'interfaccia per attivare gli impianti ACTIVE.
 * Tenendolo in mano la action bar mostra Tolleranza, RAM e Capacità.
 * Il Sandevistan si può attivare sia con il keybind dedicato sia col tasto destro.
 */
public class NeuralLinkItem extends Item {

    public NeuralLinkItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (player instanceof ServerPlayer serverPlayer) {
            SandevistanManager.activate(serverPlayer);
        }
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}
