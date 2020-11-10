package com.guillaumevdn.gcore.lib.item;

/**
 * @author GuillaumeVDN
 */
public enum ItemCheck {

	ExactSame,

	SameBase,
	SameBase_SameDurability,
	SameBase_NotMoreDamaged,

	SameBase_NameContains,
	SameBase_NameContains_SameDurability,
	SameBase_NameContains_NotMoreDamaged,

	SameBase_LoreContains,
	SameBase_LoreContains_SameDurability,
	SameBase_LoreContains_NotMoreDamaged,

	SameBase_NameContains_LoreContains,
	SameBase_NameContains_LoreContains_SameDurability,
	SameBase_NameContains_LoreContains_NotMoreDamaged,

	;

	private boolean mustHaveSameDurability, musntBeMoreDamaged, nameContains, loreContains;

	ItemCheck() {
		this.mustHaveSameDurability = ordinal() == 0 || name().contains("SameDurability");
		this.musntBeMoreDamaged = name().contains("NotMoreDamaged");
		this.nameContains = name().contains("NameContains");
		this.loreContains = name().contains("LoreContains");
	}

	// ez methods
	public boolean isExact() {
		return equals(ExactSame);
	}

	public boolean mustHaveSameDurability() {
		return mustHaveSameDurability;
	}

	public boolean musntBeMoreDamaged() {
		return musntBeMoreDamaged;
	}

	public boolean nameContains() {
		return nameContains;
	}

	public boolean loreContains() {
		return loreContains;
	}

}
