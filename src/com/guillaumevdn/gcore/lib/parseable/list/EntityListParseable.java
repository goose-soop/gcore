package com.guillaumevdn.gcore.lib.parseable.list;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.GLocale;
import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.parseable.ListParseable;
import com.guillaumevdn.gcore.lib.parseable.Parseable;
import com.guillaumevdn.gcore.lib.parseable.editor.EditorGUI;
import com.guillaumevdn.gcore.lib.parseable.editor.EditorItem;
import com.guillaumevdn.gcore.lib.parseable.editor.ModifCallback;
import com.guillaumevdn.gcore.lib.util.Utils;
import com.guillaumevdn.gcore.lib.util.Wrapper;
import com.guillaumevdn.gcore.lib.util.input.ChatInput;

public abstract class EntityListParseable<T extends Parseable> extends ListParseable<T> {

	// base
	private boolean allowDefaultCase;

	public EntityListParseable(String id, Parseable parent, boolean allowDefaultCase, String typeName, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, typeName, CaseType.UPPER, mandatory, editorSlot, editorIcon, editorDescription);
		this.allowDefaultCase = allowDefaultCase;
	}

	// get
	public T getValue(String key) {
		T elem = getElement(key);
		return elem != null ? elem : (allowDefaultCase ? getElement("DEFAULT") : null);
	}

	public T getValue(Entity entity) {
		T elem = getElement(entity.getType().name());
		if (elem == null && entity.getCustomName() != null) elem = getElement("named:" + entity.getCustomName());
		return elem != null ? elem : (allowDefaultCase ? getElement("DEFAULT") : null);
	}

	// editor
	@Override
	protected void fillEditor(final EditorGUI gui, Player player, final ModifCallback onModif) {
		// delete wrapper
		final Wrapper<Boolean> delete = new Wrapper<Boolean>(false);
		// add elements items
		for (final Parseable element : getElements().values()) {
			gui.setRegularItem(new EditorItem(element.getId(), element.getEditorSlot(), element.getEditorIcon(), "§6" + element.getId(), element.getEditorDescription()) {
				@Override
				public void onClick(final Player player, final ClickType clickType, final int pageIndex) {
					// eventually delete
					if (delete.getValue()) {
						delete.setValue(Boolean.FALSE);
						// delete and callback
						getElements().remove(element.getId());
						onModif.callback(gui, player);
						// re-fill and open
						gui.open(player);
						return;
					}
					// create and open element GUI
					element.createEditor(gui, player, onModif).open(player);
					return;
				}
			});
		}
		// new item
		gui.setPersistentItem(new EditorItem("control_item_new", 48, Mat.BLAZE_ROD, GLocale.GUI_GENERIC_EDITORITEMADD.getLine(), null) {
			@Override
			protected void onClick(final Player player, final ClickType clickType, final int pageIndex) {
				// selection gui
				EditorGUI sub = new EditorGUI(getLastData().getPlugin(), gui, Utils.getNewInventoryName(gui.getName(), "Select"), 54, 44) {
					@Override
					protected void fill() {
						// add default value
						if (allowDefaultCase && !getElements().containsKey("DEFAULT")) {
							fill("DEFAULT", Mat.NETHER_STAR);
						}
						// add regular values
						for (EntityType val : EntityType.values()) {// TODO : not all
							if (!getElements().containsKey(val.name())) {
								fill(val.name(), EditorGUI.ICON_MOB);
							}
						}
						// back item
						setPersistentItem(new EditorItem("control_item_back", 52, Mat.ARROW, GLocale.GUI_GENERIC_EDITORITEMBACK.getLine(), null) {
							@Override
							protected void onClick(final Player player, final ClickType clickType, final int pageIndex) {
								gui.open(player);
							}
						});
					}
					private void fill(final String valName, Mat icon) {
						setRegularItem(new EditorItem("value_" + valName, -1, icon, "§6" + valName, null) {
							@Override
							protected void onClick(final Player player, final ClickType clickType, final int pageIndex) {
								if (getElements().containsKey(valName)) {
									GLocale.MSG_GENERIC_DUPLICATEELEMENT.send(player, "{id}", valName);
								} else {
									createElement(valName);
									onModif.callback(gui, player);
								}
								gui.open(player);// re-fill and open
							}
						});
					}
				};
				// open it
				sub.open(player);
			}
		});
		// new named item
		gui.setPersistentItem(new EditorItem("control_item_new_named", 49, Mat.BLAZE_ROD, GLocale.GUI_GENERIC_EDITORITEMADDENTITYNAMED.getLine(), null) {
			@Override
			protected void onClick(final Player player, final ClickType clickType, final int pageIndex) {
				player.closeInventory();
				GLocale.MSG_GENERIC_CHATINPUTID.send(player);
				GCore.inst().getChatInputs().put(player, new ChatInput() {
					@Override
					public void onChat(Player player, String value) {
						if (!value.replace(" ", "").equalsIgnoreCase("cancel")) {
							value = "named:" + value.replace(" ", "_").toLowerCase();
							if (!Utils.isAlphanumeric(value.replace("_", ""))) {
								GLocale.MSG_GENERIC_INVALIDALPHANUMERIC.send(player, "{plugin}", GCore.inst().getName(), "{error}", value);
							} else if (getElements().containsKey(value)) {
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

}
