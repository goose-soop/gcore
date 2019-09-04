package com.guillaumevdn.gcore.lib.parseable.list;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.parseable.ConfigData;
import com.guillaumevdn.gcore.lib.parseable.Parseable;
import com.guillaumevdn.gcore.lib.parseable.primitive.PPInteger;
import com.guillaumevdn.gcore.lib.util.Utils;

public class EnumLPInteger<E extends Enum<E>> extends EnumListParseable<PPInteger, E> {

	// base
	private String defaultValue;
	private Integer min, max;

	public EnumLPInteger(String id, Parseable parent, boolean allowDefaultCase, Class<E> enumClass, String defaultValue, Integer min, Integer max, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, allowDefaultCase, enumClass, "number", mandatory, editorSlot, editorIcon, editorDescription);
		this.defaultValue = defaultValue;
		this.min = min;
		this.max = max;
	}

	// get
	public Integer getValue(String key, Player parser) {
		PPInteger elem = getValue(key);
		return elem != null ? elem.getParsedValue(parser) : null;
	}

	public Map<E, PPInteger> getAllValues() {
		Map<E, PPInteger> values = new HashMap<E, PPInteger>();
		for (String key : getElements().keySet()) {
			E e = Utils.valueOfOrNull(getEnumClass(), key);
			if (e != null) values.put(e, getElements().get(key));
		}
		return values;
	}

	// methods
	@Override
	public PPInteger createElement(String elementId) {
		// create data
		ConfigData data = new ConfigData(getLastData().getPlugin(), getLastData().getSuperId(), getLastData().getConfig(), getLastData().getPath().isEmpty() ? elementId : getLastData().getPath() + "." + elementId, getLastData().isSilent());
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
