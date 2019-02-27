package be.pyrrh4.pyrcore.lib.loadable.editor;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import be.pyrrh4.pyrcore.lib.gui.ClickeableItem;
import be.pyrrh4.pyrcore.lib.gui.GUI;
import be.pyrrh4.pyrcore.lib.gui.ItemData;
import be.pyrrh4.pyrcore.lib.material.Mat;

public abstract class EditorItem extends ClickeableItem {

	// base
	public EditorItem(String id, int slot, Mat type, String name, List<String> lore) {
		super(new ItemData(id, slot, type, 1, name.startsWith("§") ? name : "§6" + name, lore));
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
