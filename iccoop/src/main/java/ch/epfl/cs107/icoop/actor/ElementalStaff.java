package ch.epfl.cs107.icoop.actor;

import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.engine.actor.RPGSprite;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;
/**
 * Bâton élémentaire pouvant être collecté et permettant au joueur(ICoopPlayer) de lancer des projectiles élémentaires(ElementalProjectile).
 */
public class ElementalStaff extends ElementalItem {
	private final static int ANIMATION_DURATION = 32;
	private final static int ANIMATION_FRAMES = 8;
	private Animation animation;
	/**
	 * Constructeur pour un ElementalStaff
	 * @param owner         (Area)                : Aire à laquelle le bâton élémentaire appartient.
	 * @param orientation   (Orientation)         : Orientation du bâton élémentaire.
	 * @param position      (DiscreteCoordinates) : Coordonnées sur la grille que le bâton élémentaire doit occuper.
	 * @param animationName (String)              : Chaîne de caractères identifiant la ressource pour la construction de l'Animation du bâton élémentaire.
	 * @param element       (ElementalType)       : Type élémentaire auquel le bâton élémentaire appartient.
	 */
	public ElementalStaff(Area owner, Orientation orientation, DiscreteCoordinates position, String animationName, ElementalType element) {
		super(owner, orientation, position, element);
		final Sprite[] sprites = new Sprite[ANIMATION_FRAMES];
		for (int i = 0; i < ANIMATION_FRAMES; i++) {
			sprites[i] = new RPGSprite(animationName, 2, 2, this , new RegionOfInterest(i * 32, 0 , 32, 32), new Vector(-0.5f, 0));
		}
		animation = new Animation(ANIMATION_DURATION / ANIMATION_FRAMES , sprites);
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
	 * L'ElementalStaff accepte les interactions.
	 */
	@Override
	public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
		((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
	}
}
