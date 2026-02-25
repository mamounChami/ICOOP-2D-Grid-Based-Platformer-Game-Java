package ch.epfl.cs107.icoop.area;

import ch.epfl.cs107.icoop.actor.DarkLord;
import ch.epfl.cs107.icoop.actor.Obstacle;
import ch.epfl.cs107.icoop.handler.DialogHandler;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Dialog;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.signal.logic.Not;
/**
 * Dernière aire du jeu où on affronte le seigneur sombre(DarkLord).
 */
public class Sanctum extends ICoopArea {
	private boolean firstUpdate = true;
	private boolean win = false;
	/**
	 * Constructeur pour Sanctum.
	 * @param dialogHandler (DialogHandler) : Gestionnaire de dialogues pour publier les dialogues du sanctuaire.
	 * @param signal        (Logic)         : Signal logique représentant la résolution ou non de l'aire.
	 */
	public Sanctum(DialogHandler dialogHandler, Logic signal) {
		super(dialogHandler, signal);
	}
	/**
	* @return (String) : La chaîne de caractères "Sanctum".
	*/
	@Override
	public String getTitle() {
		return "Sanctum";
	}

	@Override
	protected void createArea() {
		registerActor(new Background(this));
		// On bloque la zone devant la porte pour empêcher les joueurs d'y rester pour attaquer le DarkLord. 
		registerActor(new Obstacle(this, Orientation.UP, new DiscreteCoordinates(7, 2)));
		registerActor(new Obstacle(this, Orientation.UP, new DiscreteCoordinates(8, 2)));
		DarkLord darkLord = new DarkLord(this, Orientation.DOWN, new DiscreteCoordinates(7, 11));
		registerActor(darkLord);
		// La mort du DarkLord équivaut à la réussite de l'aire.
		setSignal(new Not(darkLord));
	}
		
	@Override
	public DiscreteCoordinates getPlayer1SpawnPosition() {
		return new DiscreteCoordinates(7, 3);
	}

	@Override
	public DiscreteCoordinates getPlayer2SpawnPosition() {
		return new DiscreteCoordinates(8, 3);
	}
	
	@Override 
	public void update(float deltaTime) {
		if(firstUpdate) { // On ne publie le dialogue de mise en garde qu'à la première mise à jour de l'aire.
			getDialogHandler().publish(new Dialog("get_ready"));
			firstUpdate = false;
		}
		if(!win) { // Tant qu'on a pas gagné
			if(isOn()) { // Si le DarkLord est vaincu 
				win = true;
				getDialogHandler().publish(new Dialog("victory"));
			}
		}
		super.update(deltaTime);
	}
}
