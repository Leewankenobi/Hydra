package com.example.hydra.util;

import java.util.Locale;

/**
 * Device IDs are treated as case-insensitive identifiers. Normalize them at
 * every boundary so a sensor and a plant can never disagree only by case or
 * surrounding whitespace.
 */
public final class DeviceIds {

  private DeviceIds() {
  }

  public static String normalize(String deviceId) {
    return deviceId == null ? null : deviceId.trim().toLowerCase(Locale.ROOT);
  }
}
