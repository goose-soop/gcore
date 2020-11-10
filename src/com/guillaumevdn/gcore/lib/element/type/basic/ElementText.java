package com.guillaumevdn.gcore.lib.element.type.basic;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.WorkerGCore;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.editor.EditorGUI;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.basic.BasicElement;
import com.guillaumevdn.gcore.lib.element.struct.basic.SizeTolerance;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall.ClickType;
import com.guillaumevdn.gcore.lib.gui.struct.GUIItem;
import com.guillaumevdn.gcore.lib.item.ItemUtils;
import com.guillaumevdn.gcore.lib.string.Text;
import com.guillaumevdn.gcore.lib.string.TextElement;
import com.guillaumevdn.gcore.lib.wrapper.WrapperInteger;

/**
 * @author GuillaumeVDN
 */
public class ElementText extends BasicElement<Text> {

	public ElementText(Element parent, String id, Need need, Text editorDescription) {
		super("text", SizeTolerance.ALLOW_EMPTY_AND_LIST, parent, id, need.getType(), need.getDef() != null ? ((Text) need.getDef()).getCurrentLines() : null, editorDescription);
	}

	// parse
	@Override
	protected Text doParseEmpty() {
		return new TextElement();
	}

	@Override
	protected Text doParseString(String raw) {
		return new TextElement(CollectionUtils.asList(raw));
	}

	@Override
	protected Text doParseList(List<String> raw) {
		return new TextElement(raw);
	}

	// editor
	@Override
	public Mat editorIconType() {
		return CommonMats.BOOKSHELF;
	}

	// editor
	@Override
	public EditorGUI editorGUI(ClickCall fromCall) {
		return new EditorGUI(this, fromCall) {

			private WrapperInteger swapping = WrapperInteger.of(null);

			@Override
			protected boolean doFill() {

				// current
				setPersistentItem(new GUIItem("current", 47, editorIcon()));

				// set lines items
				if (getValue() != null) {
					for (int i = 0; i < getValue().size(); ++i) {
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
								List<String> newValue = getValueCopyOrNewList();
								newValue.add(lineIndex, "new line");
								setValue(newValue);
								call.getGUI().refill();
								getSuperElement().onEditorChange(ElementText.this);
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
									List<String> newValue = getValueCopyOrNewList();
									newValue.set(swapping.get(), getValueLine(lineIndex));
									newValue.set(lineIndex, getValueLine(swapping.get()));
									setValue(newValue);
									call.getGUI().refill();
									getSuperElement().onEditorChange(ElementText.this);
									swapping.set(null);
								} else {
									TextEditorGeneric.messageElementBasicListSwap.send(call.getClicker());
								}
							}
							// control + drop : delete
							else if (call.getType().equals(ClickType.CONTROL_DROP)) {
								List<String> newValue = getValueCopyOrNewList();
								newValue.remove(lineIndex);
								setValue(newValue);
								call.getGUI().refill();
								getSuperElement().onEditorChange(ElementText.this);
								// reset swap
								swapping.set(null);
							}
						}));
					}
				}

				// new line item
				setPersistentItem(new GUIItem("newline", 50, ItemUtils.createItem(CommonMats.BLAZE_ROD, TextEditorGeneric.controlAddElementName.parseLine(), null), call -> {
					if (getValue() == null) {
						setValue(CollectionUtils.asList("new line"));
					} else {
						setValue(CollectionUtils.asListMultiple(String.class, getValue(), "new line"));
					}
					call.getGUI().refill();
					getSuperElement().onEditorChange(ElementText.this);
				}));

				// random item
				setPersistentItem(new GUIItem("random", 51, ItemUtils.createItem(CommonMats.COMMAND_BLOCK, TextEditorGeneric.controlTextSetRandom.parseLine(), null), call -> {
					if (getValue() == null) {
						setValue(CollectionUtils.asList("@random"));
					} else if ("@random".equalsIgnoreCase(getValueLine(0))) {
						return;
					} else {
						setValue(CollectionUtils.asListMultiple(String.class, "@random", getValue()));
					}
					call.getGUI().refill();
					getSuperElement().onEditorChange(ElementText.this);
				}));

				// done
				return super.doFill();
			}
		};
	}

	private final ItemStack editorLineIcon(int lineIndex) {
		List<String> lore = new ArrayList<>();
		// current value
		lore.add("§r");
		lore.addAll(TextEditorGeneric.elementCurrentValueSingle.replace("{value}", () -> getValue().get(lineIndex)).parseLines());
		// control
		lore.add("§r");
		lore.addAll(TextEditorGeneric.controlEdit.parseLines());
		lore.addAll(TextEditorGeneric.controlListSwap.parseLines());
		lore.addAll(TextEditorGeneric.controlListInsert.parseLines());
		lore.addAll(TextEditorGeneric.controlDelete.parseLines());
		// -
		return ItemUtils.addAllFlags(ItemUtils.createItem(editorIconType(), "§6" + (lineIndex + 1), lore));
	}

	public void onEditorClickEdit(int lineIndex, ClickCall call) {
		call.getClicker().closeInventory();
		WorkerGCore.inst().awaitChatWithSuggestedValue(call.getClicker(), TextEditorGeneric.messageElementBasicListLineEdit, getValueLine(lineIndex), value -> {
			List<String> v = getValueCopyOrNewList();
			v.set(lineIndex, value);
			setValue(v);
			getSuperElement().onEditorChange(ElementText.this);
			call.getGUI().openFor(call.getClicker(), call.getPageIndex());
		}, () -> call.getGUI().openFor(call.getClicker(), call.getPageIndex()));
	}

}
