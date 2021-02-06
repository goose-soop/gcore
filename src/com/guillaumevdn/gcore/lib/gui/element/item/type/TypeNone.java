package com.guillaumevdn.gcore.lib.gui.element.item.type;

import java.util.function.Consumer;

import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
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
public class TypeNone extends GUIItemType {

	public TypeNone(String id) {
		super(id, true, CommonMats.CHEST);
	}

	@Override
	public ActiveItemHolder newActive(ActiveGUI instance, ItemHolder holder, ElementGUIItem element) {
		return new ActiveItemHolderElementGUIItemCommon(instance, holder, element, true) {
			@Override
			protected void build(ItemStack itemIcon, TriConsumer<ItemStack, Integer, Consumer<ClickCall>> callback) throws ParsingError {
				callback.accept(itemIcon, -1, null);
			}
		};
	}

}
