package com.example.nave.game.engines;

import java.util.Random;

import org.cocos2d.layers.CCLayer;

import com.example.nave.config.Assets;
import com.example.nave.config.Runner;
import com.example.nave.game.interfaces.MeteorsEngineDelegate;
import com.example.nave.game.objects.Meteor;

public class MeteorsEngine extends CCLayer {

	private MeteorsEngineDelegate delegate;

	public MeteorsEngine() {
		this.schedule("meteorsEngine", 1.0f / 10f);
	}

	public void meteorsEngine(float dt) {

		// pause
		if (Runner.check().isGamePlaying() && !Runner.check().isGamePaused()) {

			if (new Random().nextInt(30) == 0) {
				this.getDelegate().createMeteor(
						new Meteor(Assets.meteor).generate(), 1, 1, 1, 1, 1);
			}

		}
	}

	public void setDelegate(MeteorsEngineDelegate delegate) {
		this.delegate = delegate;
	}

	public MeteorsEngineDelegate getDelegate() {
		return delegate;
	}

}
