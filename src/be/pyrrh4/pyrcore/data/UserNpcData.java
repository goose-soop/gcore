package be.pyrrh4.pyrcore.data;

import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;

import be.pyrrh4.pyrcore.lib.gui.ItemData;
import be.pyrrh4.pyrcore.lib.npc.NpcStatus;

public class UserNpcData {

	// base
	private final int id;
	private boolean shown;
	private String name = null;
	private UUID skin = null;
	private Location location = null;
	private Double targetDistance = null;
	private Set<NpcStatus> status = null;
	private ItemData[] items = null;

	public UserNpcData(int id, boolean shown) {
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

	public UUID getSkin() {
		return skin;
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

	public void setSkin(UUID skin) {
		this.skin = skin;
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
		return name == null && skin == null && location == null && targetDistance == 5d && status == null && items == null;
	}

}
