package com.cybercraft.cyberware;

import java.util.Optional;

/**
 * Catalogo completo degli impianti cyberware.
 *
 * Ogni voce dichiara slot, tipo di attivazione, costo in Tolleranza,
 * drain passivo, capacità occupata e tier (usato dalle ricette e dal libro).
 * I comportamenti vivono in {@link ImplantEffects}, {@link SandevistanManager}
 * e {@link DoubleJumpHandler}.
 */
public enum Cyberware {
    // Sistema operativo
    SANDEVISTAN            ("sandevistan",              CyberwareSlot.OPERATING_SYSTEM, Kind.ACTIVE,  35.0f, 0.0f, 12.0f, 3),
    BERSERK                ("berserk",                  CyberwareSlot.OPERATING_SYSTEM, Kind.ACTIVE,  30.0f, 0.0f, 14.0f, 3),
    CHROME_COMPRESSOR      ("chrome_compressor",        CyberwareSlot.OPERATING_SYSTEM, Kind.PASSIVE,  0.0f, 0.0f, 10.0f, 3),

    // Corteccia frontale
    AXOLOTL                ("axolotl",                  CyberwareSlot.FRONTAL_CORTEX,   Kind.PASSIVE,  0.0f, 0.0f,  5.0f, 2),
    QUANTUM_TUNER          ("quantum_tuner",            CyberwareSlot.FRONTAL_CORTEX,   Kind.PASSIVE,  0.0f, 0.0f,  6.0f, 3),
    COX2_OPTIMIZER         ("cox2_optimizer",           CyberwareSlot.FRONTAL_CORTEX,   Kind.PASSIVE,  0.0f, 0.0f,  5.0f, 2),
    SELF_ICE               ("self_ice",                 CyberwareSlot.FRONTAL_CORTEX,   Kind.PASSIVE,  0.0f, 0.0f,  4.0f, 2),
    NEWTON_MEMORY_BOOST    ("newton_memory_boost",      CyberwareSlot.FRONTAL_CORTEX,   Kind.PASSIVE,  0.0f, 0.0f,  4.0f, 2),
    EX_DISK_RAM_UPGRADE    ("ex_disk_ram_upgrade",      CyberwareSlot.FRONTAL_CORTEX,   Kind.PASSIVE,  0.0f, 0.0f,  6.0f, 3),

    // Oculare
    KIROSHI_OPTICS         ("kiroshi_optics",           CyberwareSlot.OCULAR,           Kind.PASSIVE,  0.0f, 0.0f,  5.0f, 2),
    BEHAVIORAL_IMPRINT     ("behavioral_imprint",       CyberwareSlot.OCULAR,           Kind.PASSIVE,  0.0f, 0.0f,  4.0f, 2),

    // Mani
    SMART_LINK             ("smart_link",               CyberwareSlot.HANDS,            Kind.PASSIVE,  0.0f, 0.0f,  5.0f, 2),
    BALLISTIC_COPROCESSOR  ("ballistic_coprocessor",    CyberwareSlot.HANDS,            Kind.PASSIVE,  0.0f, 0.0f,  5.0f, 2),
    MICROGENERATOR         ("microgenerator",           CyberwareSlot.HANDS,            Kind.PASSIVE,  0.0f, 0.0f,  6.0f, 2),
    HANDLE_WRAP            ("handle_wrap",              CyberwareSlot.HANDS,            Kind.PASSIVE,  0.0f, 0.0f,  3.0f, 1),

    // Braccia
    MANTIS_BLADES          ("mantis_blades",            CyberwareSlot.ARMS,             Kind.ON_HIT,   3.0f, 0.0f,  8.0f, 2),
    GORILLA_ARMS           ("gorilla_arms",             CyberwareSlot.ARMS,             Kind.PASSIVE,  0.0f, 0.0f,  8.0f, 2),
    PROJECTILE_LAUNCHER    ("projectile_launch_system", CyberwareSlot.ARMS,             Kind.ON_HIT,  20.0f, 0.0f, 10.0f, 3),

    // Sistema nervoso
    KERENZIKOV             ("kerenzikov",               CyberwareSlot.NERVOUS,          Kind.PASSIVE,  0.0f, 0.0f,  6.0f, 2),
    REFLEX_SYNAPTIC        ("reflex_synaptic_accelerator", CyberwareSlot.NERVOUS,       Kind.PASSIVE,  0.0f, 0.0f,  7.0f, 3),
    MANEUVERING_SYSTEM     ("maneuvering_system",       CyberwareSlot.NERVOUS,          Kind.PASSIVE,  0.0f, 0.0f,  5.0f, 2),
    ADRENALINE_CONVERTER   ("adrenaline_converter",     CyberwareSlot.NERVOUS,          Kind.PASSIVE,  0.0f, 0.0f,  5.0f, 2),

    // Circolatorio
    SECOND_HEART           ("second_heart",             CyberwareSlot.CIRCULATORY,      Kind.PASSIVE,  0.0f, 0.0f, 10.0f, 3),
    BIOMONITOR             ("biomonitor",               CyberwareSlot.CIRCULATORY,      Kind.PASSIVE,  0.0f, 0.0f,  6.0f, 2),
    BLOOD_PUMP             ("blood_pump",               CyberwareSlot.CIRCULATORY,      Kind.PASSIVE,  0.0f, 0.0f,  5.0f, 2),
    HEAL_ON_KILL           ("heal_on_kill",             CyberwareSlot.CIRCULATORY,      Kind.PASSIVE,  0.0f, 0.0f,  5.0f, 2),
    SYN_LUNGS_BIOPLASTIC   ("syn_lungs_bioplastic",     CyberwareSlot.CIRCULATORY,      Kind.PASSIVE,  0.0f, 0.0f,  4.0f, 2),

    // Integumentario
    SUBDERMAL_ARMOR        ("subdermal_armor",          CyberwareSlot.INTEGUMENTARY,    Kind.PASSIVE,  0.0f, 0.0f,  7.0f, 2),
    OPTICAL_CAMO           ("optical_camo",             CyberwareSlot.INTEGUMENTARY,    Kind.PASSIVE,  0.0f, 0.0f,  7.0f, 3),
    PAIN_EDITOR            ("pain_editor",              CyberwareSlot.INTEGUMENTARY,    Kind.PASSIVE,  0.0f, 0.0f,  6.0f, 2),
    SHOCK_N_AWE            ("shock_n_awe",              CyberwareSlot.INTEGUMENTARY,    Kind.PASSIVE,  0.0f, 0.0f,  6.0f, 2),
    FIREPROOF_GROUNDING    ("fireproof_grounding",      CyberwareSlot.INTEGUMENTARY,    Kind.PASSIVE,  0.0f, 0.0f,  5.0f, 2),

    // Scheletro
    TITANIUM_BONES         ("titanium_bones",           CyberwareSlot.SKELETON,         Kind.PASSIVE,  0.0f, 0.0f,  6.0f, 2),
    DENSE_MARROW           ("dense_marrow",             CyberwareSlot.SKELETON,         Kind.PASSIVE,  0.0f, 0.0f,  6.0f, 2),
    EPIMORPHIC_SKELETON    ("epimorphic_skeleton",      CyberwareSlot.SKELETON,         Kind.PASSIVE,  0.0f, 0.0f,  8.0f, 3),
    BIONIC_JOINTS          ("bionic_joints",            CyberwareSlot.SKELETON,         Kind.PASSIVE,  0.0f, 0.0f,  6.0f, 2),
    SPRING_JOINTS          ("spring_joints",            CyberwareSlot.SKELETON,         Kind.PASSIVE,  0.0f, 0.0f,  5.0f, 1),

    // Gambe
    CYBER_LEGS             ("cyber_legs",               CyberwareSlot.LEGS,             Kind.PASSIVE,  0.0f, 1.0f,  6.0f, 1),
    REINFORCED_TENDONS     ("reinforced_tendons",       CyberwareSlot.LEGS,             Kind.ON_JUMP,  0.0f, 0.0f,  6.0f, 2),
    FORTIFIED_ANKLES       ("fortified_ankles",         CyberwareSlot.LEGS,             Kind.PASSIVE,  0.0f, 0.0f,  6.0f, 2),
    LYNX_PAWS              ("lynx_paws",                CyberwareSlot.LEGS,             Kind.PASSIVE,  0.0f, 0.0f,  4.0f, 1),
    JENKINS_TENDONS        ("jenkins_tendons",          CyberwareSlot.LEGS,             Kind.PASSIVE,  0.0f, 0.0f,  5.0f, 2);

    /** Come si attiva l'impianto. */
    public enum Kind {
        /** Sempre attivo. Pesa sulla tolleranza ogni secondo. */
        PASSIVE,
        /** Scatta quando colpisci. Costa a ogni colpo. */
        ON_HIT,
        /** Scatta quando premi salto in aria. Non consuma tolleranza. */
        ON_JUMP,
        /** Si attiva a comando con il tasto dedicato o con il Collegamento Neurale. */
        ACTIVE
    }

    private final String id;
    private final CyberwareSlot slot;
    private final Kind kind;
    private final float activationCost;
    private final float passiveDrain;
    private final float capacityCost;
    private final int tier;

    Cyberware(String id, CyberwareSlot slot, Kind kind, float activationCost, float passiveDrain,
              float capacityCost, int tier) {
        this.id = id;
        this.slot = slot;
        this.kind = kind;
        this.activationCost = activationCost;
        this.passiveDrain = passiveDrain;
        this.capacityCost = capacityCost;
        this.tier = tier;
    }

    public String id() { return id; }
    public CyberwareSlot slot() { return slot; }
    public Kind kind() { return kind; }
    /** Tolleranza consumata a ogni uso (attivazione o colpo). */
    public float activationCost() { return activationCost; }
    /** Tolleranza consumata ogni secondo finché è installato. */
    public float passiveDrain() { return passiveDrain; }
    /** Capacità cyberware occupata finché l'impianto è installato. */
    public float capacityCost() { return capacityCost; }
    /** 1 = base, 2 = avanzato, 3 = militare. Guida ricette e colore icona. */
    public int tier() { return tier; }

    public String translationKey() {
        return "cyberware.cybercraft." + id;
    }

    public String descriptionKey() {
        return "book.cybercraft.desc." + id;
    }

    public static Optional<Cyberware> byId(String id) {
        for (Cyberware c : values()) {
            if (c.id.equals(id)) {
                return Optional.of(c);
            }
        }
        return Optional.empty();
    }
}
