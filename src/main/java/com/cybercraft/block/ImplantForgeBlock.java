package com.cybercraft.block;

import com.cybercraft.menu.ImplantForgeMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;

/** Banco degli impianti: un tavolo da lavoro con l'interfaccia di crafting. */
public class ImplantForgeBlock extends CraftingTableBlock {

    public ImplantForgeBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return new SimpleMenuProvider(
                (id, inventory, player) -> new ImplantForgeMenu(id, inventory,
                        ContainerLevelAccess.create(level, pos)),
                Component.translatable("container.cybercraft.implant_forge"));
    }
}
