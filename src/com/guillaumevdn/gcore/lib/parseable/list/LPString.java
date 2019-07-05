package com.guillaumevdn.gcore.lib.parseable.list;

import java.util.List;

import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.parseable.ConfigData;
import com.guillaumevdn.gcore.lib.parseable.ListParseable;
import com.guillaumevdn.gcore.lib.parseable.Parseable;
import com.guillaumevdn.gcore.lib.parseable.primitive.PPString;

public class LPString extends ListParseable<PPString> {

	// base
	public LPString(String id, Parseable parent, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, "string", mandatory, editorSlot, editorIcon, editorDescription);
	}

	// methods
	@Override
	public PPString createElement(String elementId) {
		// create data
		boolean unknown = getLastData() == null;
		ConfigData data = unknown ? null : new ConfigData(getLastData().getPlugin(), getLastData().getSuperId(), getLastData().getConfig(), getLastData().getPath().isEmpty() ? elementId : getLastData().getPath() + "." + elementId);
		// create
		PPString element = new PPString(elementId.toLowerCase(), this, "", false, -1, getEditorIcon(), getEditorDescription());
		// load, add and return
		if (!unknown) element.load(data);
		addElement(element);
		return element;
	}

	@Override
	public PPString loadElement(String elementId, ConfigData data) {
		// create
		PPString element = new PPString(elementId, this, "", false, -1, getEditorIcon(), getEditorDescription());
		// load, add and return
		element.load(data);
		addElement(element);
		return element;
	}

}
