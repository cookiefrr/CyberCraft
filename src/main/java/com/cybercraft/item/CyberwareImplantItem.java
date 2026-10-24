package com.cybercraft.item;

import com.cybercraft.cyberware.Cyberware;
import com.cybercraft.cyberware.CyberwareManager;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/** Item che installa un impianto quando lo usi (tasto destro). */
public class CyberwareImplantItem extends Item {

    private final Cyberware implant;

    public CyberwareImplantItem(Properties properties, Cyberware implant) {
        super(properties);
        this.implant = implant;
    }

    public Cyberware implant() {
        return implant;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        // Sul client basta far partire l'animazione. La logica vera è sul server.
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResultHolder.success(stack);
        }
        CyberwareManager.InstallResult result = CyberwareManager.tryInstall(serverPlayer, implant);

        if (result == CyberwareManager.InstallResult.INSTALLED) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1); // in creativa l'item non si consuma
            }
            return InteractionResultHolder.success(stack);
        }
        return InteractionResultHolder.fail(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip,
                                TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.cybercraft.slot",
                Component.translatable(implant.slot().translationKey())).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.cybercraft.capacity",
                Math.round(implant.capacityCost())).withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("tooltip.cybercraft.tier", implant.tier())
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable(implant.descriptionKey())
                .withStyle(ChatFormatting.DARK_GRAY));
    }
}
