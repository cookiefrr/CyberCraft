package com.cybercraft.client;

import com.cybercraft.cyberware.CyberwareData;

/** Cache client dell'ultimo stato cyberware ricevuto dal server. */
public final class ClientCyberwareState {

    private static CyberwareData data = CyberwareData.DEFAULT;

    private ClientCyberwareState() {}

    public static CyberwareData get() {
        return data;
    }

    public static void set(CyberwareData value) {
        data = value;
    }

    public static void cycleHack(int delta) {
        int next = Math.floorMod(data.selectedHack() + delta,
                com.cybercraft.netrunning.Quickhack.values().length);
        data = data.withSelectedHack(next);
    }
}
