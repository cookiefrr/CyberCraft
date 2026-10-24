package com.cybercraft.item;

import com.cybercraft.cyberware.*;
import com.cybercraft.equipment.EquipmentRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

public final class NeuralLinkItem extends Item {
  public NeuralLinkItem(Properties properties) {
    super(properties);
  }

  @Override
  public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
    if (player instanceof ServerPlayer p) {
      var other =
          player.getItemInHand(
              hand == InteractionHand.MAIN_HAND
                  ? InteractionHand.OFF_HAND
                  : InteractionHand.MAIN_HAND);
      if (EquipmentRegistry.find(level, other).isPresent()) CyberwareManager.tryInstall(p, other);
      else if (player.isShiftKeyDown()) SandevistanManager.activate(p);
      else CyberwareManager.openMenu(p);
    }
    return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
  }
}
