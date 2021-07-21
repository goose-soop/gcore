package com.guillaumevdn.gcore.lib.element.type.list;

import java.util.List;

import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.list.ListElement;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParseableListElement;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsedCache;
import com.guillaumevdn.gcore.lib.location.position.ElementPosition;
import com.guillaumevdn.gcore.lib.location.position.Position;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementPositionList extends ListElement<ElementPosition> implements ParseableListElement<Position, ElementPosition> {

	public ElementPositionList(Element parent, String id, Need need, Text editorDescription) {
		super("position", true, parent, id, need, editorDescription);
	}

	// ----- element
	@Override
	protected ElementPosition createElement(String elementId) {
		return new ElementPosition(this, elementId, Need.optional(), null);
	}

	// ----- parsing
	private ParsedCache<List<Position>> cache = new ParsedCache<>();

	@Override
	public ParsedCache<List<Position>> getCache() {
		return hasParseableLocations() ? null : cache;
	}

	@Override
	public final void resetCache() {
		cache.clear();
	}

	// ----- editor
	@Override
	public Mat editorIconType() {
		return CommonMats.MINECART;
	}

}
