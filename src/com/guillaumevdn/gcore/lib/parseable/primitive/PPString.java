package com.guillaumevdn.gcore.lib.parseable.primitive;

import java.util.List;

import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.lib.gui.GUI;
import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.parseable.ParseResult;
import com.guillaumevdn.gcore.lib.parseable.Parseable;
import com.guillaumevdn.gcore.lib.parseable.PrimitiveParseable;
import com.guillaumevdn.gcore.lib.parseable.editor.EditorGUI;
import com.guillaumevdn.gcore.lib.parseable.editor.ModifCallback;
import com.guillaumevdn.gcore.lib.util.Utils;

public class PPString extends PrimitiveParseable<String> {

	// base
	public PPString(String id, Parseable parent, String defaultValue, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, defaultValue == null ? null : Utils.asList(defaultValue), "string", mandatory, editorSlot, editorIcon, editorDescription);
	}

	// parse
	@Override
	public ParseResult<String> parseValue(List<String> value, Player parsing) throws Throwable {
		if (value.isEmpty()) {
			return new ParseResult<String>(null);
		}
		return new ParseResult<String>(value.get(0));
	}

	// editor
	@Override
	protected void fillEditor(final EditorGUI gui, Player player, final ModifCallback onModif) {
		// current, raw and delete
		EditorGUI.fillItemCurrent(gui, player, 0, this, onModif);
		EditorGUI.fillItemRaw(gui, player, this, 3, getValue() == null || getValue().isEmpty() ? null : getValue().get(0), onModif);
		EditorGUI.fillItemDelete(gui, player, 6, this, onModif);
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
	protected PPString() {
		super();
	}

	@Override
	public PPString clone() {
		return (PPString) super.clone();
	}

}
