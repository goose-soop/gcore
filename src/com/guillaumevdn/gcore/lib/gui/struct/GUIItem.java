package com.guillaumevdn.gcore.lib.gui.struct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.sound.Sound;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall.ClickType;
import com.guillaumevdn.gcore.lib.tuple.GUIItemTriple;
import com.guillaumevdn.gcore.lib.tuple.IntegerPair;

/**
 * @author GuillaumeVDN
 */
public class GUIItem {

	private final String id;
	private ItemStack item;
	private final List<IntegerPair> preferredLocations;
	private List<IntegerPair> locations = new ArrayList<>(); // <page, slot>
	private Consumer<ClickCall> clickPerformer;
	private Sound clickSound;
	private Map<ClickType, Consumer<ClickCall>> overrideClicks;

	public GUIItem(String id, GUIItemTriple item) {
		this(id, item.getC(), item.getA(), null, null, null);
	}

	public GUIItem(String id, GUIItemTriple item, Consumer<ClickCall> clickPerformer) {
		this(id, item.getC(), item.getA(), clickPerformer, null, null);
	}

	public GUIItem(String id, GUIItemTriple item, Consumer<ClickCall> clickPerformer, Sound clickSound, Map<ClickType, Consumer<ClickCall>> overrideClicks) {
		this(id, item.getC(), item.getA(), clickPerformer, clickSound, overrideClicks);
	}

	public GUIItem(String id, ItemStack item) {
		this(id, null, item, null, null, null);
	}

	public GUIItem(String id, ItemStack item, Consumer<ClickCall> clickPerformer) {
		this(id, null, item, clickPerformer, null, null);
	}

	public GUIItem(String id, ItemStack item, Consumer<ClickCall> clickPerformer, Sound clickSound, Map<ClickType, Consumer<ClickCall>> overrideClicks) {
		this(id, null, item, clickPerformer, clickSound, overrideClicks);
	}

	public GUIItem(String id, int preferredSlot, ItemStack item) {
		this(id, CollectionUtils.asList(IntegerPair.of(-1, preferredSlot)), item, null, null, null);
	}

	public GUIItem(String id, int preferredSlot, ItemStack item, Consumer<ClickCall> clickPerformer) {
		this(id, CollectionUtils.asList(IntegerPair.of(-1, preferredSlot)), item, clickPerformer, null, null);
	}

	public GUIItem(String id, int preferredSlot, ItemStack item, Consumer<ClickCall> clickPerformer, Sound clickSound, Map<ClickType, Consumer<ClickCall>> overrideClicks) {
		this(id, CollectionUtils.asList(IntegerPair.of(-1, preferredSlot)), item, clickPerformer, clickSound, overrideClicks);
	}

	public GUIItem(String id, List<IntegerPair> preferredLocations, ItemStack item) {
		this(id, preferredLocations, item, null, null, null);
	}

	public GUIItem(String id, List<IntegerPair> preferredLocations, ItemStack item, Consumer<ClickCall> clickPerformer) {
		this(id, preferredLocations, item, clickPerformer, null, null);
	}

	public GUIItem(String id, List<IntegerPair> preferredLocations, ItemStack item, Consumer<ClickCall> clickPerformer, Sound clickSound, Map<ClickType, Consumer<ClickCall>> overrideClicks) {
		this.id = id;
		this.item = item;
		this.preferredLocations = preferredLocations == null ? new ArrayList<>() : preferredLocations.stream().distinct().collect(Collectors.toList());
		this.clickPerformer = clickPerformer;
		this.clickSound = clickSound;
		this.overrideClicks = overrideClicks;
	}

	// get
	public final String getId() {
		return id;
	}

	public final ItemStack getItem() {
		return item;
	}

	public final List<IntegerPair> getPreferredLocations() {
		return preferredLocations;
	}

	public final List<IntegerPair> getLocations() {
		return locations;
	}

	public final boolean isInLocation(int page, int slot) {
		return locations.stream().anyMatch(location -> location.getA() == page && location.getB() == slot);
	}

	public final boolean isInSlot(int slot) {
		return locations.stream().anyMatch(location -> location.getB() == slot);
	}

	public final Consumer<ClickCall> getClickPerformer(ClickType type) {
		Consumer<ClickCall> performer = overrideClicks == null ? null : overrideClicks.get(type);
		if (performer == null) {
			performer = clickPerformer;
		}
		if (performer != null && clickSound != null) {
			Consumer<ClickCall> actualPerformer = performer;
			performer = call -> {
				clickSound.play(call.getClicker());
				actualPerformer.accept(call);
			};
		}
		return performer;
	}

	// set
	public final void setItem(ItemStack item) {
		this.item = item;
	}

	public void setLocations(List<IntegerPair> locations) {
		this.locations = Collections.unmodifiableList(locations);
	}

	public final void setClickPerformer(Consumer<ClickCall> clickPerformer) {
		this.clickPerformer = clickPerformer;
	}

}
