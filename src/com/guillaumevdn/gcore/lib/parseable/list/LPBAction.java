package com.guillaumevdn.gcore.lib.parseable.list;

import java.util.List;

import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.npc.behavior.action.BAction;
import com.guillaumevdn.gcore.lib.npc.behavior.action.BActionType;
import com.guillaumevdn.gcore.lib.parseable.ConfigData;
import com.guillaumevdn.gcore.lib.parseable.ListParseable;
import com.guillaumevdn.gcore.lib.parseable.Parseable;
import com.guillaumevdn.gcore.lib.parseable.ParseableContainment;

public class LPBAction extends ListParseable<BAction> implements ParseableContainment<BAction> {

	// base
	public LPBAction(String id, Parseable parent, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, "behavior action", mandatory, editorSlot, editorIcon, editorDescription);
	}

	// methods
	@Override
	public BAction createElement(String elementId) {
		// create data
		ConfigData data = new ConfigData(getLastData().getPlugin(), getLastData().getSuperId(), getLastData().getConfig(), getLastData().getPath().isEmpty() ? elementId : getLastData().getPath() + "." + elementId);
		// create
		BAction element = BActionType.PLAYER_SEND_MESSAGE.createNew(elementId, this, data, false, false, -1, getEditorIcon(), null);
		// load, add and return
		element.load(data);
		addElement(element);
		return element;
	}

	@Override
	public BAction loadElement(String elementId, ConfigData data) {
		// create
		BAction element = BAction.load(elementId, this, data, false, -1, getEditorIcon(), null);
		// add and return
		if (element != null) {
			addElement(element);
		}
		return element;
	}

	@Override
	public void replaceContaining(BAction object) {
		addElement(object);
	}

}
