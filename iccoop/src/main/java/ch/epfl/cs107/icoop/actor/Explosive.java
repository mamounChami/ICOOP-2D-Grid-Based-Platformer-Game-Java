package ch.epfl.cs107.icoop.actor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.icoop.actor.Vulnerable.Vulnerability;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;
/**
 * Bombe pouvant être collectée par un ICoopPlayer, et être utilisée soit par un ICoopPlayer soit par un BombFoe.
 */
public class Explosive extends ICoopCollectable implements Interactor {
	private static final int ANIMATION_DURATION = 24;
	private Animation idleAnimation;
	private Animation explosionAnimation;
	private State state;
	private int countdown;
	private List<DiscreteCoordinates> fieldOfView;;
	/**
	 * Constructeur pour Explosive
	 * @param owner       (Area)                : Aire à laquelle la bombe appartient.
	 * @param orientation (Orientation)         : Orientation de la bombe.
	 * @param position    (DiscreteCoordinates) : Coordonnées sur la grille que la bombe doit occuper.
	 */
	public Explosive(Area owner, Orientation orientation, DiscreteCoordinates position) {
		super(owner, orientation, position);
		idleAnimation = new Animation("icoop/explosive", 2, 1, 1, this, 16, 16, ANIMATION_DURATION/6, true);
		explosionAnimation = new Animation("icoop/explosion", 7, 1, 1, this, 32, 32, ANIMATION_DURATION/7, false);
		state = State.IDLE;
		countdown = 50;
		fieldOfView = new ArrayList<DiscreteCoordinates>(4);
		fieldOfView.add(position.jump(Orientation.UP.toVector()));
		fieldOfView.add(position.jump(Orientation.RIGHT.toVector()));
		fieldOfView.add(position.jump(Orientation.DOWN.toVector()));
		fieldOfView.add(position.jump(Orientation.LEFT.toVector()));
	}
	/**
	 * Énumérateur privé des états d'un Explosive.
	 */
	private enum State {
		IDLE(),
		ACTIVE(),
		EXPLODING()
		;
	}
	/**
	 * Assignateur privé de l'état d'Explosive
	 * @param state (State) : Nouvel état que l'on veut assigner. 
	 */
	private void setState(State state) {
		this.state = state;
	}
	/**
	 * Basculer l'Explosive à l'état actif.
	 */
	public void activate() {
		setState(State.ACTIVE);
	}
	/**
	 * Basculer l'Explosive à l'état d'explosion.
	 */
	public void explode() {
		setState(State.EXPLODING);
	}
	// Explosive implémente Actor.
	@Override 
	public void update(float deltaTime) {
		if(state == State.ACTIVE) {
			--countdown;
			if(countdown <= 0) {
				setState(State.EXPLODING);
			}
			idleAnimation.update(deltaTime); // On ne met à jour l'animation de base de l'Explosive que lorsqu'il est dans l'état actif.
		}
		else if(state == State.EXPLODING) {
			// Une fois l'animation d'explosion terminée, on retire l'Explosive de la liste des acteurs de son aire.
			if(explosionAnimation.isCompleted()) {
				getOwnerArea().unregisterActor(this);
				return;
			}
			else explosionAnimation.update(deltaTime);
		}
		super.update(deltaTime);
	}
	@Override 
	public void draw(Canvas canvas) {
		if(state == State.EXPLODING)
			explosionAnimation.draw(canvas);
		else
			idleAnimation.draw(canvas);
	}
	// L'Explosive implémente Interactable.
	/**
	 * L'Explosive accepte-t-il les interactions de contact ?
	 * @return (boolean) : true si l'Explosive est inoccupé, false sinon.
	 */
	@Override
	public boolean isCellInteractable() {
		return state == State.IDLE;
	}
	/**
	 * L'Explosive accepte-t-il les interactions à distance ?
	 * @return (boolean) : true inconditionnellement. Car après explosion la bombe disparaît et ne peux plus subir d'interaction.
	 */
	@Override
	public boolean isViewInteractable() {
		return true;
	}
	/**
	 * L'Explosive accepte les interactions.
	 */
	@Override
	public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
		((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
	}
	/**
	 * @return (List<DiscreteCoordinates) : Liste immuable des coordonnées de la seule cellule que l'Explosive occupe.
	 */
	@Override
	public List<DiscreteCoordinates> getCurrentCells() {
		return Collections.singletonList(getCurrentMainCellCoordinates());
	}
	// L'Explosive implémente Interactor.
	/**
	 * Le champ de vision de l'Explosive correspond aux quatre cellules immédiatement autour de lui, que ce soit horizontalement ou verticalement.
	 * @return (List<DiscreteCoordinates) : Liste immuable des coordonnées de cellule du champ de vision de l'Explosive.
	 */
	@Override
	public List<DiscreteCoordinates> getFieldOfViewCells() {
		return Collections.unmodifiableList(fieldOfView);
	}
	/**
	 * L'Explosive est-il demandeur d'interactions de contact ?
	 * @return (boolean) : true si la bombe est en train d'exploser, false sinon.
	 */
	@Override
	public boolean wantsCellInteraction() {
		return state == State.EXPLODING;
	}
	/**
	 * L'Explosive est-il demandeur d'interactions à distance ?
	 * @return (boolean) : true si la bombe est en train d'exploser, false sinon.
	 */
	@Override
	public boolean wantsViewInteraction() {
		return state == State.EXPLODING;
	}
	private ICoopInteractionVisitor handler = new ICoopInteractionVisitor() {
		/**
		 * Destruction d'un rocher lorsque la bombe explose, que ce soit lors d'une interaction de contact ou à distance.
		 */
		@Override
		public void interactWith(Rock other, boolean isCellInteraction) {
			getOwnerArea().unregisterActor(other);
		}
		/**
		 * Tentative d'attaque physique(Vulnerability.PHYSICAL) lorsque la bombe explose,
		 * que ce soit lors d'une interaction de contact ou à distance.
		 */
		@Override
		public void interactWith(ICoopPlayer other, boolean isCellInteraction) {
			other.hit(Vulnerability.PHYSICAL, 2);
		}
		/**
		 * Tentative d'attaque physique(Vulnerability.PHYSICAL) lorsque la bombe explose,
		 * que ce soit lors d'une interaction de contact ou à distance.
		 */
		@Override
		public void interactWith(Foe other, boolean isCellInteraction) {
			other.hit(Vulnerability.PHYSICAL, 2);
		}
		/**
		 * Destruction d'un mur lorsque la bombe explose, que ce soit lors d'une interaction de contact ou à distance.
		 */
		@Override
		public void interactWith(ElementalWall other, boolean isCellInteraction) {
			getOwnerArea().unregisterActor(other);
		}
		/**
		 * Explosion d'une bombe lorsque la bombe explose, que ce soit lors d'une interaction de contact ou à distance.
		 */
		@Override
		public void interactWith(Explosive other, boolean isCellInteraction) {
			other.explode();
		}
	};
	/**
	 * Redirige la gestion des interactions de l'Explosive vers son handler de type ICoopInteractionVisitor. 
	 */
	@Override
	public void interactWith(Interactable other, boolean isCellInteraction) {
		other.acceptInteraction(handler, isCellInteraction);
	}
}
