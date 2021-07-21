package com.guillaumevdn.gcore.lib.configuration.file.node;

import com.guillaumevdn.gcore.lib.configuration.file.Node;
import com.guillaumevdn.gcore.lib.configuration.file.YMLFile;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;

/**
 * @author GuillaumeVDN
 */
public abstract class ConfigNode extends Node {

	private String id;

	ConfigNode(YMLFile file) {
		super(file);
		this.id = "";
	}

	public ConfigNode(SectionNode parent, String id) {
		super(parent);
		this.id = id; // NOT to lowercase : some keys are actually uppercase !
	}

	// ----- get
	public String getId() {
		return id;
	}

	public abstract String getTrailingComment();

	// ----- object
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		ConfigNode other = ObjectUtils.castOrNull(obj, ConfigNode.class);
		return other != null && id.equals(other.id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public String toString() {
		return getId();
	}


	// ----- clone
	@Override
	public abstract ConfigNode clone(SectionNode parent);

}
