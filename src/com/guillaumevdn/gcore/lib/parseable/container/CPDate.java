package com.guillaumevdn.gcore.lib.parseable.container;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.GLocale;
import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.parseable.ContainerParseable;
import com.guillaumevdn.gcore.lib.parseable.Parseable;
import com.guillaumevdn.gcore.lib.parseable.editor.EditorGUI;
import com.guillaumevdn.gcore.lib.parseable.primitive.PPEnum;
import com.guillaumevdn.gcore.lib.parseable.primitive.PPInteger;
import com.guillaumevdn.gcore.lib.util.Month;
import com.guillaumevdn.gcore.lib.util.WeekDay;

public class CPDate extends ContainerParseable {

	// base
	private PPInteger year = addComponent(new PPInteger("year", this, String.valueOf(GCore.inst().getCalendarInstance().get(Calendar.YEAR)), Integer.MIN_VALUE, Integer.MAX_VALUE, false, 0, EditorGUI.ICON_TECHNICAL, GLocale.GUI_GENERIC_EDITOR_YEARLORE.getLines()));
	private PPEnum<Month> month = addComponent(new PPEnum<Month>("month", this, "FEBRUARY", Month.class, "month", false, 1, EditorGUI.ICON_TECHNICAL, GLocale.GUI_GENERIC_EDITOR_MONTHLORE.getLines()));
	private PPEnum<WeekDay> day = addComponent(new PPEnum<WeekDay>("day", this, "MONDAY", WeekDay.class, "week day", false, 2, EditorGUI.ICON_TECHNICAL, GLocale.GUI_GENERIC_EDITOR_DAYLORE.getLines()));
	private PPInteger hour = addComponent(new PPInteger("hour", this, "16", 0, 23, false, 3, EditorGUI.ICON_NUMBER, GLocale.GUI_GENERIC_EDITOR_HOURLORE.getLines()));
	private PPInteger minute = addComponent(new PPInteger("minute", this, "45", 0, 59, false, 4, EditorGUI.ICON_NUMBER, GLocale.GUI_GENERIC_EDITOR_MINUTELORE.getLines()));

	public CPDate(String id, Parseable parent, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, "time during week", mandatory, editorSlot, editorIcon, editorDescription);
	}

	// get
	public PPInteger getYear() {
		return year;
	}

	public Integer getYear(Player parser) {
		return year.getParsedValue(parser);
	}

	public PPEnum<Month> getMonth() {
		return month;
	}

	public Month getMonth(Player parser) {
		return month.getParsedValue(parser);
	}

	public PPEnum<WeekDay> getDay() {
		return day;
	}

	public WeekDay getDay(Player parser) {
		return day.getParsedValue(parser);
	}

	public PPInteger getHour() {
		return hour;
	}

	public Integer getHour(Player parser) {
		return hour.getParsedValue(parser);
	}

	public PPInteger getMinute() {
		return minute;
	}

	public Integer getMinute(Player parser) {
		return minute.getParsedValue(parser);
	}

	// methods
	public Date toDate(Player parser) {
		// parse
		Integer year = getYear(parser);
		Month month = getMonth(parser);
		WeekDay day = getDay(parser);
		Integer hour = getHour(parser);
		Integer minute = getMinute(parser);
		if (year == null || month == null || day == null || hour == null || minute == null) {
			return null;
		}
		// return
		return new Date(year, month.getCalendarField(), day.getCalendarField(), hour, minute);
	}

	public static enum Status {
		BEFORE, NOW, AFTER, ERROR
    }

	/**
	 * Compare the current 'time during week' to this object (check if we're before, at, or after this object's 'time during week')
	 * @param parser the parsing player
	 * @return a result (returns ERROR if couldn't parse some of the settings)
	 */
	public Status getStatus(Player parser) {
		// parse
		Integer year = getYear(parser);
		Month month = getMonth(parser);
		WeekDay day = getDay(parser);
		Integer hour = getHour(parser);
		Integer minute = getMinute(parser);
		if (year == null || month == null || day == null || hour == null || minute == null) {
			return Status.ERROR;
		}
		// compare
		Calendar calendar = GCore.inst().getCalendarInstance();
		int todayYear = calendar.get(Calendar.YEAR);
		Month todayMonth = Month.getCurrent();
		WeekDay todayDay = WeekDay.getCurrent();
		int todayHour = calendar.get(Calendar.HOUR_OF_DAY);
		int todayMinute = calendar.get(Calendar.MINUTE);
		// we're before or after year
		if (todayYear < year) {
			return Status.BEFORE;
		} else if (todayYear > year) {
			return Status.AFTER;
		}
		// we're before or after month
		if (todayMonth.ordinal() < month.ordinal()) {
			return Status.BEFORE;
		} else if (todayMonth.ordinal() > month.ordinal()) {
			return Status.AFTER;
		}
		// we're before or after day
		if (todayDay.ordinal() < day.ordinal()) {
			return Status.BEFORE;
		} else if (todayDay.ordinal() > day.ordinal()) {
			return Status.AFTER;
		}
		// same day ; we're before or after hour
		if (todayHour < hour) {
			return Status.BEFORE;
		} else if (todayHour > hour) {
			return Status.AFTER;
		}
		// same hour ; we're before or after minute
		if (todayMinute < minute) {
			return Status.BEFORE;
		} else if (todayMinute > minute) {
			return Status.AFTER;
		}
		// same minute ; it's now !
		return Status.NOW;
	}

	// clone
	protected CPDate() {
		super();
	}

	@Override
	public CPDate clone() {
		return (CPDate) super.clone();
	}

}
