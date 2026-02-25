package ch.epfl.cs107.icoop.handler;

import ch.epfl.cs107.icoop.actor.Coin;
import ch.epfl.cs107.icoop.actor.Door;
import ch.epfl.cs107.icoop.actor.ElementalKey;
import ch.epfl.cs107.icoop.actor.ElementalStaff;
import ch.epfl.cs107.icoop.actor.ElementalWall;
import ch.epfl.cs107.icoop.actor.Explosive;
import ch.epfl.cs107.icoop.actor.Foe;
import ch.epfl.cs107.icoop.actor.Heart;
import ch.epfl.cs107.icoop.actor.HellSkull;
import ch.epfl.cs107.icoop.actor.ICoopPlayer;
import ch.epfl.cs107.icoop.actor.ManorDoor;
import ch.epfl.cs107.icoop.actor.Merchant;
import ch.epfl.cs107.icoop.actor.Obstacle;
import ch.epfl.cs107.icoop.actor.Orb;
import ch.epfl.cs107.icoop.actor.PressurePlate;
import ch.epfl.cs107.icoop.actor.Rock;
import ch.epfl.cs107.icoop.area.ICoopBehavior.ICoopCell;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;

/**
 * InteractionVisitor for the ICoop entities
 */

public interface ICoopInteractionVisitor extends AreaInteractionVisitor {
	/**
	 * Interaction avec une cellule. Sans utilité particulière. Par défaut, ne rien faire.
	 * @param other (ICoopCell) : Cellule avec laquelle on interagit.
	 * @param isCellInteraction (boolean) : true si c'est une interaction de contact, false sinon.
	 */
    default public void interactWith(ICoopCell other, boolean isCellInteraction) {}
    /**
	 * Interaction avec un joueur. Par défaut, ne rien faire.
	 * @param other (ICoopPlayer) : Joueur avec lequel on interagit.
	 * @param isCellInteraction (boolean) : true si c'est une interaction de contact, false sinon.
	 */
    default public void interactWith(ICoopPlayer other, boolean isCellInteraction) {}
    /**
	 * Interaction avec une porte. Par défaut, ne rien faire.
	 * @param other (Door) : Porte avec laquelle on interagit.
	 * @param isCellInteraction (boolean) : true si c'est une interaction de contact, false sinon.
	 */
    default public void interactWith(Door other, boolean isCellInteraction) {}
    /**
	 * Interaction avec la porte du Manoir. Par défaut, ne rien faire.
	 * @param other (ManorDoor) : Porte avec laquelle on interagit.
	 * @param isCellInteraction (boolean) : true si c'est une interaction de contact, false sinon.
	 */
    default public void interactWith(ManorDoor other, boolean isCellInteraction) {}
    /**
	 * Interaction avec une bombe. Par défaut, ne rien faire.
	 * @param other (Explosive) : Bombe avec laquelle on interagit.
	 * @param isCellInteraction (boolean) : true si c'est une interaction de contact, false sinon.
	 */
    default public void interactWith(Explosive other, boolean isCellInteraction) {}
    /**
	 * Interaction avec un rocher. Par défaut, ne rien faire.
	 * @param other (Rock) : Rocher avec lequel on interagit.
	 * @param isCellInteraction (boolean) : true si c'est une interaction de contact, false sinon.
	 */
    default public void interactWith(Rock other, boolean isCellInteraction) {}
    /**
	 * Interaction avec un orb. Par défaut, ne rien faire.
	 * @param other (Orb) : Orb avec lequel on interagit.
	 * @param isCellInteraction (boolean) : true si c'est une interaction de contact, false sinon.
	 */
    default public void interactWith(Orb other, boolean isCellInteraction) {}
    /**
	 * Interaction avec un coeur. Par défaut, ne rien faire.
	 * @param other (Heart) : Coeur avec lequel on interagit.
	 * @param isCellInteraction (boolean) : true si c'est une interaction de contact, false sinon.
	 */
    default public void interactWith(Heart other, boolean isCellInteraction) {}
    /**
	 * Interaction avec un bâton élémentaire. Par défaut, ne rien faire.
	 * @param other (ElementalStaff) : Bâton élémentaire avec lequel on interagit.
	 * @param isCellInteraction (boolean) : true si c'est une interaction de contact, false sinon.
	 */
    default public void interactWith(ElementalStaff other, boolean isCellInteraction) {}
	/**
	 * Interaction avec une plaque de pression. Par défaut, ne rien faire.
	 * @param other (PressurePlate) : Plaque de pression avec laquelle on interagit.
	 * @param isCellInteraction (boolean) : true si c'est une interaction de contact, false sinon.
	 */
    default public void interactWith(PressurePlate other, boolean isCellInteraction) {}
    /**
	 * Interaction avec un mur élémentaire. Par défaut, ne rien faire.
	 * @param other (ElementalWall) : Mur élémentaire avec lequel on interagit.
	 * @param isCellInteraction (boolean) : true si c'est une interaction de contact, false sinon.
	 */
    default public void interactWith(ElementalWall other, boolean isCellInteraction) {}
    /**
	 * Interaction avec un ennemi. Par défaut, ne rien faire.
	 * @param other (Foe) : Ennemi avec lequel on interagit.
	 * @param isCellInteraction (boolean) : true si c'est une interaction de contact, false sinon.
	 */
    default public void interactWith(Foe other, boolean isCellInteraction) {}
    /**
	 * Interaction avec un obstacle. Par défaut, ne rien faire.
	 * @param other (Obstacle) : Obstacle avec lequel on interagit.
	 * @param isCellInteraction (boolean) : true si c'est une interaction de contact, false sinon.
	 */
	default public void interactWith(Obstacle other, boolean isCellInteraction) {}
	/**
	 * Interaction avec une clé élémentaire. Par défaut, ne rien faire.
	 * @param other (ElementalKey) : Clé élémentaire avec laquelle on interagit.
	 * @param isCellInteraction (boolean) : true si c'est une interaction de contact, false sinon.
	 */
	default public void interactWith(ElementalKey other, boolean isCellInteraction) {}
	/**
	 * Interaction avec un crâne lanceur de feu. Par défaut, ne rien faire.
	 * @param other (BombFoe) : Crâne lanceur de feu avec lequel on interagit.
	 * @param isCellInteraction (boolean) : true si c'est une interaction de contact, false sinon.
	 */
	default public void interactWith(HellSkull other, boolean isCellInteraction) {}
	/**
	 * Interaction avec une pièce. Par défaut, ne rien faire.
	 * @param other (Coin) : Pièce avec laquelle on interagit.
	 * @param isCellInteraction (boolean) : true si c'est une interaction de contact, false sinon.
	 */
	default public void interactWith(Coin other, boolean isCellInteraction) {}
	/**
	 * Interaction avec un marchand. Par défaut, ne rien faire.
	 * @param other (Merchant) : Marchand avec lequel on interagit.
	 * @param isCellInteraction (boolean) : true si c'est une interaction de contact, false sinon.
	 */
	default public void interactWith(Merchant other, boolean isCellInteraction) {}
}
