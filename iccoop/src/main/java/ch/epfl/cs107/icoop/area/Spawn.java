package ch.epfl.cs107.icoop.area;

import ch.epfl.cs107.icoop.actor.Coin;
import ch.epfl.cs107.icoop.actor.Door;
import ch.epfl.cs107.icoop.actor.Explosive;
import ch.epfl.cs107.icoop.actor.ManorDoor;
import ch.epfl.cs107.icoop.actor.Merchant;
import ch.epfl.cs107.icoop.actor.Rock;
import ch.epfl.cs107.icoop.handler.DialogHandler;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Dialog;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;
/**
 * Aire initiale du jeu.
 */
public class Spawn extends ICoopArea {
	private boolean firstUpdate = true;
	/**
	 * Constructeur pour Spawn.
	 * @param dialogHandler (DialogHandler) : Gestionnaire de dialogues pour publier les dialogues de l'aire de démarrage.
	 * @param signal        (Logic)         : Signal logique représentant la résolution ou non de l'aire.
	 */
	public Spawn(DialogHandler dialogHandler, Logic signal) {
		super(dialogHandler, signal);
	}

	/**
	 * @return (String) : La chaîne de caractères "Spawn".
	 */
	@Override
	public String getTitle() {
		return "Spawn";
	}

	@Override
	protected void createArea() {
        // Habillage de l'aire
        registerActor(new Background(this));
        registerActor(new Foreground(this));
        // Zones accessibles à partir de Spawn
		registerActor(new Door(this, Orientation.LEFT, new DiscreteCoordinates(19, 15), "OrbWay", new DiscreteCoordinates[]{new DiscreteCoordinates(1, 12), new DiscreteCoordinates(1, 5)}, Logic.TRUE, new DiscreteCoordinates(19, 16)));
        registerActor(new Door(this, Orientation.LEFT, new DiscreteCoordinates(4, 0), "Maze", new DiscreteCoordinates[]{new DiscreteCoordinates(2, 39), new DiscreteCoordinates(3, 39)}, Logic.TRUE, new DiscreteCoordinates(5, 0)));
        registerActor(new ManorDoor(this, Orientation.DOWN, new DiscreteCoordinates(6, 11), "Sanctum", new DiscreteCoordinates[]{new DiscreteCoordinates(7, 3), new DiscreteCoordinates(8, 3)}, getSignal()));		// La porte du manoir ne s'ouvre que si l'aire est résolue.
		// Elements constituant l'aire
		registerActor(new Rock(this, Orientation.LEFT, new DiscreteCoordinates(10, 10)));
        registerActor(new Explosive(this, Orientation.LEFT, new DiscreteCoordinates(11, 10)));
        registerActor(new Coin(this, Orientation.LEFT, new DiscreteCoordinates(18, 16)));
        registerActor(new Coin(this, Orientation.LEFT, new DiscreteCoordinates(17, 16)));
        registerActor(new Merchant(this, Orientation.DOWN, new DiscreteCoordinates(3, 8)));
	}
	public DiscreteCoordinates getPlayer1SpawnPosition() {
		return new DiscreteCoordinates(13, 6);
	}
	public DiscreteCoordinates getPlayer2SpawnPosition() {
		return new DiscreteCoordinates(14, 6);
	}
	
	@Override
	public void update(float deltaTime) {
		if(firstUpdate) { // On ne publie le dialogue de bienvenue qu'à la première mise à jour de l'aire.
			getDialogHandler().publish(new Dialog("welcome"));
			firstUpdate = false;
		}
		super.update(deltaTime);
	}
}
