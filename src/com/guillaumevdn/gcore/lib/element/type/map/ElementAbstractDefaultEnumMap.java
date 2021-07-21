package com.guillaumevdn.gcore.lib.element.type.map;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.editor.EnumSelectorGUI;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.map.DefaultMapElement;
import com.guillaumevdn.gcore.lib.function.MapSupplier;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public abstract class ElementAbstractDefaultEnumMap<E, V extends Element> extends DefaultMapElement<E, V> {

	private final List<E> values;
	private final LinkedHashMap<E, Mat> valuesIcons;

	public ElementAbstractDefaultEnumMap(Class<E> typeClass, String valueTypeName, Element parent, String id, Need need, Text editorDescription, List<E> values) {
		super(typeClass, valueTypeName, parent, id, need, editorDescription);
		this.values = values;
		this.valuesIcons = null;
	}

	public ElementAbstractDefaultEnumMap(Class<E> typeClass, String valueTypeName, Element parent, String id, Need need, Text editorDescription, MapSupplier<E, Mat> valuesBuilder) {
		super(typeClass, valueTypeName, parent, id, need, editorDescription);
		this.values = null;
		valuesBuilder.get(this.valuesIcons = new LinkedHashMap<>());
	}

	// ----- editor
	@Override
	protected void editorAskKeyAndCreateAndAddElement(ClickCall call, BiConsumer<E, V> onCreate, Runnable onCancel) {
		if (values != null) {
			EnumSelectorGUI.openSelector(call.getClicker(), false, getKeySerializer(), () -> values.stream().filter(key -> !keys().contains(key)).collect(Collectors.toList()), editorIconType(), key -> {
				V value = createAndAddElement(key);
				onCreate.accept(key, value);
			}, onCancel);
		} else {
			LinkedHashMap<E, Mat> remaining = new LinkedHashMap<>();
			valuesIcons.forEach((key, icon) -> {
				if (!keys().contains(key)) {
					remaining.put(key, icon);
				}
			});
			EnumSelectorGUI.openSelector(call.getClicker(), false, getKeySerializer(), () -> remaining, key -> {
				V value = createAndAddElement(key);
				onCreate.accept(key, value);
			}, onCancel);
		}
	}

}
