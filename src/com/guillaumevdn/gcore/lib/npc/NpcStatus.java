/**
 * Parts of this code was from ZQuest, it was refactored by GuillaumeVDN
 */

package com.guillaumevdn.gcore.lib.npc;

import com.guillaumevdn.gcore.lib.util.ServerVersion;

public enum NpcStatus {

	// status
	ON_FIRE(1, 1),
	CROUCHED(2, 2),
	RIDING(4, 0),
	SPRINTING(8, 8),
	SWIMMING(0, 10),
	INTERACT(16, 0),
	INVISIBLE(32, 20),
	GLOWING(64, 40),
	ELYTRA(128, 80);

	// base
	private int byte113, byte114;

	private NpcStatus(int byte113, int byte114) {
		this.byte113 = byte113;
		this.byte114 = byte114;
	}

	// methods
	public int getByte() {
		return ServerVersion.IS_1_14 ? byte114 : byte113;
	}

	// static methods
	public static byte getMasked(final NpcStatus... array) {
		byte b = 0;
		for (int length = array.length, i = 0; i < length; ++i) {
			b |= (byte) array[i].getByte();
		}
		return b;
	}

}
