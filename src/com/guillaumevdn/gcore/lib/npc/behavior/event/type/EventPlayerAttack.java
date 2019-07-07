package com.guillaumevdn.gcore.lib.npc.behavior.event.type;

import java.util.List;

import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.lib.event.NpcAttackEvent;
import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.npc.Npc;
import com.guillaumevdn.gcore.lib.npc.behavior.event.BEventEvent;
import com.guillaumevdn.gcore.lib.npc.behavior.event.BEventType;
import com.guillaumevdn.gcore.lib.parseable.Parseable;

public class EventPlayerAttack extends BEventEvent<NpcAttackEvent> {

	// base
	public EventPlayerAttack(String id, Parseable parent, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, BEventType.PLAYER_ATTACK, parent, NpcAttackEvent.class, mandatory, editorSlot, editorIcon, editorDescription);
	}

	// methods
	@Override
	public boolean triggerAttempt(Player player, Npc npc, NpcAttackEvent event) {
		return true;// the event made it to this point with player and npc so it's good
	}

}
