package com.cybercraft.block;

import com.cybercraft.cyberware.CyberwareManager;
import com.cybercraft.item.CyberwareImplantItem;
import com.cybercraft.network.OpenCyberwareScreenPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/** Tavolo di installazione: installa l'impianto in mano e apre la vista degli slot. */
public class InstallationTableBlock extends Block {

    public InstallationTableBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hit) {
        if (player instanceof ServerPlayer serverPlayer) {
            OpenCyberwareScreenPayload.send(serverPlayer);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(stack.getItem() instanceof CyberwareImplantItem implantItem)) {
            if (player instanceof ServerPlayer serverPlayer) {
                OpenCyberwareScreenPayload.send(serverPlayer);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        if (player instanceof ServerPlayer serverPlayer) {
            CyberwareManager.InstallResult result =
                    CyberwareManager.tryInstall(serverPlayer, implantItem.implant());
            if (result == CyberwareManager.InstallResult.INSTALLED && !player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            if (result != CyberwareManager.InstallResult.INSTALLED) {
                return ItemInteractionResult.FAIL;
            }
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }
}
