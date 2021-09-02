package com.guillaumevdn.gcore.lib.element.type.map;

import java.util.Map;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.economy.Currency;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParseableMapElement;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsedCache;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementDouble;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementCurrencyDoubleMap extends ElementAbstractEnumMap<Currency, ElementDouble> implements ParseableMapElement<Currency, Double, ElementDouble> {

	public ElementCurrencyDoubleMap(Element parent, String id, Need need, Text editorDescription) {
		super(Currency.class, parent, id, need, editorDescription, CollectionUtils.asList(Currency.values()));
	}

	// ----- add
	@Override
	public ElementDouble createElement(String elementId) {
		ElementDouble element = new ElementDouble(this, elementId, Need.optional(), 1, null);
		element.setValue(CollectionUtils.asList("1"));  // don't use the default value otherwise it won't be saved
		return element;
	}

	// ----- parsing
	private ParsedCache<Map<Currency, Double>> cache = new ParsedCache<>();

	@Override
	public ParsedCache<Map<Currency, Double>> getCache() {
		return hasParseableLocations() ? null : cache;
	}

	@Override
	public final void resetCache() {
		cache.clear();
	}

	// ----- editor
	@Override
	public Mat editorIconType() {
		return CommonMats.ENCHANTING_TABLE;
	}

}
