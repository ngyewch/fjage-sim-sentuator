package org.arl.fjage.sentuator.simulator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Image sensor data source.
 * <p>
 * This data source can be used to represent a 2D static field.
 */
public class ImageSensorDataSource
    implements SensorDataSource {

  private final BufferedImage bufferedImage;
  private final float x1;
  private final float y1;
  private final float x2;
  private final float y2;
  private final float minValue;
  private final float maxValue;
  private final float pixelsPerMeter;

  private ImageSensorDataSource(@NotNull InputStream imageInputStream, float x1, float y1, float x2, float y2,
                                float minValue, float maxValue)
      throws IOException {
    super();

    bufferedImage = ImageIO.read(imageInputStream);
    if (bufferedImage == null) {
      throw new IOException("image could not be read");
    }
    if (bufferedImage.getColorModel().getColorSpace().getType() != ColorSpace.TYPE_GRAY) {
      throw new IllegalArgumentException("image must be grayscale");
    }

    if (x1 >= x2) {
      throw new IllegalArgumentException("x1 must be less than x2");
    }
    if (y1 >= y2) {
      throw new IllegalArgumentException("y1 must be less than y2");
    }
    if (minValue >= maxValue) {
      throw new IllegalArgumentException("minValue must be less than maxValue");
    }

    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
    this.minValue = minValue;
    this.maxValue = maxValue;

    final float xPixelsPerMeter = ((float) bufferedImage.getWidth()) / (x2 - x1);
    final float yPixelsPerMeter = ((float) bufferedImage.getHeight()) / (y2 - y1);
    pixelsPerMeter = Math.min(xPixelsPerMeter, yPixelsPerMeter);
  }

  /**
   * Creates a new ImageSensorDataSource.
   *
   * @param imageFile Image file.
   * @param x1        X coordinate of bottom left corner.
   * @param y1        Y coordinate of bottom left corner
   * @param x2        X coordinate of top right corner
   * @param y2        Y coordinate of top right corner
   * @param minValue  Minimum value of sensor reading.
   * @param maxValue  Maximum value of sensor reading.
   * @return ImageSensorDataSource.
   * @throws IOException If an I/O error occurred.
   */
  public static ImageSensorDataSource newImageSensorDataSource(@NotNull File imageFile, float x1, float y1, float x2,
                                                               float y2, float minValue, float maxValue)
      throws IOException {
    try (final InputStream inputStream = new FileInputStream(imageFile)) {
      return newImageSensorDataSource(inputStream, x1, y1, x2, y2, minValue, maxValue);
    }
  }

  /**
   * Creates a new ImageSensorDataSource.
   *
   * @param imageResourceName Image resource name.
   * @param x1                X coordinate of bottom left corner.
   * @param y1                Y coordinate of bottom left corner
   * @param x2                X coordinate of top right corner
   * @param y2                Y coordinate of top right corner
   * @param minValue          Minimum value of sensor reading.
   * @param maxValue          Maximum value of sensor reading.
   * @return ImageSensorDataSource.
   * @throws IOException If an I/O error occurred.
   */
  public static ImageSensorDataSource newImageSensorDataSourceFromResource(@NotNull String imageResourceName, float x1,
                                                                           float y1, float x2, float y2,
                                                                           float minValue, float maxValue)
      throws IOException {
    try (final InputStream inputStream = Thread.currentThread().getContextClassLoader()
        .getResourceAsStream(imageResourceName)) {
      if (inputStream == null) {
        throw new FileNotFoundException(String.format("resource not found: %s", imageResourceName));
      }
      return newImageSensorDataSource(inputStream, x1, y1, x2, y2, minValue, maxValue);
    }
  }

  /**
   * Creates a new ImageSensorDataSource.
   *
   * @param imageInputStream Image input stream.
   * @param x1               X coordinate of bottom left corner.
   * @param y1               Y coordinate of bottom left corner
   * @param x2               X coordinate of top right corner
   * @param y2               Y coordinate of top right corner
   * @param minValue         Minimum value of sensor reading.
   * @param maxValue         Maximum value of sensor reading.
   * @return ImageSensorDataSource.
   * @throws IOException If an I/O error occurred.
   */
  public static ImageSensorDataSource newImageSensorDataSource(@NotNull InputStream imageInputStream, float x1,
                                                               float y1, float x2, float y2, float minValue,
                                                               float maxValue)
      throws IOException {
    return new ImageSensorDataSource(imageInputStream, x1, y1, x2, y2, minValue, maxValue);
  }

  @Override
  public @Nullable Float getValue(@NotNull float[] coords, long timestamp) {
    if (coords.length < 2) {
      throw new IllegalArgumentException("invalid coords");
    }
    final float x = coords[0];
    final float y = coords[1];
    final int i = Math.min(Math.round((x - x1) * pixelsPerMeter), bufferedImage.getWidth() - 1);
    final int j = Math.min(Math.round((y2 - y) * pixelsPerMeter), bufferedImage.getHeight() - 1);
    if ((i < 0) || (j < 0) || (i >= bufferedImage.getWidth()) || (j >= bufferedImage.getHeight())) {
      return null;
    }
    final float[] samples = bufferedImage.getRaster()
        .getPixel(i, j, new float[bufferedImage.getColorModel().getNumComponents()]);
    final float sample = samples[0] / (float) Math.pow(2, bufferedImage.getColorModel().getComponentSize(0));
    return (sample * (maxValue - minValue)) + minValue;
  }
}
