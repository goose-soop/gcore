package com.guillaumevdn.gcore.lib.element.struct.list;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.WorkerGCore;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.configuration.file.node.SectionNode;
import com.guillaumevdn.gcore.lib.configuration.file.node.SectionNode.SectionNodeType;
import com.guillaumevdn.gcore.lib.element.editor.EditorGUI;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.map.MapElement;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall.ClickType;
import com.guillaumevdn.gcore.lib.gui.struct.GUIItem;
import com.guillaumevdn.gcore.lib.item.ItemUtils;
import com.guillaumevdn.gcore.lib.serialization.Serializer;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public abstract class ListElement<T extends Element> extends MapElement<String, T> {

	private final boolean allowCompactNestedWrite;

	public ListElement(boolean allowCompactNestedWrite, Element parent, String id, Need need, Text editorDescription) {
		super(String.class, parent, id, need, editorDescription);
		this.allowCompactNestedWrite = allowCompactNestedWrite;
	}

	protected ListElement(boolean allowCompactNestedWrite, Serializer<String> keySerializer, Element parent, String id, Need need, Text editorDescription) {
		super(keySerializer, parent, id, need, editorDescription);
		this.allowCompactNestedWrite = allowCompactNestedWrite;
	}

	// ----- add/remove

	public final T add(T element) {
		return add(element.getId(), element);
	}

	@Override
	protected abstract T createElement(String elementId);

	public final T createAndAddElement() {
		return add(createElement());
	}

	public final T createElement() {
		int i = 0;
		String key;
		do {
			key = StringUtils.alphabeticCountFor(++i);
		} while (getElement(key).isPresent());
		return createElement(key);
	}

	// ----- element

	@Override
	protected void doWrite() throws Throwable {
		final boolean existed = readContains(); // getSuperElement().getConfiguration().getBackingYML().getSectionNode(getConfigurationPath()) != null; // do
												// not override existing section types ; make it a compact nested map only if it wasn't written before

		getSuperElement().getConfiguration().write(getConfigurationPath(), null);
		if (!isEmpty()) {
			SectionNode node = null;
			if (!existed && allowCompactNestedWrite) {
				node = getSuperElement().getConfiguration().getBackingYML().mkdirs(getConfigurationPath(), SectionNodeType.COMPACT_NESTED_MAP);
				node.setSingleValue("_____TO_BE_REMOVED_____", "value", null); // when we call element.write() below, it sets the path to null ; thus, it clears
																				// the parent section, anihilating our hopes that it'll be a compact nested map
			}
			for (T element : values()) {
				element.write();
			}
			if (node != null) {
				node.removeConfigNode("_____TO_BE_REMOVED_____");
			}
		}
	}

	// ----- editor

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
				setPersistentItem(new GUIItem("new_element", 50, ItemUtils.createItem(CommonMats.BLAZE_ROD, TextEditorGeneric.controlAddElementName.parseLine(),
						TextEditorGeneric.controlAddElementWithQuick.parseLines()), call -> {
							// left-click : quickly create with a generated id
							if (call.getType().equals(ClickType.LEFT)) {
								T element = createAndAddElement();
								getSuperElement().onEditorChange(element);
								// reopen GUI (that refreshes it since it's an editor GUI)
								call.reopenGUI();
							}
							// right-click : manually enter id
							else if (call.getType().equals(ClickType.RIGHT)) {
								editorAskKeyAndCreateAndAddElement(call, (elementId, element) -> {
									getSuperElement().onEditorChange(element);
									// reopen GUI (that refreshes it since it's an editor GUI)
									call.reopenGUI();
								}, () -> call.reopenGUI());
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
