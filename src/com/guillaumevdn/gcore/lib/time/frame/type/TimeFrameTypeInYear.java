package com.guillaumevdn.gcore.lib.time.frame.type;

import java.time.LocalDateTime;
import java.time.Month;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.time.frame.ElementTimeFrame;
import com.guillaumevdn.gcore.lib.time.in.TimeInYear;

/**
 * @author GuillaumeVDN
 */
public class TimeFrameTypeInYear extends TimeFrameTypeIn<TimeInYear> {

	public TimeFrameTypeInYear(String id) {
		super(id, new TimeInYear(Month.JANUARY, 1, 0, 0), new TimeInYear(Month.DECEMBER, 31, 23, 59));
	}

	// elements
	@Override
	protected void doFillTypeSpecificElements(ElementTimeFrame frame) {
		super.doFillTypeSpecificElements(frame);
		frame.addTimeInYear("start", Need.optional(), TextEditorGeneric.descriptionTimeFrameInYearStart);
		frame.addTimeInYear("end", Need.optional(), TextEditorGeneric.descriptionTimeFrameInYearEnd);
	}

	@Override
	protected LocalDateTime minusOnePeriod(LocalDateTime time) {
		return time.minusYears(1);
	}

}
