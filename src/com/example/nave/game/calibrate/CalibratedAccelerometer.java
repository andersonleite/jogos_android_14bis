package com.example.nave.game.calibrate;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.example.nave.config.DeviceSettings;

public class CalibratedAccelerometer implements SensorEventListener {

	private CalibratedAccelerometerDelegate delegate;
	private float calibratedAccelerationX;
	private float calibratedAccelerationY;
	private float calibratedAccelerationZ;
	private float currentAccelerationX;
	private float currentAccelerationY;
	private float currentAccelerationZ;
	private String NSUDCalibrationXKey;
	private String calibrationYKey;
	private String calibrationZKey;
	private double calibratedRadiansX;
	private double calibratedRadiansY;
	private SensorManager sensorManager;

	static CalibratedAccelerometer sharedAccelerometer = null;

	public static CalibratedAccelerometer sharedAccelerometer() {
		if (sharedAccelerometer == null) {
			sharedAccelerometer = new CalibratedAccelerometer();
		}
		return sharedAccelerometer;
	}

	public CalibratedAccelerometer() {
		this.catchAccelerometer();
	}

	public void catchAccelerometer() {

		sensorManager = DeviceSettings.sensormanager;
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_GAME);

	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent acceleration) {

		acceleration.values[0] = acceleration.values[0] / 10;
		acceleration.values[1] = acceleration.values[1] / 10;
		acceleration.values[2] = acceleration.values[2] / 10;

		// Read acceleration
		this.currentAccelerationX = acceleration.values[0];
		this.currentAccelerationY = acceleration.values[1];
		this.currentAccelerationZ = acceleration.values[2];

		// Dispatch Accelerometer Read
		if (this.delegate != null) {
			try {
				if (this.delegate.getClass().getMethod(
						"calibratedAccelerometerDidAccelerate",
						SensorEventListener.class, SensorEvent.class) != null) {
					this.delegate.calibratedAccelerometerDidAccelerate(this,
							acceleration);
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
	}

	public void calibrateByAcceleration(AutoCalibrationAcceleration acceleration) {
		this.calibrateByX(acceleration.x, acceleration.y, acceleration.z);
	}

	public void calibrateByCurrentAcceleration() {
		this.calibrateByX(this.currentAccelerationX, this.currentAccelerationY,
				this.currentAccelerationZ);

	}

	public void calibrateByX(float x, float y, float z) {
		this.calibratedAccelerationX = x;
		this.calibratedAccelerationY = y;
		this.calibratedAccelerationZ = z;

		this.updateCalibratedRadians();
	}

	private void updateCalibratedRadians() {
		this.calibratedRadiansX = Math.atan2(this.calibratedAccelerationX,
				this.calibratedAccelerationZ);
		this.calibratedRadiansY = Math.atan2(this.calibratedAccelerationY, -1);

	}

	public void loadUserSavedCalibration() {
	}

	public void saveUserCalibration() {
	}

	public void setDelegate(CalibratedAccelerometerDelegate delegate) {
		this.delegate = delegate;
	}

	public CalibratedAccelerometerDelegate getDelegate() {
		return delegate;
	}

	public double calibratedDifferenceFromXRadians(double rad) {

		double radDiff = rad - this.calibratedRadiansX;
		if (radDiff > Math.PI) {
			radDiff = rad - (this.calibratedRadiansX + (2 * Math.PI));
		}
		if (radDiff < -Math.PI) {
			radDiff = (rad + (2 * Math.PI)) - this.calibratedRadiansX;
		}

		return radDiff;
	}

	public double calibratedDifferenceFromYRadians(double rad) {

		double radDiff = rad - this.calibratedRadiansY;
		if (radDiff > Math.PI) {
			radDiff = rad - (this.calibratedRadiansY + (2 * Math.PI));
		}
		if (radDiff < -Math.PI) {
			radDiff = (rad + (2 * Math.PI)) - this.calibratedRadiansY;
		}

		return radDiff;
	}

}