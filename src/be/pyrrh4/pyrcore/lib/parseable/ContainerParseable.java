package be.pyrrh4.pyrcore.lib.parseable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import be.pyrrh4.pyrcore.PCLocale;
import be.pyrrh4.pyrcore.lib.material.Mat;
import be.pyrrh4.pyrcore.lib.parseable.data.CompactDataLink;
import be.pyrrh4.pyrcore.lib.parseable.data.DataLink;
import be.pyrrh4.pyrcore.lib.parseable.data.RegularDataLink;
import be.pyrrh4.pyrcore.lib.parseable.editor.EditorGUI;
import be.pyrrh4.pyrcore.lib.parseable.editor.EditorItem;
import be.pyrrh4.pyrcore.lib.parseable.editor.ModifCallback;
import be.pyrrh4.pyrcore.lib.util.Utils;

public abstract class ContainerParseable extends Parseable {

	// base
	private String typeName;
	private Map<String, Parseable> components = new HashMap<String, Parseable>();// LOWER CASE KEY

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
		id = id.toLowerCase();
		Parseable component = components.get(id);
		if (component == null) throw new IllegalArgumentException("there's no component with id " + id);
		return component;
	}

	public <C extends Parseable> C getComponent(String id, Class<C> componentClass) {
		id = id.toLowerCase();
		Parseable component = getComponent(id);
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
	public void load(DataLink data) {
		// set last data
		setLastData(data);
		// compact data
		if (data instanceof CompactDataLink) {
			CompactDataLink compactData = (CompactDataLink) data;
			// parent has parameters
			if (getParent() != null && Utils.instanceOf(getParent().getLastData(), CompactDataLink.class) && ((CompactDataLink) getParent().getLastData()).contains()) {
				CompactDataLink parent = (CompactDataLink) getParent().getLastData();
				compactData.setContains(parent.getParameters().containsKey(getId()));
				if (compactData.contains()) {// decode parameters and load components
					compactData.setParameters(getCompactArguments(compactData.getParameters().get(getId()), compactData.getDepth() - 1));
					for (Parseable component : components.values()) {
						component.load(new CompactDataLink(this, compactData.getPlugin(), compactData.getSuperId(), compactData.getConfig(), compactData.getPath() + "." + component.getId()));
					}
				} else if (isMandatory()) {
					data.log("missing mandatory setting of type " + typeName);
				}
			}
			// parent has no parameters
			else {
				compactData.setContains(compactData.getConfig().contains(compactData.getPath()) && !compactData.getConfig().isConfigurationSection(compactData.getPath()));
				if (compactData.contains()) {
					compactData.setParameters(getCompactArguments(compactData.getConfig().getString(compactData.getPath(), null), compactData.getDepth() - 1));
					for (Parseable component : components.values()) {
						component.load(new CompactDataLink(this, compactData.getPlugin(), compactData.getSuperId(), compactData.getConfig(), compactData.getPath() + "." + component.getId()));
					}
				} else if (isMandatory()) {
					data.log("missing mandatory setting of type " + typeName);
				}
			}
		}
		// regular data
		else if (data instanceof RegularDataLink) {
			RegularDataLink regularData = (RegularDataLink) data;
			regularData.setContains(regularData.getConfig().contains(regularData.getPath()));
			if (regularData.contains()) {
				for (Parseable component : components.values()) {
					component.load(new RegularDataLink(this, regularData.getPlugin(), regularData.getSuperId(), regularData.getConfig(), regularData.getPath() + "." + component.getId()));
				}
			} else if (isMandatory()) {
				data.log("missing mandatory setting of type " + typeName);
			}
		}
	}

	@Override
	public void save(DataLink data) {
		// set last data
		setLastData(data);
		// save components one by one
		if (data instanceof RegularDataLink) {
			RegularDataLink regularData = (RegularDataLink) data;
			regularData.getConfig().set(regularData.getPath(), null);
			for (Parseable component : components.values()) {
				component.save(new RegularDataLink(this, regularData.getPlugin(), regularData.getSuperId(), regularData.getConfig(), regularData.getPath() + "." + component.getId()));
			}
			regularData.setContains(regularData.getConfig().contains(regularData.getPath()));
		}
	}

	// editor
	@Override
	public List<String> describe(int depth) {
		String spaces = Utils.copyString(" ", depth + 1);
		List<String> desc = Utils.asList(spaces + "§6> " + getId() + " :");
		for (Parseable component : components.values()) {
			if (depth == MAX_DESCRIPTION_DEPTH) {
				desc.add(spaces + " §6> " + getId() + " : §8...");
			} else {
				desc.addAll(component.describe(depth + 1));
			}
		}
		return desc;
	}

	@Override
	public void fillEditor(final EditorGUI gui, Player player, final ModifCallback onModif) {
		// add components items
		for (final Parseable component : components.values()) {
			gui.setRegularItem(new EditorItem(component.getId(), component.getEditorSlot(), component.getEditorIcon(), "§6" + component.getId(), component.getEditorDescription()) {// TODO : (same for list) instead of getEditorDescription, describe children as well
				@Override
				protected void onClick(final Player player, ClickType clickType, int pageIndex) {
					// create component GUI
					String name = Utils.getNewInventoryName(gui.getName(), component.getId());
					EditorGUI sub = new EditorGUI(component.getLastData().getPlugin(), gui, name, component.getEditorSize(), component.getEditorMaxRegularSlot()) {
						private EditorGUI subThis = this;
						@Override
						protected void fill() {
							component.fillEditor(subThis, player, onModif);
						}
					};
					// back item
					sub.setPersistentItem(new EditorItem("control_item_back", component.getEditorBackSlot(), Mat.ARROW, PCLocale.GUI_GENERIC_EDITORITEMBACK.getLine(), null) {
						@Override
						protected void onClick(final Player player, final ClickType clickType, final int pageIndex) {
							gui.open(player);
						}
					});
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

}
