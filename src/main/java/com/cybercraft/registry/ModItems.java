package com.cybercraft.registry;

import com.cybercraft.CyberCraft;
import com.cybercraft.cyberware.Cyberware;
import com.cybercraft.item.CyberwareImplantItem;
import com.cybercraft.item.EdgerunnersBookItem;
import com.cybercraft.item.NeuralLinkItem;
import com.cybercraft.netrunning.CyberdeckItem;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public final class ModItems {
  public static final java.util.List<Item> ITEMS = new java.util.ArrayList<>();

  public static final Item RAW_TITANIUM = simple("raw_titanium");
  public static final Item TITANIUM_INGOT = simple("titanium_ingot");
  public static final Item RAW_NEODYMIUM = simple("raw_neodymium");
  public static final Item NEODYMIUM_INGOT = simple("neodymium_ingot");
  public static final Item RAW_LITHIUM = simple("raw_lithium");
  public static final Item LITHIUM_INGOT = simple("lithium_ingot");

  public static final Item CIRCUIT_BOARD = simple("circuit_board");
  public static final Item SERVO_MOTOR = simple("servo_motor");
  public static final Item BIO_GEL = simple("bio_gel");
  public static final Item OPTICS_LENS = simple("optics_lens");

  public static final Item NEURAL_LINK =
      register("neural_link", NeuralLinkItem::new, new Item.Properties().stacksTo(1));
  public static final Item CYBERDECK =
      register("cyberdeck", CyberdeckItem::new, new Item.Properties().stacksTo(1));
  public static final Item EDGERUNNERS_BOOK =
      register("edgerunners_book", EdgerunnersBookItem::new, new Item.Properties().stacksTo(1));

  public static final Item MONOWIRE =
      register(
          "monowire",
          Item::new,
          new Item.Properties().stacksTo(1).attributes(monowireAttributes()));

  public static final Map<Cyberware, Item> IMPLANTS = new EnumMap<>(Cyberware.class);

  static {
    for (Cyberware cyberware : Cyberware.values()) {
      Item item =
          cyberware == Cyberware.CYBERDECK
              ? CYBERDECK
              : register(
                  cyberware.id(),
                  properties -> new CyberwareImplantItem(properties, cyberware),
                  new Item.Properties().stacksTo(1));
      IMPLANTS.put(cyberware, item);
    }
  }

  public static final Item MANTIS_BLADES = IMPLANTS.get(Cyberware.MANTIS_BLADES);
  public static final Item SANDEVISTAN = IMPLANTS.get(Cyberware.SANDEVISTAN);
  public static final Item CYBER_LEGS = IMPLANTS.get(Cyberware.CYBER_LEGS);
  public static final Item REINFORCED_TENDONS = IMPLANTS.get(Cyberware.REINFORCED_TENDONS);

  private ModItems() {}

  public static Item implant(Cyberware cyberware) {
    return IMPLANTS.get(cyberware);
  }

  private static Item simple(String name) {
    return register(name, Item::new, new Item.Properties());
  }

  private static ItemAttributeModifiers monowireAttributes() {
    return ItemAttributeModifiers.builder()
        .add(
            Attributes.ATTACK_DAMAGE,
            new AttributeModifier(
                CyberCraft.id("monowire_damage"), 6.0, AttributeModifier.Operation.ADD_VALUE),
            EquipmentSlotGroup.MAINHAND)
        .add(
            Attributes.ATTACK_SPEED,
            new AttributeModifier(
                CyberCraft.id("monowire_speed"), -2.0, AttributeModifier.Operation.ADD_VALUE),
            EquipmentSlotGroup.MAINHAND)
        .add(
            Attributes.ENTITY_INTERACTION_RANGE,
            new AttributeModifier(
                CyberCraft.id("monowire_reach"), 2.0, AttributeModifier.Operation.ADD_VALUE),
            EquipmentSlotGroup.MAINHAND)
        .build();
  }

  private static Item register(
      String name, Function<Item.Properties, ? extends Item> factory, Item.Properties properties) {
    Item item =
        net.minecraft.core.Registry.register(
            net.minecraft.core.registries.BuiltInRegistries.ITEM,
            CyberCraft.id(name),
            factory.apply(properties));
    ITEMS.add(item);
    return item;
  }

  public static void init() {}
}
