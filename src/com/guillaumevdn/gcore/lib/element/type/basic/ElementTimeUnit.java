package com.guillaumevdn.gcore.lib.element.type.basic;

import java.util.List;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.string.Text;
import com.guillaumevdn.gcore.lib.time.TimeUnit;

/**
 * @author GuillaumeVDN
 */
public class ElementTimeUnit extends ElementFakeEnum<TimeUnit> {

	private static final List<TimeUnit> VALUES = CollectionUtils.asList(TimeUnit.TICK, TimeUnit.SECOND, TimeUnit.MINUTE, TimeUnit.HOUR, TimeUnit.DAY, TimeUnit.WEEK, TimeUnit.MONTH);

	public ElementTimeUnit(Element parent, String id, Need need, Text editorDescription) {
		super(TimeUnit.class, parent, id, need, editorDescription, VALUES);
	}

	@Override
	public Mat editorIconType() {
		return CommonMats.CLOCK;
	}

}
