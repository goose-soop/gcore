package com.guillaumevdn.gcore.lib.gui.struct.instance;

import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;
import com.guillaumevdn.gcore.lib.wrapper.Wrapper;

/**
 * @author GuillaumeVDN
 */
public abstract class GUIInstanceItemHolder {

	private String id;

	public GUIInstanceItemHolder(String id) {
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
	public abstract Wrapper<GUIInstanceItem> build(GUIInstance gui, Replacer replacer);

}
