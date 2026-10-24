package com.cybercraft.item;

import com.cybercraft.cyberware.Cyberware;
import com.cybercraft.cyberware.CyberwareManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CyberwareImplantItem extends Item
    implements software.bernie.geckolib.animatable.GeoItem {
  private final software.bernie.geckolib.animatable.instance.AnimatableInstanceCache
      animationCache = software.bernie.geckolib.util.GeckoLibUtil.createInstanceCache(this);

  private final Cyberware implant;

  public CyberwareImplantItem(Properties properties, Cyberware implant) {
    super(properties);
    this.implant = implant;
  }

  @Override
  public software.bernie.geckolib.animatable.instance.AnimatableInstanceCache
      getAnimatableInstanceCache() {
    return animationCache;
  }

  @Override
  public void registerControllers(
      software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar controllers) {}

  @Override
  public void createGeoRenderer(
      java.util.function.Consumer<software.bernie.geckolib.animatable.client.GeoRenderProvider>
          consumer) {
    consumer.accept(
        new software.bernie.geckolib.animatable.client.GeoRenderProvider() {
          private com.cybercraft.client.InventoryIconRenderer renderer;

          @Override
          public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
              getGeoItemRenderer() {
            if (renderer == null)
              renderer = new com.cybercraft.client.InventoryIconRenderer(CyberwareImplantItem.this);
            return renderer;
          }
        });
  }

  public Cyberware implant() {
    return implant;
  }

  @Override
  public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
    ItemStack stack = player.getItemInHand(hand);

    if (!(player instanceof ServerPlayer serverPlayer)) {
      return InteractionResultHolder.success(stack);
    }
    CyberwareManager.InstallResult result = CyberwareManager.tryInstall(serverPlayer, stack);

    if (result == CyberwareManager.InstallResult.INSTALLED) {

      return InteractionResultHolder.success(stack);
    }
    return InteractionResultHolder.fail(stack);
  }
}
