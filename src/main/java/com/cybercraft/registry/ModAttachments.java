package com.cybercraft.registry;

import com.cybercraft.CyberCraft;
import com.cybercraft.cyberware.CyberwareData;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

/**
 * Data Attachment: dati personalizzati agganciati al giocatore.
 * Vengono salvati nel file del mondo e sopravvivono alla morte.
 */
public final class ModAttachments {

    public static final AttachmentType<CyberwareData> CYBERWARE = AttachmentRegistry.<CyberwareData>create(
            CyberCraft.id("cyberware"),
            builder -> builder
                    .initializer(() -> CyberwareData.DEFAULT) // valore per chi non ha ancora dati
                    .persistent(CyberwareData.CODEC)          // salvataggio su disco
                    .copyOnDeath());                          // gli impianti restano dopo la morte

    private ModAttachments() {}

    /** Chiamare dall'inizializzazione per forzare il caricamento della classe. */
    public static void init() {}
}
