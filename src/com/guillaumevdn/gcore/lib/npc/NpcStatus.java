/**
 * Parts of this code was from ZQuest, it was refactored by GuillaumeVDN
 */

package com.guillaumevdn.gcore.lib.npc;

import com.guillaumevdn.gcore.lib.util.ServerVersion;

public enum NpcStatus {

	// status
	ON_FIRE((byte) 1, (byte) 1),
	CROUCHED((byte) 2, (byte) 2),
	SPRINTING((byte) 8, (byte) 8),
	SWIMMING((byte) 16, (byte) 16),
	INVISIBLE((byte) 32, (byte) 32),
	GLOWING((byte) 64, (byte) 64),
	ELYTRA((byte) 128, (byte) 128);

	// base
	private byte byte113, byte114;

	private NpcStatus(byte byte113, byte byte114) {
		this.byte113 = byte113;
		this.byte114 = byte114;
	}

	// methods
	public byte getByte() {
		return ServerVersion.IS_1_14 ? byte114 : byte113;
	}

	// static methods
	public static byte getMasked(final NpcStatus... status) {
		byte b = 0;
		for (NpcStatus s : status) {
			b |= s.getByte();
		}
		return b;
	}

}
