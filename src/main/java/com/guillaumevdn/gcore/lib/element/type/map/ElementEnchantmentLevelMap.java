package com.guillaumevdn.gcore.lib.element.type.map;

import java.util.Map;

import org.bukkit.enchantments.Enchantment;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParseableMapElement;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsedCache;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementInteger;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementEnchantmentLevelMap extends ElementAbstractEnumMap<Enchantment, ElementInteger>
		implements ParseableMapElement<Enchantment, Integer, ElementInteger> {

	public ElementEnchantmentLevelMap(Element parent, String id, Need need, Text editorDescription) {
		super(Enchantment.class, parent, id, need, editorDescription, CollectionUtils.asList(Enchantment.values()));
	}

	// ----- add
	@Override
	public ElementInteger createElement(String elementId) {
		ElementInteger element = new ElementInteger(this, elementId, Need.optional(), null); // no min. value allows usage of math placeholders, <1 will mean no
																								// enchant
		element.setValue(CollectionUtils.asList("1")); // don't use the default value otherwise it won't be saved
		return element;
	}

	// ----- parsing
	private ParsedCache<Map<Enchantment, Integer>> cache = new ParsedCache<>();

	@Override
	public ParsedCache<Map<Enchantment, Integer>> getCache() {
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
