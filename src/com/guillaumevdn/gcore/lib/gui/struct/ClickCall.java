package com.guillaumevdn.gcore.lib.gui.struct;

import org.bukkit.entity.Player;

/**
 * @author GuillaumeVDN
 */
public class ClickCall {

	private Player clicker;
	private ClickType type;
	private GUI gui;
	private int pageIndex;
	private int slot;

	public ClickCall() {
	}

	public ClickCall(Player clicker, ClickType type, GUI gui, int pageIndex, int slot) {
		this.clicker = clicker;
		this.type = type;
		this.gui = gui;
		this.pageIndex = pageIndex;
		this.slot = slot;
	}

	// get
	public Player getClicker() {
		return clicker;
	}

	public ClickType getType() {
		return type;
	}

	public GUI getGUI() {
		return gui;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public int getSlot() {
		return slot;
	}

	// string
	@Override
	public String toString() {
		return "ClickCall{player=" + clicker.getName() + ",type=" + type + ",location=" + pageIndex + "/" + slot + ",gui=" + gui.getName() + "}";
	}

	// click type
	public static enum ClickType {

		LEFT,
		SHIFT_LEFT,
		RIGHT,
		SHIFT_RIGHT,
		MIDDLE,
		NUMBER_KEY,
		DOUBLE_CLICK,
		DROP,
		CONTROL_DROP,
		// doesn't work for vanilla handling
		NUMBER_KEY_1,
		NUMBER_KEY_2,
		NUMBER_KEY_3,
		NUMBER_KEY_4,
		NUMBER_KEY_5,
		NUMBER_KEY_6,
		NUMBER_KEY_7,
		NUMBER_KEY_8,
		NUMBER_KEY_9,
		KEY_OFFHAND,
		NONE
		;

		public boolean isNumberKey() {
			return name().contains("NUMBER");
		}

	}

}
