package com.guillaumevdn.gcore.lib.element.type.basic;

import org.bukkit.Color;

import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementColorList extends ElementValueList<Color> {

	public ElementColorList(Element parent, String id, Need need, Text editorDescription) {
		super(Color.class, parent, id, need, editorDescription);
	}

	// ----- editor
	@Override
	public Mat editorIconType() {
		return CommonMats.PINK_WOOL;
	}

	@Override
	protected String editorNewLine() {
		return getSerializer().serialize(Color.FUCHSIA);
	}

}
