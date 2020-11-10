package com.guillaumevdn.gcore.lib.element.type.basic;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.WorkerGCore;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.basic.BasicElement;
import com.guillaumevdn.gcore.lib.element.struct.basic.SizeTolerance;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall.ClickType;
import com.guillaumevdn.gcore.lib.serialization.Serializer;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public abstract class ElementValue<T> extends BasicElement<T> {

	private final Serializer<T> serializer;

	public ElementValue(Class<T> typeClass, Element parent, String id, Need need, Text editorDescription) {
		this(StringUtils.getReadableName(typeClass), typeClass, parent, id, need, editorDescription);
	}

	public ElementValue(Serializer<T> serializer, Element parent, String id, Need need, Text editorDescription) {
		this(serializer.getTypeName(), serializer, parent, id, need, editorDescription);
	}

	public ElementValue(String valueTypeName, Class<T> typeClass, Element parent, String id, Need need, Text editorDescription) {
		this(valueTypeName, Serializer.find(typeClass), parent, id, need, editorDescription);
	}

	public ElementValue(String valueTypeName, Serializer<T> serializer, Element parent, String id, Need need, Text editorDescription) {
		super(valueTypeName, SizeTolerance.DISALLOW_EMPTY_OR_LIST, parent, id, need.getType(), need.serializeDef(serializer), editorDescription);
		this.serializer = serializer;
	}

	// get
	public final Serializer<T> getSerializer() {
		return serializer;
	}

	public final T getParsedDefaultValue() {
		String line = getDefaultValueLine(0);
		try {
			if (line != null) {
				return serializer.deserialize(line);
			}
		} catch (Throwable ignored) {}
		return null;
	}

	// parse
	@Override
	protected T doParseString(String raw) throws ParsingError {
		T value = serializer.deserialize(raw);  // if any exception is thrown here, it'll be catched and displayed correctly
		if (value == null) {
			throw new ParsingError(this, "invalid value '" + raw + "'");
		}
		validate(value);
		return value;
	}

	protected void validate(T value) throws ParsingError {
	}

	// editor
	@Override
	public void onEditorClick(ClickCall call) {
		// left-click : enter value
		if (call.getType().equals(ClickType.LEFT)) {
			WorkerGCore.inst().awaitChatWithSuggestedValue(call.getClicker(), TextEditorGeneric.messageElementBasicEditSuggestCurrent, getValueLineOrDefault(0), value -> {
				if (StringUtils.hasPlaceholders(value)) {
					setValue(CollectionUtils.asList(value));
					getSuperElement().onEditorChange(this);
				} else {
					try {
						T parsed = doParseString(value);
						setValue(parsed == null ? null : CollectionUtils.asList(value));
						call.getGUI().setRegularItem(buildEditorItem(call.getSlot()));
						call.getGUI().openFor(call.getClicker(), call.getPageIndex());
						getSuperElement().onEditorChange(this);
					} catch (ParsingError error) {
						error.send(call.getClicker());
					}
				}
				call.getGUI().openFor(call.getClicker(), call.getPageIndex());
			}, () -> call.getGUI().openFor(call.getClicker(), call.getPageIndex()));
		}
		// other
		else {
			super.onEditorClick(call);
		}
	}

}
