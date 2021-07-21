package com.guillaumevdn.gcore.lib.element.type.basic;

import java.util.UUID;

import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementUUIDList extends ElementValueList<UUID> {

	public ElementUUIDList(Element parent, String id, Need need, Text editorDescription) {
		super(UUID.class, parent, id, need, editorDescription);
	}

	// ----- editor
	@Override
	public Mat editorIconType() {
		return CommonMats.COMMAND_BLOCK;
	}

}
