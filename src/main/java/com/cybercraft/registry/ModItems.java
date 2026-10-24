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
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;

/** Registro di tutti gli item della mod. */
public final class ModItems {

    // --- Materiali grezzi e lavorati ---
    public static final Item RAW_TITANIUM = simple("raw_titanium");
    public static final Item TITANIUM_INGOT = simple("titanium_ingot");
    public static final Item RAW_NEODYMIUM = simple("raw_neodymium");
    public static final Item NEODYMIUM_INGOT = simple("neodymium_ingot");
    public static final Item RAW_LITHIUM = simple("raw_lithium");
    public static final Item LITHIUM_INGOT = simple("lithium_ingot");

    // --- Componenti tecnologici (niente legno: solo metallo, vetro, redstone) ---
    public static final Item CIRCUIT_BOARD = simple("circuit_board");
    public static final Item SERVO_MOTOR = simple("servo_motor");
    public static final Item BIO_GEL = simple("bio_gel");
    public static final Item OPTICS_LENS = simple("optics_lens");

    // --- Strumenti ---
    public static final Item NEURAL_LINK = register("neural_link", NeuralLinkItem::new,
            new Item.Properties().stacksTo(1));
    public static final Item CYBERDECK = register("cyberdeck", CyberdeckItem::new,
            new Item.Properties().stacksTo(1));
    public static final Item EDGERUNNERS_BOOK = register("edgerunners_book", EdgerunnersBookItem::new,
            new Item.Properties().stacksTo(1));

    // --- Armi ---
    public static final Item MONOWIRE = register("monowire", Item::new,
            new Item.Properties().stacksTo(1).attributes(monowireAttributes()));

    // --- Impianti (uno per ogni voce del catalogo Cyberware) ---
    public static final Map<Cyberware, Item> IMPLANTS = new EnumMap<>(Cyberware.class);

    static {
        for (Cyberware cyberware : Cyberware.values()) {
            Item item = register(cyberware.id(),
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

    /**
     * Monocorda: danno alto, colpi lenti e portata di 2 blocchi in più della spada.
     * Gli id dei modificatori sono nostri. Il tooltip li mostra come bonus
     * invece che come valori base.
     */
    private static ItemAttributeModifiers monowireAttributes() {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(CyberCraft.id("monowire_damage"), 6.0,
                                AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED,
                        new AttributeModifier(CyberCraft.id("monowire_speed"), -2.0,
                                AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ENTITY_INTERACTION_RANGE,
                        new AttributeModifier(CyberCraft.id("monowire_reach"), 2.0,
                                AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .build();
    }

    /** Registra un item nel registro vanilla. */
    private static <T extends Item> T register(String name, Function<Item.Properties, T> factory,
                                               Item.Properties properties) {
        T item = factory.apply(properties);
        return Registry.register(BuiltInRegistries.ITEM, CyberCraft.id(name), item);
    }

    /** Chiamare dall'inizializzazione per forzare il caricamento della classe. */
    public static void init() {}
}
