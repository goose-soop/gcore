package be.guillaumevdn.gcore.lib.parseable.list;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import be.guillaumevdn.gcore.lib.gui.ItemData;
import be.guillaumevdn.gcore.lib.material.Mat;
import be.guillaumevdn.gcore.lib.parseable.ListParseable;
import be.guillaumevdn.gcore.lib.parseable.Parseable;
import be.guillaumevdn.gcore.lib.parseable.container.CPItem;
import be.guillaumevdn.gcore.lib.parseable.data.DataLink;
import be.guillaumevdn.gcore.lib.parseable.data.RegularDataLink;

public class LPItem extends ListParseable<CPItem> {

	// base
	public LPItem(String id, Parseable parent, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, "item", CaseType.LOWER, mandatory, editorSlot, editorIcon, editorDescription);
	}

	// methods
	public boolean contains(Player player, Player parser) {
		return contains(player.getInventory(), parser);
	}

	public boolean contains(Inventory inventory, Player parser) {
		for (CPItem item : getElements().values()) {
			ItemData it = item.getParsedValue(parser);
			if (it != null && !it.contains(inventory)) {
				return false;
			}
		}
		return true;
	}

	public void take(Player player, Player parser, boolean afterAction) {
		take(player.getInventory(), parser, afterAction);
		player.updateInventory();
	}

	public void take(Inventory inventory, Player parser, boolean afterAction) {
		for (CPItem item : getElements().values()) {
			ItemData it = item.getParsedValue(parser);
			if (it != null) {
				if (item.getRemoveAfterAction(parser) ? afterAction : true) {
					it.remove(inventory);
				}
			}
		}
	}

	// methods
	@Override
	public CPItem createElement(String elementId) {
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
		CPItem element = new CPItem(elementId.toLowerCase(), this, false, -1, getEditorIcon(), getEditorDescription());
		data.setComponent(element);
		element.setLastData(data);
		element.load(data);
		return element;
	}

	@Override
	public CPItem loadElement(String elementId, DataLink data) {
		// create
		CPItem element = new CPItem(elementId.toLowerCase(), this, false, -1, getEditorIcon(), getEditorDescription());
		data.setComponent(element);
		element.setLastData(data);
		element.load(data);
		return element;
	}

}
