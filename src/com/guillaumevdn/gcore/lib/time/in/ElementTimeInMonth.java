package com.guillaumevdn.gcore.lib.time.in;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementInteger;
import com.guillaumevdn.gcore.lib.string.Text;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public class ElementTimeInMonth extends ElementTimeIn<TimeInMonth> {

	private ElementInteger dayOfMonth = addInteger("day_of_month", Need.optional(), 1, 31, TextEditorGeneric.descriptionElementTimeDayOfMonth);

	public ElementTimeInMonth(Element parent, String id, Need need, Text editorDescription) {
		super(parent, id, need, editorDescription);
	}

	// ----- get
	public ElementInteger getDayOfMonth() {
		return dayOfMonth;
	}

	// ----- parse
	@Override
	public TimeInMonth doParse(Replacer replacer) throws ParsingError {
		Integer dayOfMonth = this.dayOfMonth.parseNoCatchOrThrowParsingNull(replacer);
		Integer hour = this.hour.parseNoCatchOrThrowParsingNull(replacer);
		Integer minute = this.minute.parseNoCatchOrThrowParsingNull(replacer);
		return new TimeInMonth(dayOfMonth, hour, minute);
	}

}
