package com.guillaumevdn.gcore.lib.gui;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.tuple.Pair;

/**
 * @author GuillaumeVDN
 */
public class PlayerInventoryState {

	// static base
	public static final Integer OTHER_SLOTS_DIFF = -10;

	private Inventory otherInventory;
	private Map<Integer, ItemStack> items = new HashMap<>();

	public PlayerInventoryState(Player player) {
		this(player, null, (Collection<Integer>) null);
	}

	public PlayerInventoryState(Player player, Inventory otherInventory, int... otherSlots) {
		this(player, otherInventory, CollectionUtils.asList(otherSlots));
	}

	public PlayerInventoryState(Player player, Inventory otherInventory, Collection<Integer> otherSlots) {
		// save player items
		for (int slot = 0; slot < player.getInventory().getContents().length; ++slot) {
			ItemStack item = player.getInventory().getContents()[slot];
			if (!Mat.isVoid(item)) {
				items.put(slot, item.clone());
			}
		}
		// save player cursor
		ItemStack cursor = !Mat.isVoid(player.getItemOnCursor()) ? player.getItemOnCursor().clone() : null;
		if (cursor != null) {
			items.put(-1, cursor);
		}
		// save other inventory items
		this.otherInventory = otherInventory;
		if (otherInventory != null) {
			for (int slot : otherSlots) {
				ItemStack item = otherInventory.getItem(slot);
				if (!Mat.isVoid(item)) {
					items.put(-slot + OTHER_SLOTS_DIFF, item.clone());
				}
			}
		}
	}

	// get
	public Inventory getOtherInventory() {
		return otherInventory;
	}

	public Map<Integer, ItemStack> getItems() {
		return items;
	}

	// compare
	public Pair<Map<Integer, ItemStack>, Map<Integer, ItemStack>> findChanges(PlayerInventoryState newState) {
		return InventoryState.findChanges(items, newState.items);
	}

}
