package com.guillaumevdn.gcore.lib.time.in;

import java.time.DayOfWeek;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementDayOfWeek;
import com.guillaumevdn.gcore.lib.string.Text;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public class ElementTimeInWeek extends ElementTimeIn<TimeInWeek> {

	private ElementDayOfWeek day = addDayOfWeek("day_of_week", Need.optional(DayOfWeek.MONDAY), TextEditorGeneric.descriptionElementTimeDayOfWeek);

	public ElementTimeInWeek(Element parent, String id, Need need, Text editorDescription) {
		super(parent, id, need, editorDescription);
	}

	// ----- get
	public ElementDayOfWeek getDay() {
		return day;
	}

	// ----- parse
	@Override
	public TimeInWeek doParse(Replacer replacer) throws ParsingError {
		DayOfWeek day = this.day.parseNoCatchOrThrowParsingNull(replacer);
		Integer hour = this.hour.parseNoCatchOrThrowParsingNull(replacer);
		Integer minute = this.minute.parseNoCatchOrThrowParsingNull(replacer);
		return new TimeInWeek(day, hour, minute);
	}

}
