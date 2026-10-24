package com.cybercraft.client;

/** Stato volatile dello scanner HUD (non salvato: vive solo sul client). */
public final class ScannerState {

    public static boolean active;
    public static long targetChangedAt;
    public static java.util.UUID targetId;
    public static long uploadStart;
    public static long uploadResultAt;
    public static boolean uploadSuccess;
    public static int uploadCode;

    private ScannerState() {}

    public static void toggle() {
        active = !active;
        targetId = null;
        targetChangedAt = 0L;
        uploadStart = 0L;
        uploadResultAt = 0L;
    }

    public static void close() {
        active = false;
        targetId = null;
        uploadStart = 0L;
        uploadResultAt = 0L;
    }

    public static void onTarget(java.util.UUID id) {
        if (java.util.Objects.equals(id, targetId)) {
            return;
        }
        targetId = id;
        targetChangedAt = System.currentTimeMillis();
    }

    public static void beginUpload() {
        uploadStart = System.currentTimeMillis();
        uploadResultAt = 0L;
        uploadSuccess = false;
    }

    public static void finishUpload(boolean success, int code) {
        uploadResultAt = System.currentTimeMillis();
        uploadSuccess = success;
        uploadCode = code;
    }
}
