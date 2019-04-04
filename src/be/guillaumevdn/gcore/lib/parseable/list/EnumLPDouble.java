package be.guillaumevdn.gcore.lib.parseable.list;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import be.guillaumevdn.gcore.lib.material.Mat;
import be.guillaumevdn.gcore.lib.parseable.Parseable;
import be.guillaumevdn.gcore.lib.parseable.data.DataLink;
import be.guillaumevdn.gcore.lib.parseable.data.RegularDataLink;
import be.guillaumevdn.gcore.lib.parseable.primitive.PPDouble;
import be.guillaumevdn.gcore.lib.util.Utils;

public class EnumLPDouble<E extends Enum<E>> extends EnumListParseable<PPDouble, E> {

	// base
	private String defaultValue;
	private Double min, max;

	public EnumLPDouble(String id, Parseable parent, boolean allowDefaultCase, Class<E> enumClass, String defaultValue, Double min, Double max, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, allowDefaultCase, enumClass, "decimal number", mandatory, editorSlot, editorIcon, editorDescription);
		this.defaultValue = defaultValue;
		this.min = min;
		this.max = max;
	}

	// get
	public Double getValue(String key, Player parser) {
		PPDouble elem = getValue(key);
		return elem != null ? elem.getParsedValue(parser) : null;
	}

	public Map<E, PPDouble> getAllValues() {
		Map<E, PPDouble> values = new HashMap<E, PPDouble>();
		for (String key : getElements().keySet()) {
			E e = Utils.valueOfOrNull(getEnumClass(), key);
			if (e != null) values.put(e, getElements().get(key));
		}
		return values;
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
