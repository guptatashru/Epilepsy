package com.hp.epilepsy.widget.model;

/**
 * The Class DrawerMenuItem.
 *
 * @author Said Gamal
 */
public class DrawerMenuItem {

	/** The Item name. */
	private String ItemName = "";

	/**
	 * Instantiates a new drawer menu item.
	 *
	 * @param itemName the item name
	 */
	public DrawerMenuItem(String itemName) {
		super();
		ItemName = itemName;
	}

	/**
	 * Gets the item name.
	 *
	 * @return the item name
	 */
	public String getItemName() {
		return ItemName;
	}

	/**
	 * Sets the item name.
	 *
	 * @param itemName the new item name
	 */
	public void setItemName(String itemName) {
		ItemName = itemName;
	}
}
