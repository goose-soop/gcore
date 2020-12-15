package com.guillaumevdn.gcore.lib.gui.element.item.type;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.sound.Sound;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.gui.element.item.element.ElementGUIItem;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall.ClickType;
import com.guillaumevdn.gcore.lib.gui.struct.GUIItem;
import com.guillaumevdn.gcore.lib.gui.struct.active.ActiveGUI;
import com.guillaumevdn.gcore.lib.gui.struct.active.ActiveHolderItem;
import com.guillaumevdn.gcore.lib.gui.struct.active.ItemHolder;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;
import com.guillaumevdn.gcore.lib.tuple.IntegerPair;

/**
 * @author GuillaumeVDN
 */
public class TypeNone extends GUIItemType {

	public TypeNone(String id) {
		super(id, true, CommonMats.CHEST);
	}

	@Override
	public ActiveHolderItem newActive(ActiveGUI gui, ItemHolder holder, ElementGUIItem item, Replacer replacer) throws ParsingError {
		// parse settings
		ItemStack itemIcon = item.directParseNoCatchOrThrowParsingNull("icon", replacer);
		List<IntegerPair> locations = item.parseLocations(replacer);
		Sound clickSound = item.getClickSound().parse(replacer).orNull();
		Map<ClickType, Consumer<ClickCall>> overrideClicks = item.parseOverrideClicks(replacer);
		// build item
		return new ActiveHolderItem(holder) {
			@Override
			protected void build(ActiveGUI instance, Replacer replacer, Consumer<GUIItem> callback) {
				callback.accept(new GUIItem(item.getId(), locations, itemIcon, clickSound, overrideClicks, null));
			}
		};
	}

}
