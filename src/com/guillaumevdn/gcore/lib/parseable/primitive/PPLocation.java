package com.guillaumevdn.gcore.lib.parseable.primitive;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.GLocale;
import com.guillaumevdn.gcore.lib.gui.GUI;
import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.parseable.ParseResult;
import com.guillaumevdn.gcore.lib.parseable.Parseable;
import com.guillaumevdn.gcore.lib.parseable.PrimitiveParseable;
import com.guillaumevdn.gcore.lib.parseable.editor.EditorGUI;
import com.guillaumevdn.gcore.lib.parseable.editor.EditorItem;
import com.guillaumevdn.gcore.lib.parseable.editor.ModifCallback;
import com.guillaumevdn.gcore.lib.util.Utils;
import com.guillaumevdn.gcore.lib.util.input.LocationInput;

public class PPLocation extends PrimitiveParseable<Location> {

	// base
	public PPLocation(String id, Parseable parent, String defaultValue, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, defaultValue == null ? null : Utils.asList(defaultValue), "location", mandatory, editorSlot, editorIcon, editorDescription);
	}

	// parse
	@Override
	public ParseResult<Location> parseValue(List<String> value, Player parsing) throws Throwable {
		if (value.isEmpty()) {
			return new ParseResult<Location>(null);
		}
		Location result = Utils.unserializeWXYZLocation(value.get(0));
		return result != null ? new ParseResult<Location>(result) : null;
	}

	// editor
	@Override
	protected void fillEditor(final EditorGUI gui, Player player, final ModifCallback onModif) {
		// current, raw and delete
		EditorGUI.fillItemCurrent(gui, player, 0, this, onModif);
		EditorGUI.fillItemRaw(gui, player, this, 3, getValue() == null || getValue().isEmpty() ? null : getValue().get(0), onModif);
		EditorGUI.fillItemDelete(gui, player, 6, this, onModif);
		// select
		gui.setRegularItem(new EditorItem("control_item_import", 2, Mat.ENDER_CHEST, GLocale.GUI_GENERIC_EDITORLOCATIONIMPORT.getLine(), GLocale.GUI_GENERIC_EDITORLOCATIONIMPORTLORE.getLines()) {
			@Override
			protected void onClick(final Player player, final ClickType clickType, final int pageIndex) {
				// location
				player.closeInventory();
				GLocale.MSG_GENERIC_LOCATIONINPUT.send(player);
				GCore.inst().getLocationInputs().put(player, new LocationInput() {
					@Override
					public void onChoose(Player player, Location value) {
						// replace value
						if (value != null) {
							if (getValue() != null) {
								getValue().set(0, Utils.serializeWXYZLocation(value));
							} else {
								setValue(Utils.asList(Utils.serializeWXYZLocation(value)));
							}
							onModif.callback(null, gui, player);
						}
						// re-fill and open
						gui.open(player);
					}
				});
			}
		});
	}

	@Override
	public int getEditorSize() {
		return 9;
	}

	@Override
	public List<Integer> getEditorRegularSlots() {
		return GUI.SLOTS_0_TO_7;
	}

	@Override
	public int getEditorBackSlot() {
		return 8;
	}

	// clone
	protected PPLocation() {
		super();
	}

	@Override
	public PPLocation clone() {
		return (PPLocation) super.clone();
	}

}
