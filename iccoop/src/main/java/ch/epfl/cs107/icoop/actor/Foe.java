package ch.epfl.cs107.icoop.actor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.icoop.actor.Vulnerable.Vulnerability;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Transform;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;
/**
 * Classe abstraite représentant un ennemi dans le jeu ICoop.
 */
public abstract class Foe extends MovableAreaEntity implements Interactor, Logic, Vulnerable {
	private static final int ANIMATION_DURATION = 24;
	private Health hp;
	private Animation deathAnimation;
	private int immunityTime = 0;
	private List<Vulnerability> vulnerabilities = new ArrayList<Vulnerability>();
	/**
	 * Constructeur pour un Foe
	 * @param owner           (Area)                : Aire à laquelle l'ennemi appartient.
	 * @param orientation     (Orientation)         : Orientation de l'ennemi.
	 * @param position        (DiscreteCoordinates) : Coordonnées sur la grille que l'ennemi doit occuper.
	 * @param maxHealth       (int)                 : Points de vie maximaux que l'ennemi doit avoir.
	 * @param vulnerabilities (Vulnerability[])     : Tableau des vulnérabilités auxquelles l'ennemi est vulnérable.
	 */
	public Foe(Area owner, Orientation orientation, DiscreteCoordinates position, int maxHealth, Vulnerability ...vulnerabilities) {
		super(owner, orientation, position);
		this.hp = new Health(this, Transform.I, maxHealth, false);
		this.deathAnimation = new Animation("icoop/vanish", 7, 2, 2, this, 32, 32, new Vector(-0.5f, 0f), ANIMATION_DURATION/7, false);
		for(Vulnerability v: vulnerabilities)
			this.vulnerabilities.add(v);
	}
	@Override
	public void update(float deltaTime) {
		if(hp.isOff()) { // Une fois le Foe à court de points de vie ;
			if(!deathAnimation.isCompleted()) 
				deathAnimation.update(deltaTime); // On joue son animation de mort.
			else
				getOwnerArea().unregisterActor(this); // Puis, lorsque l'animation est terminée on retire le Foe de la liste des acteurs de son aire.
		}
		else if(immunityTime > 0)
			immunityTime--;
		super.update(deltaTime);
	}
	@Override
	public void draw(Canvas canvas) {
		if(hp.isOff())
			deathAnimation.draw(canvas);
	}
	public boolean isImmune() {
		return immunityTime > 0;
	}
	protected int getImmunity() {
		return immunityTime;
	}
	// Foe implémente Logic.
	/**
	 * @return (boolean) : true si le Foe en vie, false sinon. 
	 */
	@Override
	public boolean isOn() {
		return hp.isOn();
	}
	/**
	 * @return (boolean) : true si le Foe est mort, false sinon.
	 */
	@Override
	public boolean isOff() {
		return hp.isOff();
	}
	// Foe implémente Interactable.
	/**
	 * Foe prend-il place dans une cellule ?
	 * @return (boolean) : true si le Foe est en vie, false sinon.
	 */
	@Override
	public boolean takeCellSpace() {
		return hp.isOn();
	}
	/**
	* Foe accepte-t-il les interactions de contact ?
	* @return (boolean) : true inconditionnellement.
	*/
	@Override
	public boolean isCellInteractable() {
		return true;
	}
	/**
	 * Foe accepte-t-il les interactions à distance ?
	 * @return (boolean) : true inconditionnellement.
	 */
	@Override
	public boolean isViewInteractable() {
		return true;
	}
	/**
	 * Foe accepte les interactions.
	 */
	@Override
	public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
		((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
	}
	/**
	 * @return (List<DiscreteCoordinates) : Liste immuable des coordonnées de la seule cellule que Foe occupe.
	 */
	@Override
	public List<DiscreteCoordinates> getCurrentCells() {
		return Collections.singletonList(getCurrentMainCellCoordinates());
	}
	// Foe implémente Interactor.
	/**
	 * Le champ de vision du Foe ne comprend aucune cellule.
	 * @return (List<DiscreteCoordinates) : Liste immuable vide.
	 */
	@Override
	public List<DiscreteCoordinates> getFieldOfViewCells() {
		return Collections.<DiscreteCoordinates> emptyList();
	}
	/**
	 * Foe est-il demandeur d'interactions de contact ?
	 * @return (boolean) : false inconditionnellement. 
	 */
	@Override
	public boolean wantsCellInteraction() {
		return false;
	}
	/**
	 * Foe est-il demandeur d'interactions à distance ?
	 * @return (boolean) : false inconditionnellement.
	 */
	@Override
	public boolean wantsViewInteraction() {
		return false;
	}
	// Foe implémente Vulnerable.
	/**
	 * @return (boolean) : true si le Foe est vulnérable à l'attaque et qu'il est hors de son temps d'immunité, false sinon.
	 */
	@Override
	public boolean hit(Vulnerability v, int damage) {
		if(vulnerabilities.contains(v) && !isImmune()) {
			hp.decrease(damage);
			immunityTime = 10;
			return true;
		}
		return false;
	}
}
