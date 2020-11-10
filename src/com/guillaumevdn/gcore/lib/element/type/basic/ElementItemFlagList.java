package com.guillaumevdn.gcore.lib.element.type.basic;

import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.gui.ItemFlag;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementItemFlagList extends ElementEnumList<ItemFlag> {

	public ElementItemFlagList(Element parent, String id, Need need, Text editorDescription) {
		super(ItemFlag.class, parent, id, need, editorDescription);
	}

	@Override
	public Mat editorIconType() {
		return CommonMats.PAPER;
	}

}
