package be.pyrrh4.pyrcore.lib.loadable.editor;

import java.util.List;

import org.bukkit.entity.Player;

import be.pyrrh4.pyrcore.PCLocale;
import be.pyrrh4.pyrcore.PyrCore;
import be.pyrrh4.pyrcore.lib.gui.GUI;
import be.pyrrh4.pyrcore.lib.material.Mat;
import be.pyrrh4.pyrcore.lib.util.Utils;

public abstract class EditorGUI extends GUI {

	// static items
	public static final int MAX_DESC_LENGTH = 20;
	public static final Mat ICON_IMPORTANT = Mat.DIAMOND;
	public static final Mat ICON_BLOCK = Mat.GOLD_ORE;
	public static final Mat ICON_ITEM = Mat.APPLE;
	public static final Mat ICON_EFFECT = Mat.POTION;
	public static final Mat ICON_LOCATION = Mat.MINECART;
	public static final Mat ICON_SOUND = Mat.MUSIC_DISC_CAT;
	public static final Mat ICON_TEXT = Mat.BOOK;
	public static final Mat ICON_MOB = Mat.ROTTEN_FLESH;

	// base
	private EditorGUI parent;

	public EditorGUI(EditorGUI parent, String name) {
		this(parent, name, 54, 44);
	}

	public EditorGUI(EditorGUI parent, String name, int size, int maxRegularItemSlot) {
		super(PyrCore.inst(), parent == null ? name : Utils.getNewInventoryName(parent.getName(), name), size, maxRegularItemSlot, true);
		this.parent = parent;
	}

	// get/methods
	public EditorGUI getParent() {
		return parent;
	}

	public void open(Player player) {
		if (isRegistered()) {
			unregister();
		}
		register();
		fill();
		super.open(player);
	}

	// abstract methods
	protected abstract void fill();

	// static methods
	public static List<String> fillItemLore(List<String> description, String valueTypeName, List<String> valueCurrent, boolean mandatory) {
		List<String> result = Utils.emptyList();
		int i = 0;
		for (String line : Utils.separateSentences((description == null || description.isEmpty() ? PCLocale.GUI_GENERIC_EDITORVALUENODESCLORE : PCLocale.GUI_GENERIC_EDITORVALUELORE)
				.getLines("{description}", description, "{type}", valueTypeName, "{mandatory}", mandatory ? "&cyes" : "&ano",
						"{current}", valueCurrent == null || valueCurrent.isEmpty() ? Utils.asList("§e/") : valueCurrent), 50, " ")) {
			if (++i == 22) {
				result.add(" §7...");
				break;
			}
			result.add(line);
		}
		return result;
	}

}
