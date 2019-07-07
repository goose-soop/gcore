package com.guillaumevdn.gcore.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.lib.gui.ItemData;
import com.guillaumevdn.gcore.lib.npc.NpcData;
import com.guillaumevdn.gcore.lib.npc.NpcStatus;
import com.guillaumevdn.gcore.lib.npc.behavior.RunningBehavior;

public class GUserNpcData {

	// base
	private final int id;
	private boolean shown;
	private String name = null;
	private String skinData = null;
	private String skinSignature = null;
	private Location location = null;
	private Double targetDistance = null;
	private Set<NpcStatus> status = null;
	private ItemData heldItem = null;
	private ItemData heldItemOff = null;
	private ItemData boots = null;
	private ItemData leggings = null;
	private ItemData chestplate = null;
	private ItemData helmet = null;
	private Map<String, String> variables = null;
	private List<String> behaviors = null;
	private List<RunningBehavior> runningBehaviors = new ArrayList<RunningBehavior>();

	public GUserNpcData(int id, NpcData data, Player player) {
		this.id = id;
		this.shown = data.getShown(player);
		replaceNullValues(data, player);
	}

	// methods
	/**
	 * @return the amount of modified values
	 */
	public int replaceNullValues(NpcData data, Player player) {
		int changed = 0;
		if (name == null) {
			++changed;
			name = data.getName(player);
		}
		if (skinData == null) {
			++changed;
			skinData = data.getSkinData(player);
		}
		if (skinSignature == null) {
			++changed;
			skinSignature = data.getSkinSignature(player);
		}
		if (location == null) {
			++changed;
			location = data.getLocation(player);
		}
		if (targetDistance == null) {
			++changed;
			targetDistance = data.getTargetDistance(player);
		}
		if (status == null) {
			++changed;
			status = new HashSet<NpcStatus>();
			List<NpcStatus> list = data.getStatus(player);
			if (list != null) status.addAll(list);
		}
		if (heldItem == null) {
			++changed;
			heldItem = data.getHeldItem(player);
		}
		if (heldItemOff == null) {
			++changed;
			heldItemOff = data.getHeldItemOff(player);
		}
		if (boots == null) {
			++changed;
			boots = data.getBoots(player);
		}
		if (leggings == null) {
			++changed;
			leggings = data.getLeggings(player);
		}
		if (chestplate == null) {
			++changed;
			chestplate = data.getChestplate(player);
		}
		if (helmet == null) {
			++changed;
			helmet = data.getHelmet(player);
		}
		if (variables == null) {
			++changed;
			variables = new HashMap<String, String>();
			for (String variable : data.getVariables().getElements().keySet()) {
				String value = data.getVariables().getElement(variable).getParsedValue(player);
				variables.put(variable, value == null ? "0" : value);
			}
		}
		if (behaviors == null) {
			++changed;
			behaviors = new ArrayList<String>();
			List<String> list = data.getBehaviors(player);
			if (list != null) behaviors.addAll(list);
		}
		return changed;
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

	public ItemData getHeldItem() {
		return heldItem;
	}

	public ItemData getHeldItemOff() {
		return heldItemOff;
	}

	public ItemData getBoots() {
		return boots;
	}

	public ItemData getLeggings() {
		return leggings;
	}

	public ItemData getChestplate() {
		return chestplate;
	}

	public ItemData getHelmet() {
		return helmet;
	}

	public Map<String, String> getVariables() {
		return variables;
	}
	
	public String getVariableValue(String variable) {
		String value = variables.get(variable);
		return value == null ? "0" : value;
	}

	public List<String> getBehaviors() {
		return behaviors;
	}

	public List<RunningBehavior> getRunningBehaviors() {
		return runningBehaviors;
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

	public void setHeldItem(ItemData heldItem) {
		this.heldItem = heldItem;
	}

	public void setHeldItemOff(ItemData heldItemOff) {
		this.heldItemOff = heldItemOff;
	}

	public void setBoots(ItemData boots) {
		this.boots = boots;
	}

	public void setLeggings(ItemData leggings) {
		this.leggings = leggings;
	}

	public void setChestplate(ItemData chestplate) {
		this.chestplate = chestplate;
	}

	public void setHelmet(ItemData helmet) {
		this.helmet = helmet;
	}
	
	public void setVariableValue(String variable, String value) {
		variables.put(variable, value);
	}

}
