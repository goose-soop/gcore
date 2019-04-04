package com.guillaumevdn.gcore.lib.parseable.list;

import java.util.List;

import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.parseable.ListParseable;
import com.guillaumevdn.gcore.lib.parseable.Parseable;
import com.guillaumevdn.gcore.lib.parseable.container.CPPotionEffect;
import com.guillaumevdn.gcore.lib.parseable.data.DataLink;
import com.guillaumevdn.gcore.lib.parseable.data.RegularDataLink;

public class LPPotionEffect extends ListParseable<CPPotionEffect> {

	// base
	public LPPotionEffect(String id, Parseable parent, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, "potion effect", CaseType.LOWER, mandatory, editorSlot, editorIcon, editorDescription);
	}

	// methods
	@Override
	public CPPotionEffect createElement(String elementId) {
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
		CPPotionEffect element = new CPPotionEffect(elementId.toLowerCase(), this, false, -1, getEditorIcon(), getEditorDescription());
		data.setComponent(element);
		element.setLastData(data);
		element.load(data);
		return element;
	}

	@Override
	public CPPotionEffect loadElement(String elementId, DataLink data) {
		// create
		CPPotionEffect element = new CPPotionEffect(elementId.toLowerCase(), this, false, -1, getEditorIcon(), getEditorDescription());
		data.setComponent(element);
		element.setLastData(data);
		element.load(data);
		return element;
	}

}
