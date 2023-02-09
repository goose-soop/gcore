package com.guillaumevdn.gcore.lib.gui.element.item;

import com.guillaumevdn.gcore.lib.gui.struct.active.ActiveGUI;
import com.guillaumevdn.gcore.lib.gui.struct.active.ActiveItemHolder;
import com.guillaumevdn.gcore.lib.gui.struct.active.ItemHolder;

/**
 * @author GuillaumeVDN
 */
public class ElementGUIItemHolder extends ItemHolder {

	private ElementGUIItem element;

	public ElementGUIItemHolder(ElementGUIItem item) {
		super(item.getId());
		this.element = item;
	}

	public ElementGUIItem getElement() {
		return element;
	}

	// -----

	@Override
	public boolean parsePersistent(ActiveGUI instance) {
		return element.getPersistent().parse(instance.getReplacer()).orElse(false);
	}

	@Override
	public ActiveItemHolder newActive(ActiveGUI gui) {
		return element.getType().newActive(gui, this, element);
	}

}
