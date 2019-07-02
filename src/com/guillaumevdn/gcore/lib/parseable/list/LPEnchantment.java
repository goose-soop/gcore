package com.guillaumevdn.gcore.lib.parseable.list;

import java.util.List;

import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.parseable.ConfigData;
import com.guillaumevdn.gcore.lib.parseable.ListParseable;
import com.guillaumevdn.gcore.lib.parseable.Parseable;
import com.guillaumevdn.gcore.lib.parseable.container.CPEnchantment;

public class LPEnchantment extends ListParseable<CPEnchantment> {

	// base
	public LPEnchantment(String id, Parseable parent, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, "enchantment", mandatory, editorSlot, editorIcon, editorDescription);
	}

	// methods
	@Override
	public CPEnchantment createElement(String elementId) {
		// create data
		boolean unknown = getLastData() == null;
		ConfigData data = unknown ? null : new ConfigData(getLastData().getPlugin(), getLastData().getSuperId(), getLastData().getConfig(), getLastData().getPath().isEmpty() ? elementId : getLastData().getPath() + "." + elementId);
		// create
		CPEnchantment element = new CPEnchantment(elementId.toLowerCase(), this, false, -1, getEditorIcon(), getEditorDescription());
		// load, add and return
		if (!unknown) element.load(data);
		addElement(element);
		return element;
	}

	@Override
	public CPEnchantment loadElement(String elementId, ConfigData data) {
		// create
		CPEnchantment element = new CPEnchantment(elementId, this, false, -1, getEditorIcon(), getEditorDescription());
		// load, add and return
		element.load(data);
		addElement(element);
		return element;
	}

}
