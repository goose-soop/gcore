package com.guillaumevdn.gcore.lib.gui.element.item.type.types.border;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.lib.compatibility.sound.Sound;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall.ClickType;
import com.guillaumevdn.gcore.lib.gui.struct.GUIItem;
import com.guillaumevdn.gcore.lib.tuple.IntegerPair;

/**
 * @author GuillaumeVDN
 */
public class BorderGUIItem extends GUIItem {

	public BorderGUIItem(String id, ItemStack item) {
		super(id, item);
	}

	public BorderGUIItem(String id, ItemStack item, Consumer<ClickCall> clickPerformer) {
		super(id, item, clickPerformer);
	}

	public BorderGUIItem(String id, ItemStack item, Sound clickSound, Map<ClickType, Consumer<ClickCall>> overrideClicks, Consumer<ClickCall> clickPerformer) {
		super(id, item, clickSound, overrideClicks, clickPerformer);
	}

	public BorderGUIItem(String id, int preferredSlot, ItemStack item) {
		super(id, preferredSlot, item);
	}

	public BorderGUIItem(String id, int preferredSlot, ItemStack item, Consumer<ClickCall> clickPerformer) {
		super(id, preferredSlot, item, clickPerformer);
	}

	public BorderGUIItem(String id, int preferredSlot, ItemStack item, Sound clickSound, Map<ClickType, Consumer<ClickCall>> overrideClicks, Consumer<ClickCall> clickPerformer) {
		super(id, preferredSlot, item, clickSound, overrideClicks, clickPerformer);
	}

	public BorderGUIItem(String id, List<IntegerPair> preferredLocations, ItemStack item) {
		super(id, preferredLocations, item);
	}

	public BorderGUIItem(String id, List<IntegerPair> preferredLocations, ItemStack item, Consumer<ClickCall> clickPerformer) {
		super(id, preferredLocations, item, clickPerformer);
	}

	public BorderGUIItem(String id, List<IntegerPair> preferredLocations, ItemStack item, Sound clickSound, Map<ClickType, Consumer<ClickCall>> overrideClicks, Consumer<ClickCall> clickPerformer) {
		super(id, preferredLocations, item, clickSound, overrideClicks, clickPerformer);
	}

}
