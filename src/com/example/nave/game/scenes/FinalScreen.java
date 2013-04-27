package com.example.nave.game.scenes;

import static com.example.nave.config.DeviceSettings.screenHeight;
import static com.example.nave.config.DeviceSettings.screenResolution;
import static com.example.nave.config.DeviceSettings.screenWidth;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.sound.SoundEngine;
import org.cocos2d.transitions.CCFadeTransition;
import org.cocos2d.types.CGPoint;

import com.example.nave.R;
import com.example.nave.config.Assets;
import com.example.nave.game.control.Button;
import com.example.nave.game.control.ButtonDelegate;
import com.example.nave.screens.ScreenBackground;

public class FinalScreen extends CCLayer implements ButtonDelegate{

	private ScreenBackground background;
	private Button beginButton;
	
	public CCScene scene() {
		CCScene scene = CCScene.node();
		scene.addChild(this);
		return scene;
	}
	
	public FinalScreen() {
		
		// background
		this.background = new ScreenBackground(Assets.BACKGROUND);
		this.background.setPosition(screenResolution(CGPoint.ccp(screenWidth() / 2.0f, screenHeight() / 2.0f)));
		this.addChild(this.background);
		
		// sound
		SoundEngine.sharedEngine().playSound(
				CCDirector.sharedDirector().getActivity(), R.raw.finalend, true);
		
		// image
		CCSprite title = CCSprite.sprite(Assets.FINALEND);
		title.setPosition(screenResolution(CGPoint.ccp( screenWidth() /2 , screenHeight() - 130 ))) ;
		this.addChild(title);
		
		// Enable Touch
		this.setIsTouchEnabled(true);
		this.beginButton = new Button(Assets.PLAY);
		this.beginButton.setPosition(screenResolution(CGPoint.ccp( screenWidth() /2 , screenHeight() - 300 ))) ;
		this.beginButton.setDelegate(this);
		addChild(this.beginButton);
	}

	@Override
	public void buttonClicked(Button sender) {
		if (sender.equals(this.beginButton)) {
			SoundEngine.sharedEngine().pauseSound();
			CCDirector.sharedDirector().replaceScene(new TitleScreen().scene());
		}		
	}	
	
}