package com.example.nave.game.interfaces;

import com.example.nave.game.objects.Shoot;

public interface ShootEngineDelegate {
	public void createShoot(
			Shoot shoot);

	public void removeShoot(Shoot shoot);
}
