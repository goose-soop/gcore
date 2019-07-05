package com.guillaumevdn.gcore.lib.npc.behavior.event.type;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.npc.Npc;
import com.guillaumevdn.gcore.lib.npc.behavior.event.BEvent;
import com.guillaumevdn.gcore.lib.npc.behavior.event.BEventType;
import com.guillaumevdn.gcore.lib.parseable.Parseable;

public class EventTimer extends BEvent {

	// base
	public EventTimer(String id, Parseable parent, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, BEventType.TIMER, parent, mandatory, editorSlot, editorIcon, editorDescription);
	}

	// methods
	@Override
	public boolean triggerAttempt(AttemptContext context, Player player, Npc npc, Event event) {
		return context.equals(AttemptContext.TIMER);
	}

}
