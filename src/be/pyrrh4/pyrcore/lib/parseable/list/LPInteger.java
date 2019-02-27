package be.pyrrh4.pyrcore.lib.parseable.list;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import be.pyrrh4.pyrcore.lib.material.Mat;
import be.pyrrh4.pyrcore.lib.parseable.ListParseable;
import be.pyrrh4.pyrcore.lib.parseable.Parseable;
import be.pyrrh4.pyrcore.lib.parseable.data.DataLink;
import be.pyrrh4.pyrcore.lib.parseable.data.RegularDataLink;
import be.pyrrh4.pyrcore.lib.parseable.primitive.PPInteger;

public class LPInteger extends ListParseable<PPInteger> {

	// base
	private String defaultValue;
	private Integer min, max;

	public LPInteger(String id, Parseable parent, String defaultValue, Integer min, Integer max, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, "number", CaseType.LOWER, mandatory, editorSlot, editorIcon, editorDescription);
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
		PPInteger element = new PPInteger(elementId.toLowerCase(), this, defaultValue, min, max, false, -1, getEditorIcon(), getEditorDescription());
		data.setComponent(element);
		element.setLastData(data);
		element.load(data);
		return element;
	}

	@Override
	public PPInteger loadElement(String elementId, DataLink data) {
		// create
		PPInteger element = new PPInteger(elementId.toLowerCase(), this, defaultValue, min, max, false, -1, getEditorIcon(), getEditorDescription());
		data.setComponent(element);
		element.setLastData(data);
		element.load(data);
		return element;
	}

}
