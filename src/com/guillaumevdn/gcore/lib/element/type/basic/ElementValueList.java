package com.guillaumevdn.gcore.lib.element.type.basic;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.WorkerGCore;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.element.editor.EditorGUI;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.basic.BasicElement;
import com.guillaumevdn.gcore.lib.element.struct.basic.SizeTolerance;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall.ClickType;
import com.guillaumevdn.gcore.lib.gui.struct.GUIItem;
import com.guillaumevdn.gcore.lib.item.ItemUtils;
import com.guillaumevdn.gcore.lib.serialization.Serializer;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.string.Text;
import com.guillaumevdn.gcore.lib.wrapper.WrapperInteger;

/**
 * @author GuillaumeVDN
 */
public abstract class ElementValueList<T> extends BasicElement<List<T>> {

	private final Serializer<T> serializer;

	public ElementValueList(Class<T> typeClass, Element parent, String id, Need need, Text editorDescription) {
		this(Serializer.find(typeClass), parent, id, need, editorDescription);
	}

	public ElementValueList(Serializer<T> serializer, Element parent, String id, Need need, Text editorDescription) {
		super(SizeTolerance.ALLOW_EMPTY_AND_LIST, parent, id, need.getType(), need.serializeDef(serializer), editorDescription);
		this.serializer = serializer;
	}
	
	@Override
	protected List<String> loadRawValueFrom(@Nonnull List<T> value) {
		return serializer.serialize(value);
	}

	// ----- get
	public final Serializer<T> getSerializer() {
		return serializer;
	}

	public final List<T> getParsedDefaultValue() {
		List<String> def = getDefaultValue();
		List<T> result = new ArrayList<>();
		if (def.isEmpty()) return result;
		for (String d : def) {
			T deserialized = serializer.deserialize(d);
			if (deserialized != null) {
				result.add(deserialized);
			}
		}
		return result.isEmpty() ? null : result;
	}

	// ----- parse
	@Override
	protected List<T> doParseEmpty() {
		return new ArrayList<>();
	}

	@Override
	protected List<T> doParseString(String raw) throws ParsingError {
		T single = doParseStringSingle(raw);
		return single == null ? new ArrayList<>() : CollectionUtils.asList(single);
	}

	protected T doParseStringSingle(String raw) throws ParsingError {
		T value = serializer.deserialize(raw);  // if any exception is thrown here, it'll be catched and displayed correctly
		if (value == null) {
			if (ConfigGCore.ignoreInvalidElementValues) {
				return null;  // and here goes my consistency :PepeHands:
			}
			throw new ParsingError(this, "invalid value '" + raw + "'");
		}
		validate(value);
		return value;
	}

	@Override
	protected List<T> doParseList(List<String> raw) throws ParsingError {
		List<T> value = new ArrayList<>();
		for (String r : raw) {
			T elem = doParseStringSingle(r);  // if any exception is thrown here, it'll be catched and displayed correctly
			if (elem != null) {  // if ConfigGCore.ignoreInvalidElementValues
				value.add(elem);
			}
		}
		return value;
	}

	protected void validate(T value) throws ParsingError {
	}

	// ----- editor
	@Override
	public EditorGUI editorGUI(ClickCall fromCall) {
		return new EditorGUI(this, fromCall) {

			private WrapperInteger swapping = WrapperInteger.of(null);

			@Override
			protected boolean doFill() {

				// current item
				setPersistentItem(new GUIItem("current", 47, editorIcon()));

				// set lines items
				if (getRawValue() != null) {
					for (int i = 0; i < getRawValue().size(); ++i) {
						final int lineIndex = i;
						setRegularItem(new GUIItem("line_" + lineIndex, lineIndex, editorLineIcon(lineIndex), call -> {
							// left-click : edit
							if (call.getType().equals(ClickType.LEFT)) {
								onEditorClickEdit(lineIndex, call);
								// reset swap
								swapping.set(null);
							}
							// shift + right-click : insert
							else if (call.getType().equals(ClickType.SHIFT_RIGHT)) {
								List<String> newValue = getRawValueCopyOrNewList();
								newValue.add(lineIndex, "new line");
								setValue(newValue);
								call.getGUI().refill();
								getSuperElement().onEditorChange(ElementValueList.this);
								// reset swap
								swapping.set(null);
							}
							// number key : swap
							else if (call.getType().isNumberKey()) {
								if (swapping.get() == null) {
									swapping.set(lineIndex);
									TextEditorGeneric.messageElementBasicListSwap.send(call.getClicker());
								} else if (swapping.get() != lineIndex) {
									TextEditorGeneric.messageElementBasicListSwapped.send(call.getClicker());
									List<String> newValue = getRawValueCopyOrNewList();
									newValue.set(swapping.get(), getRawValueLine(lineIndex));
									newValue.set(lineIndex, getRawValueLine(swapping.get()));
									setValue(newValue);
									call.getGUI().refill();
									getSuperElement().onEditorChange(ElementValueList.this);
									swapping.set(null);
								} else {
									TextEditorGeneric.messageElementBasicListSwap.send(call.getClicker());
								}
							}
							// control + drop : delete
							else if (call.getType().equals(ClickType.CONTROL_DROP)) {
								List<String> newValue = getRawValueCopyOrNewList();
								newValue.remove(lineIndex);
								setValue(newValue);
								call.getGUI().refill();
								getSuperElement().onEditorChange(ElementValueList.this);
								// reset swap
								swapping.set(null);
							}
							// other
							else {
								onEditorOtherClickEdit(lineIndex, call);
								// reset swap
								swapping.set(null);
							}
						}));
					}
				}

				// new line item
				setPersistentItem(new GUIItem("newline", 50, ItemUtils.createItem(CommonMats.BLAZE_ROD, TextEditorGeneric.controlAddElementName.parseLine(), null), call -> {
					if (getRawValue() == null) {
						setValue(CollectionUtils.asList(editorNewLine()));
					} else {
						setValue(CollectionUtils.asListMultiple(String.class, getRawValue(), editorNewLine()));
					}
					call.getGUI().refill();
					getSuperElement().onEditorChange(ElementValueList.this);
				}));

				// done
				return super.doFill();
			}
		};
	}

	private final ItemStack editorLineIcon(int lineIndex) {
		return ItemUtils.addAllFlags(ItemUtils.createItem(editorIconType(), "§6" + (lineIndex + 1), editorLineIconLore(lineIndex)));
	}

	protected List<String> editorLineIconLore(int lineIndex) {
		List<String> lore = new ArrayList<>();
		// current value
		lore.add("§r");
		lore.addAll(TextEditorGeneric.elementCurrentValueSingle.replace("{value}", () -> getRawValue().get(lineIndex)).parseLines());
		lore.add("§r");
		lore.addAll(TextEditorGeneric.controlEdit.parseLines());
		lore.addAll(TextEditorGeneric.controlListSwap.parseLines());
		lore.addAll(TextEditorGeneric.controlListInsert.parseLines());
		lore.addAll(TextEditorGeneric.controlDelete.parseLines());
		return lore;
	}

	public final void onEditorClickEdit(int lineIndex, ClickCall call) {
		call.getClicker().closeInventory();
		WorkerGCore.inst().awaitChatWithSuggestedValue(call.getClicker(), TextEditorGeneric.messageElementBasicListLineEdit, getRawValueLine(lineIndex), value -> {
			if (StringUtils.hasPlaceholders(value)) {
				List<String> v = getRawValueCopyOrNewList();
				v.set(lineIndex, value);
				setValue(v);
				getSuperElement().onEditorChange(ElementValueList.this);
			} else {
				try {
					T parsed = doParseStringSingle(value);
					List<String> v = getRawValueCopyOrNewList();
					if (parsed != null) {
						v.set(lineIndex, value);
					} else {
						v.remove(lineIndex);
					}
					setValue(v);
					getSuperElement().onEditorChange(ElementValueList.this);
				} catch (ParsingError error) {
					error.send(call.getClicker());
				}
			}
			call.reopenGUI();
		}, () -> call.reopenGUI());
	}

	public void onEditorOtherClickEdit(int lineIndex, ClickCall call) {
	}

	protected String editorNewLine() {
		return "new line";
	}

}
