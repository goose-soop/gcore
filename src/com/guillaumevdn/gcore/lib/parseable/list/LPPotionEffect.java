package com.guillaumevdn.gcore.lib.parseable.list;

import java.util.List;

import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.parseable.ConfigData;
import com.guillaumevdn.gcore.lib.parseable.ListParseable;
import com.guillaumevdn.gcore.lib.parseable.Parseable;
import com.guillaumevdn.gcore.lib.parseable.container.CPPotionEffect;

public class LPPotionEffect extends ListParseable<CPPotionEffect> {

	// base
	public LPPotionEffect(String id, Parseable parent, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, "potion effect", mandatory, editorSlot, editorIcon, editorDescription);
	}

	// methods
	@Override
	public CPPotionEffect createElement(String elementId) {
		// create data
		boolean unknown = getLastData() == null;
		ConfigData data = unknown ? null : new ConfigData(getLastData().getPlugin(), getLastData().getSuperId(), getLastData().getConfig(), getLastData().getPath().isEmpty() ? elementId : getLastData().getPath() + "." + elementId, getLastData().isSilent());
		// create
		CPPotionEffect element = new CPPotionEffect(elementId.toLowerCase(), this, false, -1, getEditorIcon(), getEditorDescription());
		// load, add and return
		if (!unknown) element.load(data);
		addElement(element);
		return element;
	}

	@Override
	public CPPotionEffect loadElement(String elementId, ConfigData data) {
		// create
		CPPotionEffect element = new CPPotionEffect(elementId, this, false, -1, getEditorIcon(), getEditorDescription());
		// load, add and return
		element.load(data);
		addElement(element);
		return element;
	}

}
