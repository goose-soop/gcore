package com.guillaumevdn.gcore.lib.parseable.editor;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.GLocale;
import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.gui.GUI;
import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.parseable.Parseable;
import com.guillaumevdn.gcore.lib.util.Utils;
import com.guillaumevdn.gcore.lib.util.input.ChatInput;

public abstract class EditorSelectionGUI<T> extends EditorGUI {

	// base
	private boolean awaitingDeletion = false;

	public EditorSelectionGUI(GPlugin plugin, EditorGUI parent, String name) {
		super(plugin, parent, name, 54, GUI.SLOTS_0_TO_44);
	}

	// methods
	@Override
	protected void fill() {
		// add
		setPersistentItem(new EditorItem("control_item_add", 49, Mat.BLAZE_ROD, GLocale.GUI_GENERIC_EDITORITEMADD.getLine(), null) {
			@Override
			protected void onClick(Player player, ClickType clickType, int pageIndex) {
				player.closeInventory();
				GLocale.MSG_GENERIC_CHATINPUTID.send(player);
				GCore.inst().getChatInputs().put(player, new ChatInput() {
					@Override
					public void onChat(Player player, String value) {
						if (!value.replace(" ", "").equalsIgnoreCase("cancel")) {
							value = value.replace(" ", "_").toLowerCase();
							if (!Utils.isAlphanumeric(value.replace("_", ""))) {
								GLocale.MSG_GENERIC_INVALIDALPHANUMERIC.send(player, "{plugin}", getPlugin().getName(), "{error}", value);
							} else if (doesElementExist(value)) {
								GLocale.MSG_GENERIC_DUPLICATEELEMENT.send(player, "{id}", value);
							} else {
								createElement(value);
							}
						}
						open(player);
					}
				});
			}
		});
		// delete
		setPersistentItem(new EditorItem("control_item_delete", 46, Mat.TNT_MINECART, GLocale.GUI_GENERIC_EDITORITEMDELETE.getLine(), GLocale.GUI_GENERIC_EDITORITEMDELETELORE.getLines()) {
			@Override
			protected void onClick(Player player, ClickType clickType, int pageIndex) {
				awaitingDeletion = true;
			}
		});
		// back
		if (getParent() != null) {
			setPersistentItem(new EditorItem("control_item_back", 52, Mat.ARROW, GLocale.GUI_GENERIC_EDITORITEMBACK.getLine(), null) {
				@Override
				protected void onClick(Player player, ClickType clickType, int pageIndex) {
					getParent().open(player);
				}
			});
		}
	}

	public void addItem(final T element, Mat icon, String id, String name, int slot) {
		// add item
		List<String> lore = new ArrayList<String>(), desc =  element instanceof Parseable ? ((Parseable) element).describe(0) : null;
		if (desc != null) {
			for (String line : desc) {
				lore.add(line);
				if (lore.size() >= EditorGUI.MAX_DESCRIPTION_LINES) break;
			}
		}
		setRegularItem(new EditorItem(id, slot, icon, name, lore) {
			@Override
			protected void onClick(Player player, ClickType clickType, int pageIndex) {
				// delete model
				if (awaitingDeletion) {
					awaitingDeletion = false;
					// delete element
					deleteElement(element);
					// open
					open(player);
					return;
				}
				// initialize model GUI and open it
				onElementClick(element, player, clickType, pageIndex);
			}
		});
	}

	// abstract methods
	protected abstract boolean doesElementExist(String id);
	protected abstract void onElementClick(T element, Player player, ClickType clickType, int pageIndex);
	protected abstract void createElement(String id);
	protected abstract void deleteElement(T element);

}
