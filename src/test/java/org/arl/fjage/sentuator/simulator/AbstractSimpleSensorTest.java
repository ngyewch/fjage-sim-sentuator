package org.arl.fjage.sentuator.simulator;

import org.junit.jupiter.api.Test;

import java.io.IOException;

public class AbstractSimpleSensorTest {

  @Test
  public void testAgent()
      throws IOException {
    final SensorDataSource sensorDataSource = ImageSensorDataSource.newImageSensorDataSourceFromResource(
        "heatmap.pgm", 0, 0, 200, 200, 30.0f, 40.0f);

    final TestSimpleSensor testSimpleSensor = new TestSimpleSensor("test", 1000, true);
    testSimpleSensor.register("temperate", "C", sensorDataSource);
  }
}
