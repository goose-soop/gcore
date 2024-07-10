package com.guillaumevdn.gcore.lib.element.struct.list.referenceable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.WorkerGCore;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.configuration.file.node.SectionNode;
import com.guillaumevdn.gcore.lib.configuration.file.node.SectionNode.SectionNodeType;
import com.guillaumevdn.gcore.lib.element.editor.EditorGUI;
import com.guillaumevdn.gcore.lib.element.editor.EnumSelectorGUI;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.map.AbstractMapElement;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall.ClickType;
import com.guillaumevdn.gcore.lib.gui.struct.GUIItem;
import com.guillaumevdn.gcore.lib.item.ItemUtils;
import com.guillaumevdn.gcore.lib.object.Optional;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public abstract class ReferenceableListElement<T extends Element> extends AbstractMapElement<String, Node<T>> {

	private final ElementsContainer<? extends T> ref;
	private final boolean allowCompactNestedWrite;

	protected ReferenceableListElement(ElementsContainer<? extends T> ref, boolean allowCompactNestedWrite, Element parent, String id, Need need,
			Text editorDescription) {
		super(String.class, parent, id, need, editorDescription);
		this.ref = ref;
		this.allowCompactNestedWrite = allowCompactNestedWrite;
	}

	// ----- get
	protected ElementsContainer<? extends T> getRef() {
		return ref;
	}

	public final List<T> getActualValues() {
		return Collections.unmodifiableList(values().stream().map(Node::getValue).filter(v -> v != null).collect(Collectors.toList()));
	}

	@Override
	public final Optional<Node<T>> getElement(String key) {
		return findElement(node -> node.keyOrGlobalIdEquals(key));
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

	// ----- add/remove
	public T add(String key, String globalId) {
		return super.add(key, new Node<>(ref, key, globalId)).getValue();
	}

	public T add(String key, T value) {
		return super.add(key, new Node<>(ref, key, value)).getValue();
	}

	// ----- loading and saving
	@Override
	protected final void clearBeforeRead() {
		clear();
	}

	@Override
	protected void doRead() throws Throwable {
		String path = getConfigurationPath();
		List<String> keys = getSuperElement().getConfiguration().readKeysForSection(path);

		reinitializeElements(keys.size(), 1f);
		for (String elementId : keys) {
			// global element
			String globalElementId = null;
			if (!getSuperElement().getConfiguration().isConfigurationSection(path + "." + elementId)) {
				String value = getSuperElement().getConfiguration().readString(path + "." + elementId, "");
				if (value.startsWith("global@")) {
					globalElementId = value.substring("global@".length());
				}
			}
			// add
			if (globalElementId != null) {
				add(elementId, globalElementId);
			} else {
				createAndAddLocalElement(elementId).read();
			}
		}
	}

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
			for (Node<T> element : values()) {
				element.getValue().write();
				if (element.getType().equals(NodeType.GLOBAL)) {
					getSuperElement().getConfiguration().write(getConfigurationPath() + "." + element.getKey(), "global@" + element.getGlobalId());
				}
			}
			if (node != null) {
				node.removeConfigNode("_____TO_BE_REMOVED_____");
			}
		}
	}

	protected final T createAndAddLocalElement(String elementId) {
		return add(elementId, createLocalElement(elementId));
	}

	protected abstract T createLocalElement(String elementId);

	// ----- editor
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
				setPersistentItem(new GUIItem("new_element", 50, ItemUtils.createItem(CommonMats.BLAZE_ROD, TextEditorGeneric.controlAddElementName.parseLine(),
						TextEditorGeneric.controlAddElementWithQuick.parseLines()), call -> {
							// left-click : quickly create with a generated id
							if (call.getType().equals(ClickType.LEFT)) {
								// generate id
								int i = 0;
								String key;
								do {
									key = StringUtils.alphabeticCountFor(++i);
								} while (getElement(key).isPresent());
								// create element
								createAndAddLocalElement(key);
								getSuperElement().onEditorChange(ReferenceableListElement.this);
								// reopen GUI (that refreshes it since it's an editor GUI)
								call.reopenGUI();
							}
							// right-click : manually enter id
							else if (call.getType().equals(ClickType.RIGHT)) {
								editorAskKeyAndCreateAndAddLocalElement(call, (elementId, element) -> {
									getSuperElement().onEditorChange(ReferenceableListElement.this);
									// reopen GUI (that refreshes it since it's an editor GUI)
									call.reopenGUI();
								}, () -> call.reopenGUI());
							}
						}));
				setPersistentItem(new GUIItem("new_global_element", 51,
						ItemUtils.createItem(CommonMats.BLAZE_ROD, TextEditorGeneric.controlAddGlobalElementName.parseLine(), null), call -> {
							// left-click : create
							if (call.getType().equals(ClickType.LEFT)) {
								LinkedHashMap<String, Mat> remaining = new LinkedHashMap<>();
								getRef().getIcons().entrySet().stream().sorted((a, b) -> a.getValue().compareTo(b.getValue())).forEach(entry -> {
									if (!keys().contains(entry.getKey())) {
										remaining.put(entry.getKey(), entry.getValue());
									}
								});
								EnumSelectorGUI.openSelector(call.getClicker(), false, getKeySerializer(), () -> remaining, key -> {
									add(key, key);
									getSuperElement().onEditorChange(ReferenceableListElement.this);
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
