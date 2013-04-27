package com.example.nave.game.calibrate;

import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public interface CalibratedAccelerometerDelegate {
	public void calibratedAccelerometerDidAccelerate(SensorEventListener accelerometer, SensorEvent acceleration);
}
