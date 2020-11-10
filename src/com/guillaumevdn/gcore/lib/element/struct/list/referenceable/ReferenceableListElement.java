package com.guillaumevdn.gcore.lib.element.struct.list.referenceable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import com.guillaumevdn.gcore.lib.object.Optional;
import java.util.function.BiConsumer;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.WorkerGCore;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.editor.EnumSelectorGUI;
import com.guillaumevdn.gcore.lib.element.editor.EditorGUI;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.map.AbstractMapElement;
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
public abstract class ReferenceableListElement<T extends Element> extends AbstractMapElement<String, Node<T>> {

	private final ElementsContainer<? extends T> ref;

	protected ReferenceableListElement(ElementsContainer<? extends T> ref, String typeName, Element parent, String id, Need need, Text editorDescription) {
		super(String.class, "list of " + typeName, parent, id, need, editorDescription);
		this.ref = ref;
	}

	// get
	protected ElementsContainer<? extends T> getRef() {
		return ref;
	}

	public final List<T> getActualValues() {
		List<T> values = new ArrayList<>();
		values().forEach(node -> values.add(node.getValue()));
		return Collections.unmodifiableList(values);
	}

	public final Optional<T> getActualValue(String key) {
		return getElement(key).ifPresentMap(Node::getValue);
	}

	@Override
	public final int size() {
		return super.size();
	}

	@Override
	public final boolean isEmpty() {
		return super.isEmpty();
	}

	// add/remove
	public T add(String key) {
		return add(key, (T) null);
	}

	public T add(String key, T value) {
		return super.add(key, new Node<>(ref, key, value)).getValue();
	}

	// loading and saving
	@Override
	protected final void clearBeforeRead() {
		clear();
	}

	@Override
	protected void doRead() throws Throwable {
		String path = getConfigurationPath();
		for (String elementId : getSuperElement().getConfiguration().readKeysForSection(path)) {
			// global element
			boolean global = elementId.startsWith("global@");
			if (global) elementId = elementId.substring("global@".length());
			if (global) {
				add(elementId);
			} else {
				createAndAddLocalElement(elementId).read();
			}
		}
	}

	@Override
	protected void doWrite() throws Throwable {
		
		getSuperElement().getConfiguration().write(getConfigurationPath(), null);
		for (Node<T> element : values()) {
			element.getValue().write();
			if (element.getType().equals(NodeType.GLOBAL)) {
				getSuperElement().getConfiguration().write(getConfigurationPath() + ".global@" + element.getKey(), "/");
			}
		}
	}

	protected final T createAndAddLocalElement(String elementId) {
		return add(elementId, createLocalElement(elementId));
	}

	protected abstract T createLocalElement(String elementId);

	// editor
	@Override
	public EditorGUI editorGUI(ClickCall fromCall) {
		EditorGUI editor = new EditorGUI(this, fromCall) {
			@Override
			protected boolean doFill() {
				// values
				int slot = -1;
				for (Node<T> node : values()) {
					T element = node.getValue();
					// build icon
					ItemStack icon = element.editorIcon();
					ItemMeta meta = icon.getItemMeta();
					List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
					lore.addAll(TextEditorGeneric.controlDelete.parseLines());
					lore.add("§r");
					meta.setLore(lore);
					icon.setItemMeta(meta);
					// set item
					setRegularItem(new GUIItem("element_" + element.getId(), ++slot, icon, call -> {
						// control drop : delete
						if (call.getType().equals(ClickType.CONTROL_DROP)) {
							remove(node);
							getSuperElement().onEditorChange(ReferenceableListElement.this);
							refill();
						}
						// other
						else {
							element.onEditorClick(call);
						}
					}));
				}
				// create items
				setPersistentItem(new GUIItem("new_global_element", 49, ItemUtils.createItem(CommonMats.BLAZE_ROD, TextEditorGeneric.controlAddGlobalElementName.parseLine(), null), call -> {
					// left-click : create
					if (call.getType().equals(ClickType.LEFT)) {
						LinkedHashMap<String, Mat> remaining = new LinkedHashMap<>();
						getRef().getIcons().entrySet().stream().sorted((a, b) -> a.getValue().compareTo(b.getValue())).forEach(entry -> {
							if (!keys().contains(entry.getKey())) {
								remaining.put(entry.getKey(), entry.getValue());
							}
						});
						EnumSelectorGUI.openSelector(call.getClicker(), false, getKeySerializer(), () -> remaining, key -> {
							add(key);
							getSuperElement().onEditorChange(ReferenceableListElement.this);
							// reopen GUI (that refreshes it since it's an editor GUI)
							call.getGUI().openFor(call.getClicker(), call.getPageIndex());
						}, () -> call.getGUI().openFor(call.getClicker(), call.getPageIndex()));
					}
				}));
				setPersistentItem(new GUIItem("new_element", 50, ItemUtils.createItem(CommonMats.BLAZE_ROD, TextEditorGeneric.controlAddElementName.parseLine(), TextEditorGeneric.controlAddElementWithQuick.parseLines()), call -> {
					// left-click : quickly create with a generated id
					if (call.getType().equals(ClickType.LEFT)) {
						// generate numeric id
						Integer highest = null;
						for (Node<T> node : values()) {
							Integer nb = NumberUtils.integerOrNull(node.getKey());
							if (nb != null && (highest == null || nb > highest)) {
								highest = nb;
							}
						}
						if (highest == null) highest = 0;
						// create element
						createAndAddLocalElement(String.valueOf(++highest));
						getSuperElement().onEditorChange(ReferenceableListElement.this);
						// reopen GUI (that refreshes it since it's an editor GUI)
						call.getGUI().openFor(call.getClicker(), call.getPageIndex());
					}
					// right-click : manually enter id
					else if (call.getType().equals(ClickType.RIGHT)) {
						editorAskKeyAndCreateAndAddLocalElement(call, (elementId, element) -> {
							getSuperElement().onEditorChange(ReferenceableListElement.this);
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

	protected void editorAskKeyAndCreateAndAddLocalElement(ClickCall call, BiConsumer<String, T> onCreate, Runnable onCancel) {
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
				T element = createAndAddLocalElement(id);
				onCreate.accept(element.getId(), element);
			}
		}, onCancel);

	}

}
