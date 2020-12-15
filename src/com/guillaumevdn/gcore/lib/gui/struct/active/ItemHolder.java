package com.guillaumevdn.gcore.lib.gui.struct.active;

import java.util.Objects;

import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public abstract class ItemHolder {

	private String id;

	public ItemHolder(String id) {
		this.id = id;
	}

	// get
	public String getId() {
		return id;
	}

	public boolean getPersistent(Replacer replacer) {
		return false;
	}

	// do
	public abstract ActiveHolderItem newActive(ActiveGUI gui) throws ParsingError;

	// object
	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ItemHolder && ((ItemHolder) obj).id.equals(id);
	}

}
