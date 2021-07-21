/**
 * Parts of this code was from ZQuest, it was refactored by GuillaumeVDN
 */

package com.guillaumevdn.gcore.lib.legacy_npc;

public enum NPCStatus {

	// ----- status
	ON_FIRE((byte) 1),
	CROUCHED((byte) 2),
	SPRINTING((byte) 8),
	INVISIBLE((byte) 32),
	GLOWING((byte) 64),
	ELYTRA((byte) 128);

	// ----- base
	private byte b;

	private NPCStatus(byte b) {
		this.b = b;
	}

	// ----- methods
	public byte getByte() {
		return b;
	}

	// ----- static methods
	public static byte getMasked(final NPCStatus... status) {
		byte b = 0;
		for (NPCStatus s : status) {
			b |= s.getByte();
		}
		return b;
	}

}
