package be.pyrrh4.pyrcore.lib.parseable.list;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import be.pyrrh4.pyrcore.PCLocale;
import be.pyrrh4.pyrcore.PyrCore;
import be.pyrrh4.pyrcore.lib.material.Mat;
import be.pyrrh4.pyrcore.lib.parseable.ListParseable;
import be.pyrrh4.pyrcore.lib.parseable.Parseable;
import be.pyrrh4.pyrcore.lib.parseable.editor.EditorGUI;
import be.pyrrh4.pyrcore.lib.parseable.editor.EditorItem;
import be.pyrrh4.pyrcore.lib.parseable.editor.ModifCallback;
import be.pyrrh4.pyrcore.lib.util.Utils;
import be.pyrrh4.pyrcore.lib.util.Wrapper;
import be.pyrrh4.pyrcore.lib.util.input.ChatInput;

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
	public void fillEditor(final EditorGUI gui, Player player, final ModifCallback onModif) {
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
		gui.setPersistentItem(new EditorItem("control_item_new", 48, Mat.BLAZE_ROD, PCLocale.GUI_GENERIC_EDITORITEMADD.getLine(), null) {
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
						setPersistentItem(new EditorItem("control_item_back", 52, Mat.ARROW, PCLocale.GUI_GENERIC_EDITORITEMBACK.getLine(), null) {
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
									PCLocale.MSG_GENERIC_DUPLICATEELEMENT.send(player, "{id}", valName);
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
		gui.setPersistentItem(new EditorItem("control_item_new_named", 49, Mat.BLAZE_ROD, PCLocale.GUI_GENERIC_EDITORITEMADDENTITYNAMED.getLine(), null) {
			@Override
			protected void onClick(final Player player, final ClickType clickType, final int pageIndex) {
				player.closeInventory();
				PCLocale.MSG_GENERIC_CHATINPUTID.send(player);
				PyrCore.inst().getChatInputs().put(player, new ChatInput() {
					@Override
					public void onChat(Player player, String value) {
						if (!value.replace(" ", "").equalsIgnoreCase("cancel")) {
							value = getIdCase().transform("named:" + value.replace(" ", "_"));
							if (!Utils.isAlphanumeric(value.replace("_", ""))) {
								PCLocale.MSG_GENERIC_INVALIDALPHANUMERIC.send(player, "{plugin}", PyrCore.inst().getName(), "{error}", value);
							} else if (getElements().containsKey(value)) {
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

}
