package com.guillaumevdn.gcore.lib.configuration.file;

import com.guillaumevdn.gcore.lib.configuration.file.node.SectionNode;
import com.guillaumevdn.gcore.lib.string.StringUtils;

/**
 * @author GuillaumeVDN
 */
public abstract class Node {

	private YMLFile file;
	private SectionNode parent;

	protected Node(YMLFile file) {
		this.file = file;
	}

	public Node(SectionNode parent) {
		this.parent = parent;
		this.file = parent == null ? null : parent.getFile();
	}

	// get
	public YMLFile getFile() {
		return file;
	}

	public SectionNode getParent() {
		return parent;
	}

	public int getDepthLevel() {
		return parent == null ? 0 : parent.getDepthLevel() + 1;
	}

	public final String getPrefix() {
		return StringUtils.repeatString("  ", getDepthLevel());
	}

	// write
	public abstract void write(Appendable writer) throws Throwable;

	// clone
	public abstract Node clone(SectionNode parent);

}
