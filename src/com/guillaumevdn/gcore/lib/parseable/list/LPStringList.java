package com.guillaumevdn.gcore.lib.parseable.list;

import java.util.List;

import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.parseable.ConfigData;
import com.guillaumevdn.gcore.lib.parseable.ListParseable;
import com.guillaumevdn.gcore.lib.parseable.Parseable;
import com.guillaumevdn.gcore.lib.parseable.primitive.PPStringList;
import com.guillaumevdn.gcore.lib.util.Utils;

public class LPStringList extends ListParseable<PPStringList> {

	// base
	public LPStringList(String id, Parseable parent, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, "string list", mandatory, editorSlot, editorIcon, editorDescription);
	}

	// methods
	@Override
	public PPStringList createElement(String elementId) {
		// create data
		boolean unknown = getLastData() == null;
		ConfigData data = unknown ? null : new ConfigData(getLastData().getPlugin(), getLastData().getSuperId(), getLastData().getConfig(), getLastData().getPath().isEmpty() ? elementId : getLastData().getPath() + "." + elementId);
		// create
		PPStringList element = new PPStringList(elementId.toLowerCase(), this, Utils.emptyList(), false, -1, getEditorIcon(), getEditorDescription());
		// load, add and return
		if (!unknown) element.load(data);
		addElement(element);
		return element;
	}

	@Override
	public PPStringList loadElement(String elementId, ConfigData data) {
		// create
		PPStringList element = new PPStringList(elementId, this, Utils.emptyList(), false, -1, getEditorIcon(), getEditorDescription());
		// load, add and return
		element.load(data);
		addElement(element);
		return element;
	}

}
