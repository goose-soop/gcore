package com.guillaumevdn.gcore.data;

import java.util.Set;

import org.bukkit.Location;

import com.guillaumevdn.gcore.lib.gui.ItemData;
import com.guillaumevdn.gcore.lib.npc.NpcStatus;

public class ModifiedNpcData {

	// base
	private final int id;
	private boolean shown;
	private String name = null;
	private String skinData = null, skinSignature = null;
	private Location location = null;
	private Double targetDistance = null;
	private Set<NpcStatus> status = null;
	private ItemData[] items = null;

	public ModifiedNpcData(int id, boolean shown) {
		this.id = id;
		this.shown = shown;
	}

	// get
	public int getId() {
		return id;
	}

	public boolean isShown() {
		return shown;
	}

	public String getName() {
		return name;
	}

	public String getSkinData() {
		return skinData;
	}

	public String getSkinSignature() {
		return skinSignature;
	}

	public Location getLocation() {
		return location;
	}

	public Double getTargetDistance() {
		return targetDistance;
	}

	public Set<NpcStatus> getStatus() {
		return status;
	}

	public ItemData[] getItems() {
		return items;
	}

	// set
	public void setShown(boolean shown) {
		this.shown = shown;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSkinData(String skinData) {
		this.skinData = skinData;
	}

	public void setSkinSignature(String skinSignature) {
		this.skinSignature = skinSignature;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public void setTargetDistance(Double targetDistance) {
		this.targetDistance = targetDistance;
	}

	public void setStatus(Set<NpcStatus> status) {
		this.status = status;
	}

	public void setItems(ItemData[] items) {
		this.items = items;
	}

	// methods
	public boolean isEmpty() {
		return name == null && skinData == null && skinSignature == null && location == null && targetDistance == null && status == null && items == null;
	}

}
