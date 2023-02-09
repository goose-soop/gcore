package com.guillaumevdn.gcore.lib.gui.element.item.type.types;

import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.function.QuadriConsumer;
import com.guillaumevdn.gcore.lib.gui.element.item.ElementGUIItem;
import com.guillaumevdn.gcore.lib.gui.element.item.type.GUIItemType;
import com.guillaumevdn.gcore.lib.gui.element.item.type.IconNeed;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.gui.struct.active.ActiveGUI;
import com.guillaumevdn.gcore.lib.gui.struct.active.ActiveItemHolder;
import com.guillaumevdn.gcore.lib.gui.struct.active.ActiveItemHolderElementGUIItemCommon;
import com.guillaumevdn.gcore.lib.gui.struct.active.ItemHolder;
import com.guillaumevdn.gcore.lib.string.StringUtils;

/**
 * @author GuillaumeVDN
 */
public class TypeClose extends GUIItemType {

	public TypeClose(String id) {
		super(id, IconNeed.OPTIONAL, CommonMats.ANVIL);
	}

	@Override
	public ActiveItemHolder newActive(ActiveGUI instance, ItemHolder holder, ElementGUIItem element) {
		return new ActiveItemHolderElementGUIItemCommon(instance, holder, element) {
			@Override
			protected void build(ItemStack itemIcon, QuadriConsumer<ItemStack, Set<String>, Integer, Consumer<ClickCall>> callback) throws ParsingError {
				ItemStack icon = itemIcon != null ? itemIcon : ConfigGCore.backItem;
				Set<String> placeholders = StringUtils.getPlaceholders(icon);
				icon = getInstance().getReplacer().parse(icon);

				callback.accept(icon, placeholders, -1, call -> {
					call.getClicker().closeInventory();
				});
			}
		};
	}

}
