package com.guillaumevdn.gcore.lib.cost;

import org.bukkit.inventory.ItemStack;

/**
 * @author GuillaumeVDN
 */
public class CostItem {
	
	private ItemStack item;
	private int amount;
	private String displayName;

	public CostItem(ItemStack item, String displayName) {
		this.amount = item.getAmount();
		(this.item = item.clone()).setAmount(1);
		this.displayName = displayName;
	}

	// get
	public ItemStack getItem() {
		return item;
	}

	public int getAmount() {
		return amount;
	}

	public String getDisplayName() {
		return displayName;
	}
	
	// set
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	public void alterAmount(int delta) {
		this.amount -= delta;
	}

}
