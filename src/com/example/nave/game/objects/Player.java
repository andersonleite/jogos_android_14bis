package com.example.nave.game.objects;

import static com.example.nave.config.DeviceSettings.screenWidth;
import static com.example.nave.config.DeviceSettings.screenHeight;

import org.cocos2d.actions.interval.CCFadeOut;
import org.cocos2d.actions.interval.CCScaleBy;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.actions.interval.CCSpawn;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.sound.SoundEngine;
import org.cocos2d.types.CGPoint;

import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import com.example.nave.R;
import com.example.nave.config.Assets;
import com.example.nave.config.Runner;
import com.example.nave.game.calibrate.CalibratedAccelerometer;
import com.example.nave.game.calibrate.CalibratedAccelerometerDelegate;
import com.example.nave.game.interfaces.ShootEngineDelegate;

public class Player extends CCSprite implements CalibratedAccelerometerDelegate{

	private ShootEngineDelegate delegate;

	float positionX = screenWidth() / 2;
	float positionY = 100;

	private CalibratedAccelerometer accelerometer;

	private float currentAccelX;

	private float currentAccelY;

	private float currentAccelZ;

	private double lastAccelLsX;

	private double lastAccelLsY;
	
	private float kAccelerationFactor = 90.0f;
	private float kBounceFactor = 0.95f;

	private double vx;

	private double vy;

	private float x;

	private float y;

	public Player() {
		super(Assets.NAVE);
		setPosition(positionX, positionY);
		this.schedule("update");

	}

	public void shoot() {
		if (Runner.check().isGamePlaying() && !Runner.check().isGamePaused()) {

			delegate.createShoot(new Shoot(positionX, positionY));
		}
	}

	public void setDelegate(ShootEngineDelegate delegate) {
		this.delegate = delegate;
	}

	public void moveLeft() {
		if (Runner.check().isGamePlaying() && !Runner.check().isGamePaused()) {

			if (positionX > 30) {
				positionX -= 10;
			}
			setPosition(positionX, positionY);
		}
	}

	public void moveRight() {
		if (Runner.check().isGamePlaying() && !Runner.check().isGamePaused()) {

			if (positionX < screenWidth() - 30) {
				positionX += 10;
			}
			setPosition(positionX, positionY);
		}
	}

	public void explode() {

		SoundEngine.sharedEngine().playEffect(
				CCDirector.sharedDirector().getActivity(), R.raw.over);
		SoundEngine.sharedEngine().pauseSound();

		// Stop Shoot
		this.unschedule("update");

		// Pop Actions
		float dt = 0.2f;
		CCScaleBy a1 = CCScaleBy.action(dt, 2f);
		CCFadeOut a2 = CCFadeOut.action(dt);
		CCSpawn s1 = CCSpawn.actions(a1, a2);

		// Run actions!
		this.runAction(CCSequence.actions(s1));

	}

	public void catchAccelerometer() {
		CalibratedAccelerometer.sharedAccelerometer().catchAccelerometer();
		this.accelerometer = CalibratedAccelerometer.sharedAccelerometer();
		this.accelerometer.setDelegate(this);
	}

	@Override
	public void calibratedAccelerometerDidAccelerate(
			SensorEventListener accelerometer, SensorEvent acceleration) {
		if (Runner.check().isGamePlaying() && !Runner.check().isGamePaused() ) {

			// Read acceleration
			this.currentAccelX = acceleration.values[0];
			this.currentAccelY = acceleration.values[1];
			this.currentAccelZ = acceleration.values[2];
			
			// Resolve radians
			double radX = Math.atan2(this.currentAccelX, this.currentAccelZ);
			double radY = Math.atan2(this.currentAccelY, -1);

			// Compare current position against calibration
			double accelX = -CalibratedAccelerometer.sharedAccelerometer()
					.calibratedDifferenceFromXRadians(radX);
			double accelY = -CalibratedAccelerometer.sharedAccelerometer()
					.calibratedDifferenceFromYRadians(radY);

			// Translate coordinates to leftLandscape
			double accelLsX = accelY;
			double accelLsY = accelX;
			
			
	        this.lastAccelLsX = accelLsX;
	        this.lastAccelLsY = accelLsY;
			
		}
		
	}

	public void update(float dt) {
		if (Runner.check().isGamePlaying() && !Runner.check().isGamePaused()) {

			// Add accelerometer to x and y velocities
			this.vx += this.lastAccelLsX * kAccelerationFactor/2;
			this.vy += this.lastAccelLsY * kAccelerationFactor/2;
			
			// Desacceleration
	        this.vx *= 0.86015;
	        this.vy *= 0.86015;
	        
			
			// Get Ball Data
			float radius = (this.getContentSize()).width * this
					.getScale() / 2;
			this.x = this.getPosition().x;
			this.y = this.getPosition().y;

			// Set New Position
			this.x += this.vx * dt;
			this.y += this.vy * dt;

			// Verify Bounderies
			if (this.x < 0 + radius) {
				this.vx = this.vx * -1 * kBounceFactor;
				this.x = 0 + radius;
			}
			if (this.x > screenWidth() - radius) {
				this.vx = this.vx * -1 * kBounceFactor;
				this.x = screenWidth() - radius;
			}
			if (this.y < 0 + radius) {
				this.vy = this.vy * -1 * kBounceFactor;
				this.y = 0 + radius;
			}
			if (this.y > screenHeight() - radius) {
				this.vy = this.vy * -1 * kBounceFactor;
				this.y = screenHeight() - radius;
			}

			// Update Player Position
			this.setPosition(CGPoint.ccp(this.x, this.y));

		}
	}
	
}

