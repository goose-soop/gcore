package com.guillaumevdn.gcore.lib.gui.struct.active;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.lib.compatibility.sound.Sound;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.gui.element.item.ElementGUIItem;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall.ClickType;
import com.guillaumevdn.gcore.lib.gui.struct.GUIItem;
import com.guillaumevdn.gcore.lib.tuple.IntegerPair;

public abstract class ActiveItemHolderElementGUIItem extends ActiveItemHolder {

	private ElementGUIItem element;
	private boolean requireIcon;

	public ActiveItemHolderElementGUIItem(ActiveGUI instance, ItemHolder holder, ElementGUIItem element, boolean requireIcon) {
		super(instance, holder);
		this.element = element;
		this.requireIcon = requireIcon;
	}

	// get
	public ElementGUIItem getElement() {
		return element;
	}

	// methods
	@Override
	protected final void buildItems(BiConsumer<Collection<? extends GUIItem>, Integer> callback) throws ParsingError {
		ItemStack itemIcon = requireIcon ? element.directParseNoCatchOrThrowParsingNull("icon", getInstance().getReplacer()) : element.directParseOrNull("icon", getInstance().getReplacer());
		List<IntegerPair> locations = element.parseLocations(getInstance().getReplacer());
		Sound clickSound = element.getClickSound().parse(getInstance().getReplacer()).orNull();
		Map<ClickType, Consumer<ClickCall>> overrideClicks = element.parseOverrideClicks(getInstance().getReplacer());
		buildItems(locations, itemIcon, clickSound, overrideClicks, callback);
	}

	protected abstract void buildItems(List<IntegerPair> locations, ItemStack itemIcon, Sound clickSound, Map<ClickType, Consumer<ClickCall>> overrideClicks, BiConsumer<Collection<? extends GUIItem>, Integer> callback) throws ParsingError;

}
