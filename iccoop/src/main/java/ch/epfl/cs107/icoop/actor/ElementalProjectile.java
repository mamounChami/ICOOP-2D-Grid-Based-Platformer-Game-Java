package ch.epfl.cs107.icoop.actor;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;
/**
 * Projectile élémentaire lancé par un joueur(ICoopPlayer) muni d'un bâton élémentaire(ElementalStaff).
 */
public class ElementalProjectile extends Unstoppable implements ElementalEntity {
	private static final int ANIMATION_DURATION = 12;
	private Animation animation;
	private ElementalType element;
	/**
	 * Constructeur pour un ElementalProjectile
	 * @param owner         (Area)                : Aire à laquelle le projectile élémentaire appartient.
	 * @param orientation   (Orientation)         : Orientation du projectile élémentaire.
	 * @param position      (DiscreteCoordinates) : Coordonnées sur la grille que le projectile élémentaire doit occuper.
	 * @param animationName (String)              : Chaîne de caractères identifiant la ressource pour la construction de l'Animation du projectile élémentaire.
	 * @param element       (ElementalType)       : Type élémentaire auquel le projectile élémentaire appartient.
	 * @param distance      (int)                 : Distance maximale que le projectile élémentaire peut parcourir, en nombre de cellules.
	 * @param speed         (int)                 : Vitesse à laquelle le projectile élémentaire doit se déplacer.
	 */
	public ElementalProjectile(Area owner, Orientation orientation, DiscreteCoordinates position, String animationName, ElementalType element, int distance, int speed) {
		super(owner, orientation, position, distance, speed);
		animation = new Animation(animationName, 4, 1, 1, this, 32, 32, ANIMATION_DURATION/4, true);
		this.element = element;
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
	private ICoopInteractionVisitor handler = new ICoopInteractionVisitor() {
		/**
		 * Tentative d'attaque élémentaire lors d'une interaction de contact.
		 */
		@Override
		public void interactWith(ICoopPlayer other, boolean isCellInteraction) {
			other.hit(ElementalProjectile.this.elementToVulnerability(), 1);
		}
		/**
		 * Tentative d'attaque élémentaire lors d'une interaction de contact.
		 */
		@Override
		public void interactWith(Foe other, boolean isCellInteraction) {
			other.hit(ElementalProjectile.this.elementToVulnerability(), 1);
		}
		/**
		 * Tentative d'attaque élémentaire lors d'une interaction de contact.
		 */
		@Override
		public void interactWith(HellSkull other, boolean isCellInteraction) {
			other.hit(ElementalProjectile.this.elementToVulnerability(), 1);
		}
		/**
		 * Destruction du rocher lors d'une interaction de contact.
		 */
		@Override
		public void interactWith(Rock other, boolean isCellInteraction) {
			getOwnerArea().unregisterActor(other);
		}
		/**
		 * L'ElementalProjectile est arrêté dans sa course lors d'une interaction de contact.
		 */
		@Override
		public void interactWith(Obstacle other, boolean isCellInteraction) {
			getOwnerArea().unregisterActor(ElementalProjectile.this);
		}
		/**
		 * Faire exploser la bombe lors d'une interaction de contact.
		 */
		@Override
		public void interactWith(Explosive other, boolean isCellInteraction) {
			other.explode();
			getOwnerArea().unregisterActor(ElementalProjectile.this);
		}
	};
	/**
	 * Redirige la gestion des interactions de l'ElementalProjectile à son handler de type ICoopInteractionVisitor.
	 */
	@Override
	public void interactWith(Interactable other, boolean isCellInteraction) {
		other.acceptInteraction(handler, isCellInteraction);
	}
	/**
	 * @return (List<DiscreteCoordinates>) : Liste immuable des coordonnées de la seule cellule que l'ElementalProjectile occupe.
	 */
	@Override
	public List<DiscreteCoordinates> getCurrentCells() {
		return Collections.singletonList(getCurrentMainCellCoordinates());
	}
	/**
	* Le champ de vision de l'ElementalProjectile ne comprend aucune cellule.
	* @return (List<DiscreteCoordinates>) : Liste immuable vide.
	*/
	@Override
	public List<DiscreteCoordinates> getFieldOfViewCells() {
		return Collections.<DiscreteCoordinates> emptyList();
	}
	@Override
	public boolean wantsViewInteraction() {
		return false;
	}
	@Override
	public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
		
	}
	@Override
	public ElementalType element() {
		return element;
	}
}
