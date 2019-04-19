package com.guillaumevdn.gcore.lib.gui;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.Logger;
import com.guillaumevdn.gcore.lib.Logger.Level;

public abstract class FilledGUI extends GUI {

	// base
	public FilledGUI(Plugin plugin, String name, int size, int maxRegularSlot) {
		super(plugin, name, size, maxRegularSlot, true);
	}

	// overriden
	@Override
	public void setRegularItem(ClickeableItem item) {
		checkOverwrite(item);
		super.setRegularItem(item);
	}

	@Override
	public void setPersistentItem(ClickeableItem item) {
		checkOverwrite(item);
		super.setPersistentItem(item);
	}

	public void checkOverwrite(ClickeableItem item) {
		if (item.getItemData().getSlot() < 0) return;
		ClickeableItem present = getItemInSlot(0, item.getItemData().getSlot());
		if (present != null) {
			Logger.log(Level.WARNING, getPlugin() != null ? getPlugin().getName() : GCore.inst().getName(), "Overriding item " + present.getItemData().getId() + " by " + item.getItemData().getId() + " at slot " + item.getItemData().getSlot() + " of GUI " + getName());
		}
	}

	@Override
	public void open(Player player) {
		open(player, 0);
	}

	@Override
	public void open(Player player, int pageIndex) {
		if (isRegistered()) {
			unregister();
		}
		register();
		fill();
		if (!postFill()) {
			return;
		}
		super.open(player, pageIndex);
	}

	// abstract
	protected abstract void fill();
	/**
	 * @return false if the GUI shouldn't be opened
	 */
	protected abstract boolean postFill();

}
