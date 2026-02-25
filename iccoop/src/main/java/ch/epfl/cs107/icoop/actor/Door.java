package ch.epfl.cs107.icoop.actor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;
/**
 * Une porte permettant à un ICoopPlayer de transiter d'une aire à une autre.
 * Peut être active ou non en fonction d'un signal Logic.
 */
public class Door extends AreaEntity implements Logic {
	private String destination;
	private DiscreteCoordinates[] arrival;
	private Logic signal;
	private List<DiscreteCoordinates> otherCoords = new ArrayList<DiscreteCoordinates>();
	private Sprite sprite = null;

	/**
	 * Constructeur pour une Door.
	 * @param owner       (Area)                  : Aire à laquelle la porte appartient.
	 * @param orientation (Orientation)           : Orientation de la porte.
	 * @param position    (DiscreteCoordinates)   : Coordonnées sur la grille que la porte doit occuper.
	 * @param destination (String)                : Chaînes de caractères réprésentant le nom de l'aire de destination.
	 * @param arrival     (DiscreteCoordinates[]) : Tableau des coordonnées sur la grille de l'aire de destination que les ICoopPlayer doivent occuper.
	 * @param signal      (Logic)                 : Signal logique qui régit le status actif ou inactif de la porte.
	 * @param otherCoords (DiscreteCoordinates[]) : Tableau des coordonnées sur la grille
	 */
	public Door(Area owner, Orientation orientation, DiscreteCoordinates position, String destination, DiscreteCoordinates[] arrival, Logic signal, DiscreteCoordinates ... otherCoords) {
		this(owner, orientation, position, destination, arrival, signal);
		for(DiscreteCoordinates c : otherCoords)
			this.otherCoords.add(c);
	}
	/**
	 * Constructeur pour une Door.
	 * @param owner       (Area)                  : Aire à laquelle la porte appartient.
	 * @param orientation (Orientation)           : Orientation de la porte.
	 * @param position    (DiscreteCoordinates)   : Coordonnées sur la grille que la porte doit occuper.
	 * @param destination (String)                : Chaînes de caractères réprésentant le nom de l'aire de destination.
	 * @param arrival     (DiscreteCoordinates[]) : Tableau des coordonnées sur la grille de l'aire de destination que les ICoopPlayer doivent occuper.
	 * @param signal      (Logic)                 : Signal logique qui régit le status actif ou inactif de la porte.
	 * @param spriteName  (String)                : Chaîne de caractères identifiant la ressource pour la construction du Sprite de la porte.
	 */
	public Door(Area owner, Orientation orientation, DiscreteCoordinates position, String destination, DiscreteCoordinates[] arrival, Logic signal, String spriteName) {
		super(owner, orientation, position);
		this.destination = destination;
		this.arrival = arrival;
		this.signal = signal;
		sprite = new Sprite(spriteName, 1, 1, this, new RegionOfInterest(0, 0, 32, 32));
		this.otherCoords.add(position);
	}
	/**
	 * Constructeur pour une Door.
	 * @param owner       (Area)                  : Aire à laquelle la porte appartient.
	 * @param orientation (Orientation)           : Orientation de la porte.
	 * @param position    (DiscreteCoordinates)   : Coordonnées sur la grille que la porte doit occuper.
	 * @param destination (String)                : Chaînes de caractères réprésentant le nom de l'aire de destination.
	 * @param arrival     (DiscreteCoordinates[]) : Tableau des coordonnées sur la grille de l'aire de destination que les ICoopPlayer doivent occuper.
	 * @param signal      (Logic)                 : Signal logique qui régit le status actif ou inactif de la porte.
	 */
	public Door(Area owner, Orientation orientation, DiscreteCoordinates position, String destination, DiscreteCoordinates[] arrival, Logic signal) {
		super(owner, orientation, position);
		this.destination = destination;
		this.arrival = arrival;
		this.signal = signal;
		this.otherCoords.add(position);
	}
	// Door implémente Actor
	/**
	 * Door se dessine si elle dispose d'un Sprite et quelle est active.
	 */
	@Override
	public void draw(Canvas canvas) {
		if(sprite != null && isOn())
			sprite.draw(canvas);
	}
	// Door implémente Interactable.
	/**
	 * Door peut occuper plusieurs cellules.
	 * @return (List<DiscreteCoordinates>) : Liste immuable des coordonnées que Door occupe.
	 */
	@Override
	public List<DiscreteCoordinates> getCurrentCells() {
		return Collections.unmodifiableList(otherCoords);
	}
	/**
	 * Door prend-elle dans place une cellule ?
	 * @return (boolean) : false inconditionnellement.
	 */
	@Override
	public boolean takeCellSpace() {
		return false;
	}
	/**
	 * Door accepte-t-elle les interactions de contact.
	 * @return (boolean) : true si elle est active, false sinon.
	 */
	@Override
	public boolean isCellInteractable() {
		return isOn();
	}
	/**
	 * Door accepte-t-elle les interactions à distance.
	 * @return (boolean) : false inconditionnellement.
	 */
	@Override
	public boolean isViewInteractable() {
		return false;
	}
	/**
	 * Door accepte les interactions.
	 */
	@Override
	public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
		((ICoopInteractionVisitor) v).interactWith(this, isCellInteraction);
	}
	/**
	 * @return (String) : Chaîne de caractères représentant le nom de l'aire de destination.
	 */
	public String getDestination() {
		return destination;
	}
	/** 
	 * @return (DiscreteCoordinates[]) : Tableau des coordonnées sur la grille de l'aire de destination que les ICoopPlayer doivent occuper.
	 */
	public DiscreteCoordinates[] getArrival() {
		return arrival;
	}
	
	// Door implémente Logic.
	/**
	 * @return (boolean) : true si le signal Logic associé à Door est actif, false sinon.
	 */
	@Override
	public boolean isOn() {
		return signal.isOn();
	}
	/**
	 * @return (boolean) : true si le signal Logic associé à Door est inactif, false sinon.
	 */
	@Override
	public boolean isOff() {
		return signal.isOff();
	}
	/**
	 * Assigner un nouveau signal(Logic) à Door.
	 * @param signal (Logic) : Le signal à assigner.
	 */
	protected void setSignal(Logic signal) {
		this.signal = signal;
	}
	/**
	 * @return (Logic) : Le signal logique associé à Door.
	 */
	protected Logic getSignal() {
		return signal;
	}
}
