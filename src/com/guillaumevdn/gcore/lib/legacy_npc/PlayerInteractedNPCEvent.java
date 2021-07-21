package com.guillaumevdn.gcore.lib.legacy_npc;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * @author GuillaumeVDN
 */
public class PlayerInteractedNPCEvent extends PlayerEvent {

	private NPC npc;
	private NPCInteraction interaction;

	public PlayerInteractedNPCEvent(Player player, NPC npc, NPCInteraction interaction) {
		super(player);
		this.npc = npc;
		this.interaction = interaction;
	}

	// ----- get
	public NPC getNPC() {
		return npc;
	}

	public NPCInteraction getInteraction() {
		return interaction;
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
