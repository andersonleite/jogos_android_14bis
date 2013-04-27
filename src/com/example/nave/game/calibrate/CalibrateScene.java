package com.example.nave.game.calibrate;

import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCFadeIn;
import org.cocos2d.actions.interval.CCFadeTo;
import org.cocos2d.actions.interval.CCScaleBy;
import org.cocos2d.actions.interval.CCScaleTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.events.CCTouchDispatcher;
import org.cocos2d.layers.CCColorLayer;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.opengl.CCBitmapFontAtlas;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.types.ccColor4B;

import android.hardware.SensorEvent;

import com.example.nave.game.scenes.GameScene;

import static com.example.nave.config.DeviceSettings.screenWidth;
import static com.example.nave.config.DeviceSettings.screenHeight;;

public class CalibrateScene extends CCLayer implements AutoCalibrationDelegate {

	private CCColorLayer background;
	private boolean HoldMessageOnScreen;
	private CCBitmapFontAtlas calibratingText;
	private GameScene endTarget;
	private String endSelector;
	private CCBitmapFontAtlas holdText;

	public CalibrateScene() {
		// this.scale *= ADJUST_SCALE;

		// Disable all aplications touches
		CCTouchDispatcher.sharedDispatcher().setDispatchEvents(false);

		// Add Background
		this.background = CCColorLayer.node(ccColor4B.ccc4(0, 0, 0, 175),
				screenWidth(),
				screenHeight());
		this.addChild(this.background);
	}

	public void calibrateAndPerformSelector(String selector, GameScene obj) {
		endTarget = obj;
		endSelector = selector;
		this.startCalibration();
	}

	public void startCalibration() {
		// Start Auto-Calibration
		AutoCalibration autoCalibration = new AutoCalibration();
		autoCalibration.setDelegate(this);
		autoCalibration.setAutoCalibrationTries(5);
		autoCalibration.startAutoCalibrationWithTolerance(0.3f, 1);

		this.startCalibrationAnimation();
	}

	public void endCalibration() {
		// Re-enable all aplications touches
		CCTouchDispatcher.sharedDispatcher().setDispatchEvents(true);

		// Perform End Selector
		if (endTarget != null) {
			endTarget.startGame();
		}

		// Remove Me
		this.removeMe();
	}

	private void startCalibrationAnimation() {
		this.HoldMessageOnScreen = false;

		// Add text
		this.calibratingText = CCBitmapFontAtlas.bitmapFontAtlas(
				"CALIBRATING...","UniSansBold_AlphaNum_50.fnt");
		this.calibratingText.setPosition(screenWidth()/2, screenHeight()/2 );
		this.calibratingText.setScale(1.5f);
		this.addChild(this.calibratingText);

		CCScaleTo a1 = CCScaleTo.action(0.5f, 1.6f);
		CCScaleTo a2 = CCScaleTo.action(0.5f, 1.5f);
		this.calibratingText.runAction(CCRepeatForever.action(CCSequence
				.actions(a1, a2)));

	}

	public void autoCalibrationEnded(AutoCalibration sender, boolean sucessful) {
		if (!sucessful) {
			// Set Default Calibration
			CalibratedAccelerometer.sharedAccelerometer().calibrateByX(0, 0,
					-1);
			CalibratedAccelerometer.sharedAccelerometer()
					.saveUserCalibration();

		}
		this.endCalibrationAnimation();

		// Release AutoCalibration
		sender.setDelegate(null);
	}

	private void endCalibrationAnimation() {
		float duration = 1;

		// Background Animation
		CCFadeTo aB1 = CCFadeTo.action(duration, 0);
		this.background.runAction(aB1);

		// Calibrating Text Animation
		this.calibratingText.stopAllActions();
		CCScaleBy aC1 = CCScaleBy.action(duration / 6, 1.1f);
		CCScaleTo aC2 = CCScaleTo.action(duration / 6 * 5, 0);

		this.calibratingText.runAction(CCSequence.actions(aC1, aC2));

		// End Animation
		this.runAction(CCSequence.actions(CCDelayTime.action(duration),
				CCCallFunc.action(this, "endCalibration")));
	}

	private void removeMe() {
		this.removeFromParentAndCleanup(true);

	}

	@Override
	public void autoCalibrationSucessful(AutoCalibration sender,
			AutoCalibrationAcceleration acceleration) {
		// Stop AutoCalibration
		sender.stopAutoCalibration();

		// Save Calibration
		CalibratedAccelerometer.sharedAccelerometer()
				.calibrateByAcceleration(acceleration);
		CalibratedAccelerometer.sharedAccelerometer().saveUserCalibration();

		this.endCalibrationAnimation();

		// Release AutoCalibration
		sender.setDelegate(null);
	}

	@Override
	public void autoCalibrationError(AutoCalibration AutoCalibration,
			SensorEvent lastRead) {
		if (!this.HoldMessageOnScreen) {
			this.showHoldMessage();
		}
	}

	private void showHoldMessage() {
		this.HoldMessageOnScreen = true;

		// Add text

		this.holdText = CCBitmapFontAtlas.bitmapFontAtlas("HOLD IT STILL",
				"UniSansBold_AlphaNum_50.fnt");

		this.holdText.setPosition(CGPoint.ccp(
				screenWidth() / 2, screenHeight() / 4));
		this.holdText.setScale(this.holdText.getScale() * 1.1f);
		this.holdText.setColor(ccColor3B.ccc3(255, 255, 255));
		this.addChild(this.holdText);

		CCFadeIn a1 = CCFadeIn.action(1);

		this.holdText.runAction(a1);
	}

}
