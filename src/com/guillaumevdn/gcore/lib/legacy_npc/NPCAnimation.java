package com.guillaumevdn.gcore.lib.legacy_npc;

public enum NPCAnimation {

	// ----- values
	TAKE_DAMAGE((byte) 1),
	LEAVE_BED((byte) 2),
	CRITICAL_EFFECT((byte) 4),
	MAGIC_CRITICAL_EFFECT((byte) 5);

	// ----- base
	private byte animationData;

	private NPCAnimation(byte animationId) {
		this.animationData = animationId;
	}

	// ----- get
	public byte getAnimationData() {
		return animationData;
	}

}
