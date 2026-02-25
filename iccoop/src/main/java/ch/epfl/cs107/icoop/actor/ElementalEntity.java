package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.actor.Vulnerable.Vulnerability;
/**
 * Interface désignant un type élémentaire pour les entités qui l'implémente.
 */
public interface ElementalEntity {
	/**
	 * Énumérateur de type d'élément.
	 */
	public enum ElementalType {
		FIRE,
		WATER,
		WIND
		;
	}
	/**
	 * 
	 * @return L'élément (ElementalType) d'une entité élémentaire.
	 */
	public ElementalType element();
	/**
	 * 
	 * @return La vulnérabilité(Vulnerability) à partir de l'élément(ElementalType) d'une entitée élémentaire(ElementalEntity).
	 */
	default public Vulnerability elementToVulnerability() {
		ElementalType e = element();
		switch(e) {
			case ElementalType.FIRE: return Vulnerability.FIRE;
			case ElementalType.WATER: return Vulnerability.WATER;
			case ElementalType.WIND: return Vulnerability.WIND;
			default: return Vulnerability.PHYSICAL;
		}
	}
}
