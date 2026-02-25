package ch.epfl.cs107.icoop.actor;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.play.areagame.actor.CollectableAreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;
/**
 * Classe abstraite représentant un collectable.
 */
public abstract class ICoopCollectable extends CollectableAreaEntity implements Logic {
	/**
	 * Constructeur pour un ICoopCollectable
	 * @param area       (Area)                : Aire à laquelle le collectable appartient.
	 * @param orientation (Orientation)         : Orientation de le collectable.
	 * @param position    (DiscreteCoordinates) : Coordonnées sur la grille que le collectable doit occuper.
	 */
	public ICoopCollectable(Area area, Orientation orientation, DiscreteCoordinates position) {
		super(area, orientation, position);
	}
	// L'ICoopCollectable étend CollectableAreaEntity.
	/**
	 * L'ICoopCollectable disparaît de son aire après sa collecte.
	 */
	@Override
    public void collect() {
    	super.collect();
    	getOwnerArea().unregisterActor(this);
    }
	// L'ICoopCollectable implémente Logic.
	/**
	 * @return (boolean) : true si l'objet a été collecté, false sinon.
	 */
	@Override
	public boolean isOn() {
		return isCollected();
	}
	/**
	 * @return (boolean) : true si l'objet n'a pas été collecté, false sinon.
	 */
	@Override
	public boolean isOff() {
		return !isCollected();
	}
	// L'ICoopCollectable implémente Interactable.
	/**
	 * @return (List<DiscreteCoordinates>) : Liste immuable des coordonnées de la seule cellule que l'ICoopCollectable occupe.
	 */
	@Override
	public List<DiscreteCoordinates> getCurrentCells() {
		return Collections.singletonList(getCurrentMainCellCoordinates());
	}
	/**
	 * L'ICoopCollectable prend-il place dans une cellule ?
	 * @return (boolean) : false inconditionnellement.
	 */
	@Override
	public boolean takeCellSpace() {
		return false;
	}
	/**
	 * L'ICoopCollectable accepte-t-il les interactions de contact.
	 * @return (boolean) : true inconditionnellement.
	 */
	@Override
	public boolean isCellInteractable() {
		return true;
	}
	/**
	 * L'ICoopCollectable accepte-t-il les interactions à distance.
	 * @return (boolean) : false inconditionnellement.
	 */
	@Override
	public boolean isViewInteractable() {
		return false;
	}
}
