package com.example.nave.game.control;

import org.cocos2d.events.CCTouchDispatcher;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;

import android.view.MotionEvent;

public class Button extends CCLayer {

	private CCSprite buttonImage;
	private ButtonDelegate delegate;

	public static Button buttonWithFile(String buttonImage) {
		return new Button(buttonImage);
	}

	public Button(String buttonImage) {

		// Enable Touch
		this.setIsTouchEnabled(true);

		// Set Button Image
		this.buttonImage = CCSprite.sprite(buttonImage);
		addChild(this.buttonImage);

	}

	public void setDelegate(ButtonDelegate sender) {
		this.delegate = sender;
	}

	@Override
	protected void registerWithTouchDispatcher() {
		CCTouchDispatcher.sharedDispatcher()
				.addTargetedDelegate(this, 0, false);
	}

	@Override
	public boolean ccTouchesBegan(MotionEvent event) {
		
		CGPoint touchLocation = CGPoint.make(event.getX(), event.getY());
		touchLocation = CCDirector.sharedDirector().convertToGL(touchLocation);
		touchLocation = this.convertToNodeSpace(touchLocation);

		// Check Button touched
		if (CGRect.containsPoint(
				this.getSpriteRectWithOffset(this.buttonImage), touchLocation)) {
			delegate.buttonClicked(this);
		}
		
		return true;
	}

	private CGRect getSpriteRectWithOffset(CCSprite sprite) {
		float w = sprite.getContentSize().width;
		float h = sprite.getContentSize().height;
		float x = sprite.getPosition().x - w / 2;
		float y = sprite.getPosition().y - h / 2;
		return CGRect.make(x, y, w, h);
	}

}
