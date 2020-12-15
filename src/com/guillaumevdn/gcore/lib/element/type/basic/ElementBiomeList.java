package com.guillaumevdn.gcore.lib.element.type.basic;

import org.bukkit.block.Biome;

import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementBiomeList extends ElementEnumList<Biome> {

	public ElementBiomeList(Element parent, String id, Need need, Text editorDescription) {
		super(Biome.class, parent, id, need, editorDescription);
	}

	@Override
	public Mat editorIconType() {
		return CommonMats.GRASS_BLOCK;
	}

}
