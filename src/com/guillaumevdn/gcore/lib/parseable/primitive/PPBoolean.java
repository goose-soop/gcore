package com.guillaumevdn.gcore.lib.parseable.primitive;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import com.guillaumevdn.gcore.GLocale;
import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.parseable.Parseable;
import com.guillaumevdn.gcore.lib.parseable.PrimitiveParseable;
import com.guillaumevdn.gcore.lib.parseable.editor.EditorGUI;
import com.guillaumevdn.gcore.lib.parseable.editor.EditorItem;
import com.guillaumevdn.gcore.lib.parseable.editor.ModifCallback;
import com.guillaumevdn.gcore.lib.util.Utils;

public class PPBoolean extends PrimitiveParseable<Boolean> {

	// base
	public PPBoolean(String id, Parseable parent, String defaultValue, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, Utils.asList(defaultValue), "boolean", mandatory, editorSlot, editorIcon, editorDescription);
	}

	// parse
	@Override
	public Boolean parseValue(List<String> value, Player parsing) throws Throwable {
		return !value.isEmpty() ? Boolean.valueOf(value.get(0)) : null;
	}

	// editor
	@Override
	protected void fillEditor(final EditorGUI gui, Player player, final ModifCallback onModif) {
		// current, raw and delete
		EditorGUI.fillItemCurrent(gui, player, this, 0, onModif);
		EditorGUI.fillItemRaw(gui, player, this, 3, onModif);
		EditorGUI.fillItemDelete(gui, player, this, 6, onModif);
		// toggle
		gui.setRegularItem(new EditorItem("control_item_toggle", 2, Mat.BAKED_POTATO, GLocale.GUI_GENERIC_EDITORBOOLEANTOGGLE.getLine(), GLocale.GUI_GENERIC_EDITORBOOLEANTOGGLELORE.getLines()) {
			@Override
			protected void onClick(final Player player, final ClickType clickType, final int pageIndex) {
				// replace value
				if (getValue() != null) {
					if (getValue().isEmpty()) {
						if (getDefaultValue() != null && !getDefaultValue().isEmpty()) {
							getValue().addAll(getDefaultValue());
						} else {
							getValue().add(Boolean.TRUE.toString());
						}
					} else {
						getValue().set(0, String.valueOf(!Boolean.parseBoolean(getValue().get(0))));
					}
				} else {
					setValue(getDefaultValue() != null && !getDefaultValue().isEmpty() ? Utils.asList(getDefaultValue()) : Utils.asList(Boolean.TRUE.toString()));
				}
				onModif.callback(gui, player);
				// update current item
				EditorGUI.fillItemCurrent(gui, player, PPBoolean.this, 0, onModif);
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
	protected PPBoolean() {
		super();
	}

	@Override
	public PPBoolean clone() {
		return (PPBoolean) super.clone();
	}

}
