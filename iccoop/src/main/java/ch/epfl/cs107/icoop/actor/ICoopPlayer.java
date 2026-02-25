package ch.epfl.cs107.icoop.actor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.icoop.KeyBindings;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.icoop.handler.ICoopInventory;
import ch.epfl.cs107.icoop.handler.ICoopItem;
import ch.epfl.cs107.icoop.handler.ICoopPlayerStatusGUI;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.Inventory;
import ch.epfl.cs107.play.areagame.handler.InventoryItem;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Transform;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Button;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

/**
 * A ICoopPlayer is a player for the ICoop game.
 */
public final class ICoopPlayer  extends MovableAreaEntity  implements ElementalEntity, Interactor, Logic, Inventory.Holder, Vulnerable {
	private final static int ANIMATION_DURATION = 4;
	private final static int SWORD_ANIMATION_DURATION = 2;
	private final static int STAFF_ANIMATION_DURATION = 2;
	private final static int MAX_HEALTH = 5;
	private Health hp;
	private ElementalType element;
	private OrientedAnimation currentAnimation;
	private final OrientedAnimation idleAnimation;
	private final OrientedAnimation swordAnimation;
	private final OrientedAnimation staffAnimation;
	private final KeyBindings.PlayerKeyBindings keys;
	private Door currentDoor;
	private int immunityTime;
	private ICoopInventory inventory;
	private int coinCount;
	private ICoopPlayerStatusGUI statusGUI;
	private List<Vulnerability> vulnerabilities;
	private State state = State.IDLE;
	/**
	 * Constructeur pour un ICoopPlayer.
	 * @param owner         (Area)                          : Aire à laquelle le joueur appartient.
	 * @param orientation   (Orientation)                   : Orientation du joueur.
	 * @param position      (DiscreteCoordinates)           : Coordonnées sur la grille que le joueur doit occuper.
	 * @param animationName (String)                        : Chaîne de caractères identifiant la ressource pour la construction de l'OrientedAnimation du joueur.
	 * @param keys          (KeyBindings.PlayerKeyBindings) : Jeu de touches du clavier associé aux commandes du joueur.
	 * @param element       (ElementalType)                 : Type élémentaire auquel le joueur appartient.
	 */
	public ICoopPlayer(Area owner, Orientation orientation, DiscreteCoordinates position, String animationName, KeyBindings.PlayerKeyBindings keys, ElementalType element) {
		super(owner, orientation, position);
		hp = new Health(this, Transform.I.translated(0, 1.75f), MAX_HEALTH, true);
		final Orientation[] idleOrders = {Orientation.DOWN, Orientation.RIGHT, Orientation.UP, Orientation.LEFT};
		idleAnimation = new OrientedAnimation(animationName, ANIMATION_DURATION, this, new Vector(0, 0), idleOrders, 4, 1, 2, 16, 32, true);
		currentAnimation = idleAnimation;
		final Orientation[] orders = new Orientation[] {Orientation.DOWN, Orientation.UP, Orientation.RIGHT, Orientation.LEFT};
		swordAnimation = new OrientedAnimation(animationName + ".sword", SWORD_ANIMATION_DURATION, this, new Vector(-.5f, 0), orders, 4, 2, 2, 32, 32);
		staffAnimation = new OrientedAnimation(animationName + ".staff_" + ((element == ElementalType.FIRE) ? "fire" : "water"),
				STAFF_ANIMATION_DURATION, this, new Vector(-.5f, -.20f), orders, 4, 2, 2, 32, 32);
		this.keys = keys;
		this.element = element;
		this.inventory = new ICoopInventory("Pocket");
		this.statusGUI = new ICoopPlayerStatusGUI(this, element == ElementalType.FIRE ? false : true);
		currentDoor = null;
		immunityTime = 0;
		coinCount = 0;
		vulnerabilities  = new ArrayList<Vulnerability>();
		vulnerabilities.add(Vulnerability.PHYSICAL);
		vulnerabilities.add(Vulnerability.FIRE);
		vulnerabilities.add(Vulnerability.WATER);
		vulnerabilities.add(Vulnerability.WIND);
	}
	/**
	 * Énumérateur privé des états de l'ICoopPlayer.
	 */
	private enum State {
		IDLE,
		SWORD_ATTACK,
		STAFF_ATTACK,
		SAY_YES;
	}
	/**
     * @param deltaTime elapsed time since last update, in seconds, non-negative
     */
    @Override
    public void update(float deltaTime) {
        Keyboard keyboard = getOwnerArea().getKeyboard();
        if(keyboard.get(keys.switchItem()).isPressed())
        	inventory.selectNextItem();
        switch(state) {
	        case State.IDLE: {
	        	// On vérifie si le joueur veut se déplacer.
	        	moveIfPressed(Orientation.LEFT, keyboard.get(keys.left()));
	            moveIfPressed(Orientation.UP, keyboard.get(keys.up()));
	            moveIfPressed(Orientation.RIGHT, keyboard.get(keys.right()));
	            moveIfPressed(Orientation.DOWN, keyboard.get(keys.down()));
	           
	            if(isDisplacementOccurs()) // Si aucun déplacement n'a lieu on met à jour l'animation,
	            	currentAnimation.update(deltaTime);
	            else //sinon, on réinitialise l'animation.
	            	currentAnimation.reset();
	            // Si le joueur veut utiliser son équipement.
	            if(keyboard.get(keys.useItem()).isPressed()) {
		        	ICoopItem item = inventory.getCurrentItem();
		        	switch(item) {
		        		case ICoopItem.EXPLOSIVE : {
		        			// On crée une bombe à placer en face du joueur.
	        				Explosive bomb = new Explosive(getOwnerArea(), Orientation.LEFT, getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
		        			// On s'assure que la cellule en face du joueur est disponible pour une bombe.
		        			if(getOwnerArea().canEnterAreaCells(bomb, getFieldOfViewCells())) {
		        				inventory.removePocketItem(item, 1);
		        				bomb.activate();
		        				getOwnerArea().registerActor(bomb);
		        			}
		        		} break;
		        		case ICoopItem.SWORD: {
		        			switchToSwordAttack();
		        		} break;
		        		case ICoopItem.FIRESTAFF: 
		        		case ICoopItem.WATERSTAFF:
		        		{
		        			switchToStaffAttack();
		        		} break;
		        		default:;
		        	}
		        }
	            else if(keyboard.get(keys.sayYes()).isPressed()) {
	            	state = State.SAY_YES;
	            }
	        }break;
			case State.SWORD_ATTACK: {
				// Une fois l'animation d'attaque à l'épée terminée ;
				if(currentAnimation.isCompleted()) {
					// on réinitialise l'animation et on bascule vers l'état inoccupé.
					currentAnimation.reset();
					switchToIdle();
				}
				else // L'animation n'est pas terminée, alors on la met à jour.
			        currentAnimation.update(deltaTime);
	        }break;
			case State.STAFF_ATTACK: {
				// Une fois l'animation d'attaque au bâton terminée, on lance un projectile d'eau ou de feu ;
				if(currentAnimation.isCompleted()) {
	        		if(element == ElementalType.FIRE) {
	        			getOwnerArea().registerActor(new ElementalProjectile(getOwnerArea(), getOrientation(), getFieldOfViewCells().get(0), "icoop/magicFireProjectile", ElementalType.FIRE, 4, 1));
	        		}
	        		else if(element == ElementalType.WATER) {
	        			getOwnerArea().registerActor(new ElementalProjectile(getOwnerArea(), getOrientation(), getFieldOfViewCells().get(0), "icoop/magicWaterProjectile", ElementalType.WATER, 4, 1));
	        		}
	        		// puis, on réinitialise l'animation et on bascule vers l'état inoccupé.
	        		currentAnimation.reset();
	        		switchToIdle();
	        	}
				else // L'animation n'est pas terminée, alors on la met à jour.
			        currentAnimation.update(deltaTime);
			}break;
			default : switchToIdle();
        }
        if(immunityTime > 0)
        	immunityTime--;
        super.update(deltaTime);
    }
    /**
     * Orientate and Move this player in the given orientation if the given button is down
     *
     * @param orientation (Orientation): given orientation, not null
     * @param b           (Button): button corresponding to the given orientation, not null
     */
    private boolean moveIfPressed(Orientation orientation, Button b) {
        if (b.isDown()) {
            if (!isDisplacementOccurs()) {
                orientate(orientation);
                move(ANIMATION_DURATION);
            }
            return true;
        }
        return false;
    }
    /**
     * Basculer l'ICoopPlayer à l'état inoccupé.
     */
    private void switchToIdle() {
    	state = State.IDLE;
    	currentAnimation = idleAnimation;
    }
    /**
     * Basculer l'ICoopPlayer à l'état d'attaque à l'épée.
     */
    private void switchToSwordAttack() {
    	state = State.SWORD_ATTACK;
    	currentAnimation = swordAnimation;
    }
    /**
     * Basculer l'ICoopPlayer à l'état d'attaque au bâton.
     */
    private void switchToStaffAttack() {
    	state = State.STAFF_ATTACK;
    	currentAnimation = staffAnimation;
    }
    /**
     * Leave an area by unregister this player
     */
    public void leaveArea() {
        getOwnerArea().unregisterActor(this);
        currentDoor = null; // On oublie la porte par laquelle on a transité.
    }
    /**
     * makes the player entering a given area
     * @param area     (Area):  the area to be entered, not null
     * @param position (DiscreteCoordinates): initial position in the entered area, not null
     */
    public void enterArea(Area area, DiscreteCoordinates position) {
        area.registerActor(this);
        setOwnerArea(area);
        setCurrentPosition(position.toVector());
        resetMotion();
    }
    /**
     * Center the camera on the player
     */
    public void centerCamera() {
        getOwnerArea().setViewCandidate(this);
    }
	/**
     * @param canvas target, not null
     */
    @Override
    public void draw(Canvas canvas) {
    	if((immunityTime % 2) == 0) { // On dessine l'ICoopPlayer que si son temps d'immunité est pair.
    		currentAnimation.draw(canvas);
    	}
        hp.draw(canvas);
        statusGUI.draw(canvas);
    }
    /**
	 * @return (List<DiscreteCoordinates) : Liste immuable des coordonnées de la seule cellule que l'ICoopPlayer occupe.
	 */
	@Override
	public List<DiscreteCoordinates> getCurrentCells() {
		return Collections.singletonList(getCurrentMainCellCoordinates());
	}
	/**
	 * Le champ de vision d'un ICoopPlayer se limite à la cellule directement en face de lui.
	 * @return (List<DiscreteCoordinates) : Liste immuable des coordonnées de cellule du champ de vision de l'ICoopPlayer.
	 */
	@Override
	public List<DiscreteCoordinates> getFieldOfViewCells() {
		return Collections.singletonList(getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
	}
	/**
	 * L'ICoopPlayer prend-il place dans une cellule ?
	 * @return (boolean) : true inconditionnellement.
	 */
	@Override
	public boolean takeCellSpace() {
		return true;
	}
	/**
	 * L'ICoopPlayer accepte-t-il les interactions de contact ?
	 * @return (boolean) : true inconditionnellement.
	 */
	@Override
	public boolean isCellInteractable() {
		return true;
	}
	/**
	 * L'ICoopPlayer accepte-t-il les interactions à distance ?
	 * @return (boolean) : true inconditionnellement.
	 */
	@Override
	public boolean isViewInteractable() {
		return true;
	}
	/**
	 * L'ICoopPlayer accepte les interactions.
	 */
	@Override
	public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
		((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
	}
	/**
	 * L'ICoopPlayer est-il demandeur d'interactions de contact ?
	 * @return (boolean) : true inconditionnellement.
	 */
	@Override
	public boolean wantsCellInteraction() {
		return true;
	}
	/**
	 * L'ICoopPlayer est-il demandeur d'interactions à distance ?
	 * @return (boolean) : true si l'ICoopPlayer n'est pas inoccupé(State.IDLE), false sinon.
	 */
	@Override
	public boolean wantsViewInteraction() {
		return state != State.IDLE;
	}
	/**
	 * @return (boolean) : true si l'ICoopPlayer a atteint une porte de sortie active, false sinon. 
	 */
    public boolean wantsToLeave() {
    	return currentDoor != null;
    }
    /** 
     * @return La porte(Door) que l'ICoopPlayer souhaite emprunter.
     */
    public Door getDoor() {
    	return currentDoor;
    }
    /**
     * @return (boolean) : true si l'ICoopPlayer est en vie, false sinon.
     */
    @Override
	public boolean isOn() {
		return hp.isOn();
	}
    /**
     * @return (boolean) : true si l'ICoopPlayer est mort, false sinon.
     */
    @Override
    public boolean isOff() {
    	return hp.isOff();
    }
	private ICoopInteractionVisitor ICoopPlayerInteractionHandler = new ICoopInteractionVisitor() {
		/**
		 * Collecter une pièce lors d'une interaction de contact.
		 */
		@Override
		public void interactWith(Coin other, boolean isCellInteraction) {
			other.collect();
			coinCount++;
		}
		/**
		 * Acheter chez un marchand lors d'une interaction à distance.
		 */
		@Override
		public void interactWith(Merchant other, boolean isCellInteraction) {
			if(!isCellInteraction && state == State.SAY_YES) {				
				if(coinCount > 0) {
					other.buy();
					coinCount--;
				}
			}
		}
		/**
		 * Prendre la porte lors d'une interaction de contact.
		 */
		@Override
		public void interactWith(Door other, boolean isCellInteraction) {
			currentDoor = other;
		}
		/**
		 * Lors d'une interaction de contact : collecter la bombe(Explosive) et l'ajouter à son inventaire.
		 * Lors d'une interaction à distance : activer la bombe(Explosive).
		 */
		@Override
		public void interactWith(Explosive other, boolean isCellInteraction) {
			if(isCellInteraction) {
				other.collect();
				inventory.addPocketItem(ICoopItem.EXPLOSIVE, 1);
			}
			else {
				other.activate();
			}
		}
		/**
		 * Tentative de collecte d'un Orb, et si elle réussi, retirer la vulnérabilité(Vulnerability) associée à cette Orb.
		 */
		@Override
		public void interactWith(Orb other, boolean isCellInteraction) {
			if(other.collect(element)) {
				switch(other.element()) {
					 case ElementalType.FIRE: vulnerabilities.remove(Vulnerability.FIRE); break;
					 case ElementalType.WATER: vulnerabilities.remove(Vulnerability.WATER); break;
				}
			}
		}
		/**
		 * Tentative de collecte d'un ElementalStaff, et si elle réussi, l'ajouter à son inventaire.
		 */
		@Override
		public void interactWith(ElementalStaff other, boolean isCellInteraction) {
			if(other.collect(element)) {
				switch(other.element()) {
					case ElementalType.FIRE : inventory.addPocketItem(ICoopItem.FIRESTAFF, 1); break;
					case ElementalType.WATER : 	inventory.addPocketItem(ICoopItem.WATERSTAFF, 1); break;
				}
			}
		}
		/**
		 * Tentative de collecte d'une ElementalKey, et si elle réussi, l'ajouter à son inventaire.
		 */
		@Override
		public void interactWith(ElementalKey other, boolean isCellInteraction) {
			if(other.collect(element)) {
				switch(other.element()) {
					case ElementalType.FIRE : inventory.addPocketItem(ICoopItem.FIREKEY, 1); break;
					case ElementalType.WATER : 	inventory.addPocketItem(ICoopItem.WATERKEY, 1); break;
				}		
			}
		}
		/**
		 * Collecter un Heart et restaurer un point de vie à l'ICoopPlayer.
		 */
		@Override
		public void interactWith(Heart other, boolean isCellInteraction) {
			other.collect();
			hp.increase(1);
		}
		/**
		 * Appuyer sur une PressurePlate lors d'une interaction de contact.
		 */
		@Override
		public void interactWith(PressurePlate other, boolean isCellInteraction) {
			if(isCellInteraction)
				other.press(ICoopPlayer.this);
		}
		/**
		 * Tentative d'attaque physique(Vulnerability.PHYSICAL) si l'épée est équipée lors d'une interaction à distance.
		 */
		@Override
		public void interactWith(Foe other, boolean isCellInteraction) {
			if(!isCellInteraction) {
				if(state == State.SWORD_ATTACK)
					other.hit(Vulnerability.PHYSICAL, 1);
			}
		}
		/**
		 * Tentative d'attaque physique(Vulnerability.PHYSICAL) si l'épée est équipée lors d'une interaction à distance.
		 */
		@Override
		public void interactWith(HellSkull other, boolean isCellInteraction) {
			if(!isCellInteraction) {
				if(state == State.SWORD_ATTACK)
					other.hit(Vulnerability.PHYSICAL, 1);
			}
		}
	};
	/**
	 * Redirige la gestion des interactions de l'ICoopPlayer vers son ICoopPlayerInteractionHandler.
	 */
	@Override
	public void interactWith(Interactable other, boolean isCellInteraction) {
		other.acceptInteraction(ICoopPlayerInteractionHandler, isCellInteraction);
	}
	/**
	 * @return (boolean) : true si l'ICoopPlayer est vulnérable à l'attaque et qu'il est hors de son temps d'immunité, false sinon.
	 */
	@Override
	public boolean hit(Vulnerability v, int damage) {
		if(immunityTime <= 0 && vulnerabilities.contains(v)) {
			hp.decrease(damage);
			immunityTime = 24;
			return true;
		}
		return false;
	}
	/**
	 * Remettre le temps d'immunité de l'ICoopPlayer à zéro.
	 */
	public void resetImmunity() {
		immunityTime = 0;
	}
	/**
	 * Remettre la barre de vie de l'ICoopPlayer à sa valeur initiale.
	 */
	public void resetHealth() {
		hp.resetHealth();
	}
	/**
	 * Soigner l'ICoopPlayer.
	 * @param amount : Nombre de points de vie à restaurer.
	 */
	public void heal (int amount) {
		hp.increase(amount);
	}
	/**
	 * @return (int) : Nombre de pièces que l'ICoopPlayer possède.
	 */
	public int getCoinCount() {
		return coinCount;
	}
	@Override
	public ElementalType element() {
		return element;
	}
	@Override
	public boolean possess(InventoryItem item) {
		return inventory.contains(item);
	}
	/**
	 * @return L'ICoopItem en séléction.
	 */
	public ICoopItem getCurrentItem() {
		return inventory.getCurrentItem();
	}
}
