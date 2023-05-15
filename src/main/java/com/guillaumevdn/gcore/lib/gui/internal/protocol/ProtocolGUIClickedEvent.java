package com.guillaumevdn.gcore.lib.gui.internal.protocol;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.guillaumevdn.gcore.lib.gui.struct.ClickCall.ClickType;
import com.guillaumevdn.gcore.lib.gui.struct.GUI;

public class ProtocolGUIClickedEvent extends Event {

	private final GUI gui;
	private final Player player;
	private final ClickType click;
	private final int pageIndex;
	private final int slot;

	public ProtocolGUIClickedEvent(GUI gui, Player player, ClickType click, int pageIndex, int slot) {
		super(!Bukkit.isPrimaryThread());
		this.gui = gui;
		this.player = player;
		this.click = click;
		this.pageIndex = pageIndex;
		this.slot = slot;
	}

	public GUI getGUI() {
		return gui;
	}

	public Player getPlayer() {
		return player;
	}

	public ClickType getClick() {
		return click;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public int getSlot() {
		return slot;
	}

	// ----- handlers
	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
