package com.guillaumevdn.gcore.data.usernpcs;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.item.ItemCheck;
import com.guillaumevdn.gcore.lib.item.ItemUtils;
import com.guillaumevdn.gcore.lib.legacy_npc.ElementNPC;
import com.guillaumevdn.gcore.lib.legacy_npc.NPCManager;
import com.guillaumevdn.gcore.lib.legacy_npc.NPCStatus;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

public final class UserNPC {

	private final int id;
	private Boolean shown = null;
	private String name = null;
	private String skinData = null;
	private String skinSignature = null;
	private Location location = null;
	private Double targetDistance = null;
	private List<NPCStatus> status = null;
	private ItemStack heldItem = null;
	private ItemStack heldItemOff = null;
	private ItemStack boots = null;
	private ItemStack leggings = null;
	private ItemStack chestplate = null;
	private ItemStack helmet = null;

	public UserNPC(int id) {
		this.id = id;
	}

	// get
	public int getId() {
		return id;
	}

	public boolean isEmpty() {
		return !Stream.of(shown, name, skinData, skinSignature, location, targetDistance, status, heldItem, heldItemOff, boots, leggings, chestplate, helmet).anyMatch(elem -> elem != null);
	}

	public ElementNPC getConfig() {
		return NPCManager.inst().getNpcConfig(id);
	}

	public <T> T getConfigOrElse(Function<ElementNPC, T> lookup, T def) {
		ElementNPC config = getConfig();
		T value = config == null ? null : lookup.apply(config);
		return value != null ? value : def;
	}

	public Boolean getModifiedShown() {
		return shown;
	}

	public Boolean getShown(Replacer replacer) {
		return shown != null ? shown : getConfigOrElse(config -> config.getShown().parse(replacer).orNull(), true);
	}

	public String getModifiedName() {
		return name;
	}

	public String getName(Replacer replacer) {
		return name != null ? name : getConfigOrElse(config -> config.getName().parse(replacer).orNull(), null);
	}

	public String getModifiedSkinData() {
		return skinData;
	}

	public String getSkinData(Replacer replacer) {
		return skinData != null ? skinData : getConfigOrElse(config -> config.getSkinData().parse(replacer).orNull(), null);
	}

	public String getModifiedSkinSignature() {
		return skinSignature;
	}

	public String getSkinSignature(Replacer replacer) {
		return skinSignature != null ? skinSignature : getConfigOrElse(config -> config.getSkinSignature().parse(replacer).orNull(), null);
	}

	public Location getModifiedLocation() {
		return location;
	}

	public Location getLocation(Replacer replacer) {
		return location != null ? location : getConfigOrElse(config -> config.getLocation().parse(replacer).orNull(), null);
	}

	public Double getModifiedTargetDistance() {
		return targetDistance;
	}

	public Double getTargetDistance(Replacer replacer) {
		return targetDistance != null ? targetDistance : getConfigOrElse(config -> config.getTargetDistance().parse(replacer).orNull(), null);
	}

	public List<NPCStatus> getModifiedStatus() {
		return status;
	}

	public List<NPCStatus> getStatus(Replacer replacer) {
		return status != null ? status : getConfigOrElse(config -> config.getStatus().parse(replacer).orNull(), new ArrayList<>());
	}

	public ItemStack getModifiedHeldItem() {
		return heldItem;
	}

	public ItemStack getHeldItem(Replacer replacer) {
		return heldItem != null ? heldItem : getConfigOrElse(config -> config.getHeldItem().parse(replacer).orNull(), null);
	}

	public ItemStack getModifiedHeldItemOff() {
		return heldItemOff;
	}

	public ItemStack getHeldItemOff(Replacer replacer) {
		return heldItemOff != null ? heldItemOff : getConfigOrElse(config -> config.getHeldItemOff().parse(replacer).orNull(), null);
	}

	public ItemStack getModifiedBoots() {
		return boots;
	}

	public ItemStack getBoots(Replacer replacer) {
		return boots != null ? boots : getConfigOrElse(config -> config.getBoots().parse(replacer).orNull(), null);
	}

	public ItemStack getModifiedLeggings() {
		return leggings;
	}

	public ItemStack getLeggings(Replacer replacer) {
		return leggings != null ? leggings : getConfigOrElse(config -> config.getLeggings().parse(replacer).orNull(), null);
	}

	public ItemStack getModifiedChestplate() {
		return chestplate;
	}

	public ItemStack getChestplate(Replacer replacer) {
		return chestplate != null ? chestplate : getConfigOrElse(config -> config.getChestplate().parse(replacer).orNull(), null);
	}

	public ItemStack getModifiedHelmet() {
		return helmet;
	}

	public ItemStack getHelmet(Replacer replacer) {
		return helmet != null ? helmet : getConfigOrElse(config -> config.getHelmet().parse(replacer).orNull(), null);
	}

	// set
	public void saveNonDefault(ElementNPC config, Replacer replacer, Boolean shown, String name, String skinData, String skinSignature, Location location, Double targetDistance, List<NPCStatus> status, ItemStack heldItem, ItemStack heldItemOff, ItemStack boots, ItemStack leggings, ItemStack chestplate, ItemStack helmet) {
		if (shown != null) {
			this.shown = config != null && shown == config.getShown().parse(replacer).orNull() ? null : shown;
		}
		if (name != null) {
			this.name = config != null && name.equalsIgnoreCase(config.getName().parse(replacer).orNull()) ? null : name;
		}
		if (skinData != null) {
			this.skinData = config != null && skinData.equalsIgnoreCase(config.getSkinData().parse(replacer).orNull()) ? null : skinData;
		}
		if (skinSignature != null) {
			this.skinSignature = config != null && skinSignature.equalsIgnoreCase(config.getSkinSignature().parse(replacer).orNull()) ? null : skinSignature;
		}
		if (location != null) {
			this.location = config != null && location.equals(config.getLocation().parse(replacer).orNull()) ? null : location;
		}
		if (targetDistance != null) {
			this.targetDistance = config != null && targetDistance.equals(config.getTargetDistance().parse(replacer).orNull()) ? null : targetDistance;
		}
		if (status != null) {
			this.status = config != null && CollectionUtils.contentEquals(status, config.getStatus().parse(replacer).orEmptyList(), false) ? null : status;
		}
		if (heldItem != null) {
			this.heldItem = config != null && ItemUtils.match(heldItem, config.getHeldItem().parse(replacer).orNull(), ItemCheck.ExactSame) ? null : heldItem;
		}
		if (heldItemOff != null) {
			this.heldItemOff = config != null && ItemUtils.match(heldItemOff, config.getHeldItemOff().parse(replacer).orNull(), ItemCheck.ExactSame) ? null : heldItemOff;
		}
		if (boots != null) {
			this.boots = config != null && ItemUtils.match(boots, config.getBoots().parse(replacer).orNull(), ItemCheck.ExactSame) ? null : boots;
		}
		if (leggings != null) {
			this.leggings = config != null && ItemUtils.match(leggings, config.getLeggings().parse(replacer).orNull(), ItemCheck.ExactSame) ? null : leggings;
		}
		if (chestplate != null) {
			this.chestplate = config != null && ItemUtils.match(chestplate, config.getChestplate().parse(replacer).orNull(), ItemCheck.ExactSame) ? null : chestplate;
		}
		if (helmet != null) {
			this.helmet = config != null && ItemUtils.match(helmet, config.getHelmet().parse(replacer).orNull(), ItemCheck.ExactSame) ? null : helmet;
		}
	}

}
