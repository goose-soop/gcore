package be.pyrrh4.pyrcore.lib.parseable.list;

import java.util.List;

import be.pyrrh4.pyrcore.lib.material.Mat;
import be.pyrrh4.pyrcore.lib.parseable.Parseable;
import be.pyrrh4.pyrcore.lib.parseable.data.DataLink;
import be.pyrrh4.pyrcore.lib.parseable.data.RegularDataLink;

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
		MatLPInteger element = new MatLPInteger(elementId.toLowerCase(), this, typeAllowDefault, defaultValue, min, max, false, -1, getEditorIcon(), getEditorDescription());
		data.setComponent(element);
		element.setLastData(data);
		element.load(data);
		return element;
	}

	@Override
	public MatLPInteger loadElement(String elementId, DataLink data) {
		// create
		MatLPInteger element = new MatLPInteger(elementId.toLowerCase(), this, typeAllowDefault, defaultValue, min, max, false, -1, getEditorIcon(), getEditorDescription());
		data.setComponent(element);
		element.setLastData(data);
		element.load(data);
		return element;
	}

}
