package com.example.hydra.exception;

/**
 * Thrown when a sensor posts a reading but its device ID is not paired with a
 * plant. This is a client-visible 404, not a malformed request.
 */
public class DeviceNotPairedException extends RuntimeException {

  public DeviceNotPairedException(String message) {
    super(message);
  }
}
