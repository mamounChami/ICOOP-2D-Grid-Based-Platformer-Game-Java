package ch.epfl.cs107.icoop.actor;
/**
 * Interface désignant les entités susceptibles de recevoir des dommages.
 */
public interface Vulnerable {
	/**
	 * Énumérateur des vulnérabilités possibles pour les entités qui implémentent Vulnerable.
	 */
	public enum Vulnerability {
		PHYSICAL,
		FIRE,
		WATER,
		WIND
	}
	/**
	 * Tentative d'attaque sur une entité.
	 * @param v (Vulnerability): Vulnérabilité que l'on cherche à exploiter.
	 * @param damage (int): Nombre de points de dommages à faire subir.
	 * @return true si les dommages sont subis, false sinon.
	 */
	public boolean hit(Vulnerability v, int damage);
}
