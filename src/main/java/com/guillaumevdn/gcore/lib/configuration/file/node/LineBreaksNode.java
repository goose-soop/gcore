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

	// ----- get
	public int getCount() {
		return count;
	}

	// ----- write
	@Override
	public void write(Appendable writer, WriteType type) throws Throwable {
		writer.append("\n"); // ignore count actually
		/*for (int i = 0; i < count; ++i) {
			writer.append("\n");
		}*/
	}

	@Override
	public void writeInCompact(Appendable writer, boolean compactParent) throws Throwable {
		throw new UnsupportedOperationException("can't write compact line breaks");
	}

	// ----- clone
	@Override
	public LineBreaksNode clone(SectionNode parent) {
		return new LineBreaksNode(parent, count);
	}

}
