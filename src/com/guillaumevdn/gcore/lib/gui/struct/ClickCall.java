package com.guillaumevdn.gcore.lib.gui.struct;

import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.lib.gui.element.ElementGUI;

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

	// ----- shortcuts

	public void reopenGUI() {
		if (!gui.isActive()) {
			gui.activate();
			gui.refill();
		}
		gui.openFor(clicker, pageIndex, gui.getFromCall(clicker));
	}

	/**
	 * Get the click call for the ancestor in this "call stack"
	 * This avoids infinite loops in GUIs (for types <PLUGIN>_GUI) and finds the actual previous path
	 */
	public ClickCall getAncestorFor(ElementGUI targetGUI) {
		ClickCall c = this;
		for (int tries = 0; c != null && tries < 25 /* more than enough, but necessary #1897 #1932 */; ++tries) {
			if (c.getGUI().getId().startsWith("instance_" + targetGUI.getId() + "_")) {
				return c.getGUI().getFromCall(clicker);
			}
			c = c.getGUI().getFromCall(clicker);
		}
		return this;
	}

	// ----- obj

	@Override
	public String toString() {
		return "ClickCall{player=" + clicker.getName() + ",type=" + type + ",location=" + pageIndex + "/" + slot + ",gui=" + gui.getName() + "}";
	}

	// ----- click type

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
		SWAP_OFFHAND,
		UNKNOWN,  // exists in spigot, and has happened before, but I'm not sure what it means ; maybe a custom client or something

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
		CREATIVE,  // not really used but avoids errors in creative inventory when clicking stuff in bottom inv

		NONE,
		;

		public boolean isNumberKey() {
			return name().contains("NUMBER");
		}

		public boolean isShift() {
			return name().contains("SHIFT");
		}

	}

}
