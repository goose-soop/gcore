package com.guillaumevdn.gcore.lib.element.type.basic;

import java.util.Collections;
import java.util.List;

import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public abstract class ElementFakeEnum<E> extends ElementAbstractEnum<E> {

	private final List<E> values;

	public ElementFakeEnum(Class<E> typeClass, Element parent, String id, Need need, Text editorDescription, List<E> values) {
		super(typeClass, true, parent, id, need, editorDescription);
		values.sort((a, b) -> getSerializer().serialize(a).compareTo(getSerializer().serialize(b)));
		this.values = Collections.unmodifiableList(values);
	}

	// get
	public final List<E> getValues() {
		return values;
	}

}
