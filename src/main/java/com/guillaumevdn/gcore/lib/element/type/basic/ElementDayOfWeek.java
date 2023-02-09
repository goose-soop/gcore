package com.guillaumevdn.gcore.lib.element.type.basic;

import java.time.DayOfWeek;

import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementDayOfWeek extends ElementEnum<DayOfWeek> {

	public ElementDayOfWeek(Element parent, String id, Need need, Text editorDescription) {
		super(DayOfWeek.class, parent, id, need, editorDescription);
	}

	@Override
	public Mat editorIconType() {
		return CommonMats.CLOCK;
	}

}
