package com.guillaumevdn.gcore.lib.element.type.basic;

import java.util.List;

import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public abstract class ElementFakeEnumList<E> extends ElementAbstractEnumList<E> {

	private final List<E> values;

	public ElementFakeEnumList(Class<E> typeClass, Element parent, String id, Need need, Text editorDescription, List<E> values) {
		super(typeClass, true, parent, id, need, editorDescription);
		this.values = values;
	}

	// get
	@Override
	public List<E> getValues() {
		return values;
	}

}
