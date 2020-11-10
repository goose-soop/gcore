package com.guillaumevdn.gcore.lib.time.in;

import java.time.LocalDateTime;

/**
 * @author GuillaumeVDN
 */
public class TimeInDay extends TimeIn {

	public TimeInDay(int hour, int minute) {
		super(hour, minute);
	}

	// get
	@Override
	public LocalDateTime getCurrent() {
		return LocalDateTime.now().withHour(hour).withMinute(minute).withSecond(0).withNano(0);
	}

}
