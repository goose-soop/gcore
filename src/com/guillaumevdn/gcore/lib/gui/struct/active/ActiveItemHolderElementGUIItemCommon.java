package com.guillaumevdn.gcore.lib.gui.struct.active;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.sound.Sound;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.function.TriConsumer;
import com.guillaumevdn.gcore.lib.gui.element.item.ElementGUIItem;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall.ClickType;
import com.guillaumevdn.gcore.lib.gui.struct.GUIItem;
import com.guillaumevdn.gcore.lib.tuple.IntegerPair;

public abstract class ActiveItemHolderElementGUIItemCommon extends ActiveItemHolderElementGUIItem {

	public ActiveItemHolderElementGUIItemCommon(ActiveGUI instance, ItemHolder holder, ElementGUIItem element, boolean requireIcon) {
		super(instance, holder, element, requireIcon);
	}

	// methods
	@Override
	protected final void buildItems(List<IntegerPair> locations, ItemStack itemIcon, Sound clickSound, Map<ClickType, Consumer<ClickCall>> overrideClicks, BiConsumer<Collection<? extends GUIItem>, Integer> callback) throws ParsingError {
		build(itemIcon, (icon, forcedDelay, performer) -> {
			GUIItem item = new GUIItem(getHolder().getId(), locations, icon, clickSound, overrideClicks, performer);
			callback.accept(CollectionUtils.asList(item), forcedDelay);
		});
	}

	protected abstract void build(@Nullable ItemStack itemIcon, TriConsumer<ItemStack, Integer, Consumer<ClickCall>> callback) throws ParsingError;

}
