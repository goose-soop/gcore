package com.guillaumevdn.gcore.lib.tuple;

import java.util.List;

import org.bukkit.inventory.ItemStack;

/**
 * @author GuillaumeVDN
 */
public class GUIItemTriple extends Triple<ItemStack, Boolean, List<IntegerPair>> {

	public GUIItemTriple() {
		super();
	}

	public GUIItemTriple(ItemStack item, boolean persistent, List<IntegerPair> locations) {
		super(item, persistent, locations);
	}

}
