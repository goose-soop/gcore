package com.guillaumevdn.gcore.lib.element.type.basic;


import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.player.PhysicalClickType;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementPhysicalClickType extends ElementEnum<PhysicalClickType> {

	public ElementPhysicalClickType(Element parent, String id, Need need, Text editorDescription) {
		super(PhysicalClickType.class, parent, id, need, editorDescription);
	}

	@Override
	public Mat editorIconType() {
		return CommonMats.OAK_BUTTON;
	}

}
