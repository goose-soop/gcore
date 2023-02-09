package com.guillaumevdn.gcore.lib.element.type.basic;

import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementDamageCauseList extends ElementEnumList<DamageCause> {

	public ElementDamageCauseList(Element parent, String id, Need need, Text editorDescription) {
		super(DamageCause.class, parent, id, need, editorDescription);
	}

	@Override
	public Mat editorIconType() {
		return CommonMats.DIAMOND_SWORD;
	}

}
