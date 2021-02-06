package com.guillaumevdn.gcore.lib.element.struct.map;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.element.editor.EditorGUI;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall.ClickType;
import com.guillaumevdn.gcore.lib.gui.struct.GUIItem;
import com.guillaumevdn.gcore.lib.item.ItemUtils;
import com.guillaumevdn.gcore.lib.object.Optional;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public abstract class DefaultMapElement<K, V extends Element> extends MapElement<K, V> {

	private V defaultElement = null;

	public DefaultMapElement(Class<K> keyClass, String valueTypeName, Element parent, String id, Need need, Text editorDescription) {
		super(keyClass, valueTypeName, parent, id, need, editorDescription);
	}

	protected DefaultMapElement(Class<K> keyClass, String typeName, boolean overrideTypeName, Element parent, String id, Need need, Text editorDescription) {
		super(keyClass, typeName, overrideTypeName, parent, id, need, editorDescription);
	}

	// get
	@Override
	public Optional<V> getElement(K key) {
		Optional<V> val = super.getElement(key);
		if (!val.isPresent()) val = getDefaultElement();
		return val;
	}

	public Optional<V> getDefaultElement() {
		return Optional.of(defaultElement);
	}

	@Override
	public boolean hasParseableLocations() {
		return super.hasParseableLocations() || defaultElement == null || defaultElement.hasParseableLocations();
	}

	// loading and saving
	@Override
	protected void doRead() throws Throwable {
		String path = getConfigurationPath();
		for (String elementId : getSuperElement().getConfiguration().readKeysForSection(path)) {
			// default
			if (elementId.equalsIgnoreCase("DEFAULT")) {
				(defaultElement = createDefaultElement("DEFAULT")).read();
			}
			// not default
			else {
				// invalid key
				K key = null;
				try {
					key = getKeySerializer().deserialize(elementId);
				} catch (Throwable ignored) {}
				if (key == null) {
					getSuperElement().addLoadError("key " + elementId + " at path " + path + " isn't a valid " + getKeySerializer().getTypeName());
				}
				// valid key
				else {
					createAndAddElement(key).read();
				}
			}
		}
	}

	@Override
	public abstract V createElement(String elementId);
	public abstract V createDefaultElement(String elementId);

	@Override
	protected void doWrite() throws Throwable {
		getSuperElement().getConfiguration().write(getConfigurationPath(), null);
		for (V element : values()) {
			element.write();
		}
		if (defaultElement != null) {
			defaultElement.write();
		}
	}

	// editor
	@Override
	public EditorGUI editorGUI(ClickCall fromCall) {
		EditorGUI editor = new EditorGUI(this, fromCall) {
			@Override
			protected boolean doFill() {
				// default value
				if (defaultElement != null) {
					// build icon
					ItemStack icon = defaultElement.editorIcon();
					ItemMeta meta = icon.getItemMeta();
					List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
					lore.add("§r");
					lore.addAll(TextEditorGeneric.controlDelete.parseLines());
					meta.setLore(lore);
					icon.setItemMeta(meta);
					// set item
					setPersistentItem(new GUIItem("element_" + defaultElement.getId(), 0, icon, call -> {
						// shift + right-click : delete
						if (call.getType().equals(ClickType.CONTROL_DROP)) {
							getSuperElement().onEditorChange(DefaultMapElement.this);
							defaultElement = null;
							refill();
						}
						// other
						else {
							defaultElement.onEditorClick(call);
						}
					}));
				}
				// values
				int slot = defaultElement != null ? 0 : -1;
				for (V element : values()) {
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
						// shift + right-click : delete
						if (call.getType().equals(ClickType.CONTROL_DROP)) {
							remove(element);
							getSuperElement().onEditorChange(DefaultMapElement.this);
							refill();
						}
						// other
						else {
							element.onEditorClick(call);
						}
					}));
				}
				// create item
				setPersistentItem(new GUIItem("new_element", 50, ItemUtils.createItem(CommonMats.BLAZE_ROD, TextEditorGeneric.controlAddElementName.parseLine(), TextEditorGeneric.controlAddElementWithDefault.parseLines()), call -> {
					// left-click : create
					if (call.getType().equals(ClickType.LEFT)) {
						editorAskKeyAndCreateAndAddElement(call, (key, value) -> {
							getSuperElement().onEditorChange(DefaultMapElement.this);
							// reopen GUI (that refreshes it since it's an editor GUI)
							call.getGUI().openFor(call.getClicker(), call.getPageIndex());
						}, () -> call.getGUI().openFor(call.getClicker(), call.getPageIndex()));
					}
					// right-click : create default
					else if (call.getType().equals(ClickType.RIGHT)) {
						defaultElement = createDefaultElement("DEFAULT");
						getSuperElement().onEditorChange(DefaultMapElement.this);
						// reopen GUI (that refreshes it since it's an editor GUI)
						call.getGUI().openFor(call.getClicker(), call.getPageIndex());
					}
				}));
				// done
				return super.doFill();
			}
		};
		return editor;
	}

	@Override
	protected abstract void editorAskKeyAndCreateAndAddElement(ClickCall call, BiConsumer<K, V> onCreate, Runnable onCancel);

}
