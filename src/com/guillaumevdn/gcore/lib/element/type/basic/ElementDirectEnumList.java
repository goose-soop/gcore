package com.guillaumevdn.gcore.lib.element.type.basic;

import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementDirectEnumList<E extends Enum<E>> extends ElementEnumList<E> {

	public ElementDirectEnumList(Class<E> enumClass, Element parent, String id, Need need, Text editorDescription) {
		super(enumClass, parent, id, need, editorDescription);
	}

	// ----- get
	@Override
	public Mat editorIconType() {
		return CommonMats.ENDER_CHEST;
	}

}
