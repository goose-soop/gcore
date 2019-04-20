package com.guillaumevdn.gcore.lib.parseable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import com.guillaumevdn.gcore.GLocale;
import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.parseable.editor.EditorGUI;
import com.guillaumevdn.gcore.lib.parseable.editor.EditorItem;
import com.guillaumevdn.gcore.lib.parseable.editor.ModifCallback;
import com.guillaumevdn.gcore.lib.util.Utils;

public abstract class ContainerParseable extends Parseable {

	// base
	protected String typeName;
	protected Map<String, Parseable> components = new HashMap<String, Parseable>();// LOWER CASE KEY

	protected ContainerParseable() {// used to clone object
		super();
	}

	public ContainerParseable(String id, Parseable parent, String typeName, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, mandatory, editorSlot, editorIcon, editorDescription);
		this.typeName = typeName;
	}

	public Map<String, Parseable> getComponents() {
		return components;
	}

	public Parseable getComponent(String id) {
		Parseable component = components.get(id.toLowerCase());
		if (component == null) throw new IllegalArgumentException("there's no component with id " + id);
		return component;
	}

	public <C extends Parseable> C getComponent(String id, Class<C> componentClass) {
		Parseable component = getComponent(id.toLowerCase());
		if (!Utils.instanceOf(component, componentClass)) throw new IllegalArgumentException("component with id " + id + " is not of type " + componentClass.getName());
		return (C) component;
	}

	public <T extends Parseable> T addComponent(T component) {
		components.put(component.getId().toLowerCase(), component);
		return component;
	}

	public <T extends Parseable> void removeComponent(T component) {
		components.remove(component.getId().toLowerCase());
	}

	// load and save
	@Override
	public boolean hasErrors() {
		if (getLastData() != null && getLastData().getLastError() != null) {
			return true;
		}
		for (Parseable component : components.values()) {
			if (component.hasErrors()) {
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
			for (Parseable component : components.values()) {
				component.load(new ConfigData(data.getPlugin(), data.getSuperId(), data.getConfig(), data.getPath().isEmpty() ? component.getId() : data.getPath() + "." + component.getId()));
			}
		} else if (isMandatory()) {
			data.log("missing setting (must be a " + typeName + ")");
		}
	}

	@Override
	public void save(ConfigData data) {
		if (data == null) return;
		// link
		setLastData(data);
		data.setComponent(this);
		// save
		data.getConfig().set(data.getPath(), null);
		for (Parseable component : components.values()) {
			component.save(new ConfigData(data.getPlugin(), data.getSuperId(), data.getConfig(), data.getPath().isEmpty() ? component.getId() : data.getPath() + "." + component.getId()));
		}
		data.setContains(data.getConfig().contains(data.getPath()));
	}

	// editor
	@Override
	public List<String> describe(int depth) {
		String spaces = Utils.copyString(" ", depth + 1);
		List<String> desc = Utils.asList(spaces + "§6> " + getId() + " :");
		for (Parseable component : components.values()) {
			List<String> sub = component.describe(depth + 1);
			if (depth + 1 == EditorGUI.MAX_DESCRIPTION_DEPTH && sub.size() > 1) {// max depth reached, and sub description is more than one line
				desc.add(spaces + " §6> " + component.getId() + " : §8...");
			} else {
				desc.addAll(sub);
			}
		}
		return desc;
	}

	@Override
	protected void fillEditor(final EditorGUI gui, Player player, final ModifCallback onModif) {
		// add components items
		for (final Parseable component : components.values()) {
			List<String> lore = new ArrayList<String>(), desc = component.getEditorDescription();
			if (desc != null) {
				for (String line : desc) {
					lore.add(line);
					if (lore.size() >= EditorGUI.MAX_DESCRIPTION_LINES) break;
				}
			}
			gui.setRegularItem(new EditorItem(component.getId(), component.getEditorSlot(), component.getEditorIcon(), "§6" + component.getId(), lore) {// TODO : (same for list) instead of getEditorDescription, describe children as well
				@Override
				protected void onClick(final Player player, ClickType clickType, int pageIndex) {
					// create component GUI
					String name = Utils.getNewInventoryName(gui.getName(), component.getId());
					EditorGUI sub = new EditorGUI(component.getLastData().getPlugin(), gui, name, component.getEditorSize(), component.getEditorMaxRegularSlot()) {
						private EditorGUI subThis = this;
						@Override
						protected void fill() {
							// back item
							subThis.setPersistentItem(new EditorItem("control_item_back", component.getEditorBackSlot(), Mat.ARROW, GLocale.GUI_GENERIC_EDITORITEMBACK.getLine(), null) {
								@Override
								protected void onClick(final Player player, final ClickType clickType, final int pageIndex) {
									gui.open(player);
								}
							});
							// fill
							component.fillEditor(subThis, player, onModif);
						}
					};
					// open it
					sub.open(player);
				}
			});
		}
	}

	@Override
	public int getEditorSize() {
		int maxSlot = 8;
		for (Parseable component : components.values()) {
			if (component.getEditorSlot() > maxSlot) {
				maxSlot = component.getEditorSlot();
			}
		}
		int size = maxSlot % 9 == 0 ? maxSlot + 1 : maxSlot;
		for (; size % 9 != 0; ++size);
		size += 9;
		return size > 54 ? 54 : size;
	}

	@Override
	public int getEditorMaxRegularSlot() {
		return getEditorSize() - 10;
	}

	@Override
	public int getEditorBackSlot() {
		return getEditorSize() - 2;
	}

	// clone
	@Override
	public ContainerParseable clone() {
		// clone
		ContainerParseable clone = (ContainerParseable) super.clone();
		// clone properties
		clone.typeName = typeName;
		for (String componentId : components.keySet()) {
			clone.components.put(componentId, components.get(componentId).clone());
		}
		// success
		return clone;
	}

	@Override
	public <T extends Parseable> T cloneAs(Class<T> clazz) {
		ContainerParseable clone = (ContainerParseable) super.cloneAs(clazz);
		clone.typeName = typeName;
		for (String componentId : components.keySet()) {
			clone.components.put(componentId, components.get(componentId).clone());
		}
		return (T) clone;
	}

}
