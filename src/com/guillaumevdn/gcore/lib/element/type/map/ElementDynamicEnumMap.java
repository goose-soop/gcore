package com.guillaumevdn.gcore.lib.element.type.map;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import com.guillaumevdn.gcore.lib.element.editor.EnumSelectorGUI;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.map.MapElement;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.serialization.Serializer;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public abstract class ElementDynamicEnumMap<E, V extends Element> extends MapElement<E, V> {

	public ElementDynamicEnumMap(Serializer<E> typeSerializer, String valueTypeName, Element parent, String id, Need need, Text editorDescription) {
		super(typeSerializer, valueTypeName, parent, id, need, editorDescription);
	}

	public abstract Collection<E> getValues();

	// ----- editor
	@Override
	protected void editorAskKeyAndCreateAndAddElement(ClickCall call, BiConsumer<E, V> onCreate, Runnable onCancel) {
		EnumSelectorGUI.openSelector(call.getClicker(), false, getKeySerializer(), () -> getValues().stream().sorted((a, b) -> getKeySerializer().serialize(a).compareTo(getKeySerializer().serialize(b))).filter(key -> !keys().contains(key)).collect(Collectors.toList()), editorIconType(), key -> {
			V value = createAndAddElement(key);
			onCreate.accept(key, value);
		}, onCancel);
	}

}
