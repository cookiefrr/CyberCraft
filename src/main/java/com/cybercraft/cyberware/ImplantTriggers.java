package com.cybercraft.cyberware;

import com.cybercraft.cyberware.animation.AnimationSet;
import com.cybercraft.cyberware.animation.Clip;
import com.cybercraft.cyberware.animation.ImplantAnimations;
import com.cybercraft.cyberware.animation.Trigger;
import com.cybercraft.equipment.EquipmentRegistry;
import com.cybercraft.network.ImplantAnimationPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.PacketDistributor;

public final class ImplantTriggers {
  private ImplantTriggers() {}

  public static void play(LivingEntity entity, Trigger trigger) {
    if (entity.level().isClientSide || !(entity.level() instanceof ServerLevel level)) return;
    for (var entry : CyberwareManager.get(entity).equipment()) {
      var definition = EquipmentRegistry.definition(level, entry.definition());
      if (definition == null) continue;
      String implant = definition.model().getPath();
      AnimationSet set = ImplantAnimations.get(implant);
      if (set == null) continue;
      Clip clip = set.trigger(trigger);
      if (clip == null) continue;
      PacketDistributor.sendToPlayersTrackingEntityAndSelf(
          entity, new ImplantAnimationPayload(entity.getId(), implant, clip.id()));
    }
  }
}
