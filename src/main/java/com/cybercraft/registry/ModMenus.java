package com.cybercraft.registry;

import com.cybercraft.CyberCraft;
import com.cybercraft.menu.CyberwareMenu;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModMenus {
  private static final DeferredRegister<MenuType<?>> TYPES =
      DeferredRegister.create(Registries.MENU, CyberCraft.MOD_ID);
  public static final Supplier<MenuType<CyberwareMenu>> CYBERWARE =
      TYPES.register("cyberware", () -> IMenuTypeExtension.create(CyberwareMenu::new));

  public static void init(net.neoforged.bus.api.IEventBus bus) {
    TYPES.register(bus);
  }
}
