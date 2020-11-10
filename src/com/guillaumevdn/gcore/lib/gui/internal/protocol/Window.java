package com.guillaumevdn.gcore.lib.gui.internal.protocol;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.lib.gui.struct.GUI;

/**
 * @author GuillaumeVDN
 */
public class Window {

	private int id;
	private int index;
	private GUI gui;
	private Map<Integer, ItemStack> items = new HashMap<>();
	private Set<Player> viewing = new HashSet<>();

	public Window(int id, int index, GUI gui) {
		this.id = id;
		this.index = index;
		this.gui = gui;
	}

	// get
	public int getId() {
		return id;
	}

	public int getIndex() {
		return index;
	}

	public GUI getGUI() {
		return gui;
	}

	public Map<Integer, ItemStack> getItems() {
		return items;
	}

	public Set<Player> getViewers() {
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

}
