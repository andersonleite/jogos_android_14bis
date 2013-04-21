package com.example.nave.config;

public class Runner {
	private static boolean isGamePlaying;
	private static boolean isGamePaused;
	
	static Runner runner = null;
	
	private Runner(){
		
	}
	
	public  static Runner check(){
		if (runner!=null){
			runner = new Runner();
		}
		return runner;
	}
	
	public static boolean isGamePlaying() {
		return isGamePlaying;
	}

	public static boolean isGamePaused() {
		return isGamePaused;
	}

	public static void setGamePlaying(boolean isGamePlaying) {
		Runner.isGamePlaying = isGamePlaying;
	}

	public static void setGamePaused(boolean isGamePaused) {
		Runner.isGamePaused = isGamePaused;
	}
}
