package com.cybercraft.client;

import com.cybercraft.cyberware.CyberwareData;

public final class ClientCyberwareState {

  private static CyberwareData data = CyberwareData.DEFAULT;

  private ClientCyberwareState() {}

  public static CyberwareData get() {
    return data;
  }

  public static void set(CyberwareData value) {
    data = value;
  }
}
