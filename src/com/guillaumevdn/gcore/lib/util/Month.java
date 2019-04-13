package com.guillaumevdn.gcore.lib.util;

import java.util.Calendar;

public enum Month {

	JANUARY(Calendar.JANUARY),
	FEBRUARY(Calendar.FEBRUARY),
	MARCH(Calendar.MARCH),
	APRIL(Calendar.APRIL),
	MAY(Calendar.MAY),
	JUNE(Calendar.JUNE),
	JULY(Calendar.JULY),
	AUGUST(Calendar.AUGUST),
	SEPTEMBER(Calendar.SEPTEMBER),
	OCTOBER(Calendar.OCTOBER),
	NOVEMBER(Calendar.NOVEMBER),
	DECEMBER(Calendar.DECEMBER);

	// base
	private int calendarField;

	private Month(int calendarField) {
		this.calendarField = calendarField;
	}

	// get
	public int getCalendarField() {
		return calendarField;
	}

	// static methods
	public static Month getCurrent() {
		return getFromCalendarField(Calendar.getInstance().get(Calendar.MONTH));
	}

	public static Month getFromCalendarField(int calendarField) {
		for (Month month : Month.values()) {
			if (calendarField == month.calendarField) {
				return month;
			}
		}
		return null;
	}

}
