package be.pyrrh4.pyrcore.convert.v6;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;

public class Pre6PotatoesArena {

	private String name;
	private int min = -1;
	private int max = -1;
	private Location lobby = null;
	private Location deathmatch = null;
	private HashMap<Integer, Location> spawns = new HashMap<Integer, Location>();
	private HashMap<Integer, Location> bonuses = new HashMap<Integer, Location>();
	private ArrayList<Location> signs = new ArrayList<Location>();

	public String getName() {
		return name;
	}

	public int getMin() {
		return min;
	}

	public int getMax() {
		return max;
	}

	public Location getLobby() {
		return lobby;
	}

	public Location getDeathmatch() {
		return deathmatch;
	}

	public HashMap<Integer, Location> getSpawns() {
		return spawns;
	}

	public HashMap<Integer, Location> getBonuses() {
		return bonuses;
	}

	public ArrayList<Location> getSigns() {
		return signs;
	}

}
