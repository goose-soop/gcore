package com.guillaumevdn.gcore.lib.tuple;

import org.bukkit.inventory.ItemStack;

/**
 * @author GuillaumeVDN
 */
public class ItemChancePair extends Pair<ItemStack, Integer> {

	private ItemChancePair() {
		super();
	}

	private ItemChancePair(ItemStack a, Integer b) {
		super(a, b);
	}

	// ----- static
	public static ItemChancePair of(ItemStack a, Integer b) {
		return new ItemChancePair(a, b);
	}

	public static ItemChancePair empty() {
		return new ItemChancePair();
	}

}
