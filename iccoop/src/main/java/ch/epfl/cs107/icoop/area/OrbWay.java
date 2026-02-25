package ch.epfl.cs107.icoop.area;

import ch.epfl.cs107.icoop.actor.Door;
import ch.epfl.cs107.icoop.actor.ElementalEntity.ElementalType;
import ch.epfl.cs107.icoop.actor.ElementalWall;
import ch.epfl.cs107.icoop.actor.Heart;
import ch.epfl.cs107.icoop.actor.Orb;
import ch.epfl.cs107.icoop.actor.PressurePlate;
import ch.epfl.cs107.icoop.handler.DialogHandler;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.And;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.signal.logic.Not;
/**
 * Aire permettant aux joueurs d'obtenir des orbes élémentaires(Orb).
 */
public class OrbWay extends ICoopArea {
	/**
	 * Constructeur pour OrbWay.
	 * @param dialogHandler (DialogHandler) : Gestionnaire de dialogues pour publier les dialogues de la voie d'orbe.
	 * @param signal        (Logic)         : Signal logique représentant la résolution ou non de l'aire.
	 */
	public OrbWay(DialogHandler dialogHandler, Logic signal) {
		super(dialogHandler, signal);
	}
	/**
	 * @return (String) : La chaîne de caractères "OrbWay".
	 */
	@Override
	public String getTitle() {
		return "OrbWay";
	}

	@Override
	protected void createArea() {
        registerActor(new Background(this));
        registerActor(new Foreground(this));
        registerActor(new Door(this, Orientation.RIGHT, new DiscreteCoordinates(0, 14), "Spawn", new DiscreteCoordinates[]{new DiscreteCoordinates(18, 16), new DiscreteCoordinates(18, 15)}, Logic.TRUE, new DiscreteCoordinates(0, 13), new DiscreteCoordinates(0, 12), new DiscreteCoordinates(0, 11), new DiscreteCoordinates(0, 10)));
        registerActor(new Door(this, Orientation.RIGHT, new DiscreteCoordinates(0, 8), "Spawn", new DiscreteCoordinates[]{new DiscreteCoordinates(18, 16), new DiscreteCoordinates(18, 15)}, Logic.TRUE, new DiscreteCoordinates(0, 7), new DiscreteCoordinates(0, 6), new DiscreteCoordinates(0, 5), new DiscreteCoordinates(0, 4)));
        Orb fireOrb = new Orb(this, Orientation.UP, new DiscreteCoordinates(17, 12), ElementalType.FIRE);
        Orb waterOrb = new Orb(this, Orientation.DOWN, new DiscreteCoordinates(17, 6), ElementalType.WATER);
        setSignal(new And(fireOrb, waterOrb));
		registerActor(fireOrb);
        registerActor(waterOrb);
        PressurePlate topPlate = new PressurePlate(this, Orientation.DOWN, new DiscreteCoordinates(5, 10));
        PressurePlate bottomPlate = new PressurePlate(this, Orientation.DOWN, new DiscreteCoordinates(5, 7));
        Not wallSignal1 = new Not(topPlate);
        Not wallSignal2 = new Not(bottomPlate);
        registerActor(topPlate);
        registerActor(bottomPlate);
        for(int i = 0; i < 5; i++)
        	registerActor(new ElementalWall(this, Orientation.LEFT, new DiscreteCoordinates(12, 10+i), ElementalType.FIRE, wallSignal2, "fire_wall"));
        for(int i = 0; i < 5; i++)
        	registerActor(new ElementalWall(this, Orientation.LEFT, new DiscreteCoordinates(12, 4+i), ElementalType.WATER, wallSignal1, "water_wall"));
    	registerActor(new ElementalWall(this, Orientation.LEFT, new DiscreteCoordinates(7, 6), ElementalType.FIRE, wallSignal2, "fire_wall"));
    	registerActor(new ElementalWall(this, Orientation.LEFT, new DiscreteCoordinates(7, 12), ElementalType.WATER, wallSignal1, "water_wall"));
    	registerActor(new Heart(this, Orientation.LEFT, new DiscreteCoordinates(8, 4)));
    	registerActor(new Heart(this, Orientation.LEFT, new DiscreteCoordinates(10, 6)));
    	registerActor(new Heart(this, Orientation.LEFT, new DiscreteCoordinates(5, 13)));
    	registerActor(new Heart(this, Orientation.LEFT, new DiscreteCoordinates(10, 11)));
	}

	@Override
	public DiscreteCoordinates getPlayer1SpawnPosition() {
		return new DiscreteCoordinates(1, 12);
	}
	public DiscreteCoordinates getPlayer2SpawnPosition() {
		return new DiscreteCoordinates(1, 5);
	}
}
