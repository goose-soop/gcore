/**
 * Some parts of this code were found on the internet from an old plugin named "ZQuest"
 */

package be.pyrrh4.pyrcore.lib.npc;

public enum NpcStatus {

	ON_FIRE(1), 
	CROUCHED(2), 
	SPRINTING(8), 
	INTERACT(16), 
	INVISIBLE(32), 
	GLOWING(64), 
	ELYTRA(128);

	private int stbyte;

	private NpcStatus(final int stbyte) {
		this.stbyte = stbyte;
	}

	public static byte getMasked(final int... array) {
		byte b = 0;
		for (int length = array.length, i = 0; i < length; ++i) {
			b |= (byte) array[i];
		}
		return b;
	}

	public static byte getMasked(final NpcStatus... array) {
		byte b = 0;
		for (int length = array.length, i = 0; i < length; ++i) {
			b |= (byte) array[i].stbyte;
		}
		return b;
	}

	public int s() {
		return this.stbyte;
	}

}
