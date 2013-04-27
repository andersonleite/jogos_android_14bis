package br.com.casadocodigo.bis.game.calibrate;

import java.util.ArrayList;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import br.com.casadocodigo.bis.config.DeviceSettings;


public class AutoCalibration implements SensorEventListener {

	private AutoCalibrationDelegate delegate;
	private float tolerance;
	private float secondsToDiscard;
	private float secondsToCalibration;
	private int readsToDiscard;
	private int readsToCalibration;
	private int reads;
	private boolean sucessful;
	private ArrayList accelerationsArray;
	private int autoCalibrationTries;

	private float kAutoCalibrationSeconds = 2;
	private float kAccelerometerUpdateInterval = 30;
	private SensorManager sensorManager;
	private SensorEvent lastRead;

	public AutoCalibration() {
		this.secondsToCalibration = kAutoCalibrationSeconds;
		this.delegate = null;
		this.sucessful = false;
		this.autoCalibrationTries = 10;
	}

	public void setDelegate(AutoCalibrationDelegate delegate) {
		this.delegate = delegate;
	}

	public AutoCalibrationDelegate getDelegate() {
		return delegate;
	}

	public void setAutoCalibrationTries(int autoCalibrationTries) {
		this.autoCalibrationTries = autoCalibrationTries;
	}

	public int getAutoCalibrationTries() {
		return autoCalibrationTries;
	}

	public void startAutoCalibrationWithTolerance(float tol, int sec) {
		// Catch UIAccelerometer
		sensorManager = DeviceSettings.sensormanager;
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);

		// Set AutoCalibration Parameters
		this.tolerance = tol;
		this.secondsToDiscard = sec;
		this.readsToDiscard = (int) (this.secondsToDiscard * 10);
		this.readsToCalibration = (int) (this.secondsToCalibration * 10);
		this.accelerationsArray = new ArrayList();
		this.reads = 0;

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent acceleration) {

		acceleration.values[0] = acceleration.values[0] /10;
		acceleration.values[1] = acceleration.values[1] /10;
		acceleration.values[2] = acceleration.values[2] /10;
		
		this.lastRead = acceleration;
		this.readAccelerometer(acceleration);
	}

	@SuppressWarnings("unchecked")
	private void readAccelerometer(SensorEvent acceleration) {
		this.reads++;
		// Check First Reads to Discard
		if (this.reads > this.readsToDiscard) {
			
			AutoCalibrationAcceleration accelerationValues = new AutoCalibrationAcceleration(acceleration.values[0], acceleration.values[1], acceleration.values[2]);
			
			
			// Add Acceleration Read to Array
			accelerationsArray.add(accelerationValues);
			
			
			// Check Calibration
			if (accelerationsArray.size() >= this.readsToCalibration) {
				this.checkCalibration();
			}
		}
		// Check Tries
		if (this.reads - this.readsToDiscard > this.readsToCalibration
				* this.autoCalibrationTries) {
			this.calibrationEnded();
		}
	}

	private void checkCalibration() {
		float minX;
		float minY;
		float minZ;
		float maxX;
		float maxY;
		float maxZ;

		// Calibration reads range
		int first = accelerationsArray.size() - this.readsToCalibration;
		AutoCalibrationAcceleration accelRead = (AutoCalibrationAcceleration) accelerationsArray.get(first);
		
		minX = accelRead.x;
		minY = accelRead.y;
		minZ = accelRead.z;
		
		maxX = accelRead.x;
		maxY = accelRead.y;
		maxZ = accelRead.z;

		
		// Test last reads
		for (int i = first + 1; i < accelerationsArray.size(); i++) {
			accelRead = (AutoCalibrationAcceleration) accelerationsArray.get(i);
			
			// Check Minimum
			if (accelRead.x < minX) {
				minX = accelRead.x;
			}
			if (accelRead.y < minY) {
				minY = accelRead.y;
			}
			if (accelRead.z < minZ) {
				minZ = accelRead.z;
			}

			// Check Maximum
			if (accelRead.x > maxX) {
				maxX = accelRead.x;
			}
			if (accelRead.y > maxY) {
				maxY = accelRead.y;
			}
			if (accelRead.z > maxZ) {
				maxZ = accelRead.z;
			}
		}

		// Check Tolerance
		boolean checkSucessful = true;
		if (maxX - minX > this.tolerance) {
			checkSucessful = false;
		}
		if (maxY - minY > this.tolerance) {
			checkSucessful = false;
		}
		if (maxZ - minZ > this.tolerance) {
			checkSucessful = false;
		}

		if (checkSucessful) {
			this.calibrationSucessful();
		} else {
			this.calibrationError();
		}
	}
	
	private void calibrationEnded() {
		this.stopAutoCalibration();

		try {
			if(this.delegate!=null){
			if (this.delegate.getClass().getMethod("autoCalibrationEnded",
					AutoCalibration.class, boolean.class) != null) {
				this.delegate.autoCalibrationEnded(this, this.sucessful);
			}
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	public void stopAutoCalibration() {
		// Release itself from delegate
		if (CalibratedAccelerometer.sharedAccelerometer().getDelegate() == this) {
			CalibratedAccelerometer.sharedAccelerometer().setDelegate(null);
		}
	}

	private void calibrationError() {
		this.sucessful = false;

		try {
			if(this.delegate!=null){
			if (this.delegate.getClass().getMethod("autoCalibrationError",
					AutoCalibration.class, SensorEvent.class) != null) {

				this.delegate.autoCalibrationError(this, this.lastRead);
			}}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {  
			e.printStackTrace();
		}
	}

	private void calibrationSucessful() {
		this.sucessful = true;

		try {
			
			if(this.delegate!=null){
			if (this.delegate.getClass().getMethod("autoCalibrationSucessful",
					AutoCalibration.class,
					AutoCalibrationAcceleration.class) != null) {

				delegate.autoCalibrationSucessful(this,
						this.averageAcceleration());
			}}
			
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	private AutoCalibrationAcceleration averageAcceleration() {
		float sumX = 0;
		float sumY = 0;
		float sumZ = 0;

		// Test last reads
		for (int i = accelerationsArray.size() - this.readsToCalibration; i < accelerationsArray
				.size(); i++) {
			AutoCalibrationAcceleration accelRead = (AutoCalibrationAcceleration) accelerationsArray.get(i);
			
			sumX += accelRead.x;
			sumY += accelRead.y;
			sumZ += accelRead.z;
		}

		// Set Acceleration Average
		AutoCalibrationAcceleration avgAccel = new AutoCalibrationAcceleration();
		avgAccel.x = sumX / this.readsToCalibration;
		avgAccel.y = sumY / this.readsToCalibration;
		avgAccel.z = sumZ / this.readsToCalibration;

		return avgAccel;
	}
}