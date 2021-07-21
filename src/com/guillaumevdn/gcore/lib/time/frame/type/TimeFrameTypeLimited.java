package com.guillaumevdn.gcore.lib.time.frame.type;

import java.time.ZonedDateTime;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;
import com.guillaumevdn.gcore.lib.time.frame.ElementTimeFrame;
import com.guillaumevdn.gcore.lib.time.frame.TimeFrameType;
import com.guillaumevdn.gcore.lib.time.in.ElementTimeInYear;
import com.guillaumevdn.gcore.lib.time.in.TimeInYear;
import com.guillaumevdn.gcore.lib.tuple.Pair;

/**
 * @author GuillaumeVDN
 */
public class TimeFrameTypeLimited extends TimeFrameType {

	public TimeFrameTypeLimited(String id) {
		super(id, CommonMats.CLOCK);
	}

	// ----- elements
	@Override
	protected void doFillTypeSpecificElements(ElementTimeFrame frame) {
		super.doFillTypeSpecificElements(frame);
		frame.addTimeInYear("start", Need.optional(), TextEditorGeneric.descriptionTimeFrameLimitedStart);
		frame.addTimeInYear("end", Need.optional(), TextEditorGeneric.descriptionTimeFrameLimitedEnd);
	}

	// ----- get
	@Override
	public Pair<ZonedDateTime, ZonedDateTime> getBounds(ElementTimeFrame frame, Replacer replacer, int offset) {
		// offset isn't allowed here
		if (offset != 0) {
			return null;
		}
		// invalid bounds
		TimeInYear startTime = frame.getElement("start").orNull().readContains() ? frame.getElementAs("start", ElementTimeInYear.class).parse(replacer).orNull() : null;
		TimeInYear endTime = frame.getElement("end").orNull().readContains() ? frame.getElementAs("end", ElementTimeInYear.class).parse(replacer).orNull() : null;
		if (startTime == null || endTime == null) {
			return null;
		}
		ZonedDateTime start = startTime.getCurrent();
		ZonedDateTime end = endTime.getCurrent();
		if (start.isAfter(end)) {
			return null;
		}
		// not in bounds
		ZonedDateTime now = ConfigGCore.timeNow();
		if (now.isBefore(start) || now.isAfter(end)) {
			return null;
		}
		return Pair.of(start, end);
	}

}
