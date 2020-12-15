package com.guillaumevdn.gcore.lib.gui.element.item.overrideclick;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementLinear;
import com.guillaumevdn.gcore.lib.serialization.Serializer;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementOverrideClick extends ElementLinear<OverrideClickType, OverrideClick> {

	public ElementOverrideClick(Element parent, String id, Need need, Text editorDescription) {
		super(Serializer.OVERRIDE_CLICK, parent, id, need, editorDescription, CollectionUtils.asList(OverrideClickType.values()));
	}

	@Override
	public Mat editorIconType() {
		return CommonMats.OAK_BUTTON;
	}

}
