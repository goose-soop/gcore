package com.guillaumevdn.gcore.lib.npc;

public enum NpcAnimation {

	// values
	TAKE_DAMAGE((byte) 1),
	LEAVE_BED((byte) 2),
	CRITICAL_EFFECT((byte) 4),
	MAGIC_CRITICAL_EFFECT((byte) 5);

	// base
	private byte animationData;

	private NpcAnimation(byte animationId) {
		this.animationData = animationId;
	}

	// get
	public byte getAnimationData() {
		return animationData;
	}

}
