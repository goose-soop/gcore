package com.guillaumevdn.gcore.lib.legacy_npc;

import java.util.Arrays;

import com.guillaumevdn.gcore.lib.player.PhysicalClickType;

public enum NPCInteraction {

	ATTACK(PhysicalClickType.LEFT_CLICK),
	INTERACT(PhysicalClickType.RIGHT_CLICK);

	private PhysicalClickType click;

	private NPCInteraction(PhysicalClickType click) {
		this.click = click;
	}

	public PhysicalClickType toPhysicalClickType() {
		return click;
	}

	// -----

	public static NPCInteraction fromPhysicalClickType(PhysicalClickType click) {
		return Arrays.stream(values()).filter(inter -> inter.click.equals(click)).findAny().orElse(null);
	}

}
