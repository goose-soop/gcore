package com.guillaumevdn.gcore.lib.time.frame.type;

import java.time.LocalDateTime;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.time.frame.ElementTimeFrame;
import com.guillaumevdn.gcore.lib.time.in.TimeInMonth;

/**
 * @author GuillaumeVDN
 */
public class TimeFrameTypeInMonth extends TimeFrameTypeIn<TimeInMonth> {

	public TimeFrameTypeInMonth(String id) {
		super(id, new TimeInMonth(1, 0, 0), new TimeInMonth(31, 23, 59));
	}

	// elements
	@Override
	protected void doFillTypeSpecificElements(ElementTimeFrame frame) {
		super.doFillTypeSpecificElements(frame);
		frame.addTimeInMonth("start", Need.optional(), TextEditorGeneric.descriptionTimeFrameInMonthStart);
		frame.addTimeInMonth("end", Need.optional(), TextEditorGeneric.descriptionTimeFrameInMonthEnd);
	}

	@Override
	protected LocalDateTime minusOnePeriod(LocalDateTime time) {
		return time.minusMonths(1);
	}

}
