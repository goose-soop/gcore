package com.guillaumevdn.gcore.lib.element.type.basic;

import org.bukkit.entity.EntityType;

import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementEntityTypeList extends ElementEnumList<EntityType> {

	public ElementEntityTypeList(Element parent, String id, Need need, Text editorDescription) {
		super(EntityType.class, parent, id, need, editorDescription);
	}

	@Override
	public Mat editorIconType() {
		return CommonMats.ZOMBIE_HEAD;
	}

}
