package com.guillaumevdn.gcore.lib.element.type.basic;

import java.util.LinkedHashMap;
import java.util.List;

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
public abstract class ElementAbstractEnum<E> extends ElementValue<E> {

	private boolean cacheEditorSelector;

	public ElementAbstractEnum(Class<E> enumClass, boolean cacheEditorSelector, Element parent, String id, Need need, Text editorDescription) {
		super(enumClass, parent, id, need, editorDescription);
		this.cacheEditorSelector = cacheEditorSelector;
	}

	public ElementAbstractEnum(Serializer<E> serializer, boolean cacheEditorSelector, Element parent, String id, Need need, Text editorDescription) {
		super(serializer, parent, id, need, editorDescription);
		this.cacheEditorSelector = cacheEditorSelector;
	}

	// get
	public abstract List<E> getValues();

	// editor
	@Override
	public List<String> editorIconLore() {
		List<String> lore = super.editorIconLore();
		lore.addAll(TextEditorGeneric.controlSelect.parseLines());
		return lore;
	}

	@Override
	public void onEditorClick(ClickCall call) {
		// right-click : select
		if (call.getType().equals(ClickType.RIGHT)) {
			// select with element icons
			if (ObjectUtils.instanceOf(getSerializer().getTypeClass(), Element.class)) {
				EnumSelectorGUI.openSelector(call.getClicker(), cacheEditorSelector, getSerializer(),
						// build icons
						() -> {
							LinkedHashMap<E, Mat> icons = new LinkedHashMap<>();
							getValues().stream().sorted().forEach(value -> icons.put(value, ((Element) value).editorIconType()));
							return icons;
						},
						// on select
						value -> {
							setValue(CollectionUtils.asList(getSerializer().serialize(value)));
							call.getGUI().setRegularItem(buildEditorItem(call.getSlot()));
							call.getGUI().openFor(call.getClicker(), call.getPageIndex());
							getSuperElement().onEditorChange(this);
						},
						// on cancel
						() -> {
							call.getGUI().openFor(call.getClicker(), call.getPageIndex());
						});
			}
			// select with single icon
			else {
				EnumSelectorGUI.openSelector(call.getClicker(), cacheEditorSelector, getSerializer(), () -> getValues(), editorIconType(),
						// on select
						value -> {
							setValue(CollectionUtils.asList(getSerializer().serialize(value)));
							call.getGUI().setRegularItem(buildEditorItem(call.getSlot()));
							call.getGUI().openFor(call.getClicker(), call.getPageIndex());
							getSuperElement().onEditorChange(this);
						},
						// on cancel
						() -> {
							call.getGUI().openFor(call.getClicker(), call.getPageIndex());
						});
			}
		}
		// other
		else {
			super.onEditorClick(call);
		}
	}

}
