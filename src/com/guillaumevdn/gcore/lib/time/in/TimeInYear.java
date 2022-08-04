package com.guillaumevdn.gcore.lib.time.in;

import java.time.Month;
import java.time.ZonedDateTime;

/**
 * @author GuillaumeVDN
 */
public class TimeInYear extends TimeIn {

	private Month month;
	private int dayOfMonth;

	public TimeInYear(Month month, int dayOfMonth, int hour, int minute) {
		super(hour, minute);
		this.month = month;
		this.dayOfMonth = dayOfMonth;
	}

	public Month getMonth() {
		return month;
	}

	public int getDayOfMonth() {
		return dayOfMonth;
	}

	@Override
	public ZonedDateTime getCurrent() {
		return ZonedDateTime.now().with(month).withDayOfMonth(dayOfMonth).withHour(hour).withMinute(minute).withSecond(0).withNano(0);
	}

	@Override
	public ZonedDateTime getDelta(int delta) {
		return getCurrent().plusYears(delta);
	}

}
