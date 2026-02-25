package ch.epfl.cs107.icoop.actor;

import java.util.Collections;
import java.util.List;

import ch.epfl.cs107.icoop.area.ICoopArea;
import ch.epfl.cs107.icoop.handler.ICoopInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Dialog;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;
/**
 * Porte du manoir.
 */
public class ManorDoor extends Door implements Interactor {
	private ICoopPlayer player; // Joueur auquel on montre une boîte de dialogue.
	/**
	 * Constructeur pour une ManorDoor
	 * @param owner       (Area)                  : Aire à laquelle la porte du manoir appartient.
	 * @param orientation (Orientation)           : Orientation de la porte du manoir.
	 * @param position    (DiscreteCoordinates)   : Coordonnées sur la grille que la porte du manoir doit occuper.
	 * @param destination (String)                : Chaînes de caractères réprésentant le nom de l'aire de destination.
	 * @param arrival     (DiscreteCoordinates[]) : Tableau des coordonnées sur la grille de l'aire de destination que les ICoopPlayer doivent occuper.
	 * @param signal      (Logic)                 : Signal logique qui régit le status actif ou inactif de la porte.
	 */
	public ManorDoor(Area owner, Orientation orientation, DiscreteCoordinates position, String destination,
			DiscreteCoordinates[] arrival, Logic signal) {
		super(owner, orientation, position, destination, arrival, signal);
	}
	@Override
	public void update(float deltaTime) {
		if(player != null) {
			if(!player.getCurrentMainCellCoordinates().equals(getCurrentMainCellCoordinates()))
				player = null;
		}
	}
	/**
	 * Le champ de vision de la ManorDoor ne comprend aucune cellule.
	 * @return (List<DiscreteCoordinates>) : Liste immuable vide.
	 */
	@Override
	public List<DiscreteCoordinates> getFieldOfViewCells() {
		return Collections.<DiscreteCoordinates> emptyList();
	}
	/**
	 * ManorDoor est-elle demandeuse d'interactions de contact ?
	 * @return (boolean) : true inconditionnellement.
	 */
	@Override
	public boolean wantsCellInteraction() {
		return true;
	}
	/**
	 * ManorDoor est-elle demandeuse d'interactions à distance ?
	 * @return (boolean) : false inconditionnellement.
	 */
	@Override
	public boolean wantsViewInteraction() {
		return false;
	}
	private ICoopInteractionVisitor handler = new ICoopInteractionVisitor() {
		/**
		 * Affiche un dialogue différent selon l'état d'ouverture de la porte lors d'une interaction de contact. 
		 */
		@Override
		public void interactWith(ICoopPlayer other, boolean isCellInteraction) {
			if(player == null) {
				player = other;
				if(isOn()) 
					((ICoopArea)getOwnerArea()).getDialogHandler().publish(new Dialog("get_ready"));
				else
					((ICoopArea)getOwnerArea()).getDialogHandler().publish(new Dialog("key_required"));
			}
		}
	};
	/**
	 * Redirige la gestion des interactions de la ManoDoor à son handler de type ICoopInteractionVisitor.
	 */
	@Override
	public void interactWith(Interactable other, boolean isCellInteraction) {
		other.acceptInteraction(handler, isCellInteraction);
	}
}
