package ch.epfl.cs107.icoop.handler;

import ch.epfl.cs107.play.areagame.handler.Inventory;
import ch.epfl.cs107.play.areagame.handler.InventoryItem;
/**
 * Classe représentant un inventaire pour un jeu ICoop.
 */
public class ICoopInventory extends Inventory {
	private ICoopItem currentItem;
	/**
	 * Constructeur pour un ICoopInventory
	 * @param pocketName (String) : Chaîne de caractères du nom avec lequel la seule poche de l'inventaire sera identifiée.
	 */
	public ICoopInventory(String pocketName) {
		super(pocketName);
		addPocketItem(ICoopItem.SWORD, 1);
		addPocketItem(ICoopItem.EXPLOSIVE, 3);
		currentItem = ICoopItem.SWORD;
	}
	/** 
	 * @return (ICoopItem) : L'objet en cours de séléction.
	 */
	public ICoopItem getCurrentItem() {
		return currentItem;
	}
	@Override
	public boolean removePocketItem(InventoryItem item, int quantity) {
		if(super.removePocketItem(item, quantity)) {			
			if(!contains(item)) { // Si l'objet retiré n'est plus représenté dans l'inventaire,
				selectNextItem(); // alors on passe au suivant.				
			}			
			return true;
		}
		return false;
	}
	/**
	 * Séléctionner le prochain objet dans l'inventaire.
	 */
	public void selectNextItem() {
		// On vérifie si on possède un objet correspondant aux objets qui suivent l'objet courrant dans l'énumérateur ICoopItem.
		for(int i = currentItem.ordinal() + 1; i < ICoopItem.values().length; i++) {			
			if(contains(ICoopItem.values()[i])) { // On possède cet objet dans il devient le nouvel objet courrant.
				currentItem = ICoopItem.values()[i];
				return;
			}
		}
		// On vérifie si on possède un objet correspondant aux objets qui précèdent l'objet courrant dans l'énumérateur ICoopItem, en partant de zéro.
		for(int i = 0; i < currentItem.ordinal(); i++) {			
			if(contains(ICoopItem.values()[i])) { // On possède cet objet dans il devient le nouvel objet courrant.
				currentItem = ICoopItem.values()[i];
				return;
			}
		}
	}
}
