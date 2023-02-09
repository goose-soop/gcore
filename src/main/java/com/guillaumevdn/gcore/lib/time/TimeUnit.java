package com.guillaumevdn.gcore.lib.time;

import java.util.function.Function;

/**
 * @author GuillaumeVDN
 */
public enum TimeUnit {

	MILLISECOND(millis -> millis),
	TICK(ticks -> ticks * 50L),
	SECOND(seconds -> seconds * 1000L),
	MINUTE(minutes -> SECOND.toMillis(minutes * 60L)),
	HOUR(hours -> MINUTE.toMillis(hours * 60L)),
	DAY(days -> HOUR.toMillis(days * 24L)),
	WEEK(weeks -> DAY.toMillis(weeks * 7L)),
	MONTH(months -> WEEK.toMillis(months * 31L));

	// ----- base
	private Function<Long, Long> toMillis;

	TimeUnit(Function<Long, Long> toMillis) {
		this.toMillis = toMillis;
	}

	// ----- do
	public long toMillis(long duration) {
		return toMillis.apply(duration);
	}

	public int toTicks(long duration) {
		return (int) (toMillis(duration) / 50L);
	}

}
