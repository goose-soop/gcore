package com.guillaumevdn.gcore.lib.element.struct.container.typable;

import java.util.LinkedHashMap;
import java.util.List;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.editor.EnumSelectorGUI;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementFakeEnum;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall.ClickType;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public abstract class ElementTypableElementType<T extends TypableElementType> extends ElementFakeEnum<T> {

	private TypableElementTypes<T> types;

	public ElementTypableElementType(TypableElementTypes<T> types, Element parent, String id, Text editorDescription) {
		super(types.getTypeClass(), parent, id, Need.optional(types.defaultValue()), editorDescription, CollectionUtils.asList(types.values()));
		this.types = types;
	}

	// editor
	@Override
	public List<String> editorIconLore() {
		List<String> lore = super.nonControlEditorIconLore();
		lore.add("§r");
		lore.addAll(TextEditorGeneric.controlSelect.parseLines());
		return lore;
	}

	@Override
	public void onEditorClick(ClickCall call) {
		// right-click : select
		if (call.getType().equals(ClickType.RIGHT)) {
			LinkedHashMap<T, Mat> types = new LinkedHashMap<>();
			this.types.values().stream().sorted().forEach(type -> {
				types.put(type, type.getIcon());
			});
			EnumSelectorGUI.openSelector(call.getClicker(), false, getSerializer(), () -> types, value -> {
				// set value and update parent elements
				T previousType = parseGeneric().orNull();
				setValue(CollectionUtils.asList(getSerializer().serialize(value)));
				try {
					if (getParent() != null) {
						TypableContainerElement<T> parent = (TypableContainerElement<T>) getParent();
						if (previousType != null) previousType.clearElements(parent);
						value.fillTypeSpecificElements(parent);
						parent.onTypeChange(previousType, value);
					}
				} catch (ClassCastException ignored) {}
				// reopen GUI (that refreshes it since it's an editor GUI)
				call.getGUI().openFor(call.getClicker(), call.getPageIndex());
				getSuperElement().onEditorChange(this);
			}, () -> call.getGUI().openFor(call.getClicker(), call.getPageIndex()));
		}
	}

}
