package ch.epfl.cs107.icoop.actor;

import java.util.ArrayList;
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
 * Les artificiers sont capable de mouvements aléatoires ainsi que de mouvements ciblés, visant un joueur(ICoopPlayer).
 * Une fois proche de leur cible, ils larguent une bombe puis se protège un moment.
 */
public class BombFoe extends Foe {
	private static final int MAX_HEALTH = 2;
	private static final int ANIMATION_DURATION = 24;
	private static final int MAX_IDLE_TIME = 24;
	private static final int MIN_IDLE_TIME = 0;
	private static final int MAX_PROTECTING_TIME = 120;
	private static final int MIN_PROTECTING_TIME = 72;
	private static final int FAST_SPEED = 6;
	private static final int NORMAL_SPEED = 2;
	private static final int SLOW_SPEED = 1;
	private static final int MAX_VIEW_RANGE = 8;
	private ICoopPlayer target;
	private State state;
	private int idleTime;
	private int protectingTime;
	private OrientedAnimation idleAnimation;
	private OrientedAnimation protectingAnimation;
	/**
	 * Constructeur pour un BombFoe
	 * @param owner           (Area)                : Aire à laquelle l'artificier appartient.
	 * @param orientation     (Orientation)         : Orientation de l'artificier.
	 * @param position        (DiscreteCoordinates) : Coordonnées sur la grille que l'artificier doit occuper.
	 * @param maxHealth       (int)                 : Points de vie maximaux que l'artificier doit avoir.
	 * @param vulnerabilities (Vulnerability[])     : Tableau des vulnérabilités auxquelles l'artificier est vulnérable.
	 */
	public BombFoe(Area owner, Orientation orientation, DiscreteCoordinates position, int maxHealth, Vulnerability ...vulnerabilities) {
		super(owner, orientation, position, maxHealth, vulnerabilities);
		final Vector anchor = new Vector(-0.5f, 0);
		final Orientation[] orders = new Orientation[]{Orientation.DOWN, Orientation.RIGHT, Orientation.UP, Orientation.LEFT};
		idleAnimation = new OrientedAnimation("icoop/bombFoe", ANIMATION_DURATION/3, this, anchor, orders, 4, 2, 2, 32, 32, true);
		protectingAnimation = new OrientedAnimation("icoop/bombFoe.protecting", ANIMATION_DURATION/3, this, anchor, orders, 4, 2, 2, 32, 32, false);
		state = State.IDLE;
		idleTime = 0;
		protectingTime = 0;
		target = null;
	}
	/**
	 * Constructeur pour un BombFoe
	 * @param owner           (Area)                : Aire à laquelle l'artificier appartient.
	 * @param orientation     (Orientation)         : Orientation de l'artificier.
	 * @param position        (DiscreteCoordinates) : Coordonnées sur la grille que l'artificier doit occuper.
	 */
	public BombFoe(Area owner, Orientation orientation, DiscreteCoordinates position) {
		this(owner, orientation, position, MAX_HEALTH, Vulnerability.PHYSICAL, Vulnerability.FIRE);
	}
	/**
	 * Énumérateur privé des états de BombFoe.
	 */
	private enum State {
		IDLE,
		ATTACKING,
		PROTECTING,
	}
	
	@Override
	public void update(float deltaTime) {
		if(isOn()) { // Le BombFoe doit être en vie pour se mettre à jour.
			idleTime--;
			if(idleTime <= 0) {
				idleTime = 0; // On évite de décrémenter la variable indéfiniment et de risquer un underflow.
				switch(state) {
					case State.IDLE: {
							if(!isDisplacementOccurs()) {
								if(!randomMove(NORMAL_SPEED)) { // Si on ne se déplace pas,
									switchToIdle(); // alors on réinitialise son temps d'inoccupation.							
								}
								idleAnimation.reset();
							}
							else if(idleAnimation.isCompleted())
								idleAnimation.reset();
						idleAnimation.update(deltaTime);
					} break;
					case State.ATTACKING: {
						if(!isDisplacementOccurs() ) {
							// Si la distance avec la cible est inférieure à 3,
							if(DiscreteCoordinates.distanceBetween(getCurrentMainCellCoordinates(), target.getCurrentMainCellCoordinates()) < 3.0) { 
								switchToProtecting(); // On tente de passer à l'état de protection.
							}
							else { // La cible est encore trop loin alors on s'en rapproche.
								// Mise en oeuvre d'un déplacement ciblée.
								Vector delta = target.getPosition().sub(getPosition()); // On calcule la distance entre le BombFoe et sa cible.
								float absX = Math.abs(delta.x);
								float absY = Math.abs(delta.y);
								delta = absX > absY ? new Vector(delta.x, 0) : new Vector(0, delta.y); // On choisit la composante horizontale ou verticale la plus grande pour réduire l'écart avec sa cible.
								if(orientate(Orientation.fromVector(delta))) // On essaie de s'orienter vers sa cible.
									move(ANIMATION_DURATION/NORMAL_SPEED); // Si on y arrive, on fait un pas régulier.
								else
									move(ANIMATION_DURATION/FAST_SPEED); // Sinon, on fait un pas rapide.
							}
							idleAnimation.reset();
						}
						else {
							if(idleAnimation.isCompleted())
								idleAnimation.reset();
							idleAnimation.update(deltaTime);
						}
						
					} break;
					case State.PROTECTING: {
						protectingTime--;
						if(protectingTime > 0) {
							if(protectingAnimation.isCompleted() && !isDisplacementOccurs()) 
								randomMove(SLOW_SPEED);
							else
								protectingAnimation.update(deltaTime);
						}
						else {
							protectingAnimation.reset();
							switchToIdle();
						}
					} break;
				}
			}
		}
		super.update(deltaTime);
	}
	@Override
	public void draw(Canvas canvas) {
		if((getImmunity() % 2) == 0 && isOn()) { // On dessine le BombFoe temps qu'il est en vie est que son temps d'immunité est pair.
			switch(state) {
				case  State.IDLE: 
				case  State.ATTACKING:
				{
					idleAnimation.draw(canvas);
				} break;
				case  State.PROTECTING: {
					protectingAnimation.draw(canvas);
				} break;
			}
		}
		super.draw(canvas);
	}
	/**
	 * Déplacement aléatoire dans une direction au hasard à une vitesse donnée
	 * @param speedFactor (int) : Vitesse de déplacement.
	 * @return (boolean) : true si le déplacement a lieu, false sinon.
	 */
	private boolean randomMove(int speedFactor) {
		double randDouble = RandomGenerator.getInstance().nextDouble();
		if(randDouble < 0.4f) {
			int randomInt = RandomGenerator.getInstance().nextInt(4);
			orientate(Orientation.fromInt(randomInt));
			move(ANIMATION_DURATION/speedFactor);
			return true;
		}
		return false;
	}
	/**
	 * Basculer le BombFoe vers l'état inoccupé pour une durée aléatoire.
	 */
	private void switchToIdle() {
		state = State.IDLE;
		idleTime = RandomGenerator.getInstance().nextInt(MIN_IDLE_TIME, MAX_IDLE_TIME);
	}
	/**
	 * Basculer le BombFoe vers l'état d'attaque, en ciblant l'ICoopPlayer target.
	 * @param target (ICoopPlayer) : Joueur que le BombFoe doit cibler.
	 */
	private void switchToAttacking(ICoopPlayer target) {
		this.target = target;
		state = State.ATTACKING;
	}
	/**
	 * Basculer le BombFoe vers l'état de protection pour une durée aléatoire, seulement si une bombe peut être placer en face de lui.
	 * @return (boolean) : true si la bombe a pu être posée, false sinon.
	 */
	private boolean switchToProtecting() {
		DiscreteCoordinates frontCell = getCurrentMainCellCoordinates().jump(getOrientation().toVector());
		Explosive bomb = new Explosive(getOwnerArea(), Orientation.LEFT, frontCell);
		if(getOwnerArea().canEnterAreaCells(bomb, Collections.singletonList(frontCell))) {
			target = null; // On oublie sa cible.
			state = State.PROTECTING;
			protectingTime = RandomGenerator.getInstance().nextInt(MIN_PROTECTING_TIME, MAX_PROTECTING_TIME);												
			bomb.activate();
			getOwnerArea().registerActor(bomb);
			return true;
		}
		return false;
	}
	// BombFoe implémente Interactor.
	/**
	 * BombFoe est-il demandeur d'interactions à distance ?
	 * @return (boolean) : true s'il est vivant, et qu'il n'est pas en état de protection ou en période d'immunité, false sinon.
	 */
	@Override
	public boolean wantsViewInteraction() {
		return isOn() && state != State.PROTECTING && !isImmune();
	}
	/**
	 * Le champ de vision de BombFoe s'étend jusqu'à huit cellules en face de lui lorsqu'il n'est pas dans l'état d'attaque, une cellule sinon.
	 * @return (List<DiscreteCoordinates) : Liste immuable du champ de vision de BombFoe.  
	 */
	@Override
	public List<DiscreteCoordinates> getFieldOfViewCells() {
		DiscreteCoordinates cell = getCurrentMainCellCoordinates().jump(getOrientation().toVector());
		if(state == State.ATTACKING)
			return Collections.singletonList(cell);
		else {
			ArrayList<DiscreteCoordinates> list = new ArrayList<DiscreteCoordinates>(MAX_VIEW_RANGE);			
			list.add(cell);
			for(int i = 1; i < MAX_VIEW_RANGE; i++) {
				cell = cell.jump(getOrientation().toVector());
				list.add(cell);
			}
			return Collections.unmodifiableList(list);
		}
		
	}
	private ICoopInteractionVisitor handler = new ICoopInteractionVisitor() {
		/**
		 * Basculer vers l'état d'attaque en prenant le joueur entré dans le champ de vision pour cible, lors d'une interaction à distance.
		 */
		@Override
		public void interactWith(ICoopPlayer other, boolean isCellInteraction) {
			switchToAttacking(other);
		}
		
	};
	/**
	 * Redirige la gestion des interactions de BombFoe à son handler de type ICoopInteractionVisitor.
	 */
	@Override
	public void interactWith(Interactable other, boolean isCellInteraction) {
		other.acceptInteraction(handler, isCellInteraction);
	}
	/**
	 * @return (boolean) : true si le BombFoe est vulnérable à l'attaque, qu'il est hors de son temps d'immunité et qu'il ne se protège pas, false sinon.
	 */
	@Override
	public boolean hit(Vulnerability v, int damage) {
		if(state != State.PROTECTING && super.hit(v, damage)) {
			idleTime = 0;
			return true;
		}
		return false;
	}
}
