package com.example.nave.game.control;

import static com.example.nave.config.DeviceSettings.screenHeight;
import static com.example.nave.config.DeviceSettings.screenResolution;
import static com.example.nave.config.DeviceSettings.screenWidth;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.types.CGPoint;

import com.example.nave.config.Assets;

public class MenuButtons extends CCLayer implements ButtonDelegate {

	private Button playButton;
	private Button highscoredButton;
	private Button helpButton;
	private Button soundButton;
	
	public static MenuButtons menuButtons() {
		return new MenuButtons();
	}

	public MenuButtons() {

		// Enable Touch
		this.setIsTouchEnabled(true);
		
		// Create Buttons
		this.playButton 	  = Button.buttonWithFile(Assets.play);
		this.highscoredButton = Button.buttonWithFile(Assets.highscore);
		this.helpButton 	  = Button.buttonWithFile(Assets.help);
		this.soundButton 	  = Button.buttonWithFile(Assets.sound);
		
		// Set Buttons Delegates
		this.playButton.setDelegate(this);
		this.highscoredButton.setDelegate(this);
		this.helpButton.setDelegate(this);
		this.soundButton.setDelegate(this);
		
		// set position
		setButtonspPosition();

		// Add Buttons to Screen
		addChild(playButton);
		addChild(highscoredButton);
		addChild(helpButton);
		addChild(soundButton);
		
	}
	
	private void setButtonspPosition() {

		// Buttons Positions
		playButton.setPosition(screenResolution(CGPoint.ccp( screenWidth() /2 , screenHeight() - 250 ))) ;
		highscoredButton.setPosition(screenResolution(CGPoint.ccp( screenWidth() /2 , screenHeight() - 300 ))) ;
		helpButton.setPosition(screenResolution(CGPoint.ccp( screenWidth() /2 , screenHeight() - 350 ))) ;
		soundButton.setPosition(screenResolution(CGPoint.ccp( screenWidth() /2 - 100 , screenHeight() - 420 ))) ;
	}

	@Override
	public void buttonClicked(Button sender) {
		
		if (sender.equals(this.playButton)) {
			System.out.println("Button clicked: Play");
		}
		
		if (sender.equals(this.highscoredButton)) {
			System.out.println("Button clicked: Highscore");
		}

		if (sender.equals(this.helpButton)) {
			System.out.println("Button clicked: Help");
		}

		if (sender.equals(this.soundButton)) {
			System.out.println("Button clicked: Sound");
		}

	}

}
