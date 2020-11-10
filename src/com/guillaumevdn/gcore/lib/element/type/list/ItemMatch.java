package com.guillaumevdn.gcore.lib.element.type.list;

import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.lib.item.ItemCheck;

/**
 * @author GuillaumeVDN
 */
public class ItemMatch {

	private ItemStack item;
	private int goal;
	private ItemCheck check;

	public ItemMatch(ItemStack item, int goal, ItemCheck check) {
		this.item = item;
		this.goal = goal;
		this.check = check;
	}

	// get
	public ItemStack getItem() {
		return item;
	}

	public int getGoal() {
		return goal;
	}

	public ItemCheck getCheck() {
		return check;
	}

}
