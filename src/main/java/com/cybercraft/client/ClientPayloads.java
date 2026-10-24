package com.cybercraft.client;
import com.cybercraft.network.*;
import net.minecraft.client.Minecraft;
public final class ClientPayloads {
    public static void sync(CyberwareSyncPayload p) { ClientCyberwareState.set(p.toData()); }
    public static void openCyberware() { Minecraft.getInstance().setScreen(new CyberwareScreen()); }
    public static void openBook() { Minecraft.getInstance().setScreen(new EdgerunnersBookScreen()); }
    public static void result(HackResultPayload p) { if (ScannerState.active) ScannerState.finishUpload(p.success(),p.code()); }
}
