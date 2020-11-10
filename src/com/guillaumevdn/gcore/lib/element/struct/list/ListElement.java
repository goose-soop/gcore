package com.guillaumevdn.gcore.lib.element.struct.list;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.WorkerGCore;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.element.editor.EditorGUI;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.map.MapElement;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall.ClickType;
import com.guillaumevdn.gcore.lib.gui.struct.GUIItem;
import com.guillaumevdn.gcore.lib.item.ItemUtils;
import com.guillaumevdn.gcore.lib.number.NumberUtils;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public abstract class ListElement<T extends Element> extends MapElement<String, T> {

	public ListElement(String elementTypeName, Element parent, String id, Need need, Text editorDescription) {
		super(String.class, "list of " + elementTypeName, true, parent, id, need, editorDescription);
	}

	// add/remove
	public final T add(T element) {
		return add(element.getId(), element);
	}

	@Override
	protected abstract T createElement(String elementId);

	public final T createAndAddElement() {
		return add(createElement());
	}

	public final T createElement() {
		// generate numeric id
		Integer highest = null;
		for (T value : values()) {
			Integer nb = NumberUtils.integerOrNull(value.getId());
			if (nb != null && (highest == null || nb > highest)) {
				highest = nb;
			}
		}
		if (highest == null) highest = 0;
		// create
		return createElement("" + highest);
	}

	@Override
	public EditorGUI editorGUI(ClickCall fromCall) {
		EditorGUI editor = new EditorGUI(this, fromCall) {
			@Override
			protected boolean doFill() {
				// values
				int slot = -1;
				for (T element : values()) {
					// build icon
					ItemStack icon = element.editorIcon();
					ItemMeta meta = icon.getItemMeta();
					List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
					lore.add("§r");
					lore.addAll(TextEditorGeneric.controlDelete.parseLines());
					meta.setLore(lore);
					icon.setItemMeta(meta);
					// set item
					setRegularItem(new GUIItem("element_" + element.getId(), ++slot, icon, call -> {
						// control drop : delete
						if (call.getType().equals(ClickType.CONTROL_DROP)) {
							remove(element);
							getSuperElement().onEditorChange(ListElement.this);
							refill();
						}
						// other
						else {
							element.onEditorClick(call);
						}
					}));
				}
				// create item
				setPersistentItem(new GUIItem("new_element", 50, ItemUtils.createItem(CommonMats.BLAZE_ROD, TextEditorGeneric.controlAddElementName.parseLine(), TextEditorGeneric.controlAddElementWithQuick.parseLines()), call -> {
					// left-click : quickly create with a generated id
					if (call.getType().equals(ClickType.LEFT)) {
						T element = createAndAddElement();
						getSuperElement().onEditorChange(element);
						// reopen GUI (that refreshes it since it's an editor GUI)
						call.getGUI().openFor(call.getClicker(), call.getPageIndex());
					}
					// right-click : manually enter id
					else if (call.getType().equals(ClickType.RIGHT)) {
						editorAskKeyAndCreateAndAddElement(call, (elementId, element) -> {
							getSuperElement().onEditorChange(element);
							// reopen GUI (that refreshes it since it's an editor GUI)
							call.getGUI().openFor(call.getClicker(), call.getPageIndex());
						}, () -> call.getGUI().openFor(call.getClicker(), call.getPageIndex()));
					}
				}));
				// done
				return super.doFill();
			}
		};
		return editor;
	}

	@Override
	protected void editorAskKeyAndCreateAndAddElement(ClickCall call, BiConsumer<String, T> onCreate, Runnable onCancel) {
		WorkerGCore.inst().awaitChat(call.getClicker(), TextEditorGeneric.messageElementCreateEnterId, raw -> {
			// invalid id, or already exists
			final String id = raw.toLowerCase().trim();
			if (!StringUtils.isAlphanumeric(id.replace("_", ""))) {
				TextEditorGeneric.messageElementCreateInvalidId.replace("{value}", () -> id).send(call.getClicker());
				onCancel.run();
			} else if (getElement(id).isPresent()) {
				TextEditorGeneric.messageElementCreateAlreadyExists.replace("{value}", () -> id).send(call.getClicker());
				onCancel.run();
			}
			// create element
			else {
				T element = createAndAddElement(id);
				onCreate.accept(element.getId(), element);
			}
		}, onCancel);

	}

}
