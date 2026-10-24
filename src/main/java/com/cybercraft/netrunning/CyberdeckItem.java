package com.cybercraft.netrunning;

import com.cybercraft.cyberware.CyberwareData;
import com.cybercraft.cyberware.CyberwareManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Cyberdeck.
 *  - Tasto destro mentre miri un mob: lancia il Quickhack selezionato.
 *  - Maiusc più tasto destro: passa al Quickhack successivo.
 * Ogni hack consuma RAM. La RAM si rigenera da sola (vedi CyberwareManager).
 */
public class CyberdeckItem extends Item {

    public CyberdeckItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResultHolder.success(stack);
        }
        if (player.isShiftKeyDown()) {
            NetrunnerManager.select(serverPlayer, 1);
        } else {
            CyberwareData data = CyberwareManager.get(serverPlayer);
            NetrunnerManager.cast(serverPlayer, data.selectedHack());
        }
        return InteractionResultHolder.success(stack);
    }
}
