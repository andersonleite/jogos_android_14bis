package br.com.casadocodigo.bis.game.calibrate;

import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public interface CalibratedAccelerometerDelegate {
	public void calibratedAccelerometerDidAccelerate(SensorEventListener accelerometer, SensorEvent acceleration);
}
