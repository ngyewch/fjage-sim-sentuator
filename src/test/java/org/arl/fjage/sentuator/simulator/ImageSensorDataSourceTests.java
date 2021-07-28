package org.arl.fjage.sentuator.simulator;

import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class ImageSensorDataSourceTests {

  @Test
  public void testInvalidResourceName()
      throws IOException {
    assertThrows(FileNotFoundException.class, () ->
        ImageSensorDataSource.newImageSensorDataSourceFromResource("non-existent.pgm",
            0, 0, 200, 200, 10.0f, 13.0f));
  }

  @Test
  public void testPgm()
      throws IOException {
    final SensorDataSource sensorDataSource = ImageSensorDataSource.newImageSensorDataSourceFromResource(
        "heatmap.pgm", 0, 0, 200, 200, 10.0f, 13.0f);
    assertNotNull(sensorDataSource.getValue(new float[]{100.0f, 100.0f}, 0));
    assertNull(sensorDataSource.getValue(new float[]{100.0f, 300.0f}, 0));
  }

  @Test
  public void testPng()
      throws IOException {
    final SensorDataSource sensorDataSource = ImageSensorDataSource.newImageSensorDataSourceFromResource(
        "heatmap.png", 0, 0, 200, 200, 10.0f, 13.0f);
  }

  @Test
  public void testNonGrayscalePng() {
    assertThrows(IllegalArgumentException.class, () -> ImageSensorDataSource.newImageSensorDataSourceFromResource(
        "heatmap-non-grayscale.png", 0, 0, 200, 200, 10.0f, 13.0f));
  }
}
