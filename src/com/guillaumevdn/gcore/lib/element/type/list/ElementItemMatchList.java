package com.guillaumevdn.gcore.lib.element.type.list;

import java.util.List;

import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.list.ListElement;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParseableListElement;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsedCache;
import com.guillaumevdn.gcore.lib.element.type.container.ElementItemMatch;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementItemMatchList extends ListElement<ElementItemMatch> implements ParseableListElement<ItemMatch, ElementItemMatch> {

	private boolean allowCustomCheck;

	public ElementItemMatchList(Element parent, String id, Need need, boolean allowCustomCheck, Text editorDescription) {
		super("item", parent, id, need, editorDescription);
		this.allowCustomCheck = allowCustomCheck;
	}

	// element
	@Override
	protected ElementItemMatch createElement(String elementId) {
		return new ElementItemMatch(this, elementId, Need.optional(), allowCustomCheck, null);
	}

	// parsing
	private ParsedCache<List<ItemMatch>> cache = new ParsedCache<>();

	@Override
	public ParsedCache<List<ItemMatch>> getCache() {
		return hasParseableLocations() ? null : cache;
	}

	@Override
	public void resetCache() {
		cache.clear();
	}

	// editor
	@Override
	public Mat editorIconType() {
		return CommonMats.NETHER_STAR;
	}

}
