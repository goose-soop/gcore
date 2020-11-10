package com.guillaumevdn.gcore.lib.time.in;

import java.time.LocalDateTime;
import java.time.Month;

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

	// get
	public Month getMonth() {
		return month;
	}

	public int getDayOfMonth() {
		return dayOfMonth;
	}

	@Override
	public LocalDateTime getCurrent() {
		return LocalDateTime.now().with(month).withDayOfMonth(dayOfMonth).withHour(hour).withMinute(minute).withSecond(0).withNano(0);
	}

}
