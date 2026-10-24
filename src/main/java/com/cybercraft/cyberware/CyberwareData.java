package com.cybercraft.cyberware;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;

/**
 * Stato cyberware di un giocatore. Salvato come Data Attachment permanente.
 * Il record è immutabile: per cambiare qualcosa si crea una copia con i metodi with*.
 *
 * @param implants                id degli impianti installati
 * @param tolerance               barra Tolleranza (0 a MAX_TOLERANCE)
 * @param ram                     energia del Cyberdeck (0 a MAX_RAM)
 * @param overloaded              true se la tolleranza si è svuotata e il corpo sta cedendo
 * @param selectedHack            indice del Quickhack selezionato
 * @param sandevistanEnd          tick di gioco in cui finisce il raffreddamento del Sandevistan
 * @param sandevistanActiveUntil  tick di gioco in cui finisce l'effetto slow-motion
 */
public record CyberwareData(
        List<String> implants,
        float tolerance,
        float ram,
        boolean overloaded,
        int selectedHack,
        long sandevistanEnd,
        long sandevistanActiveUntil) {

    public static final float MAX_TOLERANCE = 100.0f;
    public static final float MAX_RAM = 100.0f;

    public static final CyberwareData DEFAULT =
            new CyberwareData(List.of(), MAX_TOLERANCE, MAX_RAM, false, 0, 0L, 0L);

    /** Codec per il salvataggio su disco. I campi opzionali permettono di aggiungerne altri in futuro. */
    public static final Codec<CyberwareData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.listOf().optionalFieldOf("implants", List.of()).forGetter(CyberwareData::implants),
            Codec.FLOAT.optionalFieldOf("tolerance", MAX_TOLERANCE).forGetter(CyberwareData::tolerance),
            Codec.FLOAT.optionalFieldOf("ram", MAX_RAM).forGetter(CyberwareData::ram),
            Codec.BOOL.optionalFieldOf("overloaded", false).forGetter(CyberwareData::overloaded),
            Codec.INT.optionalFieldOf("selected_hack", 0).forGetter(CyberwareData::selectedHack),
            Codec.LONG.optionalFieldOf("sandevistan_end", 0L).forGetter(CyberwareData::sandevistanEnd),
            Codec.LONG.optionalFieldOf("sandevistan_active_until", 0L)
                    .forGetter(CyberwareData::sandevistanActiveUntil)
    ).apply(instance, CyberwareData::new));

    public CyberwareData {
        implants = List.copyOf(implants); // copia difensiva: la lista resta immutabile
    }

    public boolean has(Cyberware cyberware) {
        return implants.contains(cyberware.id());
    }

    /** RAM massima del Cyberdeck. L'Ex-Disk migliora solo la rigenerazione. */
    public float maxRam() {
        return MAX_RAM;
    }

    /** Impianto che occupa lo slot dato (se presente). */
    public java.util.Optional<Cyberware> inSlot(CyberwareSlot slot) {
        for (String id : implants) {
            java.util.Optional<Cyberware> c = Cyberware.byId(id);
            if (c.isPresent() && c.get().slot() == slot) {
                return c;
            }
        }
        return java.util.Optional.empty();
    }

    public CyberwareData withImplant(Cyberware cyberware) {
        List<String> copy = new ArrayList<>(implants);
        copy.add(cyberware.id());
        return new CyberwareData(copy, tolerance, ram, overloaded, selectedHack,
                sandevistanEnd, sandevistanActiveUntil);
    }

    public CyberwareData withoutImplant(Cyberware cyberware) {
        List<String> copy = new ArrayList<>(implants);
        copy.remove(cyberware.id());
        return new CyberwareData(copy, tolerance, ram, overloaded, selectedHack,
                sandevistanEnd, sandevistanActiveUntil);
    }

    public CyberwareData withTolerance(float value) {
        float clamped = Math.max(0.0f, Math.min(MAX_TOLERANCE, value));
        return new CyberwareData(implants, clamped, ram, overloaded, selectedHack,
                sandevistanEnd, sandevistanActiveUntil);
    }

    public CyberwareData withRam(float value) {
        float clamped = Math.max(0.0f, Math.min(MAX_RAM, value));
        return new CyberwareData(implants, tolerance, clamped, overloaded, selectedHack,
                sandevistanEnd, sandevistanActiveUntil);
    }

    public CyberwareData withOverloaded(boolean value) {
        return new CyberwareData(implants, tolerance, ram, value, selectedHack,
                sandevistanEnd, sandevistanActiveUntil);
    }

    public CyberwareData withSelectedHack(int value) {
        return new CyberwareData(implants, tolerance, ram, overloaded, value,
                sandevistanEnd, sandevistanActiveUntil);
    }

    /** Registra insieme l'inizio dell'effetto e la fine del cooldown del Sandevistan. */
    public CyberwareData withSandevistan(long activeUntil, long cooldownEnd) {
        return new CyberwareData(implants, tolerance, ram, overloaded, selectedHack,
                cooldownEnd, activeUntil);
    }
}
