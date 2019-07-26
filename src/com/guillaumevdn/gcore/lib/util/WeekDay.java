package com.guillaumevdn.gcore.lib.util;

import java.util.Calendar;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.GLocale;
import com.guillaumevdn.gcore.lib.messenger.Text;

public enum WeekDay {

	MONDAY(Calendar.MONDAY, GLocale.MISC_GENERIC_DAY_MONDAY),
	TUESDAY(Calendar.TUESDAY, GLocale.MISC_GENERIC_DAY_TUESDAY),
	WEDNESDAY(Calendar.WEDNESDAY, GLocale.MISC_GENERIC_DAY_WEDNESDAY),
	THURSDAY(Calendar.THURSDAY, GLocale.MISC_GENERIC_DAY_THURSDAY),
	FRIDAY(Calendar.FRIDAY, GLocale.MISC_GENERIC_DAY_FRIDAY),
	SATURDAY(Calendar.SATURDAY, GLocale.MISC_GENERIC_DAY_SATURDAY),
	SUNDAY(Calendar.SUNDAY, GLocale.MISC_GENERIC_DAY_SUNDAY);

	// base
	private int calendarField;
	private Text text;

	private WeekDay(int calendarField, Text text) {
		this.calendarField = calendarField;
		this.text = text;
	}

	// get
	public int getCalendarField() {
		return calendarField;
	}

	public Text getText() {
		return text;
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
