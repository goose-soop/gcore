package com.guillaumevdn.gcore.lib.util;

import java.util.Calendar;

import com.guillaumevdn.gcore.GCore;

public enum WeekDay {

	MONDAY(Calendar.MONDAY),
	TUESDAY(Calendar.TUESDAY),
	WEDNESDAY(Calendar.WEDNESDAY),
	THURSDAY(Calendar.THURSDAY),
	FRIDAY(Calendar.FRIDAY),
	SATURDAY(Calendar.SATURDAY),
	SUNDAY(Calendar.SUNDAY);

	// base
	private int calendarField;

	private WeekDay(int calendarField) {
		this.calendarField = calendarField;
	}

	// get
	public int getCalendarField() {
		return calendarField;
	}

	// static methods
	public static WeekDay getCurrent() {
		return getFromCalendarField(GCore.inst().getCalendarInstance().get(Calendar.DAY_OF_WEEK));
	}

	public static WeekDay getFromCalendarField(int calendarField) {
		for (WeekDay day : WeekDay.values()) {
			if (calendarField == day.calendarField) {
				return day;
			}
		}
		return null;
	}

}
