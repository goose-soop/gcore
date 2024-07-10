package com.guillaumevdn.gcore.lib.gui.element;

import java.util.Collection;
import java.util.Set;

import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.function.TriConsumer;
import com.guillaumevdn.gcore.lib.gui.struct.GUIItem;
import com.guillaumevdn.gcore.lib.gui.struct.active.ActiveGUI;
import com.guillaumevdn.gcore.lib.gui.struct.active.ActiveItemHolder;
import com.guillaumevdn.gcore.lib.gui.struct.active.ItemHolder;

/**
 * @author GuillaumeVDN
 */
public abstract class ExtraGUIItemHolder extends ItemHolder {

	public ExtraGUIItemHolder(String id) {
		super(id);
	}

	@Override
	public ActiveItemHolder newActive(ActiveGUI gui) {
		return new ActiveItemHolder(gui, this) {

			@Override
			protected void buildItems(TriConsumer<Collection<? extends GUIItem>, Set<String>, Integer> callback) throws ParsingError {
				ExtraGUIItemHolder.this.build(callback);
			}

		};
	}

	protected abstract void build(TriConsumer<Collection<? extends GUIItem>, Set<String>, Integer> callback) throws ParsingError;

}
