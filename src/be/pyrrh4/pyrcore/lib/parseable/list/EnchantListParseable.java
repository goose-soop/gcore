package be.pyrrh4.pyrcore.lib.parseable.list;

import java.util.List;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import be.pyrrh4.pyrcore.PCLocale;
import be.pyrrh4.pyrcore.lib.material.Mat;
import be.pyrrh4.pyrcore.lib.parseable.ListParseable;
import be.pyrrh4.pyrcore.lib.parseable.Parseable;
import be.pyrrh4.pyrcore.lib.parseable.editor.EditorGUI;
import be.pyrrh4.pyrcore.lib.parseable.editor.EditorItem;
import be.pyrrh4.pyrcore.lib.parseable.editor.ModifCallback;
import be.pyrrh4.pyrcore.lib.util.Utils;
import be.pyrrh4.pyrcore.lib.util.Wrapper;

public abstract class EnchantListParseable<T extends Parseable> extends ListParseable<T> {

	// base
	private boolean allowDefaultCase;

	public EnchantListParseable(String id, Parseable parent, boolean allowDefaultCase, String typeName, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, typeName, CaseType.UPPER, mandatory, editorSlot, editorIcon, editorDescription);
		this.allowDefaultCase = allowDefaultCase;
	}

	// get
	public T getValue(String key) {
		T elem = getElement(key);
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
					EditorGUI sub = element.createEditor(gui, player, onModif);
					sub.open(player);
					return;
				}
			});
		}
		// new item
		gui.setPersistentItem(new EditorItem("control_item_new", 49, Mat.BLAZE_ROD, PCLocale.GUI_GENERIC_EDITORITEMADD.getLine(), null) {
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
						for (Enchantment val : Enchantment.values()) {
							if (!getElements().containsKey(val.getName())) {
								fill(val.getName(), EditorGUI.ICON_ENCHANTMENT);
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
