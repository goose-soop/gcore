package com.guillaumevdn.gcore.lib.parseable.container;

import java.util.List;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.GLocale;
import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.parseable.ContainerParseable;
import com.guillaumevdn.gcore.lib.parseable.Parseable;
import com.guillaumevdn.gcore.lib.parseable.editor.EditorGUI;
import com.guillaumevdn.gcore.lib.parseable.primitive.PPEnchantment;
import com.guillaumevdn.gcore.lib.parseable.primitive.PPInteger;

public class CPEnchantment extends ContainerParseable {

	// base
	private PPEnchantment type = addComponent(new PPEnchantment("type", this, null, false, 0, EditorGUI.ICON_ENCHANTMENT, GLocale.GUI_GENERIC_EDITOR_ENCHANTMENT_TYPELORE.getLines()));
	private PPInteger level = addComponent(new PPInteger("level", this, "1", 1, Integer.MAX_VALUE, false, 1, EditorGUI.ICON_NUMBER_LEVEL, GLocale.GUI_GENERIC_EDITOR_ENCHANTMENT_LEVELLORE.getLines()));

	public CPEnchantment(String id, Parseable parent, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, "enchantment", mandatory, editorSlot, editorIcon, editorDescription);
	}

	// get
	public PPEnchantment getType() {
		return type;
	}

	public Enchantment getType(Player parser) {
		return type.getParsedValue(parser);
	}

	public PPInteger getLevel() {
		return level;
	}

	public Integer getLevel(Player parser) {
		return level.getParsedValue(parser);
	}

	// clone
	protected CPEnchantment() {
		super();
	}

	@Override
	public CPEnchantment clone() {
		return (CPEnchantment) super.clone();
	}

}
