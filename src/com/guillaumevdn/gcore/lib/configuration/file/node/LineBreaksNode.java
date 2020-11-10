package com.guillaumevdn.gcore.lib.configuration.file.node;

import com.guillaumevdn.gcore.lib.configuration.file.Node;

/**
 * @author GuillaumeVDN
 */
public class LineBreaksNode extends Node {

	private final int count;

	public LineBreaksNode(SectionNode parent, int count) {
		super(parent);
		this.count = count;
	}

	// get
	public int getCount() {
		return count;
	}

	// write
	@Override
	public void write(Appendable writer) throws Throwable {
		writer.append("\n"); // ignore count actually
		/*for (int i = 0; i < count; ++i) {
			writer.append("\n");
		}*/
	}

	// clone
	@Override
	public LineBreaksNode clone(SectionNode parent) {
		return new LineBreaksNode(parent, count);
	}

}
