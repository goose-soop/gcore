package com.guillaumevdn.gcore.migration.v8_0.data;

import java.util.List;

import org.bukkit.Location;

import com.guillaumevdn.gcore.lib.legacy_npc.NPCStatus;

public class V7UserNpcData {

	public int id;
	public Boolean shown;
	public String name;
	public String skinData;
	public String skinSignature;
	public Location location;
	public Double targetDistance;
	public List<NPCStatus> status;
	public V7ItemData heldItem;
	public V7ItemData heldItemOff;
	public V7ItemData boots;
	public V7ItemData leggings;
	public V7ItemData chestplate;
	public V7ItemData helmet;

}
