package com.guillaumevdn.gcore.lib.parseable.list;

import java.util.List;

import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.parseable.Parseable;
import com.guillaumevdn.gcore.lib.parseable.data.DataLink;
import com.guillaumevdn.gcore.lib.parseable.data.RegularDataLink;

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
		DataLink data;
		if (getLastData() instanceof RegularDataLink) {
			RegularDataLink compact = (RegularDataLink) getLastData();
			data = new RegularDataLink(null, compact.getPlugin(), compact.getSuperId(), compact.getConfig(), compact.getPath() + "." + elementId);
		} else if (getLastData() instanceof RegularDataLink) {
			RegularDataLink compact = (RegularDataLink) getLastData();
			data = new RegularDataLink(null, compact.getPlugin(), compact.getSuperId(), compact.getConfig(), compact.getPath() + "." + elementId);
		} else {
			return null;
		}
		// create
		MatLPDouble element = new MatLPDouble(elementId.toLowerCase(), this, typeAllowDefault, defaultValue, min, max, false, -1, getEditorIcon(), getEditorDescription());
		data.setComponent(element);
		element.setLastData(data);
		element.load(data);
		return element;
	}

	@Override
	public MatLPDouble loadElement(String elementId, DataLink data) {
		// create
		MatLPDouble element = new MatLPDouble(elementId.toLowerCase(), this, typeAllowDefault, defaultValue, min, max, false, -1, getEditorIcon(), getEditorDescription());
		data.setComponent(element);
		element.setLastData(data);
		element.load(data);
		return element;
	}

}
