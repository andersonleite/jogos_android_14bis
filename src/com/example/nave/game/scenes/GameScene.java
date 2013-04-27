package com.example.nave.game.scenes;

import static com.example.nave.config.DeviceSettings.screenHeight;
import static com.example.nave.config.DeviceSettings.screenResolution;
import static com.example.nave.config.DeviceSettings.screenWidth;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.sound.SoundEngine;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;

import com.example.nave.R;
import com.example.nave.config.Assets;
import com.example.nave.config.Runner;
import com.example.nave.game.calibrate.CalibrateScene;
import com.example.nave.game.control.GameButtons;
import com.example.nave.game.engines.MeteorsEngine;
import com.example.nave.game.interfaces.MeteorsEngineDelegate;
import com.example.nave.game.interfaces.PauseDelegate;
import com.example.nave.game.interfaces.ShootEngineDelegate;
import com.example.nave.game.objects.Meteor;
import com.example.nave.game.objects.Player;
import com.example.nave.game.objects.Score;
import com.example.nave.game.objects.Shoot;
import com.example.nave.screens.PauseScreen;
import com.example.nave.screens.ScreenBackground;

public class GameScene 	
					extends CCLayer 
					implements 	MeteorsEngineDelegate,
								ShootEngineDelegate, 
								PauseDelegate {
	
	// Layers
	private CCLayer meteorsLayer;
	private CCLayer scoreLayer;
	private CCLayer playerLayer;
	private CCLayer shootsLayer;
	private CCLayer layerTop;

	// Engines
	private MeteorsEngine meteorsEngine;

	// Arrays
	@SuppressWarnings("rawtypes") private ArrayList meteorsArray;
	@SuppressWarnings("rawtypes") private ArrayList playersArray;
	@SuppressWarnings("rawtypes") private ArrayList shootsArray;

	// Screens
	private PauseScreen pauseScreen;
	
	// Game Objects
	private Player player;
	private Score score;
	private boolean autoCalibration;
	private ScreenBackground background;
	
	public static CCScene createGame() {
		
		// Create Scene
		GameScene layer = new GameScene();
		CCScene scene = CCScene.node();
		scene.addChild(layer);
		
		// Start AutoCalibration
		CalibrateScene calibrate = new CalibrateScene();
		layer.layerTop.addChild(calibrate);
		calibrate.calibrateAndPerformSelector("startGame", layer);
		layer.autoCalibration = true;
		
		return scene;
	}

	private GameScene() {

		// Background	
		this.background = new ScreenBackground(Assets.BACKGROUND);
		this.background.setPosition(screenResolution(CGPoint.ccp(
				screenWidth() / 2.0f, screenHeight() / 2.0f)));
		this.addChild(this.background);

		GameButtons gameButtonsLayer = GameButtons.gameButtons();
		gameButtonsLayer.setDelegate(this);
		this.addChild(gameButtonsLayer);
		
		// Create Layers
		this.meteorsLayer = CCLayer.node();
		this.playerLayer = CCLayer.node();
		this.scoreLayer = CCLayer.node();

		this.addGameObjects();

		this.shootsLayer = CCLayer.node();
		this.layerTop = CCLayer.node();
		
		// Add Layers
		this.addChild(this.meteorsLayer);
		this.addChild(this.playerLayer);
		this.addChild(this.scoreLayer);
		this.addChild(this.shootsLayer);
		this.addChild(this.layerTop);
		
		this.setIsTouchEnabled(true);

		// sons
		SoundEngine.sharedEngine().playSound(
				CCDirector.sharedDirector().getActivity(), R.raw.music, true);

		preloadCache();
	}

	private void preloadCache() {
		SoundEngine.sharedEngine().preloadEffect(
				CCDirector.sharedDirector().getActivity(), R.raw.shoot);

		SoundEngine.sharedEngine().preloadEffect(
				CCDirector.sharedDirector().getActivity(), R.raw.bang);

		SoundEngine.sharedEngine().preloadEffect(
				CCDirector.sharedDirector().getActivity(), R.raw.over);
	}

	private void addGameObjects() {
		this.meteorsArray = new ArrayList();
		this.meteorsEngine = new MeteorsEngine();

		this.player = new Player();		
		this.playerLayer.addChild(this.player);

		// score
		this.score = new Score();
		this.score.setDelegate(this);
		this.scoreLayer.addChild(this.score);

		// startgame
		this.playersArray = new ArrayList();
		this.playersArray.add(this.player);

		this.shootsArray = new ArrayList();
		this.player.setDelegate(this);
	}

	public void startGame() {
		
		// Set Game Status
		// PAUSE
		Runner.check().setGamePlaying(true);
		Runner.check().setGamePaused(false);
		
		// Catch Accelerometer
		player.catchAccelerometer();
		
		// pause
		SoundEngine.sharedEngine().setEffectsVolume(1f);
		SoundEngine.sharedEngine().setSoundVolume(1f);

		// startgame
		this.schedule("checkHits");

		this.startEngines();
	}
	
	@Override
	public void onEnter() {
		super.onEnter();

		// Start Game when transition did finish
		if (!this.autoCalibration) {
			this.startGame();
		}		
	}

	// startgame
	public void checkHits(float dt) {
		boolean hitTest = false;

		hitTest = this.checkRadiusHitsOfArray(this.meteorsArray,
				this.shootsArray, false, this, "meteoroHit");

		hitTest = this.checkRadiusHitsOfArray(this.meteorsArray,
				this.playersArray, false, this, "playerHit");

	}

	private boolean checkRadiusHitsOfArray(ArrayList array1, ArrayList array2,
			boolean b, GameScene gameScene, String hit) {

		boolean result = false;
		int len1 = array1.size();
		int len2 = array2.size();
		boolean breakFor = false;

		if (len1 > 0 && len2 > 0) {
			for (int i = 0; i < len1; i++) {
				// Get Object from First Array
				CGRect rect1 = getBoarders((CCSprite) array1.get(i));

				for (int j = 0; j < len2; j++) {
					// Get Object from Second Array
					CGRect rect2 = getBoarders((CCSprite) array2.get(j));

					// Check Hit!
					if (CGRect.intersects(rect1, rect2)) {
						System.out.println("Colision Detected: " + hit);
						result = true;

						Method method;
						try {
							method = GameScene.class.getMethod(hit,
									CCSprite.class, CCSprite.class);

							method.invoke(gameScene, array1.get(i),
									array2.get(j));

						} catch (SecurityException e1) {
							e1.printStackTrace();
						} catch (NoSuchMethodException e1) {
							e1.printStackTrace();
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		return result;
	}

	public CGRect getBoarders(CCSprite object) {
		CGRect rect = object.getBoundingBox();
		CGPoint GLpoint = rect.origin;
		CGRect GLrect = CGRect.make(GLpoint.x, GLpoint.y, rect.size.width,
				rect.size.height);

		return GLrect;
	}

	private void startEngines() {
		this.addChild(this.meteorsEngine);
		this.meteorsEngine.setDelegate(this);
	}

	@Override
	public void createMeteor(Meteor meteor) {

		this.meteorsLayer.addChild(meteor);
		meteor.setDelegate(this);
		meteor.start();
		this.meteorsArray.add(meteor);

	}

	public boolean shoot() {
		player.shoot();
		return true;
	}

	@Override
	public void createShoot(Shoot shoot) {

		this.shootsLayer.addChild(shoot);
		shoot.setDelegate(this);
		shoot.start();
		this.shootsArray.add(shoot);

	}

	public void moveLeft() {
		player.moveLeft();
	}

	public void moveRight() {
		player.moveRight();
	}

	public void meteoroHit(CCSprite meteor, CCSprite shoot) {
		((Meteor) meteor).shooted();
		((Shoot) shoot).explode();
		this.score.increase();
	}

	@Override
	public void removeMeteor(Meteor meteor) {
		this.meteorsArray.remove(meteor);

	}

	@Override
	public void removeShoot(Shoot shoot) {
		this.shootsArray.remove(shoot);

	}

	public void playerHit(CCSprite meteor, CCSprite player) {
		((Meteor) meteor).shooted();
		((Player) player).explode();
		CCDirector.sharedDirector().replaceScene(new GameOverScreen().scene());
	}

	// PAUSE
	// ===========
	public void pauseGameAndShowLayer() {

		if (Runner.check().isGamePlaying() && !Runner.check().isGamePaused()) {
			this.pauseGame();
		}

		if (Runner.check().isGamePaused() && Runner.check().isGamePlaying()
				&& this.pauseScreen == null) {

			this.pauseScreen = new PauseScreen();
			this.layerTop.addChild(this.pauseScreen);
			this.pauseScreen.setDelegate(this);
		}

	}

	private void pauseGame() {
		if (!Runner.check().isGamePaused() && Runner.check().isGamePlaying()) {
			Runner.setGamePaused(true);
		}
	}

	@Override
	public void resumeGame() {
		if (Runner.check().isGamePaused() || !Runner.check().isGamePlaying()) {

			// Resume game
			this.pauseScreen = null;
			Runner.setGamePaused(false);
			this.setIsTouchEnabled(true);
		}
	}

	@Override
	public void quitGame() {
		SoundEngine.sharedEngine().setEffectsVolume(0f);
		SoundEngine.sharedEngine().setSoundVolume(0f);
		
		CCDirector.sharedDirector().replaceScene(new TitleScreen().scene());

	}

	public void startFinalScreen() {
		CCDirector.sharedDirector().replaceScene(new FinalScreen().scene());
	}


}


