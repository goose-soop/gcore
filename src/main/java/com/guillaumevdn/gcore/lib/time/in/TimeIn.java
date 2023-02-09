package com.guillaumevdn.gcore.lib.time.in;

import java.time.ZonedDateTime;

/**
 * @author GuillaumeVDN
 */
public abstract class TimeIn {

	protected final int hour, minute;

	public TimeIn(int hour, int minute) {
		this.hour = hour;
		this.minute = minute;
	}

	// ----- get
	public final int getHour() {
		return hour;
	}

	public final int getMinute() {
		return minute;
	}

	public abstract ZonedDateTime getCurrent();
	public abstract ZonedDateTime getDelta(int delta);

}
