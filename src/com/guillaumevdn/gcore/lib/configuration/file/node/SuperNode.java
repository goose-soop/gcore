package com.guillaumevdn.gcore.lib.configuration.file.node;

import com.guillaumevdn.gcore.lib.configuration.file.Node;
import com.guillaumevdn.gcore.lib.configuration.file.YMLFile;

/**
 * @author GuillaumeVDN
 */
public final class SuperNode extends SectionNode {

	public SuperNode(YMLFile file) {
		super(file);
	}

	// get
	@Override
	public int getDepthLevel() {
		return -1;
	}

	// write
	@Override
	public void write(Appendable writer) throws Throwable {
		for (int i = 0; i < getNodes().size(); ++i) {
			Node elem = getNodes().get(i);
			elem.write(writer);
			if (i + 1 < getNodes().size() && !(elem instanceof LineBreaksNode) && !(getNodes().get(i + 1) instanceof LineBreaksNode)) {
				writer.append("\n");
			}
		}
	}

}
