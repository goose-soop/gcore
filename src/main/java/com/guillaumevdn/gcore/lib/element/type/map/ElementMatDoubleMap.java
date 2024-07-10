package com.guillaumevdn.gcore.lib.element.type.map;

import java.util.Map;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParseableMapElement;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsedCache;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementDouble;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementMatDoubleMap extends ElementAbstractEnumMap<Mat, ElementDouble> implements ParseableMapElement<Mat, Double, ElementDouble> {

	public ElementMatDoubleMap(Element parent, String id, Need need, Text editorDescription) {
		super(Mat.class, parent, id, need, editorDescription, CollectionUtils.asList(Mat.values()));
	}

	// ----- add
	@Override
	public ElementDouble createElement(String elementId) {
		ElementDouble element = new ElementDouble(this, elementId, Need.optional(), 1, null);
		element.setValue(CollectionUtils.asList("1")); // don't use the default value otherwise it won't be saved
		return element;
	}

	// ----- parsing
	private ParsedCache<Map<Mat, Double>> cache = new ParsedCache<>();

	@Override
	public ParsedCache<Map<Mat, Double>> getCache() {
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
