package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;
/**
 * Clé élémentaire qui peut être récupérée dans l'aire Arena et qui permet d'ouvrir la porte du manoir de l'aire Spawn.
 */
public class ElementalKey extends ElementalItem {
	private Sprite sprite;
	/**
	 * Constructeur pour une ElementalKey
	 * @param owner       (Area)                : Aire à laquelle la clé élémentaire appartient.
	 * @param orientation (Orientation)         : Orientation de la clé élémentaire.
	 * @param position    (DiscreteCoordinates) : Coordonnées sur la grille que la clé élémentaire doit occuper.
	 * @param spriteName  (String)              : Chaîne de caractères identifiant la ressource pour la construction du Sprite de la clé élémentaire.
	 * @param element     (ElementalType)       : Type élémentaire auquel la clé élémentaire appartient.
	 */
	public ElementalKey(Area owner, Orientation orientation, DiscreteCoordinates position, String spriteName, ElementalType element) {
		super(owner, orientation, position, element);
		sprite = new Sprite(spriteName, 0.6f, 0.6f, this);
	}
	@Override
	public void draw(Canvas canvas) {
		if(isOff())
			sprite.draw(canvas);
	}
	/**
	 * L'ElementalKey accepte les interactions.
	 */
	@Override
	public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
		((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
	}
}
