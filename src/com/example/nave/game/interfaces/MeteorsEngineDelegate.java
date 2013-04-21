package com.example.nave.game.interfaces;

import com.example.nave.game.objects.Meteor;

public interface MeteorsEngineDelegate {
	public void createMeteor(
			Meteor meteor, float x, float y, float vel, double ang, int vl);

	public void removeMeteor(Meteor meteor);
}
