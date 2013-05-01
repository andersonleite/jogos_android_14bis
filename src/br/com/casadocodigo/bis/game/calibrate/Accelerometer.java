package br.com.casadocodigo.bis.game.calibrate;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import br.com.casadocodigo.bis.config.DeviceSettings;

public class Accelerometer implements SensorEventListener {

	private AccelerometerDelegate delegate;

	private float currentAccelerationX;
	private float currentAccelerationY;

	private float calibratedAccelerationX;
	private float calibratedAccelerationY; 
	
	private SensorManager sensorManager;

	private int calibrated;

	static Accelerometer sharedAccelerometer = null;

	public static Accelerometer sharedAccelerometer() {
		if (sharedAccelerometer == null) {
			sharedAccelerometer = new Accelerometer();
		}
		return sharedAccelerometer;
	}

	public Accelerometer() {
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
		
		if(calibrated < 100){
			this.calibratedAccelerationX += acceleration.values[0];
			this.calibratedAccelerationY += acceleration.values[1];
			
			System.out.println(acceleration.values[0]);
			System.out.println(acceleration.values[1]);
			
			calibrated++;
			
			if (calibrated == 100 ) {
				this.calibratedAccelerationX /= 100;
				this.calibratedAccelerationY /= 100;
			}

			return;
			
		} 
				
		// Read acceleration
		this.currentAccelerationX = acceleration.values[0] - this.calibratedAccelerationX;
		this.currentAccelerationY = acceleration.values[1] - this.calibratedAccelerationY;

		System.out.println("=====================");
		System.out.println(calibrated);
		System.out.println(this.calibratedAccelerationX);
		System.out.println(this.calibratedAccelerationY);
		System.out.println("---");
		System.out.println(this.currentAccelerationX);
		System.out.println(this.currentAccelerationY);
		System.out.println("---");
		System.out.println(acceleration.values[0]);
		System.out.println(acceleration.values[1]);

		
		// Dispatch Accelerometer Read
		if (this.delegate != null) {
			this.delegate.accelerometerDidAccelerate(currentAccelerationX, currentAccelerationY);
		}

	}

	public void setDelegate(AccelerometerDelegate delegate) {
		this.delegate = delegate;
	}

	public AccelerometerDelegate getDelegate() {
		return delegate;
	}

	public double calibratedDifferenceFromXRadians(double rad) {

		double radDiff = rad; // - this.calibratedRadiansX;
		if (radDiff > Math.PI) {
			radDiff = rad - 2 * Math.PI;
		}
		if (radDiff < -Math.PI) {
			radDiff = (rad + (2 * Math.PI));// - this.calibratedRadiansX;
		}

		return radDiff;
	}

	public double calibratedDifferenceFromYRadians(double rad) {

		double radDiff = rad;// - this.calibratedRadiansY;
		if (radDiff > Math.PI) {
			radDiff = rad - 2 * Math.PI;
		}
		if (radDiff < -Math.PI) {
			radDiff = (rad + (2 * Math.PI));// - this.calibratedRadiansY;
		}

		return radDiff;
	}

}