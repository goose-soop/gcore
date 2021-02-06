package com.guillaumevdn.gcore.lib.gui.struct.active.modified;

import java.util.function.Consumer;

import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.function.TriConsumer;
import com.guillaumevdn.gcore.lib.gui.element.item.ElementGUIItem;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.gui.struct.active.ActiveGUI;
import com.guillaumevdn.gcore.lib.gui.struct.active.ActiveItemHolder;
import com.guillaumevdn.gcore.lib.gui.struct.active.ActiveItemHolderElementGUIItemCommon;
import com.guillaumevdn.gcore.lib.gui.struct.active.ItemHolder;

/**
 * @author GuillaumeVDN
 */
public abstract class ModifiedConfigHolderItem extends ItemHolder {

	private ElementGUIItem element;

	public ModifiedConfigHolderItem(String id, ElementGUIItem element) {
		super(id);
		this.element = element;
	}

	@Override
	public ActiveItemHolder newActive(ActiveGUI instance) {
		return new ActiveItemHolderElementGUIItemCommon(instance, this, element, true) {
			@Override
			protected void build(ItemStack itemIcon, TriConsumer<ItemStack, Integer, Consumer<ClickCall>> callback) throws ParsingError {
				callback.accept(itemIcon, -1, call -> onClick(call));
			}
		};
	}

	protected ItemStack maybeModifyIcon(ItemStack icon) {
		return icon;
	}

	protected abstract void onClick(ClickCall call);

}
