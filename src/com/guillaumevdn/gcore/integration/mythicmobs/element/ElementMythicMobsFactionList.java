package com.guillaumevdn.gcore.integration.mythicmobs.element;

import java.util.List;
import java.util.stream.Collectors;

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
public class ElementMythicMobsFactionList extends ElementAbstractEnumList<String> {

	public ElementMythicMobsFactionList(Element parent, String id, Need need, Text editorDescription) {
		super(String.class, false, parent, id, need, editorDescription);
	}

	@Override
	public List<String> getValues() {
		return MythicMobs.inst().getMobManager().getMobTypes().stream().map(MythicMob::getFaction).distinct().collect(Collectors.toList());
	}

	@Override
	public Mat editorIconType() {
		return CommonMats.ZOMBIE_HEAD;
	}

}
