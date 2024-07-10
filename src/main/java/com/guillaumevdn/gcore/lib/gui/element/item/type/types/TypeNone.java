package com.guillaumevdn.gcore.lib.gui.element.item.type.types;

import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.inventory.ItemStack;

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
public class TypeNone extends GUIItemType {

	public TypeNone(String id) {
		super(id, IconNeed.REQUIRED, CommonMats.CHEST);
	}

	@Override
	public ActiveItemHolder newActive(ActiveGUI instance, ItemHolder holder, ElementGUIItem element) {
		return new ActiveItemHolderElementGUIItemCommon(instance, holder, element) {

			@Override
			protected void build(ItemStack itemIcon, QuadriConsumer<ItemStack, Set<String>, Integer, Consumer<ClickCall>> callback) throws ParsingError {

				Set<String> placeholders = StringUtils.getPlaceholders(itemIcon);
				itemIcon = getInstance().getReplacer().parse(itemIcon);

				callback.accept(itemIcon, placeholders, -1, null);
			}

		};
	}

}
