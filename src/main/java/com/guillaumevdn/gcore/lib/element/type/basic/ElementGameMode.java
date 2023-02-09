package com.guillaumevdn.gcore.lib.element.type.basic;

import org.bukkit.GameMode;

import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementGameMode extends ElementEnum<GameMode> {

	public ElementGameMode(Element parent, String id, Need need, Text editorDescription) {
		super(GameMode.class, parent, id, need, editorDescription);
	}

	@Override
	public Mat editorIconType() {
		return CommonMats.BOOK;
	}

}
