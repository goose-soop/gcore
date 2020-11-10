package com.guillaumevdn.gcore.lib.element.type.basic;

import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.permission.Permission;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementPermission extends ElementValue<Permission> {

	public ElementPermission(Element parent, String id, Need need, Text editorDescription) {
		super(Permission.class, parent, id, need, editorDescription);
	}

	// editor
	@Override
	public Mat editorIconType() {
		return CommonMats.IRON_DOOR;
	}

}
