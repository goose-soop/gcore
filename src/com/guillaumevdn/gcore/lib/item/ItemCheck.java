package com.guillaumevdn.gcore.lib.item;

/**
 * @author GuillaumeVDN
 */
public enum ItemCheck {

	ExactSame,
	ExactSame_ExceptDurability,

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
		this.mustHaveSameDurability = name().equals("ExactSame") || name().contains("SameDurability");
		this.musntBeMoreDamaged = name().contains("NotMoreDamaged");
		this.nameContains = name().contains("NameContains");
		this.loreContains = name().contains("LoreContains");
	}

	// ----- ez methods
	public boolean isExact() {
		return equals(ExactSame) || equals(ExactSame_ExceptDurability);  // durability don't use this method, so this is okay
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
