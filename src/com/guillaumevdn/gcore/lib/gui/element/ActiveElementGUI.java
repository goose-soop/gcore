package com.guillaumevdn.gcore.lib.gui.element;

import java.util.Collection;
import java.util.stream.Collectors;

import com.guillaumevdn.gcore.lib.gui.element.item.ElementGUIItem;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.gui.struct.GUIType;
import com.guillaumevdn.gcore.lib.gui.struct.active.ActiveGUI;
import com.guillaumevdn.gcore.lib.gui.struct.active.ItemHolder;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public class ActiveElementGUI extends ActiveGUI {

	private ElementGUI element;

	public ActiveElementGUI(ElementGUI element, Replacer replacer, ClickCall fromCall, Option... options) {
		super(element.getPlugin(), "instance_" + element.getId() + "_" + StringUtils.generateRandomAlphanumericString(5), element.getName().parse(replacer).orElse("?"), element.getSize().parse(replacer).orElse(GUIType.CHEST_6_ROW), replacer, fromCall, options);
		this.element = element;
	}

	// get
	public ElementGUI getElement() {
		return element;
	}

	@Override
	public Collection<ItemHolder> getContents() {
		return element.getContents().stream().map(ElementGUIItem::getHolder).collect(Collectors.toList());
	}

}
