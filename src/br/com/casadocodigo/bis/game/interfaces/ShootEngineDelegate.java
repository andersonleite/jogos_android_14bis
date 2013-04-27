package br.com.casadocodigo.bis.game.interfaces;

import br.com.casadocodigo.bis.game.objects.Shoot;

public interface ShootEngineDelegate {
	public void createShoot(
			Shoot shoot);

	public void removeShoot(Shoot shoot);
}
