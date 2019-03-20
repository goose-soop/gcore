package be.pyrrh4.pyrcore.lib.parseable.editor;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import be.pyrrh4.pyrcore.PCLocale;
import be.pyrrh4.pyrcore.PyrCore;
import be.pyrrh4.pyrcore.lib.PyrPlugin;
import be.pyrrh4.pyrcore.lib.material.Mat;
import be.pyrrh4.pyrcore.lib.parseable.Parseable;
import be.pyrrh4.pyrcore.lib.util.Utils;
import be.pyrrh4.pyrcore.lib.util.input.ChatInput;

public abstract class EditorSelectionGUI<T> extends EditorGUI {

	// base
	private boolean awaitingDeletion = false;

	public EditorSelectionGUI(PyrPlugin plugin, EditorGUI parent, String name) {
		super(plugin, parent, name, 54, 44);
	}

	// methods
	@Override
	protected void fill() {
		// add
		setPersistentItem(new EditorItem("control_item_add", 49, Mat.BLAZE_ROD, PCLocale.GUI_GENERIC_EDITORITEMADD.getLine(), null) {
			@Override
			protected void onClick(Player player, ClickType clickType, int pageIndex) {
				player.closeInventory();
				PCLocale.MSG_GENERIC_CHATINPUTID.send(player);
				PyrCore.inst().getChatInputs().put(player, new ChatInput() {
					@Override
					public void onChat(Player player, String value) {
						if (!value.replace(" ", "").equalsIgnoreCase("cancel")) {
							value = value.replace(" ", "_").toLowerCase();
							if (!Utils.isAlphanumeric(value.replace("_", ""))) {
								PCLocale.MSG_GENERIC_INVALIDALPHANUMERIC.send(player, "{plugin}", getPlugin().getName(), "{error}", value);
							} else if (doesElementExist(value)) {
								PCLocale.MSG_GENERIC_DUPLICATEELEMENT.send(player, "{id}", value);
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
		setPersistentItem(new EditorItem("control_item_delete", 46, Mat.TNT_MINECART, PCLocale.GUI_GENERIC_EDITORITEMDELETE.getLine(), PCLocale.GUI_GENERIC_EDITORITEMDELETELORE.getLines()) {
			@Override
			protected void onClick(Player player, ClickType clickType, int pageIndex) {
				awaitingDeletion = true;
			}
		});
		// back
		if (getParent() != null) {
			setPersistentItem(new EditorItem("control_item_back", 52, Mat.ARROW, PCLocale.GUI_GENERIC_EDITORITEMBACK.getLine(), null) {
				@Override
				protected void onClick(Player player, ClickType clickType, int pageIndex) {
					getParent().open(player);
				}
			});
		}
	}

	public void addItem(final T element, Mat icon, String id, String name, int slot) {
		// add item
		setRegularItem(new EditorItem(id, slot, icon, name, element instanceof Parseable ? ((Parseable) element).describe(0) : null) {
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
