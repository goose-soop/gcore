package com.guillaumevdn.gcore.lib.gui.element.item.overrideclick;

import java.util.Map;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParseableMapElement;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsedCache;
import com.guillaumevdn.gcore.lib.element.type.map.ElementAbstractDefaultEnumMap;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall.ClickType;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementClickTypeOverrideClickMap extends ElementAbstractDefaultEnumMap<ClickType, ElementOverrideClick> implements ParseableMapElement<ClickType, OverrideClick, ElementOverrideClick> {

	public ElementClickTypeOverrideClickMap(Element parent, String id, Need need, Text editorDescription) {
		super(ClickType.class, "override click", parent, id, need, editorDescription, CollectionUtils.asList(ClickType.values()));
	}

	// ----- add
	@Override
	public ElementOverrideClick createElement(String elementId) {
		return new ElementOverrideClick(this, elementId, Need.optional(new OverrideClick(OverrideClickType.NONE)), null);
	}

	@Override
	public ElementOverrideClick createDefaultElement(String elementId) {
		return new ElementOverrideClick(this, elementId, Need.optional(new OverrideClick(OverrideClickType.NONE)), null);
	}

	// ----- parsing
	private ParsedCache<Map<ClickType, OverrideClick>> cache = new ParsedCache<>();

	@Override
	public ParsedCache<Map<ClickType, OverrideClick>> getCache() {
		return hasParseableLocations() ? null : cache;
	}

	@Override
	public final void resetCache() {
		cache.clear();
	}

	// ----- editor
	@Override
	public Mat editorIconType() {
		return CommonMats.COMMAND_BLOCK;
	}

}
