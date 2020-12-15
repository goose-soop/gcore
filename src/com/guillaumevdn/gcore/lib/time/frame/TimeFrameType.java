package com.guillaumevdn.gcore.lib.time.frame;

import java.time.ZonedDateTime;

import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.container.typable.TypableElementType;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;
import com.guillaumevdn.gcore.lib.tuple.Pair;

/**
 * @author GuillaumeVDN
 */
public abstract class TimeFrameType extends TypableElementType<ElementTimeFrame> {

	public TimeFrameType(String id, Mat icon) {
		super(id, icon);
	}

	// get current
	public abstract Pair<ZonedDateTime, ZonedDateTime> getBounds(ElementTimeFrame frame, Replacer replacer, int offset);

}
