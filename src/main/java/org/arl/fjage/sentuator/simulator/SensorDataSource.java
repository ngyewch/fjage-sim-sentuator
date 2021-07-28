package org.arl.fjage.sentuator.simulator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Sensor data source. Uses right-handed coordinates.
 */
public interface SensorDataSource {

  /**
   * Get value at the specified coordinates and timestamp.
   *
   * @param coords    Coordinates.
   * @param timestamp Timestamp (milliseconds since epoch).
   * @return Value.
   */
  @Nullable
  Float getValue(@NotNull float[] coords, long timestamp);
}
