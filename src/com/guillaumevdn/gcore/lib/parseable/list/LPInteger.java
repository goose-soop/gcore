package com.guillaumevdn.gcore.lib.parseable.list;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.parseable.ConfigData;
import com.guillaumevdn.gcore.lib.parseable.ListParseable;
import com.guillaumevdn.gcore.lib.parseable.Parseable;
import com.guillaumevdn.gcore.lib.parseable.primitive.PPInteger;

public class LPInteger extends ListParseable<PPInteger> {

	// base
	private String defaultValue;
	private Integer min, max;

	public LPInteger(String id, Parseable parent, String defaultValue, Integer min, Integer max, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, "number", mandatory, editorSlot, editorIcon, editorDescription);
		this.defaultValue = defaultValue;
		this.min = min;
		this.max = max;
	}

	// get
	public String getDefaultValue() {
		return defaultValue;
	}

	public Integer getMin() {
		return min;
	}

	public Integer getMax() {
		return max;
	}

	public PPInteger getValue(String key) {
		PPInteger elem = getElement(key);
		return elem != null ? elem : getElement("DEFAULT");
	}

	public Integer getValue(String key, Player parser) {
		PPInteger elem = getValue(key);
		return elem != null ? elem.getParsedValue(parser) : null;
	}

	public Map<Integer, PPInteger> getAllValues() {
		Map<Integer, PPInteger> values = new HashMap<Integer, PPInteger>();
		for (String key : getElements().keySet()) {
			try {
				values.put(Integer.valueOf(key), getElements().get(key));
			} catch (NumberFormatException ignored) {}
		}
		return values;
	}

	// methods
	@Override
	public PPInteger createElement(String elementId) {
		// create data
		ConfigData data = new ConfigData(getLastData().getPlugin(), getLastData().getSuperId(), getLastData().getConfig(), getLastData().getPath().isEmpty() ? elementId : getLastData().getPath() + "." + elementId);
		// create
		PPInteger element = new PPInteger(elementId.toLowerCase(), this, defaultValue, min, max, false, -1, getEditorIcon(), getEditorDescription());
		// load, add and return
		element.load(data);
		addElement(element);
		return element;
	}

	@Override
	public PPInteger loadElement(String elementId, ConfigData data) {
		// create
		PPInteger element = new PPInteger(elementId, this, defaultValue, min, max, false, -1, getEditorIcon(), getEditorDescription());
		// load, add and return
		element.load(data);
		addElement(element);
		return element;
	}

}
