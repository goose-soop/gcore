package com.guillaumevdn.gcore.lib.parseable.list;

import java.util.List;

import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.npc.behavior.condition.BCondition;
import com.guillaumevdn.gcore.lib.npc.behavior.condition.BConditionType;
import com.guillaumevdn.gcore.lib.parseable.ConfigData;
import com.guillaumevdn.gcore.lib.parseable.ListParseable;
import com.guillaumevdn.gcore.lib.parseable.Parseable;
import com.guillaumevdn.gcore.lib.parseable.ParseableContainment;

public class LPBCondition extends ListParseable<BCondition> implements ParseableContainment<BCondition> {

	// base
	public LPBCondition(String id, Parseable parent, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, "behavior condition", mandatory, editorSlot, editorIcon, editorDescription);
	}

	// methods
	@Override
	public BCondition createElement(String elementId) {
		// create data
		ConfigData data = new ConfigData(getLastData().getPlugin(), getLastData().getSuperId(), getLastData().getConfig(), getLastData().getPath().isEmpty() ? elementId : getLastData().getPath() + "." + elementId);
		// create
		BCondition element = BConditionType.NPC_VARIABLE_CHECK.createNew(elementId, this, data, false, false, -1, getEditorIcon(), null);
		// load, add and return
		element.load(data);
		addElement(element);
		return element;
	}

	@Override
	public BCondition loadElement(String elementId, ConfigData data) {
		// create
		BCondition element = BCondition.load(elementId, this, data, false, -1, getEditorIcon(), null);
		// add and return
		if (element != null) {
			addElement(element);
		}
		return element;
	}

	@Override
	public void replaceContaining(BCondition object) {
		addElement(object);
	}

}
