package be.guillaumevdn.gcore.lib.parseable.list;

import java.util.List;

import org.bukkit.entity.Player;

import be.guillaumevdn.gcore.lib.material.Mat;
import be.guillaumevdn.gcore.lib.parseable.ListParseable;
import be.guillaumevdn.gcore.lib.parseable.Parseable;
import be.guillaumevdn.gcore.lib.parseable.data.DataLink;
import be.guillaumevdn.gcore.lib.parseable.data.RegularDataLink;
import be.guillaumevdn.gcore.lib.parseable.primitive.PPDouble;

public class LPDouble extends ListParseable<PPDouble> {

	// base
	private String defaultValue;
	private Double min, max;

	public LPDouble(String id, Parseable parent, String defaultValue, Double min, Double max, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, "decimal number", CaseType.LOWER, mandatory, editorSlot, editorIcon, editorDescription);
		this.defaultValue = defaultValue;
		this.min = min;
		this.max = max;
	}

	// get
	public String getDefaultValue() {
		return defaultValue;
	}

	public Double getMin() {
		return min;
	}

	public Double getMax() {
		return max;
	}

	public PPDouble getValue(String key) {
		PPDouble elem = getElement(key);
		return elem != null ? elem : getElement("DEFAULT");
	}

	public Double getValue(String key, Player parser) {
		PPDouble elem = getValue(key);
		return elem != null ? elem.getParsedValue(parser) : null;
	}

	// methods
	@Override
	public PPDouble createElement(String elementId) {
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
		PPDouble element = new PPDouble(elementId.toLowerCase(), this, defaultValue, min, max, false, -1, getEditorIcon(), getEditorDescription());
		data.setComponent(element);
		element.setLastData(data);
		element.load(data);
		return element;
	}

	@Override
	public PPDouble loadElement(String elementId, DataLink data) {
		// create
		PPDouble element = new PPDouble(elementId.toLowerCase(), this, defaultValue, min, max, false, -1, getEditorIcon(), getEditorDescription());
		data.setComponent(element);
		element.setLastData(data);
		element.load(data);
		return element;
	}

}
