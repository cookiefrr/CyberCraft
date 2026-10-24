package com.cybercraft.menu;

import com.cybercraft.registry.ModBlocks;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;

/**
 * Menu del Banco Impianti. CraftingMenu accetta solo il tavolo vanilla,
 * quindi qui il controllo di validita' usa il nostro blocco: altrimenti
 * il server chiude la schermata un tick dopo l'apertura.
 */
public class ImplantForgeMenu extends CraftingMenu {

    private final ContainerLevelAccess access;

    public ImplantForgeMenu(int containerId, Inventory inventory, ContainerLevelAccess access) {
        super(containerId, inventory, access);
        this.access = access;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, ModBlocks.IMPLANT_FORGE);
    }
}
