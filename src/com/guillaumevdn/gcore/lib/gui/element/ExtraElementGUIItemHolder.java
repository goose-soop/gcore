package com.guillaumevdn.gcore.lib.gui.element;

import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.function.QuadriConsumer;
import com.guillaumevdn.gcore.lib.gui.element.item.ElementGUIItem;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.gui.struct.active.ActiveGUI;
import com.guillaumevdn.gcore.lib.gui.struct.active.ActiveItemHolder;
import com.guillaumevdn.gcore.lib.gui.struct.active.ActiveItemHolderElementGUIItemCommon;
import com.guillaumevdn.gcore.lib.gui.struct.active.ItemHolder;

/**
 * @author GuillaumeVDN
 */
public abstract class ExtraElementGUIItemHolder extends ItemHolder {

	private ElementGUIItem element;

	public ExtraElementGUIItemHolder(ElementGUIItem element) {
		super(element.getId());
		this.element = element;
	}

	@Override
	public ActiveItemHolder newActive(ActiveGUI gui) {
		return new ActiveItemHolderElementGUIItemCommon(gui, this, element) {
			@Override
			protected void build(ItemStack itemIcon, QuadriConsumer<ItemStack, Set<String>, Integer, Consumer<ClickCall>> callback) throws ParsingError {
				ExtraElementGUIItemHolder.this.build(itemIcon, callback);
			}
		};
	}

	@Override
	public boolean parsePersistent(ActiveGUI gui) {
		return element.getPersistent().directParseOrElse(gui.getReplacer(), false);
	}

	protected abstract void build(ItemStack itemIcon, QuadriConsumer<ItemStack, Set<String>, Integer, Consumer<ClickCall>> callback) throws ParsingError;

}
