package be.pyrrh4.pyrcore.lib.parseable.primitive;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import be.pyrrh4.pyrcore.PCLocale;
import be.pyrrh4.pyrcore.lib.material.Mat;
import be.pyrrh4.pyrcore.lib.parseable.Parseable;
import be.pyrrh4.pyrcore.lib.parseable.PrimitiveParseable;
import be.pyrrh4.pyrcore.lib.parseable.editor.EditorGUI;
import be.pyrrh4.pyrcore.lib.parseable.editor.EditorItem;
import be.pyrrh4.pyrcore.lib.parseable.editor.ModifCallback;
import be.pyrrh4.pyrcore.lib.util.Utils;

public class PPDouble extends PrimitiveParseable<Double> {

	// base
	private Double min, max;

	public PPDouble(String id, Parseable parent, String defaultValue, Double min, Double max, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, Utils.asList(defaultValue), "decimal number", mandatory, editorSlot, editorIcon, editorDescription);
		this.min = min;
		this.max = max;
	}

	// parse
	@Override
	public Double parseValue(List<String> value, Player parsing) throws Throwable {
		Double parsed = !value.isEmpty() ? parse(value.get(0)) : null;
		return parsed != null ? ((min != null && parsed < min) || (max != null && parsed > max) ? null : parsed) : null;
	}
	
	public static Double parse(String raw) {
		return Utils.calculateExpression(raw);
	}

	// editor
	@Override
	protected void fillEditor(final EditorGUI gui, Player player, final ModifCallback onModif) {
		// current, raw and delete
		EditorGUI.fillItemCurrent(gui, player, this, 18, onModif);
		EditorGUI.fillItemRaw(gui, player, this, 21, onModif);
		EditorGUI.fillItemDelete(gui, player, this, 24, onModif);
		// delta items
		for (int i = 0; i < 9; ++i) fillDeltaItem(gui, player, Math.pow(10, i == 3 ? 0 : i - 3), i, Mat.GREEN_WOOL, onModif);
		for (int i = 0; i < 9; ++i) fillDeltaItem(gui, player, -Math.pow(10, i == 3 ? 0 : i - 3), 9 + i, Mat.RED_WOOL, onModif);
	}

	private void fillDeltaItem(final EditorGUI gui, Player player, final double delta, final int slot, final Mat icon, final ModifCallback onModif) {
		gui.setRegularItem(new EditorItem("control_item_" + (delta > 0 ? "add_" + delta : "take_" + Math.abs(delta)), slot, icon, (delta > 0 ? PCLocale.GUI_GENERIC_EDITORNUMBERADD : PCLocale.GUI_GENERIC_EDITORNUMBERTAKE).getLine("{amount}", Math.abs(delta)), PCLocale.GUI_GENERIC_EDITORNUMBERADDTAKELORE.getLines()) {
			@Override
			protected void onClick(final Player player, final ClickType clickType, final int pageIndex) {
				// replace value
				if (getValue() != null) {
					Double value;
					try {
						value = Double.valueOf(getValue().get(0));
					} catch (Throwable ignored) {
						value = min != null ? min : 0;
					}
					value += delta;
					if (min != null && value < min) value = min;
					else if (max != null && value > max) value = max;
					getValue().set(0, value.toString());
				} else {
					setValue(getDefaultValue() != null && !getDefaultValue().isEmpty() ? Utils.asList(getDefaultValue()) : Utils.asList(min != null ? min.toString() : "0"));
				}
				onModif.callback(gui, player);
				// update current item
				EditorGUI.fillItemCurrent(gui, player, PPDouble.this, 18, onModif);
			}
		});
	}

	@Override
	public int getEditorSize() {
		return 27;
	}

	@Override
	public int getEditorMaxRegularSlot() {
		return 25;
	}

	@Override
	public int getEditorBackSlot() {
		return 26;
	}

	// clone
	protected PPDouble() {
		super();
	}

	@Override
	public PPDouble clone() {
		// clone
		PPDouble clone = (PPDouble) super.clone();
		// clone properties
		clone.min = min;
		clone.max = max;
		// success
		return clone;
	}

}
