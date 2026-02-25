package ch.epfl.cs107.icoop.actor;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;
/**
 * Obstacle bloquant le chemin et ne pouvant pas être détruit.
 */
public class Obstacle extends AreaEntity {
	private Sprite sprite;
	/**
	 * Constructeur pour un Obstacle
	 * @param area       (Area)                : Aire à laquelle l'obstacle appartient.
	 * @param orientation (Orientation)         : Orientation de l'obstacle.
	 * @param position    (DiscreteCoordinates) : Coordonnées sur la grille que l'obstacle doit occuper.
	 */
	public Obstacle(Area area, Orientation orientation, DiscreteCoordinates position) {
		super(area, orientation, position);
		sprite = new Sprite("rock.2", 1f, 1f, this);
	}
	/**
	 * Constructeur optionnel pour changer l'apparence par défault de l'obstacle.
	 * @param spriteName (String) : Chaîne de caractères identifiant la ressource pour la construction du Sprite de l'obstacle. 
	 */
	public Obstacle(Area area, Orientation orientation, DiscreteCoordinates position, String spriteName) {
		super(area, orientation, position);
		sprite = new Sprite(spriteName, 1f, 1f, this);
	}
	@Override
	public void draw(Canvas canvas) {
		sprite.draw(canvas);
	}
	/**
	 * @return (List<DiscreteCoordinates>) : Liste immuable des coordonnées de la seule cellule que l'Obstacle occupe.
	 */
	@Override
	public List<DiscreteCoordinates> getCurrentCells() {
		return Collections.singletonList(getCurrentMainCellCoordinates());
	}
	/**
	 * L'Obstacle prend-il place dans une cellule ?
	 * @return (boolean) : true inconditionnellement.
	 */
	@Override
	public boolean takeCellSpace() {
		return true;
	}
	/**
	 * L'Obstacle accepte-t-il les interactions de contact ?
	 * @return (boolean) : true inconditionnellement.
	 */
	@Override
	public boolean isCellInteractable() {
		return true;
	}
	/**
	 * L'Obstacle accepte-t-il les interactions à disctance ?
	 * @return (boolean) : true inconditionnellement.
	 */
	@Override
	public boolean isViewInteractable() {
		return true;
	}
	/**
	 * L'Obstacle accepte les interactions.
	 */
	@Override
	public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
		((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
	}

}
