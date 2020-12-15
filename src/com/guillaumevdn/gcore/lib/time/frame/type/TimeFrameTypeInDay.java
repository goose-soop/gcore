package com.guillaumevdn.gcore.lib.time.frame.type;

import java.time.ZonedDateTime;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.time.frame.ElementTimeFrame;
import com.guillaumevdn.gcore.lib.time.in.TimeInDay;

/**
 * @author GuillaumeVDN
 */
public class TimeFrameTypeInDay extends TimeFrameTypeIn<TimeInDay> {

	public TimeFrameTypeInDay(String id) {
		super(id, new TimeInDay(0, 0), new TimeInDay(23, 59));
	}

	// elements
	@Override
	protected void doFillTypeSpecificElements(ElementTimeFrame frame) {
		super.doFillTypeSpecificElements(frame);
		frame.addTimeInDay("start", Need.optional(), TextEditorGeneric.descriptionTimeFrameInWeekStart);
		frame.addTimeInDay("end", Need.optional(), TextEditorGeneric.descriptionTimeFrameInWeekEnd);
	}

	@Override
	protected ZonedDateTime minusOnePeriod(ZonedDateTime time) {
		return time.minusDays(1);
	}

}
