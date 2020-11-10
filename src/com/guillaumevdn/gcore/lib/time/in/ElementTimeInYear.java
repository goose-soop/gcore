package com.guillaumevdn.gcore.lib.time.in;

import java.time.Month;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementInteger;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementMonth;
import com.guillaumevdn.gcore.lib.string.Text;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public class ElementTimeInYear extends ElementTimeIn<TimeInYear> {

	private ElementMonth month = addMonth("month", Need.optional(), TextEditorGeneric.descriptionElementTimeMonth);
	private ElementInteger dayOfMonth = addInteger("day_of_month", Need.optional(0), 1, 31, TextEditorGeneric.descriptionElementTimeDayOfMonth);

	public ElementTimeInYear(Element parent, String id, Need need, Text editorDescription) {
		super("time in year", parent, id, need, editorDescription);
	}

	// get
	public ElementMonth getMonth() {
		return month;
	}

	public ElementInteger getDayOfMonth() {
		return dayOfMonth;
	}

	// parse
	@Override
	public TimeInYear doParse(Replacer replacer) throws ParsingError {
		Month month = this.month.parseNoCatchOrThrowParsingNull(replacer);
		Integer dayOfMonth = this.dayOfMonth.parseNoCatchOrThrowParsingNull(replacer);
		Integer hour = this.hour.parseNoCatchOrThrowParsingNull(replacer);
		Integer minute = this.minute.parseNoCatchOrThrowParsingNull(replacer);
		return new TimeInYear(month, dayOfMonth, hour, minute);
	}

}
