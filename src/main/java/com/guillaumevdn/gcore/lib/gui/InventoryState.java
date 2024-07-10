package com.guillaumevdn.gcore.lib.gui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.item.ItemCheck;
import com.guillaumevdn.gcore.lib.item.ItemUtils;
import com.guillaumevdn.gcore.lib.tuple.Pair;

/**
 * @author GuillaumeVDN
 */
public class InventoryState {

	public static final Integer OTHER_SLOTS_DIFF = -10;

	private transient Inventory inventory;
	private Map<Integer, ItemStack> items = new HashMap<>();

	public InventoryState(Inventory inventory, int startSlot, int endSlot) {
		this.inventory = inventory;
		// save items
		for (int slot = startSlot; slot <= endSlot; ++slot) {
			ItemStack item = inventory.getContents()[slot];
			if (!Mat.isVoid(item)) {
				items.put(slot, item.clone());
			}
		}
	}

	public InventoryState(Map<Integer, ItemStack> items) {
		this.inventory = null;
		this.items = items;
	}

	// ----- get
	public Inventory getInventory() {
		return inventory;
	}

	public Map<Integer, ItemStack> getItems() {
		return items;
	}

	// ----- set
	@Deprecated
	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	// ----- compare
	public Pair<Map<Integer, ItemStack>, Map<Integer, ItemStack>> findChanges(InventoryState newState) {
		return findChanges(items, newState.items);
	}

	public static Pair<Map<Integer, ItemStack>, Map<Integer, ItemStack>> findChanges(Map<Integer, ItemStack> initialItems,
			Map<Integer, ItemStack> currentItems) {
		// get all slots
		Set<Integer> slots = new HashSet<>();
		slots.addAll(initialItems.keySet());
		slots.addAll(currentItems.keySet());
		// check for changes
		Map<Integer, ItemStack> removed = new HashMap<>();
		Map<Integer, ItemStack> added = new HashMap<>();
		for (int slot : slots) {
			// get items and clone them
			ItemStack initialItem = initialItems.get(slot);
			if (initialItem != null)
				initialItem = initialItem.clone();
			ItemStack currentItem = currentItems.get(slot);
			if (currentItem != null)
				currentItem = currentItem.clone();
			// still has item on the slot
			if (!Mat.isVoid(currentItem)) {
				// had an item before
				if (initialItem != null) {
					// different type of item
					if (!ItemUtils.match(initialItem, currentItem, ItemCheck.ExactSame)) {
						removed.put(slot, initialItem);
						added.put(slot, currentItem);
					}
					// same type of item
					else {
						int delta = currentItem.getAmount() - initialItem.getAmount(); // correct order ; will be positive if we have more, negative if we have
																						// less
						if (delta > 0) {
							currentItem.setAmount(delta);
							added.put(slot, currentItem);
						} else if (delta < 0) {
							currentItem.setAmount(-delta);
							removed.put(slot, currentItem);
						}
					}
				}
				// had no item before
				else {
					added.put(slot, currentItem);
				}
			}
			// has no item on the slot but used to have
			else if (!Mat.isVoid(initialItem)) {
				removed.put(slot, initialItem);
			}
		}
		// return
		return Pair.of(removed, added);
	}

}
