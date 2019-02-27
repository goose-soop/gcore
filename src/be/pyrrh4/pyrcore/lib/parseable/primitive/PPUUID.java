package be.pyrrh4.pyrcore.lib.parseable.primitive;

import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import be.pyrrh4.pyrcore.lib.material.Mat;
import be.pyrrh4.pyrcore.lib.parseable.Parseable;
import be.pyrrh4.pyrcore.lib.parseable.PrimitiveParseable;
import be.pyrrh4.pyrcore.lib.parseable.editor.EditorGUI;
import be.pyrrh4.pyrcore.lib.parseable.editor.ModifCallback;
import be.pyrrh4.pyrcore.lib.util.Utils;

public class PPUUID extends PrimitiveParseable<UUID> {

	// base
	public PPUUID(String id, Parseable parent, String defaultValue, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, Utils.asList(defaultValue), "unique id", mandatory, editorSlot, editorIcon, editorDescription);
	}

	// parse
	@Override
	public UUID parseValue(List<String> value, Player parsing) throws Throwable {
		return !value.isEmpty() ? UUID.fromString(value.get(0)) : null;
	}

	// editor
	@Override
	public void fillEditor(final EditorGUI gui, Player player, final ModifCallback onModif) {
		// current, raw and delete
		EditorGUI.fillItemCurrent(gui, player, this, 0, onModif);
		EditorGUI.fillItemRaw(gui, player, this, 3, onModif);
		EditorGUI.fillItemDelete(gui, player, this, 6, onModif);
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
	protected PPUUID() {
		super();
	}

	@Override
	public PPUUID clone() {
		return (PPUUID) super.clone();
	}

}
