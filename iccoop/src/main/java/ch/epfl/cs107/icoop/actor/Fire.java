package ch.epfl.cs107.icoop.actor;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.icoop.actor.Vulnerable.Vulnerability;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;
/**
 * Projectile lancé par les crânes de feu(HellSkull).
 */
public class Fire extends Unstoppable implements ElementalEntity {
	private Animation animation;
	/**
	 * Constructeur pour un Fire
	 * @param owner       (Area)                : Aire à laquelle la flamme appartient.
	 * @param orientation (Orientation)         : Orientation de la flamme.
	 * @param position    (DiscreteCoordinates) : Coordonnées sur la grille que la flamme doit occuper.
	 * @param distance    (int)                 : Distance maximale que la flamme peut parcourir, en nombre de cellules.
	 * @param speed       (int)                 : Vitesse à laquelle la flamme doit se déplacer.
	 */
	public Fire(Area owner, Orientation orientation, DiscreteCoordinates position, int distance, int speed) {
		super(owner, orientation, position, distance, speed);
		animation = new Animation("icoop/fire", 7, 1, 1, this, 16, 16, 4, true);
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
	// Fire implémente Interactor.
	private ICoopInteractionVisitor handler = new ICoopInteractionVisitor() {
		/**
		 * Tentative d'attaque de feu(Vulnerability.FIRE) lors d'une interaction de contact.
		 */
		@Override
		public void interactWith(ICoopPlayer other, boolean isCellInteraction) {
			other.hit(Vulnerability.FIRE, 1);
		}
		/**
		 * Tentative d'attaque de feu(Vulnerability.FIRE) lors d'une interaction de contact.
		 */
		@Override
		public void interactWith(Foe other, boolean isCellInteraction) {
			other.hit(Vulnerability.FIRE, 1);
		}
		/**
		 * Faire exploser la bombe lors d'une interaction de contact.
		 */
		@Override
		public void interactWith(Explosive other, boolean isCellInteraction) {
			other.explode();
		}
	};
	/**
	 * Redirige la gestion des interactions du Fire vers son handler de type ICoopInteractionVisitor.
	 */
	@Override
	public void interactWith(Interactable other, boolean isCellInteraction) {
		other.acceptInteraction(handler, isCellInteraction);
	}
	/**
	 * @return (List<DiscreteCoordinates) : Liste immuable des coordonnées de la seule cellule que Fire occupe.
	 */
	@Override
	public List<DiscreteCoordinates> getCurrentCells() {
		return Collections.singletonList(getCurrentMainCellCoordinates());
	}
	/**
	 * Le champ de vision de Fire ne comprend aucune cellule.
	 * @return (List<DiscreteCoordinate>) : Liste immuable vide.
	 */
	@Override
	public List<DiscreteCoordinates> getFieldOfViewCells() {
		return Collections.<DiscreteCoordinates> emptyList();
	}
	/**
	 * Fire est-il demandeur d'interactions à distance ?
	 * @return (boolean) : false inconditionnellement.
	 */
	@Override
	public boolean wantsViewInteraction() {
		return false;
	}
	/**
	 * Fire n'accepte pas les interactions.
	 */
	@Override
	public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {}
	@Override
	public ElementalType element() {
		return ElementalType.FIRE;
	}
}
