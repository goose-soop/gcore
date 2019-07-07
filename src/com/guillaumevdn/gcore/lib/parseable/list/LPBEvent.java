package com.guillaumevdn.gcore.lib.parseable.list;

import java.util.List;

import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.npc.behavior.event.BEvent;
import com.guillaumevdn.gcore.lib.npc.behavior.event.BEventType;
import com.guillaumevdn.gcore.lib.parseable.ConfigData;
import com.guillaumevdn.gcore.lib.parseable.ListParseable;
import com.guillaumevdn.gcore.lib.parseable.Parseable;
import com.guillaumevdn.gcore.lib.parseable.ParseableContainment;

public class LPBEvent extends ListParseable<BEvent> implements ParseableContainment<BEvent> {

	// base
	public LPBEvent(String id, Parseable parent, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, "behavior event", mandatory, editorSlot, editorIcon, editorDescription);
	}

	// methods
	@Override
	public BEvent createElement(String elementId) {
		// create data
		ConfigData data = new ConfigData(getLastData().getPlugin(), getLastData().getSuperId(), getLastData().getConfig(), getLastData().getPath().isEmpty() ? elementId : getLastData().getPath() + "." + elementId);
		// create
		BEvent element = BEventType.BEHAVIOR_CALL.createNew(elementId, getParent(), data, false, false, -1, getEditorIcon(), null);
		// load, add and return
		element.load(data);
		addElement(element);
		return element;
	}

	@Override
	public BEvent loadElement(String elementId, ConfigData data) {
		// create
		BEvent element = BEvent.load(elementId, this, data, false, -1, getEditorIcon(), null);
		// add and return
		if (element != null) {
			addElement(element);
		}
		return element;
	}

	@Override
	public void replaceContaining(BEvent object) {
		addElement(object);
	}

}
