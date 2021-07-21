package com.guillaumevdn.gcore.lib.gui.struct.active;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.lib.compatibility.sound.Sound;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.function.TriConsumer;
import com.guillaumevdn.gcore.lib.gui.element.item.ElementGUIItem;
import com.guillaumevdn.gcore.lib.gui.element.item.type.IconNeed;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall.ClickType;
import com.guillaumevdn.gcore.lib.gui.struct.GUIItem;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;
import com.guillaumevdn.gcore.lib.tuple.IntegerPair;

public abstract class ActiveItemHolderElementGUIItem extends ActiveItemHolder {

	private ElementGUIItem element;

	public ActiveItemHolderElementGUIItem(ActiveGUI instance, ItemHolder holder, ElementGUIItem element) {
		super(instance, holder);
		this.element = element;
	}

	public ElementGUIItem getElement() {
		return element;
	}

	@Override
	protected final void buildItems(TriConsumer<Collection<? extends GUIItem>, Set<String>, Integer> callback) throws ParsingError {
		ItemStack itemIcon = element.getType().getIconNeed().equals(IconNeed.REQUIRED) ? element.directParseNoCatchOrThrowParsingNull("icon", Replacer.GENERIC) : element.directParseOrNull("icon", Replacer.GENERIC);  // use a generic replacer ; placeholders will be parsed in the method below (and remembered for update efficiency)
		List<IntegerPair> locations = element.parseLocations(getInstance().getReplacer());
		Sound clickSound = element.getClickSound().parse(getInstance().getReplacer()).orNull();
		Map<ClickType, Consumer<ClickCall>> overrideClicks = element.parseOverrideClicks(getInstance().getReplacer());
		buildItems(locations, itemIcon, clickSound, overrideClicks, callback);
	}

	protected abstract void buildItems(List<IntegerPair> locations, ItemStack itemIcon, Sound clickSound, Map<ClickType, Consumer<ClickCall>> overrideClicks, TriConsumer<Collection<? extends GUIItem>, Set<String>, Integer> callback) throws ParsingError;

}
