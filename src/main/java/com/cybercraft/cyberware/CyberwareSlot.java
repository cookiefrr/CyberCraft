package com.cybercraft.cyberware;

/**
 * Slot corporei del catalogo Cyberpunk 2077.
 * In ogni slot può essere installato un solo impianto alla volta.
 */
public enum CyberwareSlot {
    OPERATING_SYSTEM("operating_system"),
    FRONTAL_CORTEX("frontal_cortex"),
    OCULAR("ocular"),
    HANDS("hands"),
    ARMS("arms"),
    NERVOUS("nervous"),
    CIRCULATORY("circulatory"),
    INTEGUMENTARY("integumentary"),
    SKELETON("skeleton"),
    LEGS("legs");

    private final String id;

    CyberwareSlot(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    /** Chiave di traduzione del nome dello slot. */
    public String translationKey() {
        return "slot.cybercraft." + id;
    }
}
