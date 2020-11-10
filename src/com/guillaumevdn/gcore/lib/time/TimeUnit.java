package com.guillaumevdn.gcore.lib.time;

import java.util.function.Function;

/**
 * @author GuillaumeVDN
 */
public enum TimeUnit {

	MILLISECOND(millis -> millis),
	TICK(ticks -> ticks * 50L),
	SECOND(seconds -> seconds * 1000L),
	MINUTE(minutes -> minutes * 60000L),
	HOUR(hours -> hours * 3600000L),
	DAY(days -> days * 8640000L),
	WEEK(weeks -> weeks * 60480000L),
	MONTH(months -> months * 2678400000L);

	// base
	private Function<Long, Long> toMillis;

	TimeUnit(Function<Long, Long> toMillis) {
		this.toMillis = toMillis;
	}

	// do
	public long toMillis(long duration) {
		return toMillis.apply(duration);
	}

}
