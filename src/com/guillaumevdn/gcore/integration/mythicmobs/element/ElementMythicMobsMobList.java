package com.guillaumevdn.gcore.integration.mythicmobs.element;

import java.util.List;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementAbstractEnumList;
import com.guillaumevdn.gcore.lib.string.Text;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;

/**
 * @author GuillaumeVDN
 */
public class ElementMythicMobsMobList extends ElementAbstractEnumList<MythicMob> {

	public ElementMythicMobsMobList(Element parent, String id, Need need, Text editorDescription) {
		super(MythicMob.class, false, parent, id, need, editorDescription);
	}

	@Override
	public List<MythicMob> getValues() {
		return CollectionUtils.asList(MythicMobs.inst().getMobManager().getMobTypes());
	}

	@Override
	public Mat editorIconType() {
		return CommonMats.ZOMBIE_HEAD;
	}

}
