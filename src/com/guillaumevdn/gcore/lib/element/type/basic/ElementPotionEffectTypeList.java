package com.guillaumevdn.gcore.lib.element.type.basic;

import org.bukkit.potion.PotionEffectType;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementPotionEffectTypeList extends ElementFakeEnumList<PotionEffectType> {

	public ElementPotionEffectTypeList(Element parent, String id, Need need, Text editorDescription) {
		super(PotionEffectType.class, parent, id, need, editorDescription, CollectionUtils.asList(PotionEffectType.values()));
	}

	@Override
	public Mat editorIconType() {
		return CommonMats.POTION;
	}

}
