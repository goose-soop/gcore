package com.guillaumevdn.gcore.lib.time;

/**
 * @author GuillaumeVDN
 */
public final class TimeUtils {

	public static long getMillisSince(long oldTime) {
		if (oldTime == 0L) {
			return -1L;
		} else {
			return System.currentTimeMillis() - oldTime;
		}
	}

	public static long getSecondsInMillis(int seconds) {
		return (long) (seconds) * 1000L;
	}

	public static long getMinutesInMillis(int minutes) {
		return (long) (minutes) * 60L * 1000L;
	}

	public static long getHoursInMillis(int hours) {
		return (long) (hours) * 60L * 60L * 1000L;
	}

	public static int getMillisInSeconds(long millis) {
		return (int) (millis / 1000L);
	}

	public static int getMillisInMinutes(long millis) {
		return getMillisInSeconds(millis) / 60;
	}

	public static int getMillisInHours(long millis) {
		return getMillisInSeconds(millis) / 60;
	}

	public static long getSecondsInTicks(int seconds) {
		return (long) (seconds) * 20L;
	}

	public static long getMinutesInTicks(int minutes) {
		return getSecondsInTicks(minutes * 60);
	}

	public static long getHoursInTicks(int hours) {
		return getSecondsInTicks(hours * 60 * 60);
	}

}
