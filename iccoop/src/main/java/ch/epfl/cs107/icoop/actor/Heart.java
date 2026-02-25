package ch.epfl.cs107.icoop.actor;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;
/**
 * Collectable octroyant un point de vie à un ICoopPlayer.
 */
public class Heart extends ICoopCollectable{
	private Animation animation;
	/**
	 * Constructeur pour un Heart
	 * @param owner       (Area)                : Aire à laquelle le coeur appartient.
	 * @param orientation (Orientation)         : Orientation de le coeur.
	 * @param position    (DiscreteCoordinates) : Coordonnées sur la grille que le coeur doit occuper.
	 */
	public Heart(Area owner, Orientation orientation, DiscreteCoordinates position) {
		super(owner, orientation, position);
		animation = new Animation("icoop/heart", 4, 1, 1, this, 16, 16, 6, true);
	}
	@Override
	public void update(float deltaTime) {
		animation.update(deltaTime);
		super.update(deltaTime);
	}
	@Override 
	public void draw(Canvas canvas) {
		animation.draw(canvas);
	}
	/**
	 * Heart accepte les interactions.
	 */
	@Override
	public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
		((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);		
	}
}
