package be.guillaumevdn.gcore.lib.parseable.editor;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import be.guillaumevdn.gcore.lib.gui.ClickeableItem;
import be.guillaumevdn.gcore.lib.gui.GUI;
import be.guillaumevdn.gcore.lib.gui.ItemData;
import be.guillaumevdn.gcore.lib.material.Mat;

public abstract class EditorItem extends ClickeableItem {

	// base
	public EditorItem(String id, int slot, Mat type, String name, List<String> lore) {
		super(new ItemData(id, slot, type, 1, "§6" + name, lore).setHideFlags(true));
	}

	// methods
	@Override
	public boolean onClick(Player player, ClickType clickType, GUI gui, int pageIndex) {
		onClick(player, clickType, pageIndex);
		return true;
	}

	// abstract methods
	protected abstract void onClick(Player player, ClickType clickType, int pageIndex);

}
