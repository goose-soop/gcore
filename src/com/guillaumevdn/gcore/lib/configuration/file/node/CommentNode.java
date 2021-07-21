package com.guillaumevdn.gcore.lib.configuration.file.node;

import java.util.List;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.configuration.file.Node;

/**
 * @author GuillaumeVDN
 */
public class CommentNode extends Node {

	private List<String> lines;

	public CommentNode(SectionNode parent, List<String> lines) {
		super(parent);
		this.lines = lines;
	}

	// ----- get
	public List<String> getLines() {
		return lines;
	}

	// ----- write
	@Override
	public void write(Appendable writer, WriteType type) throws Throwable {
		String prefix = getPrefix();
		for (String line : lines) {
			writer.append(prefix + "#" + line + "\n");
		}
	}

	@Override
	public void writeInCompact(Appendable writer, boolean compactParent) throws Throwable {
		throw new UnsupportedOperationException("can't write a compact comment");
	}

	// ----- clone
	@Override
	public CommentNode clone(SectionNode parent) {
		return new CommentNode(parent, CollectionUtils.asList(lines));
	}

}
