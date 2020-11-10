package com.guillaumevdn.gcore.lib.time.in;

import java.time.LocalDateTime;

/**
 * @author GuillaumeVDN
 */
public class TimeInMonth extends TimeIn {

	private int dayOfMonth;

	public TimeInMonth(int dayOfMonth, int hour, int minute) {
		super(hour, minute);
		this.dayOfMonth = dayOfMonth;
	}

	// get
	public int getDayOfMonth() {
		return dayOfMonth;
	}

	@Override
	public LocalDateTime getCurrent() {
		return LocalDateTime.now().withDayOfMonth(dayOfMonth).withHour(hour).withMinute(minute).withSecond(0).withNano(0);
	}

}
