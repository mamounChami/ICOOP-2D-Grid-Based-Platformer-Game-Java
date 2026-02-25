package ch.epfl.cs107.icoop.area;

import ch.epfl.cs107.icoop.actor.*;
import ch.epfl.cs107.icoop.actor.ElementalEntity.ElementalType;
import ch.epfl.cs107.icoop.handler.DialogHandler;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.And;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.signal.logic.Not;
/**
 * Aire jonchée de pièges et d'ennemis où les joueurs vont devoir coopérer pour avancer et obtenir les bâtons élémentaires.
 */
public class Maze extends ICoopArea {
	/**
	 * Constructeur pour Maze.
	 * @param dialogHandler (DialogHandler) : Gestionnaire de dialogues pour publier les dialogues du labyrinthe.
	 * @param signal        (Logic)         : Signal logique représentant la résolution ou non de l'aire.
	 */
	public Maze(DialogHandler dialogHandler, Logic signal) {
		super(dialogHandler, signal);
	}
	/**
	 * @return (String): La chaîne de caractères "Maze".
	 */
	@Override
	public String getTitle() {
		return "Maze";
	}

	@Override
	protected void createArea() {
        registerActor(new Background(this));
        registerActor(new Foreground(this));
        registerActor(new ElementalWall(this, Orientation.LEFT, new DiscreteCoordinates(4, 35), ElementalType.WATER, Logic.TRUE, "water_wall"));
    	registerActor(new ElementalWall(this, Orientation.LEFT, new DiscreteCoordinates(4, 36), ElementalType.WATER, Logic.TRUE, "water_wall"));
    	PressurePlate topPlate = new PressurePlate(this, Orientation.DOWN, new DiscreteCoordinates(6, 33));
    	Not wallSignal1 = new Not(topPlate);
    	registerActor(topPlate);
    	registerActor(new ElementalWall(this, Orientation.LEFT, new DiscreteCoordinates(6, 35), ElementalType.FIRE, wallSignal1, "fire_wall"));
    	registerActor(new ElementalWall(this, Orientation.LEFT, new DiscreteCoordinates(6, 36), ElementalType.FIRE, wallSignal1, "fire_wall"));
    	registerActor(new ElementalWall(this, Orientation.DOWN, new DiscreteCoordinates(2, 34), ElementalType.FIRE, Logic.TRUE, "fire_wall"));
    	registerActor(new ElementalWall(this, Orientation.DOWN, new DiscreteCoordinates(3, 34), ElementalType.FIRE, Logic.TRUE, "fire_wall"));
    	registerActor(new Explosive(this, Orientation.LEFT, new DiscreteCoordinates(6, 25)));
    	registerActor(new ElementalWall(this, Orientation.DOWN, new DiscreteCoordinates(5, 24), ElementalType.WATER, Logic.TRUE, "water_wall"));
    	registerActor(new ElementalWall(this, Orientation.DOWN, new DiscreteCoordinates(6, 24), ElementalType.WATER, Logic.TRUE, "water_wall"));
    	PressurePlate bottomPlate = new PressurePlate(this, Orientation.DOWN, new DiscreteCoordinates(9, 25));
    	Not wallSignal2 = new Not(bottomPlate);
    	registerActor(bottomPlate);
    	registerActor(new ElementalWall(this, Orientation.DOWN, new DiscreteCoordinates(8, 21), ElementalType.FIRE, wallSignal2, "fire_wall"));
    	// Les Heart à la fin du chemin d'eau.
    	registerActor(new Heart(this, Orientation.LEFT, new DiscreteCoordinates(14, 17)));
    	registerActor(new Heart(this, Orientation.LEFT, new DiscreteCoordinates(14, 19)));
    	registerActor(new Heart(this, Orientation.LEFT, new DiscreteCoordinates(15, 18)));
    	registerActor(new Heart(this, Orientation.LEFT, new DiscreteCoordinates(16, 19)));
    	registerActor(new ElementalWall(this, Orientation.DOWN, new DiscreteCoordinates(8, 4), ElementalType.WATER, Logic.TRUE, "water_wall"));
    	registerActor(new ElementalWall(this, Orientation.DOWN, new DiscreteCoordinates(13, 4), ElementalType.FIRE, Logic.TRUE, "fire_wall"));
    	// Les HellSkull
    	for(int i = 0; i < 10; i += 2) {
    		registerActor(new HellSkull(this, Orientation.RIGHT, new DiscreteCoordinates(12, 25+i)));
    		registerActor(new HellSkull(this, Orientation.RIGHT, new DiscreteCoordinates(10, 26+i)));
    	}
    	// Les BombFoe
    	registerActor(new BombFoe(this, Orientation.DOWN, new DiscreteCoordinates(5, 15)));
    	registerActor(new BombFoe(this, Orientation.DOWN, new DiscreteCoordinates(6, 17)));
    	registerActor(new BombFoe(this, Orientation.DOWN, new DiscreteCoordinates(10, 17)));
    	registerActor(new BombFoe(this, Orientation.DOWN, new DiscreteCoordinates(5, 14)));
    	// Bâtons élémentaires
    	ElementalStaff fireStaff = new ElementalStaff(this, Orientation.UP, new DiscreteCoordinates(13,2), "icoop/staff_fire", ElementalType.FIRE);
    	ElementalStaff waterStaff = new ElementalStaff(this, Orientation.UP, new DiscreteCoordinates(8,2), "icoop/staff_water", ElementalType.WATER);
    	setSignal(new And(fireStaff, waterStaff));
    	registerActor(fireStaff);
    	registerActor(waterStaff);
    	// Porte de sortie vers Arena
    	registerActor(new Door(this, Orientation.LEFT, new DiscreteCoordinates(19, 6), "Arena",
    			new DiscreteCoordinates[] {new DiscreteCoordinates(4, 5), new DiscreteCoordinates(14, 15)}, // Coordonées d'arrivée
    			Logic.TRUE, new DiscreteCoordinates(19, 7)));
    	// Rochers bloquant la porte de sortie
    	registerActor(new Rock(this, Orientation.LEFT, new DiscreteCoordinates(18, 6)));
    	registerActor(new Rock(this, Orientation.LEFT, new DiscreteCoordinates(18, 7)));
		// Les pièces
		registerActor(new Coin(this, Orientation.LEFT, new DiscreteCoordinates(9, 17)));
		registerActor(new Coin(this, Orientation.LEFT, new DiscreteCoordinates(5, 17)));
		registerActor(new Coin(this, Orientation.LEFT, new DiscreteCoordinates(7, 15)));
		registerActor(new Coin(this, Orientation.LEFT, new DiscreteCoordinates(9, 25)));
	}

	@Override
	public DiscreteCoordinates getPlayer1SpawnPosition() {
		return new DiscreteCoordinates(2, 39);
	}

	@Override
	public DiscreteCoordinates getPlayer2SpawnPosition() {
		return new DiscreteCoordinates(3, 39);
	}
}
