package ch.epfl.cs107.icoop.actor;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Entity;
import ch.epfl.cs107.play.engine.actor.RPGSprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;
/**
 * Plaque de pression qui représente un signal Logic selon que la plaque est pressée ou non.
 */
public class PressurePlate extends AreaEntity implements Logic{
	private RPGSprite sprite;
	private ICoopPlayer pressor;
	/**
	 * Constructeur pour une PressurePlate
	 * @param owner       (Area)                : Aire à laquelle la plaque appartient.
	 * @param orientation (Orientation)         : Orientation de la plaque.
	 * @param position    (DiscreteCoordinates) : Coordonnées sur la grille que la plaque doit occuper.
	 */
	public PressurePlate(Area owner, Orientation orientation, DiscreteCoordinates position) {
		super(owner, orientation, position);
		pressor = null;
		sprite = new RPGSprite("GroundPlateOff", 1, 1, this);
	}
	@Override
	public void update(float deltaTime) {
		if(pressor != null) { // Si une Entity est sur la PressurePlate
			if(!pressor.getCurrentMainCellCoordinates().equals(getCurrentMainCellCoordinates())) // On vérifie que cette Entity est toujours sur la même position que celle de la PressurePlate
				pressor = null; // Et si non, on oublie l'Entity.
		}
		super.update(deltaTime);
	}
	@Override
	public void draw(Canvas canvas) {
		sprite.draw(canvas);
	}
	// PressurePlate implémente Interactable.
	/**
	 * @return (List<DiscreteCoordinates>) : Liste immuable des coordonnées de la seule cellule que la PressurePlate occupe.
	 */
	@Override
	public List<DiscreteCoordinates> getCurrentCells() {
		return Collections.singletonList(getCurrentMainCellCoordinates());
	}
	/**
	 * PressurePlate prend-elle place dans une cellule ?
	 * @return (boolean) : false inconditionnellement.
	 */
	@Override
	public boolean takeCellSpace() {
		return false;
	}
	/**
	 * PressurePlate accepte-t-elle les interactions de contact ?
	 * @return (boolean) : true inconditionnellement.
	 */
	@Override
	public boolean isCellInteractable() {
		return true;
	}
	/**
	 * PressurePlate accepte-t-elle les interactions à distance ?
	 * @return (boolean): false inconditionnellement.
	 */
	@Override
	public boolean isViewInteractable() {
		return false;
	}
	/**
	 * PressurePlacte accepte les interactions.
	 */
	@Override
	public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
		((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);		
	}
	// PressurePlate implémente Logic.
	/**
	* @return (boolean) : true s'il y a une entité pressant sur la PressurePlate, false sinon.
	*/
	@Override
	public boolean isOn() {
		return pressor != null;
	}
	/**
	 * @return (boolean) : true si aucune entité ne presse la PressurePlate, false sinon.
	 */
	@Override
	public boolean isOff() {
		return pressor == null;
	}
	/**
	 * Appuyer sur la PressurePlate avec une entité donnée.
	 * @param e (ICoopPlayer) : Joueur qui veut faire pression sur la PressurePlate.
	 */
	public void press(ICoopPlayer e) {
		pressor = e;
	}
}
