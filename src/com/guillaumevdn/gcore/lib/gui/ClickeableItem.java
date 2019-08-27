package com.guillaumevdn.gcore.lib.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ClickeableItem {

	// base
	private ItemData item;
	private int pageIndex = 0;
	private int slot = 0;

	public ClickeableItem(ItemData item) {
		this.item = item;
	}

	public ItemData getItemData() {
		return item;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public int getSlot() {
		return slot;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}

	public ItemStack getGUIStack() {
		return item.getItemStack();
	}

	// click
	/**
	 * @return true if the event must be cancelled
	 */
	public void onClick(final Player player, final ClickType clickType, final GUI gui, final int pageIndex) {
	}

}
