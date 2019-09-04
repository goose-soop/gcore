package com.guillaumevdn.gcore.lib.parseable.list;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.parseable.ConfigData;
import com.guillaumevdn.gcore.lib.parseable.Parseable;
import com.guillaumevdn.gcore.lib.parseable.primitive.PPDouble;
import com.guillaumevdn.gcore.lib.util.Utils;

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
		ConfigData data = new ConfigData(getLastData().getPlugin(), getLastData().getSuperId(), getLastData().getConfig(), getLastData().getPath().isEmpty() ? elementId : getLastData().getPath() + "." + elementId, getLastData().isSilent());
		// create
		PPDouble element = new PPDouble(elementId.toLowerCase(), this, defaultValue, min, max, false, -1, getEditorIcon(), getEditorDescription());
		// load, add and return
		element.load(data);
		addElement(element);
		return element;
	}

	@Override
	public PPDouble loadElement(String elementId, ConfigData data) {
		// create
		PPDouble element = new PPDouble(elementId, this, defaultValue, min, max, false, -1, getEditorIcon(), getEditorDescription());
		// load, add and return
		element.load(data);
		addElement(element);
		return element;
	}

}
