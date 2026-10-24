package com.cybercraft.netrunning;

import com.cybercraft.cyberware.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

public final class CyberdeckItem extends com.cybercraft.item.CyberwareImplantItem {
  public CyberdeckItem(Properties properties) {
    super(properties, Cyberware.CYBERDECK);
  }

  @Override
  public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
    var stack = player.getItemInHand(hand);
    if (player instanceof ServerPlayer p) {
      if (!CyberwareManager.get(p).has(Cyberware.CYBERDECK)) CyberwareManager.tryInstall(p, stack);
      else if (player.isShiftKeyDown()) NetrunnerManager.select(p, 1);
      else CyberwareManager.openMenu(p);
    }
    return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
  }
}
