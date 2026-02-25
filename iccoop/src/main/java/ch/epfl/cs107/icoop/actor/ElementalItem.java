package ch.epfl.cs107.icoop.actor;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;
/**
 * Classe abstraite représentant une entité élémentaire qui peut être collectée et qui fait office de signal(Logic).
 */
public abstract class ElementalItem extends ICoopCollectable implements ElementalEntity {
	private ElementalType element;
	/**
	 * Constructeur pour un ElementalItem
	 * @param owner       (Area)                : Aire à laquelle l'objet appartient.
	 * @param orientation (Orientation)         : Orientation de l'objet.
	 * @param position    (DiscreteCoordinates) : Coordonnées sur la grille que l'objet doit occuper.
	 * @param element     (ElementalType)       : Type élémentaire auquel l'objet appartient.
	 */
	public ElementalItem(Area owner, Orientation orientation, DiscreteCoordinates position, ElementalType element) {
		super(owner, orientation, position);
		this.element = element;
	}
	/**
	 * Spécialisation de la méthode collect() de ICoopCollectable qui prend en compte les types d'élément.
	 * L'ElementalItem ne se laisse collecter que si l'élément passé en argument est le même que le sien.  
	 * @param otherElement (ElementalType) : Type d'élément de l'autre entité.
	 * @return (boolean) : true si la collecte à lieu, false sinon.
	 */
	public boolean collect(ElementalType otherElement) {
		if(element == otherElement) {
			super.collect();
			return true;
		}
		return false;
	}
	
	@Override
	public ElementalType element() {
		return element;
	}
	/**
	 * L'ElementalItem accepte les interactions.
	 */
	@Override
	public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
		((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
	}
}
