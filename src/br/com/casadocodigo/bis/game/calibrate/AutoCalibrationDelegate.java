package br.com.casadocodigo.bis.game.calibrate;

import android.hardware.SensorEvent;

public interface AutoCalibrationDelegate {

	void autoCalibrationEnded(AutoCalibration bsAutoCalibration, boolean sucessful);

	void autoCalibrationSucessful(AutoCalibration bsAutoCalibration, AutoCalibrationAcceleration bsAutoCalibrationAcceleration);

	void autoCalibrationError(AutoCalibration bsAutoCalibration, SensorEvent lastRead);

}
