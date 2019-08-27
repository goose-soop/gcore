package com.guillaumevdn.gcore.lib.gui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.util.Pair;

public class InventoryState {

	// static base
	public static final Integer OTHER_SLOTS_DIFF = -10;

	// base
	private Map<Integer, ItemStack> items = new HashMap<Integer, ItemStack>();

	public InventoryState(Player player, Inventory otherInventory, int... otherSlots) {
		// save player items
		for (int slot = 0; slot < player.getInventory().getContents().length; ++slot) {
			ItemStack item = player.getInventory().getContents()[slot];
			if (!Mat.fromItem(item).isAir()) {
				items.put(slot, item.clone());
			}
		}
		// save player cursor
		ItemStack cursor = !Mat.fromItem(player.getItemOnCursor()).isAir() ? player.getItemOnCursor().clone() : null;
		if (cursor != null) {
			items.put(-1, cursor);
		}
		// save other inventory items
		for (int slot : otherSlots) {
			ItemStack item = otherInventory.getItem(slot);
			if (!Mat.fromItem(item).isAir()) {
				items.put(-slot + OTHER_SLOTS_DIFF, item.clone());
			}
		}
	}

	// get
	public Map<Integer, ItemStack> getItems() {
		return items;
	}

	// compare
	public Pair<Map<Integer, ItemStack>, Map<Integer, ItemStack>> getChanges(InventoryState newState) {
		// get all slots
		Set<Integer> slots = new HashSet<Integer>();
		for (int slot : items.keySet()) slots.add(slot);
		for (int slot : newState.items.keySet()) slots.add(slot);
		// check for changes
		Map<Integer, ItemStack> removed = new HashMap<Integer, ItemStack>();
		Map<Integer, ItemStack> added = new HashMap<Integer, ItemStack>();
		for (int slot : slots) {
			// get items and clone them
			ItemStack oldItem = items.get(slot);
			if (oldItem != null) oldItem = oldItem.clone();
			ItemStack newItem = newState.items.get(slot);
			if (newItem != null) newItem = newItem.clone();
			// has new item
			if (!Mat.fromItem(newItem).isAir()) {
				if (oldItem != null) {// had old item
					int delta = newItem.getAmount() - oldItem.getAmount();
					if (delta > 0) {
						newItem.setAmount(delta);
						added.put(slot, newItem);
					} else if (delta < 0) {
						newItem.setAmount(-delta);
						removed.put(slot, newItem);
					}
				} else {// no old item
					added.put(slot, newItem);
				}
			}
			// hasn't new item but had old item
			else if (!Mat.fromItem(oldItem).isAir()) {
				removed.put(slot, oldItem);
			}
		}
		// return
		return new Pair<Map<Integer, ItemStack>, Map<Integer, ItemStack>>(removed, added);
	}

}
