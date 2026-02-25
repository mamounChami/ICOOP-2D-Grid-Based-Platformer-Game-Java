package ch.epfl.cs107.icoop.area;

import ch.epfl.cs107.icoop.actor.Door;
import ch.epfl.cs107.icoop.actor.ElementalEntity.ElementalType;
import ch.epfl.cs107.icoop.actor.ElementalKey;
import ch.epfl.cs107.icoop.handler.DialogHandler;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Dialog;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.And;
import ch.epfl.cs107.play.signal.logic.Logic;
/**
 * Aire sans porte de sortie, où il faut que chaque joueur ramasse la clé élémentaire qui lui est associé pour activer un téléporteur qui est la seule issue.
 * Dernier défi avant de pouvoir ouvrir la porte du manoir.
 */
public class Arena extends ICoopArea {
	private boolean firstUpdate = true;
	/**
	 * Constructeur pour Arena.
	 * @param dialogHandler (DialogHandler) : Gestionnaire de dialogues pour publier les dialogues de l'arène.
	 * @param signal        (Logic)         : Signal logique représentant la résolution ou non de l'aire.
	 */
	public Arena(DialogHandler dialogHandler, Logic signal) {
		super(dialogHandler, signal);
	}
	/**
	 * @return (String) : La chaîne de caractères "Arena".
	 */
	@Override
	public String getTitle() {
		return "Arena";
	}
	@Override
	protected void createArea() {
        registerActor(new Background(this));
        registerActor(new Foreground(this));
        ElementalKey fireKey = new ElementalKey(this, Orientation.RIGHT, new DiscreteCoordinates(9, 16), "icoop/key_red", ElementalType.FIRE);
        ElementalKey waterKey = new ElementalKey(this, Orientation.RIGHT, new DiscreteCoordinates(9, 4), "icoop/key_blue", ElementalType.WATER);
        // Le téléporteur s'ouvre si les deux clés élémentaires sont collectées.
        Door teleporter = new Door(this, Orientation.UP, new DiscreteCoordinates(10, 11), "Spawn", new DiscreteCoordinates[] {new DiscreteCoordinates(13, 6), new DiscreteCoordinates(14, 6)}, new And(fireKey, waterKey), "shadow");
        // Arena est résolue si le téléporteur est ouvert.
        setSignal(teleporter);
        registerActor(teleporter);
        registerActor(fireKey);
        registerActor(waterKey);
	}	
	public DiscreteCoordinates getPlayer1SpawnPosition() {
		return new DiscreteCoordinates(4, 5);
	}
	public DiscreteCoordinates getPlayer2SpawnPosition() {
		return new DiscreteCoordinates(14, 15);
	}


	@Override
	public void update(float deltaTime) {
		if(firstUpdate) { // On ne publie le dialogue du défi qu'à la première mise à jour de l'aire.
			getDialogHandler().publish(new Dialog("life_for_life"));
			firstUpdate = false;
		}
		super.update(deltaTime);
	}

}
