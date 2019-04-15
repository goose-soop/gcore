package com.guillaumevdn.gcore.lib.npc.navigation;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;

import com.guillaumevdn.gcore.lib.npc.Npc;

public interface Navigator {

	// methods
	public World getWorld();
	public Point getStart();
	public Location getCurrentStep();
	public Point getTarget();
	public List<Location> getPath();
	public void start();
	public void cancel();
	public List<Npc> getAffected();

}
