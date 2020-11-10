package com.guillaumevdn.gcore.lib.element.type.basic;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.compatibility.sound.Sound;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementSound extends ElementFakeEnum<Sound> {

	public ElementSound(Element parent, String id, Need need, Text editorDescription) {
		super(Sound.class, parent, id, need, editorDescription, CollectionUtils.asList(Sound.values()));
	}

	@Override
	public Mat editorIconType() {
		return CommonMats.NOTE_BLOCK;
	}

}
