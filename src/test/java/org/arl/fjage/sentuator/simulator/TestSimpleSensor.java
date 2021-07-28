package org.arl.fjage.sentuator.simulator;

public class TestSimpleSensor
    extends AbstractSimpleSensor {

  private float[] location;

  public TestSimpleSensor(String sentuatorName, long pollingInterval, boolean autoEnable) {
    super(sentuatorName, pollingInterval, autoEnable);
  }

  @Override
  protected float[] getLocation() {
    return location;
  }

  public void setLocation(float[] location) {
    this.location = location;
  }
}
