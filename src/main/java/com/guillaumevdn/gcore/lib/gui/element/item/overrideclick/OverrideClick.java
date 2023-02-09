package com.guillaumevdn.gcore.lib.gui.element.item.overrideclick;

import java.util.List;

import com.guillaumevdn.gcore.lib.element.type.basic.LinearObject;

/**
 * @author GuillaumeVDN
 */
public class OverrideClick extends LinearObject<OverrideClickType> {

	public OverrideClick(OverrideClickType type) {
		this(type, null);
	}

	public OverrideClick(OverrideClickType type, List<String> arguments) {
		super(type, arguments);
	}
	
}
