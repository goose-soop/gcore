package com.guillaumevdn.gcore.lib.gui;

import java.io.File;

import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.gui.element.ElementGUI;
import com.guillaumevdn.gcore.lib.gui.element.item.ElementGUIItem;

/**
 * @author GuillaumeVDN
 */
public class ConfirmGUIElement extends ElementGUI {

	private ElementGUIItem itemConfirm = add(new ElementGUIItem(this, "item_confirm", Need.required(), null));
	private ElementGUIItem itemCancel = add(new ElementGUIItem(this, "item_cancel", Need.optional(), null));

	public ConfirmGUIElement(File file, String id) {
		super(file, id, true);
	}

	public ElementGUIItem getItemConfirm() {
		return itemConfirm;
	}

	public ElementGUIItem getItemCancel() {
		return itemCancel;
	}

}
