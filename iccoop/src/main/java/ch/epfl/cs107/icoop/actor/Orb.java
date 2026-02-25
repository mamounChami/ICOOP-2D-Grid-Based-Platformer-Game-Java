package ch.epfl.cs107.icoop.actor;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.icoop.ICoop;
import ch.epfl.cs107.icoop.area.ICoopArea;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.engine.actor.RPGSprite;
import ch.epfl.cs107.play.engine.actor.Dialog;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.window.Canvas;
/**
 * Collectable de l'aire OrbWay octroyant une invulnérabilité élémentaire à un ICoopPlayer selon leur élément(ElementalType).
 */
public class Orb extends ElementalItem {
	private final static int ANIMATION_DURATION = 24;
	private final static int ANIMATION_FRAMES = 6;
	private Animation animation;
	/**
	 * Constructeur pour un Orb
	 * @param owner       (Area)                : Aire à laquelle l'orbe appartient.
	 * @param orientation (Orientation)         : Orientation de l'orbe.
	 * @param position    (DiscreteCoordinates) : Coordonnées sur la grille que l'orbe doit occuper.
	 * @param element        (OrbType)             : Type d'orbe auquel l'orbe appartient.
	 */
	public Orb(Area owner, Orientation orientation, DiscreteCoordinates position, ElementalType element) {
		super(owner, orientation, position, element);
		final Sprite[] sprites = new Sprite[ANIMATION_FRAMES];
		for (int i = 0; i < ANIMATION_FRAMES; i++) {
			sprites[i] = new RPGSprite("icoop/orb", 1, 1, this , new RegionOfInterest(i * 32, element == ElementalType.FIRE ? 64 : 0 , 32, 32));
		}
		animation = new Animation(ANIMATION_DURATION / ANIMATION_FRAMES , sprites);
	}
	// Orb implémente Actor.
	@Override
	public void update(float deltaTime) {
		animation.update(deltaTime);
		super.update(deltaTime);
	}
	@Override
	public void draw(Canvas canvas) {
		animation.draw(canvas);
	}
	// Orb implémente Interactable.
	/**
	 * Orb accepte les interactions.
	 */
	@Override
	public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
		((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
	}
	// Orb implémente Collectable.
	/**
	 * Orb ne se laisse collecter que si l'élément passé en argument est le même que le sien, puis il publie un Dialog spécifique à son élément.
	 */
	@Override
	public boolean collect(ElementalType otherElement) {
		if(super.collect(otherElement)) {
			((ICoopArea)getOwnerArea()).getDialogHandler().publish(new Dialog(element() == ElementalType.FIRE ? "orb_fire_msg" : "orb_water_msg"));
			return true;
		}
		return false;
	}
}
