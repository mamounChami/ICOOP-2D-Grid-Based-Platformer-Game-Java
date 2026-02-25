package ch.epfl.cs107.icoop.actor;

import java.util.Collections;	
import java.util.List;

import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
/**
 * Classe abstraite réprésentant un projectile.
 */
public abstract class Unstoppable extends MovableAreaEntity implements Interactor {
	private static final int MOVE_DURATION = 4;
	private int distance;
	private int speed; 
	/**
	 * Constructeur pour un Unstoppable
	 * @param owner       (Area)                : Aire à laquelle le projectile appartient.
	 * @param orientation (Orientation)         : Orientation de le projectile.
	 * @param position    (DiscreteCoordinates) : Coordonnées sur la grille que le projectile doit occuper.
	 * @param distance    (int)                 : Distance maximale que le projectile peut parcourir, en nombre de cellules.
	 * @param speed       (int)                 : Vitesse à laquelle le projectile doit se déplacer.
	 */
	public Unstoppable(Area owner, Orientation orientation, DiscreteCoordinates position, int distance, int speed) {
		super(owner, orientation, position);
		this.speed = speed;
		this.distance = distance;
	}
	@Override
	public void update(float deltaTime) {
		if(distance > 0) {
			if(!isDisplacementOccurs()) {
				if(getOwnerArea().canEnterAreaCells(this, Collections.singletonList(getCurrentMainCellCoordinates().jump(getOrientation().toVector())))) {
					distance--;
					move(MOVE_DURATION/speed);
				}
				else
					getOwnerArea().unregisterActor(this);
			}
		}
		else
			getOwnerArea().unregisterActor(this);
		super.update(deltaTime);
	}
	/**
	 * L'Unstoppable prend-il place dans une cellule ?
	 * @return (boolean) : false inconditionnellement.
	 */
	@Override
	public boolean takeCellSpace() {
		return false;
	}
	/**
	 * L'Unstoppable accepte-t-il les interactions de contact ?
	 * @return (boolean) : false inconditionnellement.
	 */
	@Override
	public boolean isCellInteractable() {
		return false;
	}
	/**
	 * L'Unstoppable accepte-t-il les interactions à distance ?
	 * @return (boolean) : false inconditionnellement.
	 */
	@Override
	public boolean isViewInteractable() {
		return false;
	}
	/**
	 * L'Unstoppable est-il demandeur d'interactions de contact ?
	 * @return (boolean) : true inconditionnellement.
	 */
	@Override
	public boolean wantsCellInteraction() {
		return true;
	}
}
