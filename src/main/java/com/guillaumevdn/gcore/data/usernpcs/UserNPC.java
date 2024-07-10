package com.guillaumevdn.gcore.data.usernpcs;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.concurrency.AtomicRef;
import com.guillaumevdn.gcore.lib.item.ItemCheck;
import com.guillaumevdn.gcore.lib.item.ItemReference;
import com.guillaumevdn.gcore.lib.item.ItemUtils;
import com.guillaumevdn.gcore.lib.legacy_npc.ElementNPC;
import com.guillaumevdn.gcore.lib.legacy_npc.NPCManager;
import com.guillaumevdn.gcore.lib.legacy_npc.NPCStatus;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

public final class UserNPC {

	private final int id;
	private AtomicRef<Boolean> shown = AtomicRef.of(null);
	private AtomicRef<String> name = AtomicRef.of(null);
	private AtomicRef<String> skinData = AtomicRef.of(null);
	private AtomicRef<String> skinSignature = AtomicRef.of(null);
	private AtomicRef<Location> location = AtomicRef.of(null);
	private AtomicRef<Double> targetDistance = AtomicRef.of(null);
	private AtomicRef<List<NPCStatus>> status = AtomicRef.of(null);
	private AtomicRef<ItemStack> heldItem = AtomicRef.of(null);
	private AtomicRef<ItemStack> heldItemOff = AtomicRef.of(null);
	private AtomicRef<ItemStack> boots = AtomicRef.of(null);
	private AtomicRef<ItemStack> leggings = AtomicRef.of(null);
	private AtomicRef<ItemStack> chestplate = AtomicRef.of(null);
	private AtomicRef<ItemStack> helmet = AtomicRef.of(null);

	public UserNPC(int id) {
		this.id = id;
	}

	// ----- get
	public int getId() {
		return id;
	}

	public boolean isEmpty() {
		return !Stream.of(shown, name, skinData, skinSignature, location, targetDistance, status, heldItem, heldItemOff, boots, leggings, chestplate, helmet)
				.anyMatch(elem -> elem.get() != null);
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
		return shown.get();
	}

	public Boolean getShown(Replacer replacer) {
		Boolean shown = getModifiedShown();
		return shown != null ? shown : getConfigOrElse(config -> config.getShown().parse(replacer).orNull(), true);
	}

	public String getModifiedName() {
		return name.get();
	}

	public String getName(Replacer replacer) {
		String name = getModifiedName();
		return name != null ? name : getConfigOrElse(config -> config.getName().parse(replacer).orNull(), null);
	}

	public String getModifiedSkinData() {
		return skinData.get();
	}

	public String getSkinData(Replacer replacer) {
		String skinData = getModifiedSkinData();
		return skinData != null ? skinData : getConfigOrElse(config -> config.getSkinData().parse(replacer).orNull(), null);
	}

	public String getModifiedSkinSignature() {
		return skinSignature.get();
	}

	public String getSkinSignature(Replacer replacer) {
		String skinSignature = getModifiedSkinSignature();
		return skinSignature != null ? skinSignature : getConfigOrElse(config -> config.getSkinSignature().parse(replacer).orNull(), null);
	}

	public Location getModifiedLocation() {
		return location.get();
	}

	public Location getLocation(Replacer replacer) {
		Location location = getModifiedLocation();
		return location != null ? location : getConfigOrElse(config -> config.getLocation().parse(replacer).orNull(), null);
	}

	public Double getModifiedTargetDistance() {
		return targetDistance.get();
	}

	public Double getTargetDistance(Replacer replacer) {
		Double targetDistance = getModifiedTargetDistance();
		return targetDistance != null ? targetDistance : getConfigOrElse(config -> config.getTargetDistance().parse(replacer).orNull(), null);
	}

	public List<NPCStatus> getModifiedStatus() {
		return status.get();
	}

	public List<NPCStatus> getStatus(Replacer replacer) {
		List<NPCStatus> status = getModifiedStatus();
		return status != null ? status : getConfigOrElse(config -> config.getStatus().parse(replacer).orNull(), new ArrayList<>());
	}

	public ItemStack getModifiedHeldItem() {
		return heldItem.get();
	}

	public ItemStack getHeldItem(Replacer replacer) {
		ItemStack heldItem = getModifiedHeldItem();
		return heldItem != null ? heldItem : getConfigOrElse(config -> config.getHeldItem().parse(replacer).orNull(), null);
	}

	public ItemStack getModifiedHeldItemOff() {
		return heldItemOff.get();
	}

	public ItemStack getHeldItemOff(Replacer replacer) {
		ItemStack heldItemOff = getModifiedHeldItemOff();
		return heldItemOff != null ? heldItemOff : getConfigOrElse(config -> config.getHeldItemOff().parse(replacer).orNull(), null);
	}

	public ItemStack getModifiedBoots() {
		return boots.get();
	}

	public ItemStack getBoots(Replacer replacer) {
		ItemStack boots = getModifiedBoots();
		return boots != null ? boots : getConfigOrElse(config -> config.getBoots().parse(replacer).orNull(), null);
	}

	public ItemStack getModifiedLeggings() {
		return leggings.get();
	}

	public ItemStack getLeggings(Replacer replacer) {
		ItemStack leggings = getModifiedLeggings();
		return leggings != null ? leggings : getConfigOrElse(config -> config.getLeggings().parse(replacer).orNull(), null);
	}

	public ItemStack getModifiedChestplate() {
		return chestplate.get();
	}

	public ItemStack getChestplate(Replacer replacer) {
		ItemStack chestplate = getModifiedChestplate();
		return chestplate != null ? chestplate : getConfigOrElse(config -> config.getChestplate().parse(replacer).orNull(), null);
	}

	public ItemStack getModifiedHelmet() {
		return helmet.get();
	}

	public ItemStack getHelmet(Replacer replacer) {
		ItemStack helmet = getModifiedHelmet();
		return helmet != null ? helmet : getConfigOrElse(config -> config.getHelmet().parse(replacer).orNull(), null);
	}

	// ----- set
	public void saveNonDefault(ElementNPC config, Replacer replacer, Boolean shown, String name, String skinData, String skinSignature, Location location,
			Double targetDistance, List<NPCStatus> status, ItemStack heldItem, ItemStack heldItemOff, ItemStack boots, ItemStack leggings, ItemStack chestplate,
			ItemStack helmet) {
		if (shown != null) {
			this.shown.set(config != null && shown == config.getShown().parse(replacer).orNull() ? null : shown);
		}
		if (name != null) {
			this.name.set(config != null && name.equalsIgnoreCase(config.getName().parse(replacer).orNull()) ? null : name);
		}
		if (skinData != null) {
			this.skinData.set(config != null && skinData.equalsIgnoreCase(config.getSkinData().parse(replacer).orNull()) ? null : skinData);
		}
		if (skinSignature != null) {
			this.skinSignature.set(config != null && skinSignature.equalsIgnoreCase(config.getSkinSignature().parse(replacer).orNull()) ? null : skinSignature);
		}
		if (location != null) {
			this.location.set(config != null && location.equals(config.getLocation().parse(replacer).orNull()) ? null : location);
		}
		if (targetDistance != null) {
			this.targetDistance.set(config != null && targetDistance.equals(config.getTargetDistance().parse(replacer).orNull()) ? null : targetDistance);
		}
		if (status != null) {
			this.status.set(config != null && CollectionUtils.contentEquals(status, config.getStatus().parse(replacer).orEmptyList(), false) ? null : status);
		}
		if (heldItem != null) {
			this.heldItem
					.set(config != null && ItemUtils.match(heldItem, ItemReference.of(config.getHeldItem(), replacer), ItemCheck.ExactSame) ? null : heldItem);
		}
		if (heldItemOff != null) {
			this.heldItemOff.set(config != null && ItemUtils.match(heldItemOff, ItemReference.of(config.getHeldItemOff(), replacer), ItemCheck.ExactSame) ? null
					: heldItemOff);
		}
		if (boots != null) {
			this.boots.set(config != null && ItemUtils.match(boots, ItemReference.of(config.getBoots(), replacer), ItemCheck.ExactSame) ? null : boots);
		}
		if (leggings != null) {
			this.leggings
					.set(config != null && ItemUtils.match(leggings, ItemReference.of(config.getLeggings(), replacer), ItemCheck.ExactSame) ? null : leggings);
		}
		if (chestplate != null) {
			this.chestplate.set(
					config != null && ItemUtils.match(chestplate, ItemReference.of(config.getChestplate(), replacer), ItemCheck.ExactSame) ? null : chestplate);
		}
		if (helmet != null) {
			this.helmet.set(config != null && ItemUtils.match(helmet, ItemReference.of(config.getHelmet(), replacer), ItemCheck.ExactSame) ? null : helmet);
		}
	}

}
