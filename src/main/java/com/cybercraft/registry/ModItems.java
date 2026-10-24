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
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
  public static final DeferredRegister.Items ITEMS =
      DeferredRegister.createItems(CyberCraft.MOD_ID);

  public static final DeferredItem<Item> RAW_TITANIUM = simple("raw_titanium");
  public static final DeferredItem<Item> TITANIUM_INGOT = simple("titanium_ingot");
  public static final DeferredItem<Item> RAW_NEODYMIUM = simple("raw_neodymium");
  public static final DeferredItem<Item> NEODYMIUM_INGOT = simple("neodymium_ingot");
  public static final DeferredItem<Item> RAW_LITHIUM = simple("raw_lithium");
  public static final DeferredItem<Item> LITHIUM_INGOT = simple("lithium_ingot");

  public static final DeferredItem<Item> CIRCUIT_BOARD = simple("circuit_board");
  public static final DeferredItem<Item> SERVO_MOTOR = simple("servo_motor");
  public static final DeferredItem<Item> BIO_GEL = simple("bio_gel");
  public static final DeferredItem<Item> OPTICS_LENS = simple("optics_lens");

  public static final DeferredItem<Item> NEURAL_LINK =
      register("neural_link", NeuralLinkItem::new, new Item.Properties().stacksTo(1));
  public static final DeferredItem<Item> CYBERDECK =
      register("cyberdeck", CyberdeckItem::new, new Item.Properties().stacksTo(1));
  public static final DeferredItem<Item> EDGERUNNERS_BOOK =
      register("edgerunners_book", EdgerunnersBookItem::new, new Item.Properties().stacksTo(1));

  public static final DeferredItem<Item> MONOWIRE =
      register(
          "monowire",
          Item::new,
          new Item.Properties().stacksTo(1).attributes(monowireAttributes()));

  public static final Map<Cyberware, DeferredItem<Item>> IMPLANTS = new EnumMap<>(Cyberware.class);

  static {
    for (Cyberware cyberware : Cyberware.values()) {
      DeferredItem<Item> item =
          cyberware == Cyberware.CYBERDECK
              ? CYBERDECK
              : register(
                  cyberware.id(),
                  properties -> new CyberwareImplantItem(properties, cyberware),
                  new Item.Properties().stacksTo(1));
      IMPLANTS.put(cyberware, item);
    }
  }

  public static final DeferredItem<Item> MANTIS_BLADES = IMPLANTS.get(Cyberware.MANTIS_BLADES);
  public static final DeferredItem<Item> SANDEVISTAN = IMPLANTS.get(Cyberware.SANDEVISTAN);
  public static final DeferredItem<Item> CYBER_LEGS = IMPLANTS.get(Cyberware.CYBER_LEGS);
  public static final DeferredItem<Item> REINFORCED_TENDONS =
      IMPLANTS.get(Cyberware.REINFORCED_TENDONS);

  private ModItems() {}

  public static Item implant(Cyberware cyberware) {
    return IMPLANTS.get(cyberware).get();
  }

  private static DeferredItem<Item> simple(String name) {
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

  private static DeferredItem<Item> register(
      String name, Function<Item.Properties, ? extends Item> factory, Item.Properties properties) {
    return ITEMS.register(name, () -> factory.apply(properties));
  }

  public static void init(net.neoforged.bus.api.IEventBus bus) {
    ITEMS.register(bus);
  }
}
