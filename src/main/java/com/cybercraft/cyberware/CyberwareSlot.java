package com.cybercraft.cyberware;

import com.cybercraft.CyberCraft;
import net.minecraft.resources.ResourceLocation;

public record CyberwareSlot(String id) {
  public static final CyberwareSlot OPERATING_SYSTEM = new CyberwareSlot("operating_system");
  public static final CyberwareSlot FRONTAL_CORTEX = new CyberwareSlot("frontal_cortex");
  public static final CyberwareSlot OCULAR = new CyberwareSlot("ocular");
  public static final CyberwareSlot HANDS = new CyberwareSlot("hands");
  public static final CyberwareSlot ARMS = new CyberwareSlot("arms");
  public static final CyberwareSlot NERVOUS = new CyberwareSlot("nervous");
  public static final CyberwareSlot CIRCULATORY = new CyberwareSlot("circulatory");
  public static final CyberwareSlot INTEGUMENTARY = new CyberwareSlot("integumentary");
  public static final CyberwareSlot SKELETON = new CyberwareSlot("skeleton");
  public static final CyberwareSlot LEGS = new CyberwareSlot("legs");

  public static final CyberwareSlot CYBERDECK = new CyberwareSlot("cyberdeck");

  public ResourceLocation resource() {
    return CyberCraft.id(id);
  }

  public String translationKey() {
    return "slot.cybercraft." + id;
  }
}
