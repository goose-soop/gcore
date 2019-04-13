package com.guillaumevdn.gcore.lib.parseable.primitive;

import java.util.List;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import com.guillaumevdn.gcore.GLocale;
import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.parseable.ParseResult;
import com.guillaumevdn.gcore.lib.parseable.Parseable;
import com.guillaumevdn.gcore.lib.parseable.PrimitiveParseable;
import com.guillaumevdn.gcore.lib.parseable.editor.EditorGUI;
import com.guillaumevdn.gcore.lib.parseable.editor.EditorItem;
import com.guillaumevdn.gcore.lib.parseable.editor.ModifCallback;
import com.guillaumevdn.gcore.lib.util.Utils;

public class PPEnchantment extends PrimitiveParseable<Enchantment> {

	// base
	public PPEnchantment(String id, Parseable parent, String defaultValue, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, defaultValue == null ? null : Utils.asList(defaultValue), "enchantment type", mandatory, editorSlot, editorIcon, editorDescription);
	}

	// parse
	@Override
	public ParseResult<Enchantment> parseValue(List<String> value, Player parsing) throws Throwable {
		if (value.isEmpty()) {
			return new ParseResult<Enchantment>(null);
		}
		Enchantment result = Utils.enchantmentOrNull(value.get(0));
		return result != null ? new ParseResult<Enchantment>(result) : null;
	}

	// editor
	@Override
	protected void fillEditor(final EditorGUI gui, Player player, final ModifCallback onModif) {
		// current, raw and delete
		EditorGUI.fillItemCurrent(gui, player, this, 0, onModif);
		EditorGUI.fillItemRaw(gui, player, this, 3, onModif);
		EditorGUI.fillItemDelete(gui, player, this, 6, onModif);
		// select
		gui.setRegularItem(new EditorItem("control_item_select", 2, Mat.ENDER_CHEST, GLocale.GUI_GENERIC_EDITORENUMSELECT.getLine(), GLocale.GUI_GENERIC_EDITORENUMSELECTLORE.getLines()) {
			@Override
			protected void onClick(final Player player, final ClickType clickType, final int pageIndex) {
				// selection gui
				EditorGUI sub = new EditorGUI(getLastData().getPlugin(), gui, Utils.getNewInventoryName(gui.getName(), "Select"), 54, 44) {
					@Override
					protected void fill() {
						// add values
						for (final Enchantment val : Enchantment.values()) {
							final String valName = val.getName();
							setRegularItem(new EditorItem("value_" + valName, -1, EditorGUI.ICON_ENCHANTMENT, "§6" + valName, null) {
								@Override
								protected void onClick(final Player player, final ClickType clickType, final int pageIndex) {
									// replace value
									if (getValue() != null) {
										getValue().set(0, valName);
									} else {
										setValue(Utils.asList(valName));
									}
									onModif.callback(gui, player);
									// re-fill and open
									gui.open(player);
								}
							});
						}
					}
				};
				// open it
				sub.open(player);
			}
		});
	}

	@Override
	public int getEditorSize() {
		return 9;
	}

	@Override
	public int getEditorMaxRegularSlot() {
		return 7;
	}

	@Override
	public int getEditorBackSlot() {
		return 8;
	}

	// clone
	protected PPEnchantment() {
		super();
	}

	@Override
	public PPEnchantment clone() {
		return (PPEnchantment) super.clone();
	}

}
