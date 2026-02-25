package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.engine.actor.RPGSprite;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;
/**
 * Pièce à collecter pour acheter des bombes(Explosive) chez le marchand(Merchant).
 */
public class Coin extends ICoopCollectable {
	private static final int ANIMATION_DURATION = 12; 
	private Animation animation;
	/**
	 * Constructeur pour Coin
	 * @param owner       (Area)                : Aire à laquelle la pièce appartient.
	 * @param orientation (Orientation)         : Orientation de la pièce.
	 * @param position    (DiscreteCoordinates) : Coordonnées sur la grille que la pièce doit occuper.
	 */
	public Coin(Area owner, Orientation orientation, DiscreteCoordinates position) {
		super(owner, orientation, position);
		Sprite[] sprites = RPGSprite.extractSprites("icoop/coin", 4, 1, 1, this, 16, 16);
		animation = new Animation(ANIMATION_DURATION/3, sprites, true);
	}
	@Override
	public void update(float deltaTime) {
		animation.update(deltaTime);
		super.update(deltaTime);
	}
	@Override
	public void draw(Canvas canvas) {
		animation.draw(canvas);
		super.draw(canvas);
	}
	/**
	 * Coin accepte les interactions.
	 */
	@Override
	public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
		((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);		
	}
}
