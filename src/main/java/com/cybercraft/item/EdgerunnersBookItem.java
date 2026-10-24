package com.cybercraft.item;

import com.cybercraft.network.OpenBookPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Edgerunner's Book: tasto destro per aprire il manuale illustrato. */
public class EdgerunnersBookItem extends Item {

    public EdgerunnersBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player instanceof ServerPlayer serverPlayer) {
            OpenBookPayload.send(serverPlayer);
        }
        return InteractionResultHolder.success(stack);
    }
}
