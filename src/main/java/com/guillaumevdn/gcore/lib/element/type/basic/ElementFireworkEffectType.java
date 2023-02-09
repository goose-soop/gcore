package com.guillaumevdn.gcore.lib.element.type.basic;

import org.bukkit.FireworkEffect;

import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementFireworkEffectType extends ElementEnum<FireworkEffect.Type> {

	public ElementFireworkEffectType(Element parent, String id, Need need, Text editorDescription) {
		super(FireworkEffect.Type.class, parent, id, need, editorDescription);
	}

	@Override
	public Mat editorIconType() {
		return CommonMats.NETHER_STAR;
	}

}
