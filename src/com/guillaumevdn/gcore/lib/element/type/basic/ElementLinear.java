package com.guillaumevdn.gcore.lib.element.type.basic;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.WorkerGCore;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.editor.EnumSelectorGUI;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall.ClickType;
import com.guillaumevdn.gcore.lib.serialization.LinearSerializer;
import com.guillaumevdn.gcore.lib.serialization.Serializer;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.string.Text;
import com.guillaumevdn.gcore.lib.tuple.Pair;

/**
 * @author GuillaumeVDN
 */
public abstract class ElementLinear<T extends LinearObjectType, L extends LinearObject<T>> extends ElementValue<L> {

	private final LinkedHashMap<T, Mat> types;
	private final Serializer<T> typeSerializer;

	public ElementLinear(LinearSerializer<T, L> serializer, Element parent, String id, Need need, Text editorDescription, List<T> types) {
		super(serializer, parent, id, need, editorDescription);
		this.types = new LinkedHashMap<>();
		types.forEach(type -> this.types.put(type, type.getIcon()));
		this.typeSerializer = serializer.getTypeSerializer();
	}

	// get
	public Serializer<T> getTypeSerializer() {
		return typeSerializer;
	}

	// editor
	@Override
	public List<String> editorIconLore() {
		List<String> lore = super.editorIconLore();
		lore.addAll(TextEditorGeneric.controlLinearSelectType.parseLines());
		lore.addAll(TextEditorGeneric.controlLinearEditParameters.parseLines());
		return lore;
	}

	@Override
	public void onEditorClick(ClickCall call) {
		// right-click : edit parameters
		if (call.getType().equals(ClickType.RIGHT)) {
			WorkerGCore.inst().awaitChatWithSuggestedValue(call.getClicker(), TextEditorGeneric.messageElementBasicEditSuggestCurrent, getValueLineOrDefault(0), value -> {
				Pair<T, List<String>> current = getCurrentPair();
				if (StringUtils.hasPlaceholders(value)) {
					setValue(current.getA() == null ? null : CollectionUtils.asList(current.getA().name() + " " + value));
					getSuperElement().onEditorChange(this);
				} else {
					try {
						L parsed = doParseString(current.getA().name() + " " + value);
						setValue(parsed == null ? null : CollectionUtils.asList(getSerializer().serialize(parsed)));
						call.getGUI().setRegularItem(buildEditorItem(call.getSlot()));
						getSuperElement().onEditorChange(this);
					} catch (ParsingError error) {
						error.send(call.getClicker());
					}
				}
				call.getGUI().openFor(call.getClicker(), call.getPageIndex());
			}, () -> call.getGUI().openFor(call.getClicker(), call.getPageIndex()));
		}
		// shift + left-click : select type
		else if (call.getType().equals(ClickType.SHIFT_LEFT)) {
			EnumSelectorGUI.openSelector(call.getClicker(), true, getTypeSerializer(), () -> types,
					// on select
					value -> {
						// edit value ; try to keep old parameters with new type, or just override new type if not possible
						Pair<T, List<String>> current = getCurrentPair();
						try {
							L parsed = doParseString(value.name() + (current.getB().isEmpty() ? "" : " " + StringUtils.toTextString(",", current.getB())));
							setValue(parsed == null ? null : CollectionUtils.asList(getSerializer().serialize(parsed)));
						} catch (ParsingError error) {
							setValue(CollectionUtils.asList(value.name()));
						}
						// open GUI and change
						call.getGUI().setRegularItem(buildEditorItem(call.getSlot()));
						call.getGUI().openFor(call.getClicker(), call.getPageIndex());
						getSuperElement().onEditorChange(this);
					},
					// on cancel
					() -> {
						call.getGUI().openFor(call.getClicker(), call.getPageIndex());
					});
		}
		// other
		else {
			super.onEditorClick(call);
		}
	}

	private Pair<T, List<String>> getCurrentPair() {
		if (getValue() == null) {
			return Pair.of(null, new ArrayList<>());
		}
		List<String> split = StringUtils.split(getValueLine(0), " ", 1);
		T type = getTypeSerializer().deserialize(split.get(0));
		split.remove(0);
		return Pair.of(type, split.isEmpty() || type == null ? new ArrayList<>() : split);
	}

}
