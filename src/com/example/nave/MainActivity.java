package com.example.nave;

import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.opengl.CCGLSurfaceView;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.example.nave.game.TitleScreen;

public class MainActivity extends Activity {

	protected CCGLSurfaceView glSurfaceView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// landscape orientation
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// view
		glSurfaceView = new CCGLSurfaceView(this);
		setContentView(glSurfaceView);
		CCDirector.sharedDirector().attachInView(glSurfaceView);
		
		// configure CCDirector 
		CCDirector.sharedDirector().setScreenSize(320, 480);
		
		// Starts title screen
		CCScene scene = new TitleScreen().scene();
		CCDirector.sharedDirector().runWithScene(scene);
	}

}
