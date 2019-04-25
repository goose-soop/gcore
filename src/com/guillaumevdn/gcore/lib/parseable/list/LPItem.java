package com.guillaumevdn.gcore.lib.parseable.list;

import java.util.List;

import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.parseable.ConfigData;
import com.guillaumevdn.gcore.lib.parseable.ListParseable;
import com.guillaumevdn.gcore.lib.parseable.Parseable;
import com.guillaumevdn.gcore.lib.parseable.container.CPItem;

public class LPItem extends ListParseable<CPItem> {

	// base
	public LPItem(String id, Parseable parent, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, "item", CaseType.LOWER, mandatory, editorSlot, editorIcon, editorDescription);
	}

	// methods
	public boolean contains(Player player, Player parser) {
		for (CPItem item : getElements().values()) {
			if (!item.contains(player, parser)) {
				return false;
			}
		}
		return true;
	}

	public void take(Player player, Player parser, boolean force) {
		for (CPItem item : getElements().values()) {
			item.remove(player, parser, force);
		}
	}

	// methods
	@Override
	public CPItem createElement(String elementId) {
		// create data
		ConfigData data = new ConfigData(getLastData().getPlugin(), getLastData().getSuperId(), getLastData().getConfig(), getLastData().getPath().isEmpty() ? elementId : getLastData().getPath() + "." + elementId);
		// create
		CPItem element = new CPItem(elementId.toLowerCase(), this, false, -1, getEditorIcon(), getEditorDescription());
		// load, add and return
		element.load(data);
		addElement(element);
		return element;
	}

	@Override
	public CPItem loadElement(String elementId, ConfigData data) {
		// create
		CPItem element = new CPItem(elementId, this, false, -1, getEditorIcon(), getEditorDescription());
		// load, add and return
		element.load(data);
		addElement(element);
		return element;
	}

}
