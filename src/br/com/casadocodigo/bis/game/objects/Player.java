package br.com.casadocodigo.bis.game.objects;

import static br.com.casadocodigo.bis.config.DeviceSettings.screenWidth;

import org.cocos2d.actions.interval.CCFadeOut;
import org.cocos2d.actions.interval.CCScaleBy;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.actions.interval.CCSpawn;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.sound.SoundEngine;
import org.cocos2d.types.CGPoint;

import br.com.casadocodigo.bis.R;
import br.com.casadocodigo.bis.config.Assets;
import br.com.casadocodigo.bis.config.Runner;
import br.com.casadocodigo.bis.game.calibrate.Accelerometer;
import br.com.casadocodigo.bis.game.calibrate.AccelerometerDelegate;
import br.com.casadocodigo.bis.game.interfaces.ShootEngineDelegate;

public class Player extends CCSprite implements AccelerometerDelegate{

	private static final double NOISE = 1;

	private ShootEngineDelegate delegate;

	float positionX = screenWidth() / 2;
	float positionY = 100;

	private Accelerometer accelerometer;

	private float currentAccelX;

	private float currentAccelY;
	
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
		Accelerometer.sharedAccelerometer().catchAccelerometer();
		this.accelerometer = Accelerometer.sharedAccelerometer();
		this.accelerometer.setDelegate(this);
	}

	@Override
	public void accelerometerDidAccelerate(float x, float y) {
		if (Runner.check().isGamePlaying() && !Runner.check().isGamePaused() ) {

			System.out.println("X: " + x);
			System.out.println("Y: " + y);
			
			// Read acceleration
			this.currentAccelX = x;
			this.currentAccelY = y;
						
		}
		
	}

	public void update(float dt) {
		if (Runner.check().isGamePlaying() && !Runner.check().isGamePaused()) {

			//fazer primeiro com tudo zero depois colocar essa constant
			if(this.currentAccelX< -NOISE){
				this.positionX++;
			}
			
			if(this.currentAccelX> NOISE){
				this.positionX--;
			}
			
			if(this.currentAccelY< -NOISE){
				this.positionY++;
			}
			
			if(this.currentAccelY> NOISE){
				this.positionY--;
			}
			
			// Update Player Position
			this.setPosition(CGPoint.ccp(this.positionX, this.positionY));

		}
	}
	
}