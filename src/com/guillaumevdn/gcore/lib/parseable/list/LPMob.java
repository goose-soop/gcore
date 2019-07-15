package com.guillaumevdn.gcore.lib.parseable.list;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.parseable.ConfigData;
import com.guillaumevdn.gcore.lib.parseable.ListParseable;
import com.guillaumevdn.gcore.lib.parseable.Parseable;
import com.guillaumevdn.gcore.lib.parseable.container.CPMob;

public class LPMob extends ListParseable<CPMob> {

	// base
	public LPMob(String id, Parseable parent, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, "mob", mandatory, editorSlot, editorIcon, editorDescription);
	}

	// methods
	public boolean isValid(Entity entity, Player parser) {
		if (getElements().isEmpty()) return true;
		for (CPMob mob : getElements().values()) {
			if (mob.isValid(entity, parser)) {
				return true;
			}
		}
		return false;
	}

	// methods
	@Override
	public CPMob createElement(String elementId) {
		// create data
		ConfigData data = new ConfigData(getLastData().getPlugin(), getLastData().getSuperId(), getLastData().getConfig(), getLastData().getPath().isEmpty() ? elementId : getLastData().getPath() + "." + elementId);
		// create
		CPMob element = new CPMob(elementId.toLowerCase(), this, false, -1, getEditorIcon(), getEditorDescription());
		// load, add and return
		element.load(data);
		addElement(element);
		return element;
	}

	@Override
	public CPMob loadElement(String elementId, ConfigData data) {
		// create
		CPMob element = new CPMob(elementId, this, false, -1, getEditorIcon(), getEditorDescription());
		// load, add and return
		element.load(data);
		addElement(element);
		return element;
	}

}
