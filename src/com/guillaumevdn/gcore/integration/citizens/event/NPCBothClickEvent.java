package com.guillaumevdn.gcore.integration.citizens.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.guillaumevdn.gcore.lib.player.PhysicalClickType;

import net.citizensnpcs.api.event.NPCClickEvent;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;

public class NPCBothClickEvent extends Event implements Cancellable {

	private NPCClickEvent event;
	private PhysicalClickType click;

	public NPCBothClickEvent(NPCClickEvent event, PhysicalClickType click) {
		super(!Bukkit.isPrimaryThread());
		this.event = event;;
		this.click = click;
	}

	public PhysicalClickType getClick() {
		return click;
	}

	public NPC getNPC() {
		return event.getNPC();
	}

	public Player getClicker() {
		return event.getClicker();
	}

	public boolean isCancelled() {
		return event.isCancelled();
	}

	public void setCancelled(boolean cancelled) {
		event.setCancelled(cancelled);
	}

	// ----- handlers
	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	// ----- call
	public static void callFrom(NPCRightClickEvent event) {
		Bukkit.getPluginManager().callEvent(new NPCBothClickEvent(event, event.getClicker().isSneaking() ? PhysicalClickType.RIGHT_CLICK_SNEAK : PhysicalClickType.RIGHT_CLICK));
	}

	public static void callFrom(NPCLeftClickEvent event) {
		Bukkit.getPluginManager().callEvent(new NPCBothClickEvent(event, event.getClicker().isSneaking() ? PhysicalClickType.LEFT_CLICK_SNEAK : PhysicalClickType.LEFT_CLICK));
	}

}
