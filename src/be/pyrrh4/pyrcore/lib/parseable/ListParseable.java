package be.pyrrh4.pyrcore.lib.parseable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import be.pyrrh4.pyrcore.PCLocale;
import be.pyrrh4.pyrcore.PyrCore;
import be.pyrrh4.pyrcore.lib.material.Mat;
import be.pyrrh4.pyrcore.lib.parseable.data.CompactDataLink;
import be.pyrrh4.pyrcore.lib.parseable.data.DataLink;
import be.pyrrh4.pyrcore.lib.parseable.data.RegularDataLink;
import be.pyrrh4.pyrcore.lib.parseable.editor.EditorGUI;
import be.pyrrh4.pyrcore.lib.parseable.editor.EditorItem;
import be.pyrrh4.pyrcore.lib.parseable.editor.ModifCallback;
import be.pyrrh4.pyrcore.lib.util.Utils;
import be.pyrrh4.pyrcore.lib.util.Wrapper;
import be.pyrrh4.pyrcore.lib.util.input.ChatInput;

public abstract class ListParseable<T extends Parseable> extends Parseable {

	// base
	private String elementTypeName;
	private CaseType idCase;
	private Map<String, T> elements = new HashMap<String, T>();// LOWER/UPPER CASE KEY

	public ListParseable(String id, Parseable parent, String elementTypeName, CaseType idCase, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, mandatory, editorSlot, editorIcon, editorDescription);
		this.elementTypeName = elementTypeName;
		this.idCase = idCase;
	}

	public String getElementTypeName() {
		return elementTypeName;
	}

	public CaseType getIdCase() {
		return idCase;
	}

	public Map<String, T> getElements() {
		return elements;
	}

	public T getElement(String id) {
		return elements.get(idCase.transform(id));
	}

	/** @return the previous value associated with the same id, or null if there was none */
	public T addElement(T element) {
		return elements.put(idCase.transform(element.getId()), element);
	}

	/** @return the removed value associated with the id, or null if there was none */
	public T removeElement(String id) {
		return elements.remove(idCase.transform(id));
	}

	/** Create an element with the specified id, and add it to the list */
	public abstract T createElement(String elementId);

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
				if (compactData.contains()) {// decode parameters and load elements
					compactData.setParameters(getCompactArguments(compactData.getParameters().get(getId()), compactData.getDepth() - 1));
					for (String elementId : compactData.getParameters().keySet()) {
						loadElement(elementId, new CompactDataLink(null, compactData.getPlugin(), compactData.getSuperId(), compactData.getConfig(), compactData.getPath() + "." + elementId));
					}
				} else if (isMandatory()) {
					data.log("missing mandatory list of type " + getElementTypeName());
				}
			}
			// parent has no parameters
			else {
				compactData.setContains(compactData.getConfig().contains(compactData.getPath()) && !compactData.getConfig().isConfigurationSection(compactData.getPath()));
				if (compactData.contains()) {
					compactData.setParameters(getCompactArguments(compactData.getConfig().getString(compactData.getPath(), null), compactData.getDepth() - 1));
					for (String elementId : compactData.getParameters().keySet()) {
						loadElement(elementId, new CompactDataLink(null, compactData.getPlugin(), compactData.getSuperId(), compactData.getConfig(), compactData.getPath() + "." + elementId));
					}
				} else if (isMandatory()) {
					data.log("missing mandatory list of type " + getElementTypeName());
				}
			}
		}
		// regular data
		else if (data instanceof RegularDataLink) {
			RegularDataLink regularData = (RegularDataLink) data;
			regularData.setContains(regularData.getConfig().contains(regularData.getPath()));
			if (regularData.contains()) {
				for (String elementId : regularData.getConfig().getKeysForSection(regularData.getPath(), false)) {
					loadElement(elementId, new RegularDataLink(null, regularData.getPlugin(), regularData.getSuperId(), regularData.getConfig(), regularData.getPath() + "." + elementId));
				}
			} else if (isMandatory()) {
				data.log("missing mandatory list of type " + getElementTypeName());
			}
		}
	}

	public abstract T loadElement(String elementId, DataLink data);

	@Override
	public void save(DataLink data) {
		// set last data
		setLastData(data);
		// save elements one by one
		if (data instanceof RegularDataLink) {
			RegularDataLink regularData = (RegularDataLink) data;
			regularData.getConfig().set(regularData.getPath(), null);
			for (Parseable element : elements.values()) {
				element.save(new RegularDataLink(this, regularData.getPlugin(), regularData.getSuperId(), regularData.getConfig(), regularData.getPath() + "." + element.getId()));
			}
			regularData.setContains(regularData.getConfig().contains(regularData.getPath()));
		}
	}

	// editor
	@Override
	public List<String> describe(int depth) {
		String spaces = Utils.copyString(" ", depth + 1);
		List<String> desc = Utils.asList(spaces + "§6> " + getId() + " :");
		for (Parseable element : elements.values()) {
			if (depth == MAX_DESCRIPTION_DEPTH) {
				desc.add(spaces + " §6> " + getId() + " : §8...");
			} else {
				desc.addAll(element.describe(depth + 1));
			}
		}
		return desc;
	}

	@Override
	protected void fillEditor(final EditorGUI gui, Player player, final ModifCallback onModif) {
		// delete wrapper
		final Wrapper<Boolean> delete = new Wrapper<Boolean>(false);
		// add elements items
		for (final Parseable element : elements.values()) {
			gui.setRegularItem(new EditorItem(element.getId(), element.getEditorSlot(), element.getEditorIcon(), "§6" + element.getId(), element.getEditorDescription()) {
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
					EditorGUI sub = new EditorGUI(element.getLastData().getPlugin(), gui, name, element.getEditorSize(), element.getEditorMaxRegularSlot()) {
						private EditorGUI subThis = this;
						@Override
						protected void fill() {
							element.fillEditor(subThis, player, onModif);
						}
					};
					// back item
					sub.setPersistentItem(new EditorItem("control_item_back", getEditorBackSlot(), Mat.ARROW, PCLocale.GUI_GENERIC_EDITORITEMBACK.getLine(), null) {
						@Override
						protected void onClick(final Player player, final ClickType clickType, final int pageIndex) {
							gui.open(player);
						}
					});
					// open it
					sub.open(player);
					return;
				}
			});
		}
		// new item
		gui.setPersistentItem(new EditorItem("control_item_new", 49, Mat.BLAZE_ROD, PCLocale.GUI_GENERIC_EDITORITEMADD.getLine(), null) {
			@Override
			protected void onClick(final Player player, final ClickType clickType, final int pageIndex) {
				player.closeInventory();
				PCLocale.MSG_GENERIC_CHATINPUTID.send(player);
				PyrCore.inst().getChatInputs().put(player, new ChatInput() {
					@Override
					public void onChat(Player player, String value) {
						if (!value.replace(" ", "").equalsIgnoreCase("cancel")) {
							value = idCase.transform(value.replace(" ", "_"));
							if (!Utils.isAlphanumeric(value.replace("_", ""))) {
								PCLocale.MSG_GENERIC_INVALIDALPHANUMERIC.send(player, "{plugin}", PyrCore.inst().getName(), "{error}", value);
							} else if (elements.containsKey(value)) {
								PCLocale.MSG_GENERIC_DUPLICATEELEMENT.send(player, "{id}", value);
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
		gui.setPersistentItem(new EditorItem("control_item_delete", 46, Mat.TNT_MINECART, PCLocale.GUI_GENERIC_EDITORITEMDELETE.getLine(), PCLocale.GUI_GENERIC_EDITORITEMDELETELORE.getLines()) {
			@Override
			protected void onClick(final Player player, final ClickType clickType, final int pageIndex) {
				delete.setValue(Boolean.TRUE);
				PCLocale.MSG_GENERIC_DELETEELEMENT.send(player);
			}
		});
	}

	// editor
	@Override
	public int getEditorSize() {
		return 54;
	}

	@Override
	public int getEditorMaxRegularSlot() {
		return 44;
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
		clone.idCase = idCase;
		for (String elementId : elements.keySet()) {
			clone.elements.put(elementId, (T) elements.get(elementId).clone());
		}
		// success
		return clone;
	}

	// id case
	public static enum CaseType {

		UPPER,
		LOWER;

		public String transform(String string) {
			switch (this) {
			case UPPER:
				return string.toUpperCase();
			case LOWER:
				return string.toLowerCase();
			default:
				return string;
			}
		}

	}

}
