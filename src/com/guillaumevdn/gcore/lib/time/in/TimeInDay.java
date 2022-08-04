package com.guillaumevdn.gcore.lib.time.in;

import java.time.ZonedDateTime;

/**
 * @author GuillaumeVDN
 */
public class TimeInDay extends TimeIn {

	public TimeInDay(int hour, int minute) {
		super(hour, minute);
	}

	@Override
	public ZonedDateTime getCurrent() {
		return ZonedDateTime.now().withHour(hour).withMinute(minute).withSecond(0).withNano(0);
	}

	@Override
	public ZonedDateTime getDelta(int delta) {
		return getCurrent().plusDays(delta);
	}

}
