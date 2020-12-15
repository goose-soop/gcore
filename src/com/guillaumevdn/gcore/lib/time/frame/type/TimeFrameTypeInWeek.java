package com.guillaumevdn.gcore.lib.time.frame.type;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.time.frame.ElementTimeFrame;
import com.guillaumevdn.gcore.lib.time.in.TimeInWeek;

/**
 * @author GuillaumeVDN
 */
public class TimeFrameTypeInWeek extends TimeFrameTypeIn<TimeInWeek> {

	public TimeFrameTypeInWeek(String id) {
		super(id, new TimeInWeek(DayOfWeek.MONDAY, 0, 0), new TimeInWeek(DayOfWeek.SUNDAY, 23, 59));
	}

	// elements
	@Override
	protected void doFillTypeSpecificElements(ElementTimeFrame frame) {
		super.doFillTypeSpecificElements(frame);
		frame.addTimeInWeek("start", Need.optional(), TextEditorGeneric.descriptionTimeFrameInWeekStart);
		frame.addTimeInWeek("end", Need.optional(), TextEditorGeneric.descriptionTimeFrameInWeekEnd);
	}

	@Override
	protected ZonedDateTime minusOnePeriod(ZonedDateTime time) {
		return time.minusWeeks(1);
	}

}
