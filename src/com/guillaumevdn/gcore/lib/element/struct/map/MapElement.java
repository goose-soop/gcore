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
import com.guillaumevdn.gcore.lib.serialization.Serializer;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public abstract class MapElement<K, V extends Element> extends AbstractMapElement<K, V> {

	public MapElement(Class<K> keyClass, Element parent, String id, Need need, Text editorDescription) {
		super(keyClass, parent, id, need, editorDescription);
	}

	public MapElement(Serializer<K> keySerializer, Element parent, String id, Need need, Text editorDescription) {
		super(keySerializer, parent, id, need, editorDescription);
	}

	// ----- get
	@Override
	public final int size() {
		return super.size();
	}

	@Override
	public final boolean isEmpty() {
		return super.isEmpty();
	}

	public Optional<V> getValue(K key) {
		return super.getElement(key);
	}

	@Override
	public void clear() {
		super.clear();
	}

	// ----- add/remove
	@Override
	public final V add(K key, V value) {
		return super.add(key, value);
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
			// invalid key
			K key = null;
			try {
				key = getKeySerializer().deserialize(elementId);
			} catch (Throwable ignored) {
				/*ignored.printStackTrace();
				Bukkit.getLogger().info("element id " + elementId + ", type serializer " + getKeySerializer());*/
			}
			if (key == null) {
				getSuperElement().addLoadError("key " + elementId + " at path " + path + " isn't a valid " + getKeySerializer().getTypeName());
			}
			// valid key
			else {
				V element = createAndAddElement(key);
				element.read();
			}
		}
	}

	@Override
	protected void doWrite() throws Throwable {
		getSuperElement().getConfiguration().write(getConfigurationPath(), null);
		for (V element : values()) {
			element.write();
		}
	}

	public final V createAndAddElement(K key) {
		return add(key, createElement(getKeySerializer().serialize(key)));
	}

	protected abstract V createElement(String elementId);

	// ----- editor
	@Override
	public EditorGUI editorGUI(ClickCall fromCall) {
		EditorGUI editor = new EditorGUI(this, fromCall) {
			@Override
			protected boolean doFill() {
				// values
				int slot = -1;
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
							getSuperElement().onEditorChange(MapElement.this);
							refill();
						}
						// other
						else {
							element.onEditorClick(call);
						}
					}));
				}
				// create item
				setPersistentItem(new GUIItem("new_element", 50, ItemUtils.createItem(CommonMats.BLAZE_ROD, TextEditorGeneric.controlAddElementName.parseLine(), null), call -> {
					// left-click : create
					if (call.getType().equals(ClickType.LEFT)) {
						editorAskKeyAndCreateAndAddElement(call, (key, value) -> {
							getSuperElement().onEditorChange(MapElement.this);
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

	protected abstract void editorAskKeyAndCreateAndAddElement(ClickCall call, BiConsumer<K, V> onCreate, Runnable onCancel);

}
