package com.guillaumevdn.gcore.lib.parseable.list;

import java.util.List;

import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.parseable.ConfigData;
import com.guillaumevdn.gcore.lib.parseable.Parseable;

public class MatLPMatLPDouble extends MatListParseable<MatLPDouble> {

	// base
	private boolean typeAllowDefault;
	private String defaultValue;
	private Double min, max;

	public MatLPMatLPDouble(String id, Parseable parent, boolean allowDefaultCase, boolean typeAllowDefault, String defaultValue, Double min, Double max, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, allowDefaultCase, "list of decimal numbers", mandatory, editorSlot, editorIcon, editorDescription);
		this.typeAllowDefault = typeAllowDefault;
		this.defaultValue = defaultValue;
		this.min = min;
		this.max = max;
	}

	// methods
	@Override
	public MatLPDouble createElement(String elementId) {
		// create data
		ConfigData data = new ConfigData(getLastData().getPlugin(), getLastData().getSuperId(), getLastData().getConfig(), getLastData().getPath().isEmpty() ? elementId : getLastData().getPath() + "." + elementId);
		// create
		MatLPDouble element = new MatLPDouble(elementId.toLowerCase(), this, typeAllowDefault, defaultValue, min, max, false, -1, getEditorIcon(), getEditorDescription());
		// load, add and return
		element.load(data);
		addElement(element);
		return element;
	}

	@Override
	public MatLPDouble loadElement(String elementId, ConfigData data) {
		// create
		MatLPDouble element = new MatLPDouble(elementId, this, typeAllowDefault, defaultValue, min, max, false, -1, getEditorIcon(), getEditorDescription());
		// load, add and return
		element.load(data);
		addElement(element);
		return element;
	}

}
