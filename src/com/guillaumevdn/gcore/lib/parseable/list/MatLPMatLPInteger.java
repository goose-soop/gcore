package com.guillaumevdn.gcore.lib.parseable.list;

import java.util.List;

import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.parseable.ConfigData;
import com.guillaumevdn.gcore.lib.parseable.Parseable;

public class MatLPMatLPInteger extends MatListParseable<MatLPInteger> {

	// base
	private boolean typeAllowDefault;
	private String defaultValue;
	private Integer min, max;

	public MatLPMatLPInteger(String id, Parseable parent, boolean allowDefaultCase, boolean typeAllowDefault, String defaultValue, Integer min, Integer max, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, allowDefaultCase, "list of numbers", mandatory, editorSlot, editorIcon, editorDescription);
		this.typeAllowDefault = typeAllowDefault;
		this.defaultValue = defaultValue;
		this.min = min;
		this.max = max;
	}

	// methods
	@Override
	public MatLPInteger createElement(String elementId) {
		// create data
		ConfigData data = new ConfigData(getLastData().getPlugin(), getLastData().getSuperId(), getLastData().getConfig(), getLastData().getPath().isEmpty() ? elementId : getLastData().getPath() + "." + elementId);
		// create
		MatLPInteger element = new MatLPInteger(elementId.toLowerCase(), this, typeAllowDefault, defaultValue, min, max, false, -1, getEditorIcon(), getEditorDescription());
		// load, add and return
		element.load(data);
		addElement(element);
		return element;
	}

	@Override
	public MatLPInteger loadElement(String elementId, ConfigData data) {
		// create
		MatLPInteger element = new MatLPInteger(elementId, this, typeAllowDefault, defaultValue, min, max, false, -1, getEditorIcon(), getEditorDescription());
		// load, add and return
		element.load(data);
		addElement(element);
		return element;
	}

}
