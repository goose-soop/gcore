package com.guillaumevdn.gcore.lib.time.in;

import java.time.ZonedDateTime;

/**
 * @author GuillaumeVDN
 */
public class TimeInMonth extends TimeIn {

	private int dayOfMonth;

	public TimeInMonth(int dayOfMonth, int hour, int minute) {
		super(hour, minute);
		this.dayOfMonth = dayOfMonth;
	}

	public int getDayOfMonth() {
		return dayOfMonth;
	}

	@Override
	public ZonedDateTime getCurrent() {
		ZonedDateTime time = ZonedDateTime.now();
		int dayOfMonth = this.dayOfMonth;
		if (dayOfMonth < 1) {
			dayOfMonth = 1;
		}
		int maxDate = time.toLocalDate().lengthOfMonth();
		if (dayOfMonth > maxDate) {
			dayOfMonth = maxDate;
		}
		return time.withDayOfMonth(dayOfMonth).withHour(hour).withMinute(minute).withSecond(0).withNano(0);
	}

}
