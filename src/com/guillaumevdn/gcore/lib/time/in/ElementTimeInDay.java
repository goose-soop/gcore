package com.guillaumevdn.gcore.lib.time.in;

import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.string.Text;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public class ElementTimeInDay extends ElementTimeIn<TimeInDay> {

	public ElementTimeInDay(Element parent, String id, Need need, Text editorDescription) {
		super("time in day", parent, id, need, editorDescription);
	}

	// parse
	@Override
	public TimeInDay doParse(Replacer replacer) throws ParsingError {
		Integer hour = this.hour.parseNoCatchOrThrowParsingNull(replacer);
		Integer minute = this.minute.parseNoCatchOrThrowParsingNull(replacer);
		return new TimeInDay(hour, minute);
	}

}
