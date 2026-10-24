package com.cybercraft.equipment;

import com.cybercraft.CyberCraft;
import com.cybercraft.cyberware.Cyberware;
import java.util.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public final class ImplantBehaviors {
  private static final Map<ResourceLocation, ImplantBehavior> BEHAVIORS = new HashMap<>();

  public static void register(ResourceLocation id, ImplantBehavior behavior) {
    if (BEHAVIORS.putIfAbsent(id, Objects.requireNonNull(behavior)) != null)
      throw new IllegalArgumentException("Duplicate implant behavior: " + id);
  }

  public static boolean contains(ResourceLocation id) {
    return BEHAVIORS.containsKey(id)
        || id.getNamespace().equals(CyberCraft.MOD_ID) && Cyberware.byId(id.getPath()).isPresent();
  }

  public static void tick(LivingEntity entity, InstalledImplant implant) {
    var behavior = BEHAVIORS.get(implant.behavior());
    if (behavior != null) behavior.tick(entity);
  }

  public static void changed(LivingEntity entity, InstalledImplant implant, boolean equipped) {
    var behavior = BEHAVIORS.get(implant.behavior());
    if (behavior != null) {
      if (equipped) behavior.equipped(entity);
      else behavior.unequipped(entity);
    }
  }

  private ImplantBehaviors() {}
}
