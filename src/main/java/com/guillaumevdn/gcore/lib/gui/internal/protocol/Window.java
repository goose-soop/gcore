package com.guillaumevdn.gcore.lib.gui.internal.protocol;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.lib.concurrency.RWHashMap;
import com.guillaumevdn.gcore.lib.concurrency.RWHashSet;
import com.guillaumevdn.gcore.lib.gui.struct.GUI;

/**
 * @author GuillaumeVDN
 */
public class Window {

	private int id;
	private int index;
	private int stateId;
	private GUI gui;
	private RWHashMap<Integer, ItemStack> items = new RWHashMap<>(10, 1f);
	private RWHashSet<Player> viewing = new RWHashSet<>(1);

	public Window(int id, int index, GUI gui) {
		this.id = id;
		this.index = index;
		this.gui = gui;
	}

	public int getId() {
		return id;
	}

	public int getIndex() {
		return index;
	}

	public GUI getGUI() {
		return gui;
	}

	public RWHashMap<Integer, ItemStack> getItems() {
		return items;
	}

	public RWHashSet<Player> getViewers() {
		return viewing;
	}

	public int firstEmpty() {
		for (int slot = 0; slot < gui.getType().getSize(); ++slot) {
			if (!items.containsKey(slot)) {
				return slot;
			}
		}
		return -1;
	}

	public int incrementStateId() {
		return (stateId = stateId + 1 & 0x7FFF);
	}

}
