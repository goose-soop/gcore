package com.guillaumevdn.gcore.lib.element.type.basic;

import com.guillaumevdn.gcore.lib.compatibility.bossbar.BossbarFlag;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementBossbarFlag extends ElementEnum<BossbarFlag> {

	public ElementBossbarFlag(Element parent, String id, Need need, Text editorDescription) {
		super(BossbarFlag.class, parent, id, need, editorDescription);
	}

	@Override
	public Mat editorIconType() {
		return CommonMats.PAPER;
	}

}
