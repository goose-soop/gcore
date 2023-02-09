package com.guillaumevdn.gcore.lib.gui.struct.active;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.function.QuintConsumer;
import com.guillaumevdn.gcore.lib.function.TriConsumer;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.gui.struct.GUIItem;
import com.guillaumevdn.gcore.lib.tuple.IntegerPair;

public abstract class ActiveItemHolderCommon extends ActiveItemHolder {

	public ActiveItemHolderCommon(ActiveGUI instance, ItemHolder holder) {
		super(instance, holder);
	}

	@Override
	protected final void buildItems(TriConsumer<Collection<? extends GUIItem>, Set<String>, Integer> callback) throws ParsingError {
		build((icon, placeholders, forcedDelay, locations, performer) -> {
			GUIItem item = new GUIItem(getHolder().getId(), locations, icon, performer);
			callback.accept(CollectionUtils.asList(item), placeholders, forcedDelay);
		});
	}

	protected abstract void build(QuintConsumer<ItemStack, Set<String>, Integer, List<IntegerPair>, Consumer<ClickCall>> callback) throws ParsingError;

}
