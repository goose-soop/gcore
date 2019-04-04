package com.guillaumevdn.gcore.lib.npc.navigation;

import java.util.List;

import org.bukkit.World;

public interface Navigator {

	// methods
	public World getWorld();
	public Point getStart();
	public Point getCurrentStep();
	public Point getTarget();
	public List<Point> getPath();
	public void start();
	public void cancel();

}
