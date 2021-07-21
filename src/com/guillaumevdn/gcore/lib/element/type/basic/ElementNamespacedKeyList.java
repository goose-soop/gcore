package com.guillaumevdn.gcore.lib.element.type.basic;

import org.bukkit.NamespacedKey;

import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementNamespacedKeyList extends ElementValueList<NamespacedKey> {

	public ElementNamespacedKeyList(Element parent, String id, Need need, Text editorDescription) {
		super(NamespacedKey.class, parent, id, need, editorDescription);
	}

	// ----- editor
	@Override
	public Mat editorIconType() {
		return CommonMats.PAPER;
	}

	@Override
	protected String editorNewLine() {
		return getSerializer().serialize(new NamespacedKey("minecraft", "key_here"));
	}

}
