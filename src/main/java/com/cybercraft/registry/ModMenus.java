package com.cybercraft.registry;

import com.cybercraft.CyberCraft;
import com.cybercraft.menu.CyberwareMenu;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.inventory.MenuType;

public final class ModMenus {
  public static final MenuType<CyberwareMenu> CYBERWARE =
      Registry.register(
          BuiltInRegistries.MENU,
          CyberCraft.id("cyberware"),
          new ExtendedScreenHandlerType<CyberwareMenu, Integer>(
              CyberwareMenu::new, ByteBufCodecs.VAR_INT));

  public static void init() {}
}
