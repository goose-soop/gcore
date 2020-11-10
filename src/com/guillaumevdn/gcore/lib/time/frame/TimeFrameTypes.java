package com.guillaumevdn.gcore.lib.time.frame;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.element.struct.container.typable.TypableElementTypes;
import com.guillaumevdn.gcore.lib.time.frame.type.TimeFrameTypeInDay;
import com.guillaumevdn.gcore.lib.time.frame.type.TimeFrameTypeInMonth;
import com.guillaumevdn.gcore.lib.time.frame.type.TimeFrameTypeInWeek;
import com.guillaumevdn.gcore.lib.time.frame.type.TimeFrameTypeInYear;
import com.guillaumevdn.gcore.lib.time.frame.type.TimeFrameTypeLimited;
import com.guillaumevdn.gcore.lib.time.frame.type.TimeFrameTypeNone;

/**
 * @author GuillaumeVDN
 */
public final class TimeFrameTypes extends TypableElementTypes<TimeFrameType> {

	public TimeFrameTypes() {
		super(TimeFrameType.class);
	}

	// types
	public final TimeFrameTypeInDay		DAILY 		= register(new TimeFrameTypeInDay("DAILY"));
	public final TimeFrameTypeInWeek 	WEEKLY 		= register(new TimeFrameTypeInWeek("WEEKLY"));
	public final TimeFrameTypeInMonth	MONTHLY 	= register(new TimeFrameTypeInMonth("MONTHLY"));
	public final TimeFrameTypeInYear 	YEARLY 		= register(new TimeFrameTypeInYear("YEARLY"));
	public final TimeFrameTypeLimited 	LIMITED		= register(new TimeFrameTypeLimited("LIMITED"));
	public final TimeFrameTypeNone 		NONE 		= register(new TimeFrameTypeNone("NONE"));

	// values
	public static TimeFrameTypes inst() {
		return GCore.inst().getTimeFrameTypes();
	}

	@Override
	public TimeFrameType defaultValue() {
		return NONE;
	}

}
