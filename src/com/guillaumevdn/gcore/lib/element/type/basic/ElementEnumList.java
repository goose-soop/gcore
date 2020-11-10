package com.guillaumevdn.gcore.lib.element.type.basic;

import java.util.List;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public abstract class ElementEnumList<E extends Enum<E>> extends ElementAbstractEnumList<E> {

	private final List<E> values;

	public ElementEnumList(Class<E> enumClass, Element parent, String id, Need need, Text editorDescription) {
		super(enumClass, true, parent, id, need, editorDescription);
		this.values = CollectionUtils.asList(enumClass.getEnumConstants());
	}

	// get
	@Override
	public List<E> getValues() {
		return values;
	}

}
