package ch.epfl.cs107.icoop.handler;

import ch.epfl.cs107.play.areagame.handler.InventoryItem;
/**
 * Énumérateur des objets d'inventaire pour un jeu ICoop.
 */
public enum ICoopItem implements InventoryItem {
	SWORD("Sword", "icoop/sword.icon"),
	FIREKEY("FireKey", "icoop/key_red"),
	WATERKEY("WaterKey", "icoop/key_blue"),
	FIRESTAFF("FireStaff", "icoop/staff_fire.icon"),
	WATERSTAFF("WaterStaff", "icoop/staff_water.icon"),
	EXPLOSIVE("Explosive", "icoop/explosive")
	;
	final String name;
	final String sprite;
	ICoopItem(String name, String spriteName) {
		this.name = name;
		this.sprite = spriteName;
	}
	@Override
	public int getPocketId() {
		return 0;
	}

	@Override
	public String getName() {
		return this.name;
	}

}
