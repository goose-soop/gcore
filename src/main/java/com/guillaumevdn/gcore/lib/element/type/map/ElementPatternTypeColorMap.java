package com.guillaumevdn.gcore.lib.element.type.map;

import java.util.Map;

import org.bukkit.DyeColor;
import org.bukkit.block.banner.PatternType;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParseableMapElement;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsedCache;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementDyeColor;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementPatternTypeColorMap extends ElementAbstractEnumMap<PatternType, ElementDyeColor> implements ParseableMapElement<PatternType, DyeColor, ElementDyeColor> {

	public ElementPatternTypeColorMap(Element parent, String id, Need need, Text editorDescription) {
		super(PatternType.class, parent, id, need, editorDescription, CollectionUtils.asList(PatternType.values()));
	}

	// ----- add
	@Override
	public ElementDyeColor createElement(String elementId) {
		ElementDyeColor elem = new ElementDyeColor(this, elementId, Need.optional(), null);
		elem.setValue(CollectionUtils.asList(DyeColor.PURPLE.name()));  // set a value to force it to save
		return elem;
	}

	// ----- parsing
	private ParsedCache<Map<PatternType, DyeColor>> cache = new ParsedCache<>();

	@Override
	public ParsedCache<Map<PatternType, DyeColor>> getCache() {
		return hasParseableLocations() ? null : cache;
	}

	@Override
	public final void resetCache() {
		cache.clear();
	}

	// ----- editor
	@Override
	public Mat editorIconType() {
		return CommonMats.BLUE_BANNER;
	}

}
