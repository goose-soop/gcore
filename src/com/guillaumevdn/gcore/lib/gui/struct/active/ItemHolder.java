package com.guillaumevdn.gcore.lib.gui.struct.active;

import java.util.Objects;

import javax.annotation.Nonnull;

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

	public boolean parsePersistent(ActiveGUI gui) {
		return false;
	}

	// do
	@Nonnull
	public abstract ActiveItemHolder newActive(ActiveGUI gui);

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
