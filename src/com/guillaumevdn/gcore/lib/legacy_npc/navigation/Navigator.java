package com.guillaumevdn.gcore.lib.legacy_npc.navigation;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;

import com.guillaumevdn.gcore.lib.legacy_npc.NPC;

public interface Navigator {

	// methods
	World getWorld();
	Point getStart();
	Location getCurrentStep();
	Point getTarget();
	List<Location> getPath();
	void start();
	void cancel();
	List<NPC> getAffected();

}
