package com.guillaumevdn.gcore.lib.location.position;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.container.typable.ElementTypableElementType;
import com.guillaumevdn.gcore.lib.element.struct.container.typable.TypableContainerElement;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParseableElement;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsedCache;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.string.Text;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public class ElementPosition extends TypableContainerElement<PositionType> implements ParseableElement<Position> {

	public ElementPosition(Element parent, String id, Need need, Text editorDescription) {
		super(PositionTypes.inst(), "position", parent, id, need, editorDescription);
	}

	@Override
	protected ElementTypableElementType<PositionType> addType() {
		return add(new ElementPositionType(this, "type", TextEditorGeneric.descriptionPositionType));
	}

	// parsing
	private ParsedCache<Position> cache = new ParsedCache<>();

	@Override
	public ParsedCache<Position> getCache() {
		return hasParseableLocations() ? null : cache;
	}

	@Override
	public final void resetCache() {
		cache.clear();
	}

	@Override
	public Position doParse(Replacer replacer) throws ParsingError {
		return getType().doParse(this, replacer);
	}

}
