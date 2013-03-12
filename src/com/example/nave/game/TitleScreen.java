package com.example.nave.game;

import static com.example.nave.config.DeviceSettings.screenHeight;
import static com.example.nave.config.DeviceSettings.screenResolution;
import static com.example.nave.config.DeviceSettings.screenWidth;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;

import com.example.nave.config.Assets;
import com.example.nave.game.control.MenuButtons;
import com.example.nave.screens.ScreenBackground;

public class TitleScreen extends CCLayer {

	private ScreenBackground background;
	
	public CCScene scene() {
		CCScene scene = CCScene.node();
		scene.addChild(this);
		return scene;
	}

	public TitleScreen() {
	
		// background
		this.background = new ScreenBackground(Assets.background);
		this.background.setPosition(screenResolution(CGPoint.ccp(screenWidth() / 2.0f, screenHeight() / 2.0f)));
		this.addChild(this.background);
		
		// logo
		CCSprite title = CCSprite.sprite(Assets.logo);
		title.setPosition(screenResolution(CGPoint.ccp( screenWidth() /2 , screenHeight() - 130 ))) ;
		this.addChild(title);
		
		// Add options layer
		MenuButtons menuLayer = MenuButtons.menuButtons();
		this.addChild(menuLayer);
		
	}
}
