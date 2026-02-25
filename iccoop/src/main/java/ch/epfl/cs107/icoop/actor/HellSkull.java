package ch.epfl.cs107.icoop.actor;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.icoop.actor.Vulnerable.Vulnerability;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.math.random.RandomGenerator;
import ch.epfl.cs107.play.window.Canvas;
/**
 * Les crânes de feu font subir des dommages de feu à qui les touche.
 * Ils lancent aléatoirement des boules de feu(Fire) devant eux.
 */
public class HellSkull extends Foe {
	private static final int MAX_HEALTH = 1;
	private static final int ANIMATION_DURATION = 12;
	private OrientedAnimation animation;
	private float cooldownTime = 0.0f; // Temps restant avant le prochain lancer de flamme(Fire).
	/**
	 * Constructeur pour un HellSkull
	 * @param owner           (Area)                : Aire à laquelle le crâne de feu appartient.
	 * @param orientation     (Orientation)         : Orientation de le crâne de feu.
	 * @param position        (DiscreteCoordinates) : Coordonnées sur la grille que le crâne de feu doit occuper.
	 * @param maxHealth       (int)                 : Points de vie maximaux que le crâne de feu doit avoir.
	 * @param vulnerabilities (Vulnerability[])     : Tableau des vulnérabilités auxquelles le crâne de feu est vulnérable.
	 */
	public HellSkull(Area owner, Orientation orientation, DiscreteCoordinates position, int maxHealth, Vulnerability ...vulnerabilities) {
		super(owner, orientation, position, maxHealth, vulnerabilities);
		Orientation[] orders = new Orientation[]{ Orientation.UP, Orientation.LEFT, Orientation.DOWN, Orientation.RIGHT};
		animation = new OrientedAnimation("icoop/flameskull", ANIMATION_DURATION/3, this, new Vector(-0.5f, -0.5f), orders, 3, 2, 2, 32, 32, true);
	}
	public HellSkull(Area owner, Orientation orientation, DiscreteCoordinates position) {
		this(owner, orientation, position, MAX_HEALTH, Vulnerability.PHYSICAL, Vulnerability.WATER);
	}
	@Override
	public void update(float deltaTime) {
		if(isOn()) { // Le HellSkull doit être en vie pour se mettre à jour.
			cooldownTime -= deltaTime;
			if(cooldownTime <= 0.0f) { // Une fois le temps de refroidissement écoulé.
				Area area = getOwnerArea();
				int maxDimension = Math.max(area.getWidth(), area.getWidth()); // On prend la dimension la plus importante de l'aire pour être sûr de couvrir la distance maximale.
				area.registerActor(new Fire(getOwnerArea(), getOrientation(), getCurrentMainCellCoordinates().jump(getOrientation().toVector()), maxDimension, 1)); // On lance un Fire devant soit.
				cooldownTime = RandomGenerator.getInstance().nextFloat(0.5f, 2.f); // On génère aléatoirement un nouveau temps de refroidissement.
			}
			animation.update(deltaTime);
		}
		super.update(deltaTime);
	}
	@Override
	public void draw(Canvas canvas) {
		if((getImmunity() % 2) == 0 && isOn()) // On dessine le HellSkull que s'il est en vie est que son temps d'immunité est pair.
			animation.draw(canvas);
		super.draw(canvas);
	}
	// Le HellSkull implémente Interactable.
	/**
	 * Le HellSkull accepte les interactions.
	 */
	@Override
	public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteractable) {
		((ICoopInteractionVisitor) v).interactWith(this, isCellInteractable);
	}
	// Le HellSkull implémente Interactor.
	/**
	 * Le HellSkull prend-il place dans une cellule ?
	 * @return (boolean) : false inconditionnellement.
	 */
	@Override
	public boolean takeCellSpace() {
		return false;
	}
	/**
	 * Le HellSkull est-il demandeur d'interactions de contact ?
	 * @return (boolean) : true si le HellSkull est vivant, false sinon.
	 */
	@Override
	public boolean wantsCellInteraction() {
		return isOn();
	}
	
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
	};
	/**
	 * Redirige la gestion des interactions du HellSkull vers son handler de type ICoopInteractionVisitor.
	 */
	@Override
	public void interactWith(Interactable other, boolean isCellInteraction) {
		other.acceptInteraction(handler, isCellInteraction);
	}
}
