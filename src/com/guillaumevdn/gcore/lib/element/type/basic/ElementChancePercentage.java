package com.guillaumevdn.gcore.lib.element.type.basic;

import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.number.NumberUtils;
import com.guillaumevdn.gcore.lib.string.Text;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public class ElementChancePercentage extends ElementDouble {

	public ElementChancePercentage(Element parent, String id, Need need, Text editorDescription) {
		super(parent, id, need, 0d, 100d, editorDescription);
	}

	public boolean tryLuck(Replacer replacer) {
		final double chance = directParseOrNull(replacer);
		return NumberUtils.random(0d, 100d) <= chance;
	}

}
