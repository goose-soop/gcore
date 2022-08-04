package com.guillaumevdn.gcore.integration.mythicmobs.v5.element;

import java.util.List;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementAbstractEnum;
import com.guillaumevdn.gcore.lib.string.Text;

import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.api.mobs.MythicMob;

/**
 * @author GuillaumeVDN
 */
public class ElementMythicMobsMob extends ElementAbstractEnum<MythicMob> {

	public ElementMythicMobsMob(Element parent, String id, Need need, Text editorDescription) {
		super(MythicMob.class, false, parent, id, need, editorDescription);
	}

	@Override
	public List<MythicMob> getValues() {
		return CollectionUtils.asList(MythicProvider.get().getMobManager().getMobTypes());
	}

	@Override
	public Mat editorIconType() {
		return CommonMats.ZOMBIE_HEAD;
	}

}
