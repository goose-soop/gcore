package com.guillaumevdn.gcore.lib.time.in;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

/**
 * @author GuillaumeVDN
 */
public class TimeInWeek extends TimeIn {

	private DayOfWeek day;

	public TimeInWeek(DayOfWeek day, int hour, int minute) {
		super(hour, minute);
		this.day = day;
	}

	// get
	public DayOfWeek getDay() {
		return day;
	}

	@Override
	public LocalDateTime getCurrent() {
		return LocalDateTime.now().with(day).withHour(hour).withMinute(minute).withSecond(0).withNano(0);
	}

}
