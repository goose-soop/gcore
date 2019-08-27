package com.guillaumevdn.gcore.lib.parseable.editor;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import com.guillaumevdn.gcore.lib.gui.ClickeableItem;
import com.guillaumevdn.gcore.lib.gui.GUI;
import com.guillaumevdn.gcore.lib.gui.ItemData;
import com.guillaumevdn.gcore.lib.material.Mat;

public abstract class EditorItem extends ClickeableItem {

	// base
	public EditorItem(String id, int slot, Mat type, String name, List<String> lore) {
		super(new ItemData(id, slot, type, 1, "§6" + name, lore).setHideFlags(true));
	}

	// methods
	@Override
	public void onClick(Player player, ClickType clickType, GUI gui, int pageIndex) {
		onClick(player, clickType, pageIndex);
	}

	// abstract methods
	protected abstract void onClick(Player player, ClickType clickType, int pageIndex);

}
