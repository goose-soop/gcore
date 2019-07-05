package com.guillaumevdn.gcore.lib.npc.behavior.event;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.npc.Npc;
import com.guillaumevdn.gcore.lib.parseable.Parseable;
import com.guillaumevdn.gcore.lib.util.Utils;

public abstract class BEventEvent<T extends Event> extends BEvent {

	// base
	private Class<T> eventClass;

	public BEventEvent(String id, BEventType type, Parseable parent, Class<T> eventClass, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, type, parent, mandatory, editorSlot, editorIcon, editorDescription);
		this.eventClass = eventClass;
	}

	// methods
	@Override
	public boolean triggerAttempt(AttemptContext context, Player player, Npc npc, Event event) {
		return context.equals(AttemptContext.EVENT) && Utils.instanceOf(event, eventClass) ? triggerAttempt(player, npc, (T) event) : false;
	}

	// abstract methods
	public abstract boolean triggerAttempt(Player player, Npc npc, T event);

}
