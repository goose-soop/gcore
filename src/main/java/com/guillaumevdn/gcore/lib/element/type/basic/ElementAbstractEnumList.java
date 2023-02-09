package com.guillaumevdn.gcore.lib.element.type.basic;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.editor.EnumSelectorGUI;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall.ClickType;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.serialization.Serializer;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public abstract class ElementAbstractEnumList<E> extends ElementValueList<E> {

	private boolean cacheEditorSelector;

	public ElementAbstractEnumList(Class<E> typeClass, boolean cacheEditorSelector, Element parent, String id, Need need, Text editorDescription) {
		super(typeClass, parent, id, need, editorDescription);
		this.cacheEditorSelector = cacheEditorSelector;
	}

	public ElementAbstractEnumList(Serializer<E> serializer, boolean cacheEditorSelector, Element parent, String id, Need need, Text editorDescription) {
		super(serializer, parent, id, need, editorDescription);
		this.cacheEditorSelector = cacheEditorSelector;
	}

	// ----- get
	public abstract List<E> getValues();

	@Override
	protected List<String> editorLineIconLore(int lineIndex) {
		List<String> lore = super.editorLineIconLore(lineIndex);
		lore.addAll(TextEditorGeneric.controlSelect.parseLines());
		return lore;
	}

	@Override
	public void onEditorOtherClickEdit(int lineIndex, ClickCall call) {
		// right-click : select
		if (call.getType().equals(ClickType.RIGHT)) {
			List<E> v = getValues();
			if (getRawValue() != null) {
				v = v.stream().filter(value -> !CollectionUtils.containsIgnoreCase(getRawValue(), getSerializer().serialize(value))).collect(Collectors.toList());
			}
			List<E> values = v; // pepega
			// select with element icons
			if (ObjectUtils.instanceOf(getSerializer().getTypeClass(), Element.class)) {
				EnumSelectorGUI.openSelector(call.getClicker(), cacheEditorSelector, getSerializer(),
						// build icons
						() -> {
							LinkedHashMap<E, Mat> icons = new LinkedHashMap<>();
							values.forEach(value -> icons.put(value, ((Element) value).editorIconType()));
							return icons;
						},
						// on select
						value -> {
							List<String> newValue = getRawValueCopy();
							if (newValue == null) {
								setValue(CollectionUtils.asList(getSerializer().serialize(value)));
							} else {
								if (lineIndex >= getRawValue().size()) {
									newValue.add(getSerializer().serialize(value));
								} else {
									newValue.set(lineIndex, getSerializer().serialize(value));
								}
								setValue(newValue);
							}
							// reopen GUI (that refreshes it since it's an editor GUI)
							call.reopenGUI();
							getSuperElement().onEditorChange(this);
						},
						// on cancel
						() -> {
							call.reopenGUI();
						});
			}
			// select with single icon
			else {
				EnumSelectorGUI.openSelector(call.getClicker(), cacheEditorSelector, getSerializer(), () -> values, editorIconType(),
						// on select
						value -> {
							List<String> newValue = getRawValueCopy();
							if (newValue == null) {
								setValue(CollectionUtils.asList(getSerializer().serialize(value)));
							} else {
								if (lineIndex >= getRawValue().size()) {
									newValue.add(getSerializer().serialize(value));
								} else {
									newValue.set(lineIndex, getSerializer().serialize(value));
								}
								setValue(newValue);
							}
							// reopen GUI (that refreshes it since it's an editor GUI)
							call.reopenGUI();
							getSuperElement().onEditorChange(this);
						},
						// on cancel
						() -> {
							call.reopenGUI();
						});
			}
		}
	}

	@Override
	protected String editorNewLine() {
		List<E> values = getValues();
		if (getRawValue() != null) {
			values = values.stream().filter(value -> !CollectionUtils.containsIgnoreCase(getRawValue(), getSerializer().serialize(value))).collect(Collectors.toList());
		}
		return getSerializer().serialize(CollectionUtils.random(!values.isEmpty() ? values : getValues()));
	}

}
