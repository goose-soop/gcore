package com.guillaumevdn.gcore.lib.gui.struct.active;

import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.function.QuadriConsumer;
import com.guillaumevdn.gcore.lib.function.TriConsumer;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.gui.struct.GUIItem;

public abstract class ActiveItemHolderSimpleItem extends ActiveItemHolder {

	public ActiveItemHolderSimpleItem(ActiveGUI instance, ItemHolder holder) {
		super(instance, holder);
	}

	@Override
	protected final void buildItems(TriConsumer<Collection<? extends GUIItem>, Set<String>, Integer> callback) throws ParsingError {
		build((icon, placeholders, forcedDelay, performer) -> {
			final GUIItem item = new GUIItem(getHolder().getId(), icon, performer);
			callback.accept(CollectionUtils.asList(item), placeholders, forcedDelay);
		});
	}

	protected abstract void build(QuadriConsumer<ItemStack, Set<String>, Integer, Consumer<ClickCall>> callback) throws ParsingError;

}
