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

public abstract class ElementLinearList<T extends LinearObjectType, L extends LinearObject<T>> extends ElementValueList<L> {

	private final LinkedHashMap<T, Mat> types;
	private final Serializer<T> typeSerializer;

	public ElementLinearList(LinearSerializer<T, L> serializer, Element parent, String id, Need need, Text editorDescription, List<T> types) {
		super(serializer, parent, id, need, editorDescription);
		this.types = new LinkedHashMap<>();
		types.forEach(type -> this.types.put(type, type.getIcon()));
		this.typeSerializer = serializer.getTypeSerializer();
	}

	// ----- get
	public Serializer<T> getTypeSerializer() {
		return typeSerializer;
	}

	// ----- editor
	@Override
	protected String editorNewLine() {
		T type = CollectionUtils.random(types.keySet());
		return type.name() + (type.requireParam() ? " parameters_here" : "");
	}

	@Override
	protected List<String> editorLineIconLore(int lineIndex) {
		List<String> lore = super.editorLineIconLore(lineIndex);
		lore.addAll(TextEditorGeneric.controlLinearSelectType.parseLines());
		lore.addAll(TextEditorGeneric.controlLinearEditParameters.parseLines());
		return lore;
	}

	@Override
	public void onEditorOtherClickEdit(int lineIndex, ClickCall call) {
		// shift + left-click : select type
		if (call.getType().equals(ClickType.SHIFT_LEFT)) {
			EnumSelectorGUI.openSelector(call.getClicker(), true, getTypeSerializer(), () -> types,
					// on select
					value -> {
						// edit value ; try to keep old parameters with new type, or just override new type if not possible
						Pair<T, List<String>> current = getCurrentPair(lineIndex);
						try {
							L parsed = doParseLine(value.name() + (current.getB().isEmpty() ? "" : " " + StringUtils.toTextString(",", current.getB())));
							setNewValueLine(lineIndex, parsed != null ? getSerializer().serialize(parsed) : value.name());
						} catch (ParsingError error) {
							setNewValueLine(lineIndex, null);
						}
						// open GUI and change
						getSuperElement().onEditorChange(this);
						call.reopenGUI();
					},
					// on cancel
					() -> {
						call.reopenGUI();
					});
		}
		// right-click : edit parameters
		else if (call.getType().equals(ClickType.RIGHT)) {
			Pair<T, List<String>> current = getCurrentPair(lineIndex);
			WorkerGCore.inst().awaitChatWithSuggestedValue(call.getClicker(), TextEditorGeneric.messageElementBasicEditSuggestCurrent,
					StringUtils.toTextString(" ", current.getB()), value -> {
						if (StringUtils.hasPlaceholders(value)) {
							setNewValueLine(lineIndex, current.getA() == null ? null : current.getA().name() + " " + value);
							call.getGUI().setRegularItem(buildEditorItem(call.getPageIndex(), call.getSlot()));
							getSuperElement().onEditorChange(this);
						} else {
							try {
								L parsed = current.getA() == null ? null : doParseLine(current.getA().name() + " " + value);
								setNewValueLine(lineIndex, parsed == null ? null : getSerializer().serialize(parsed));
								call.getGUI().setRegularItem(buildEditorItem(call.getPageIndex(), call.getSlot()));
								getSuperElement().onEditorChange(this);
							} catch (ParsingError error) {
								error.send(call.getClicker());
							}
						}
						call.reopenGUI();
					}, () -> call.reopenGUI());
		}
	}

	private L doParseLine(String line) throws ParsingError {
		List<L> parsed = doParseString(line);
		return parsed.isEmpty() ? null : parsed.get(0);
	}

	private void setNewValueLine(int lineIndex, String newLineSerialized) {
		List<String> newListValue = null;
		if (getRawValue() != null) {
			newListValue = CollectionUtils.asList(getRawValue());
			if (newLineSerialized == null) {
				newListValue.remove(lineIndex);
			} else {
				newListValue.set(lineIndex, newLineSerialized);
			}
		} else {
			if (newLineSerialized != null) {
				newListValue = CollectionUtils.asList(newLineSerialized);
			}
		}
		setValue(newListValue);
	}

	private Pair<T, List<String>> getCurrentPair(int lineIndex) {
		List<String> split = StringUtils.split(getRawValueLine(lineIndex), " ", 1);
		T type = getTypeSerializer().deserialize(split.get(0));
		split.remove(0);
		return Pair.of(type, split.isEmpty() || type == null ? new ArrayList<>() : split);
	}

}
