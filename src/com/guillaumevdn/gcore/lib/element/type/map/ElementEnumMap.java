package com.guillaumevdn.gcore.lib.element.type.map;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.function.MapSupplier;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public abstract class ElementEnumMap<E extends Enum<E>, V extends Element> extends ElementAbstractEnumMap<E, V> {
	
	public ElementEnumMap(Class<E> enumClass, String valueTypeName, Element parent, String id, Need need, Text editorDescription) {
		super(enumClass, valueTypeName, parent, id, need, editorDescription, CollectionUtils.asList(enumClass.getEnumConstants()));
	}

	public ElementEnumMap(Class<E> enumClass, String valueTypeName, Element parent, String id, Need need, Text editorDescription, MapSupplier<E, Mat> valuesBuilder) {
		super(enumClass, valueTypeName, parent, id, need, editorDescription, valuesBuilder);
	}

}
