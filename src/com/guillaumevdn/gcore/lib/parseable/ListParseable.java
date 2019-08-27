package com.guillaumevdn.gcore.lib.parseable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.GLocale;
import com.guillaumevdn.gcore.lib.gui.GUI;
import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.parseable.editor.EditorGUI;
import com.guillaumevdn.gcore.lib.parseable.editor.EditorItem;
import com.guillaumevdn.gcore.lib.parseable.editor.ModifCallback;
import com.guillaumevdn.gcore.lib.util.Utils;
import com.guillaumevdn.gcore.lib.util.Wrapper;
import com.guillaumevdn.gcore.lib.util.input.ChatInput;

public abstract class ListParseable<T extends Parseable> extends Parseable {

	// base
	private String elementTypeName;
	private Map<String, T> elements = new HashMap<String, T>();// LOWER-CASE KEY

	public ListParseable(String id, Parseable parent, String elementTypeName, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, mandatory, editorSlot, editorIcon, editorDescription);
		this.elementTypeName = elementTypeName;
	}

	public String getElementTypeName() {
		return elementTypeName;
	}

	public Map<String, T> getElements() {
		return elements;
	}

	public T getElement(String id) {
		return id == null ? null : elements.get(id.toLowerCase());
	}
	
	/** @return the previous value associated with the same id, or null if there was none */
	public T addElement(T element) {
		return elements.put(element.getId().toLowerCase(), element);
	}

	/** @return the removed value associated with the id, or null if there was none */
	public T removeElement(String id) {
		return id == null ? null : elements.remove(id.toLowerCase());
	}

	public void clearElements() {
		elements.clear();
	}

	/** Create an element with the specified id, and add it to the list */
	public abstract T createElement(String elementId);

	// load and save
	@Override
	public boolean hasErrors() {
		if (getLastData() != null && getLastData().getLastError() != null) {
			return true;
		}
		for (T element : elements.values()) {
			if (element.hasErrors()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void load(ConfigData data) {
		if (data == null) return;
		// link
		setLastData(data);
		data.setComponent(this);
		// load
		data.setContains(data.getConfig().contains(data.getPath()));
		if (data.contains()) {
			for (String elementId : data.getConfig().getKeysForSection(data.getPath(), false)) {
				loadElement(elementId, new ConfigData(data.getPlugin(), data.getSuperId(), data.getConfig(), data.getPath().isEmpty() ? elementId : data.getPath() + "." + elementId));
			}
		} else if (isMandatory()) {
			data.log("missing setting (must be a list of " + elementTypeName + ")");
		}
	}

	public abstract T loadElement(String elementId, ConfigData data);

	@Override
	public void save(ConfigData data) {
		if (data == null) return;
		// link
		setLastData(data);
		data.setComponent(this);
		// save
		data.getConfig().set(data.getPath(), null);
		for (T element : Utils.asSortedList(elements.values())) {
			element.save(new ConfigData(data.getPlugin(), data.getSuperId(), data.getConfig(), data.getPath().isEmpty() ? element.getId() : data.getPath() + "." + element.getId()));
		}
		data.setContains(data.getConfig().contains(data.getPath()));
	}

	// editor
	@Override
	public List<String> describe(int depth) {
		String spaces = Utils.copyString(" ", depth + 1);
		List<String> desc = Utils.asList(spaces + "§6> " + getId() + " :" + (elements.isEmpty() ? " §8(empty value)" : ""));
		for (Parseable element : Utils.asSortedList(elements.values(), Utils.objectSorter)) {
			List<String> sub = element.describe(depth + 1);
			if (depth + 1 == EditorGUI.MAX_DESCRIPTION_DEPTH && sub.size() > 1) {// max depth reached, and sub description is more than one line
				desc.add(spaces + " §6> " + element.getId() + " : §8...");
			} else {
				desc.addAll(sub);
			}
		}
		return desc;
	}

	@Override
	protected void fillEditor(final EditorGUI gui, Player player, final ModifCallback onModif) {
		// delete wrapper
		final Wrapper<Boolean> delete = new Wrapper<Boolean>(false);
		// add elements items
		for (final Parseable element : Utils.asSortedList(elements.values())) {
			// build lore
			List<String> lore = new ArrayList<String>();
			if (element.getEditorDescription() != null) {
				for (String line : element.getEditorDescription()) {
					if (lore.size() >= EditorGUI.MAX_DESCRIPTION_LINES) break;
					lore.add(line);
				}
			}
			for (String line : element.describe(1)) {
				if (lore.size() >= EditorGUI.MAX_DESCRIPTION_LINES) break;
				lore.add(line);
			}
			// set item
			gui.setRegularItem(new EditorItem(element.getId(), element.getEditorSlot(), element.getEditorIcon(), "§6" + element.getId(), lore) {
				@Override
				public void onClick(final Player player, final ClickType clickType, final int pageIndex) {
					// eventually delete
					if (delete.getValue()) {
						delete.setValue(Boolean.FALSE);
						// delete and callback
						elements.remove(element.getId());
						onModif.callback(gui, player);
						// re-fill and open
						gui.open(player);
						return;
					}
					// create element GUI
					String name = Utils.getNewInventoryName(gui.getName(), element.getId());
					EditorGUI sub = new EditorGUI(element.getLastData().getPlugin(), gui, name, element.getEditorSize(), element.getEditorRegularSlots()) {
						private EditorGUI subThis = this;
						@Override
						protected void fill() {
							// back item
							subThis.setPersistentItem(new EditorItem("control_item_back", element.getEditorBackSlot(), Mat.ARROW, GLocale.GUI_GENERIC_EDITORITEMBACK.getLine(), null) {
								@Override
								protected void onClick(final Player player, final ClickType clickType, final int pageIndex) {
									gui.open(player);
								}
							});
							// fill
							element.fillEditor(subThis, player, onModif);
						}
					};
					// open it
					sub.open(player);
					return;
				}
			});
		}
		// new item
		gui.setPersistentItem(new EditorItem("control_item_new", 49, Mat.BLAZE_ROD, GLocale.GUI_GENERIC_EDITORITEMADD.getLine(), null) {
			@Override
			protected void onClick(final Player player, final ClickType clickType, final int pageIndex) {
				player.closeInventory();
				GLocale.MSG_GENERIC_CHATINPUTID.send(player);
				GCore.inst().getChatInputs().put(player, new ChatInput() {
					@Override
					public void onChat(Player player, String value) {
						if (!value.replace(" ", "").equalsIgnoreCase("cancel")) {
							value = value.replace(" ", "_").toLowerCase();
							if (!Utils.isAlphanumeric(value.replace("_", ""))) {
								GLocale.MSG_GENERIC_INVALIDALPHANUMERIC.send(player, "{plugin}", GCore.inst().getName(), "{error}", value);
							} else if (elements.containsKey(value)) {
								GLocale.MSG_GENERIC_DUPLICATEELEMENT.send(player, "{id}", value);
							} else {
								createElement(value);
								onModif.callback(gui, player);
							}
						}
						gui.open(player);// re-fill and open
					}
				});
			}
		});
		// delete item
		gui.setPersistentItem(new EditorItem("control_item_delete", 46, Mat.TNT_MINECART, GLocale.GUI_GENERIC_EDITORITEMDELETE.getLine(), GLocale.GUI_GENERIC_EDITORITEMDELETELORE.getLines()) {
			@Override
			protected void onClick(final Player player, final ClickType clickType, final int pageIndex) {
				delete.setValue(Boolean.TRUE);
				GLocale.MSG_GENERIC_DELETEELEMENT.send(player);
			}
		});
	}

	// editor
	@Override
	public int getEditorSize() {
		return 54;
	}

	@Override
	public List<Integer> getEditorRegularSlots() {
		return GUI.SLOTS_0_TO_44;
	}

	@Override
	public int getEditorBackSlot() {
		return 52;
	}

	// clone
	protected ListParseable() {
		super();
	}

	@Override
	public ListParseable<T> clone() {
		// clone
		ListParseable<T> clone = (ListParseable<T>) super.clone();
		// clone properties
		clone.elementTypeName = elementTypeName;
		for (String elementId : elements.keySet()) {
			clone.elements.put(elementId, (T) elements.get(elementId).clone());
		}
		// success
		return clone;
	}

}
