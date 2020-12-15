package com.guillaumevdn.gcore.lib.time.frame;

import java.time.ZonedDateTime;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.container.typable.ElementTypableElementType;
import com.guillaumevdn.gcore.lib.element.struct.container.typable.TypableContainerElement;
import com.guillaumevdn.gcore.lib.string.Text;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;
import com.guillaumevdn.gcore.lib.tuple.Pair;

/**
 * @author GuillaumeVDN
 */
public class ElementTimeFrame extends TypableContainerElement<TimeFrameType> {

	public ElementTimeFrame(Element parent, String id, Need need, Text editorDescription) {
		super(TimeFrameTypes.inst(), "time frame", parent, id, need, editorDescription);
	}

	@Override
	protected ElementTypableElementType<TimeFrameType> addType() {
		return add(new ElementTimeFrameType(this, "type", TextEditorGeneric.descriptionTimeFrameType));
	}

	// match
	public Pair<ZonedDateTime, ZonedDateTime> getActive(Replacer replacer) {
		return getBounds(replacer, 0);
	}

	public Pair<ZonedDateTime, ZonedDateTime> getNext(Replacer replacer) {
		return getBounds(replacer, 1);
	}

	public Pair<ZonedDateTime, ZonedDateTime> getBounds(Replacer replacer, int offset) {
		return getType().getBounds(this, replacer, offset);
	}

}
