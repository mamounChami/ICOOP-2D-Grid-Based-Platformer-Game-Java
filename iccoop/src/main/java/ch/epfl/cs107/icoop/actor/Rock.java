package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
/**
 * Rocher qui peut être détruit par une bombe(Explosive) ou un projectile(Unstoppable).
 */
public class Rock extends Obstacle {
	/**
	 * Constructeur pour un Rock
	 * @param owner       (Area)                : Aire à laquelle le rocher appartient.
	 * @param orientation (Orientation)         : Orientation de le rocher.
	 * @param position    (DiscreteCoordinates) : Coordonnées sur la grille que le rocher doit occuper.
	 */
	public Rock(Area owner, Orientation orientation, DiscreteCoordinates position) {
		super(owner, orientation, position, "rock.1");
	}
	/**
	 * Rock accepte les interactions.
	 */
	@Override
	public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
		((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
	}
}
