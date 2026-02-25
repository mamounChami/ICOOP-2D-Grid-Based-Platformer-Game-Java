package ch.epfl.cs107.icoop.actor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.icoop.actor.ElementalEntity.ElementalType;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.math.random.RandomGenerator;
import ch.epfl.cs107.play.window.Canvas;
/**
 * Le DarkLord est le dernier ennemi du jeu, c'est un boss.
 * Il manie le vent et se téléporte si on est trop près de son côté de l'aire.
 * Il est vulnérablie au feu et à l'eau.
 */
public class DarkLord extends Foe {
	private final static int ANIMATION_DURATION = 12;
	private OrientedAnimation idleAnimation;
	private OrientedAnimation attackAnimation;
	private State state;
	private int cooldown;
	private Orientation attackOrientation;
	private OrientedAnimation shadowSprite;
	private List<DiscreteCoordinates> fieldOfView;
	private int teleportationTargetIndex;
	private int teleportingTime;
	/**
	 * Constructeur pour un DarkLord
	 * @param owner       (Area)                : Aire à laquelle le seigneur sombre appartient.
	 * @param orientation (Orientation)         : Orientation de le seigneur sombre.
	 * @param position    (DiscreteCoordinates) : Coordonnées sur la grille que le seigneur sombre doit occuper.
	 */
	public DarkLord(Area owner, Orientation orientation, DiscreteCoordinates position) {
		super(owner, orientation, position, 10, Vulnerability.FIRE, Vulnerability.WATER);
		Orientation[] orders = {Orientation.UP, Orientation.LEFT, Orientation.DOWN, Orientation.RIGHT};
		idleAnimation = new OrientedAnimation("icoop/DarkLord", ANIMATION_DURATION/4, this, new Vector(-.45f, 0.5f), orders, 3, 2, 2, 32, 32);
		attackAnimation = new OrientedAnimation("icoop/DarkLord.spell", ANIMATION_DURATION/4, this, new Vector(-.45f, 0.5f), orders, 3, 2, 2, 32, 32);
		shadowSprite = new OrientedAnimation("icoop/DarkLordTransparent", ANIMATION_DURATION/4, this, new Vector(-.45f, 0.5f), orders, 3, 2, 2, 32, 32);
		state = State.IDLE;
		cooldown = 50;
		attackOrientation = Orientation.DOWN;
		teleportationTargetIndex = 0;
		teleportingTime = 0;
		// Sanctum est une aire 16×16, de laquelle le DarkLord doit pouvoir observer
		// trois rangées ou colonnes autour de sa position.
		// On a prérempli une liste avec toutes les cellules de son champ de vision,
		// mais nous ne communiquerons au moteur que celles sur lesquelles le
		// DarkLord est posté.
		fieldOfView = new ArrayList<DiscreteCoordinates>(16*3*4);
		// Trois rangées en haut
		for(int y = 10; y < 13; y++)
			for(int x = 0; x < 16; x++)
				fieldOfView.add(new DiscreteCoordinates(x, y));
		// Trois colonnes à droite
		for(int x = 12; x < 15; x++)
			for(int y = 0; y < 16; y++) 
				fieldOfView.add(new DiscreteCoordinates(x, y));
		// Trois rangées en bas
		for(int y = 3; y < 6; y++)
			for(int x = 0; x < 16; x++)
				fieldOfView.add(new DiscreteCoordinates(x, y));
		// Trois colonnes à gauche
		for(int x = 1; x < 4; x++)
			for(int y = 0; y < 16; y++)
				fieldOfView.add(new DiscreteCoordinates(x, y));
		
	}
	/**
	 * Énumérateur privé des états du DarkLord.
	 */
	private enum State {
		IDLE,
		ATTACKING,
		TELEPORTING
		;
	}
	@Override
	public void update(float deltaTime) {
		if(isOn()) { // Le DarkLord doit être en vie pour se mettre à jour.
			switch(state) {
				case State.IDLE : {
					cooldown--;
					if(!isDisplacementOccurs() ) {
						if(cooldown <= 0) {
							switchToAttacking();
						}
						else {
							randomMove(1);
							idleAnimation.reset();
						}
					}
					else	
						idleAnimation.update(deltaTime);
				} break;
				case State.ATTACKING : {
					if(attackAnimation.isCompleted()) {
						Area area = getOwnerArea();
						// On crée nos trois projectiles
						DiscreteCoordinates mainCell = getCurrentMainCellCoordinates();
						Vector attackVector = attackOrientation.toVector();
						ElementalProjectile windProjectile0 = new ElementalProjectile(area, attackOrientation, mainCell.jump(attackVector), "icoop/magicWindProjectile", ElementalType.WIND, 16, 1);
						ElementalProjectile windProjectile1 = new ElementalProjectile(area, attackOrientation, mainCell.jump(attackVector.add(attackOrientation.hisRight().toVector())), "icoop/magicWindProjectile", ElementalType.WIND, 16, 1);
						ElementalProjectile windProjectile2 = new ElementalProjectile(area, attackOrientation, mainCell.jump(attackVector.add(attackOrientation.hisLeft().toVector())), "icoop/magicWindProjectile", ElementalType.WIND, 16, 1);
						// Et s'ils peuvent entrer, on les enregistre dans la liste des acteurs de l'aire
						if(area.canEnterAreaCells(windProjectile0, windProjectile0.getCurrentCells()))
							area.registerActor(windProjectile0);
						if(area.canEnterAreaCells(windProjectile1, windProjectile1.getCurrentCells()))
							area.registerActor(windProjectile1);
						if(area.canEnterAreaCells(windProjectile2, windProjectile2.getCurrentCells()))
							area.registerActor(windProjectile2);
						attackAnimation.reset();
						switchToIdle();
					}
					else
						attackAnimation.update(deltaTime);
				} break;
				case State.TELEPORTING : {
					teleportingTime--;
					if(!isDisplacementOccurs()) {
						if(teleportingTime <= 0) {
							teleport(teleportationTargetIndex);
							switchToIdle();
						}
						else
							randomMove(4);
					}
				} break;
			}
		}
		super.update(deltaTime);
	}
	@Override
	public void draw(Canvas canvas) {
		if((getImmunity() % 2) == 0 && isOn()) {
			switch(state) {
				case State.IDLE : {
						idleAnimation.draw(canvas);
				} break;
				case State.ATTACKING : {
					attackAnimation.draw(canvas);
				} break;
				case State.TELEPORTING : {
					shadowSprite.draw(canvas);
				} break;
			}
		}
		super.draw(canvas);
	}
	/**
	 * Basculer vers l'état inoccupé.
	 */
	private void switchToIdle() {
		state = State.IDLE;
		cooldown = RandomGenerator.getInstance().nextInt(50, 100); // Temps de refroidissement avant une nouvelle attaque.
	}
	/**
	 * Basculer vers l'état d'attaque.
	 */
	private void switchToAttacking() {
		state = State.ATTACKING;
		orientate(attackOrientation);
	}
	/**
	 * Basculer vers l'état de téléportation.
	 */
	private void switchToTeleporting() {
		state = State.TELEPORTING;
		teleportationTargetIndex = RandomGenerator.getInstance().nextInt(0, 4); // Côté de l'aire vers lequel on souhaite se téléporter.
		teleportingTime = RandomGenerator.getInstance().nextInt(20, 50); // Temps où on garde la forme d'une ombre invulnérable avant de se téléporter pour de bon.
	}
	/**
	 * Déplacement aléatoire mais seulement latéralement à la direction d'attaque.
	 * @param speedFactor (int) : Facteur de vitesse.
	 */
	private void randomMove(int speedFactor) {
		int random = RandomGenerator.getInstance().nextInt(0, 2);
		if(random == 0) {
			orientate(attackOrientation.hisRight());
			move(ANIMATION_DURATION/speedFactor);
		}
		else {
			orientate(attackOrientation.hisLeft());
			move(ANIMATION_DURATION/speedFactor);
		}
	}
	/**
	 * Se téléporter d'un côté de l'aire. Si on est déjà de ce côté, alors on se téléporte à l'opposé.
	 * @param areaEdge (int) : Côté de l'aire vers lequel on souhaite se téléporter.
	 */
	private void teleport(int areaEdge) {
		// On fixe quatre positions sur lesquelles le DarkLord peut se placer,
		// elles sont dans le même ordre que pour la classe Orientation.
		DiscreteCoordinates[] areaEdges = { 
			new DiscreteCoordinates(7, 11), // UP
			new DiscreteCoordinates(13, 7), // RIGHT
			new DiscreteCoordinates(8, 4),  // DOWN
			new DiscreteCoordinates(2, 8)   // LEFT
		};
		// On vérifie qu'on n'est pas déjà dans ce côté de l'aire
		if(attackOrientation.opposite() == Orientation.fromInt(areaEdge)) 
			areaEdge = Orientation.fromInt(areaEdge).opposite().ordinal(); // Si on l'est, on se met du côté opposé à celui qui était souhaité.		
		DiscreteCoordinates teleportationTarget = areaEdges[areaEdge];
		attackOrientation = Orientation.fromInt(areaEdge).opposite(); // On attaque à l'opposé de sa position
		// On se téléporte pour de bon
		changePosition(teleportationTarget);
		orientate(attackOrientation);
	}
	/**
	 * Le DarkLord prend-il place dans une cellule ?
	 * @return (boolean) : true s'il est en vie et qu'il ne se téléporte pas, false sinon.
	 */
	@Override
	public boolean takeCellSpace() {
		return isOn() && state != State.TELEPORTING;
	}
	/**
	 * Le champ de vision du DarkLord s'étend à trois rangées ou colonnes selon sa position.
	 * @return (List<DiscreteCoordinates>) : Liste immuable des coordonnées de cellule du champ de vision du DarkLord.
	 */
	@Override
	public List<DiscreteCoordinates> getFieldOfViewCells() {
		int startIndex = attackOrientation.opposite().ordinal()*16*3;
		return Collections.unmodifiableList(fieldOfView.subList(startIndex, startIndex+16*3));
	}
	/**
	 * Le DarkLord est-il demandeur d'interactions à distance ?
	 * @return (boolean) : true s'il ne se téléporte pas, false sinon.
	 */	
	@Override
	public boolean wantsViewInteraction() {
		return state != State.TELEPORTING;
	}
	private ICoopInteractionVisitor handler = new ICoopInteractionVisitor() {
		/**
		 * Se téléporter car le joueur est trop près, lors d'une interaction à distance.
		 */
		@Override
		public void interactWith(ICoopPlayer other, boolean isCellInteraction) {			
			switchToTeleporting();
		}
	};
	/**
	 * Redirige la gestion des interactions du DarkLord à son handler de type ICoopInteractionVisitor.
	 */
	@Override
	public void interactWith(Interactable other, boolean isCellInteraction) {
		other.acceptInteraction(handler, isCellInteraction);
	}
	
}
