package org.arl.fjage.sentuator.simulator;

import org.arl.fjage.sentuator.GenericMeasurement;
import org.arl.fjage.sentuator.Measurement;
import org.arl.fjage.sentuator.Quantity;
import org.arl.fjage.sentuator.Sentuator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract simple sensor.
 */
public abstract class AbstractSimpleSensor
    extends Sentuator {

  private final long pollingInterval;
  private final boolean autoEnable;
  private final List<Source> sources = new ArrayList<>();

  /**
   * Constructs a new AbstractSimpleSensor.
   *
   * @param sentuatorName   Sentuator name.
   * @param pollingInterval Polling interval (ms).
   * @param autoEnable      Auto enable.
   */
  public AbstractSimpleSensor(@NotNull String sentuatorName, long pollingInterval, boolean autoEnable) {
    super();

    this.sentuatorName = sentuatorName;
    this.pollingInterval = pollingInterval;
    this.autoEnable = autoEnable;
  }

  /**
   * Associates a data source with a quantity.
   *
   * @param name             Quantity name.
   * @param units            Units.
   * @param sensorDataSource Sensor data source.
   */
  public void register(@NotNull String name, @Nullable String units, @NotNull SensorDataSource sensorDataSource) {
    sources.add(new Source(name, units, sensorDataSource));
  }

  @Override
  protected void setup() {
    setPollingInterval(pollingInterval);
  }

  @Override
  protected void startup() {
    if (autoEnable) {
      setEnable(true);
    }
  }

  @Override
  protected Measurement measure() {
    final float[] location = getLocation();
    final GenericMeasurement m = new GenericMeasurement();
    m.setSensorType(sentuatorName);
    if ((location != null) && (location.length >= 2)) {
      for (final Source source : sources) {
        final Float value = source.getSensorDataSource().getValue(location, currentTimeMillis());
        if (value != null) {
          m.set(source.getName(), new Quantity(value, source.getUnits()));
        }
      }
    }
    return m;
  }

  /**
   * Returns the current location.
   *
   * @return Current location.
   */
  @Nullable
  protected abstract float[] getLocation();

  private static class Source {

    private final String name;
    private final String units;
    private final SensorDataSource sensorDataSource;

    private Source(@NotNull String name, @Nullable String units, @NotNull SensorDataSource sensorDataSource) {
      this.name = name;
      this.units = units;
      this.sensorDataSource = sensorDataSource;
    }

    @NotNull
    public String getName() {
      return name;
    }

    @Nullable
    public String getUnits() {
      return units;
    }

    @NotNull
    public SensorDataSource getSensorDataSource() {
      return sensorDataSource;
    }
  }
}
