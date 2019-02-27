package be.pyrrh4.pyrcore.convert.v6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;

@SuppressWarnings("unused")
public class V6PotatoesArena {

	private int min = -1;
	private int max = -1;
	private Location lobby = null;
	private Location deathmatch = null;
	private Map<Integer, Location> spawns = new HashMap<Integer, Location>();
	private Map<Integer, Location> bonuses = new HashMap<Integer, Location>();
	private List<Location> signs = new ArrayList<Location>();

	public V6PotatoesArena(Pre6PotatoesArena arena) {
		this.min = arena.getMin();
		this.max = arena.getMax();
		this.lobby = arena.getLobby();
		this.deathmatch = arena.getDeathmatch();
		this.spawns = arena.getSpawns();
		this.bonuses = arena.getBonuses();
		this.signs = arena.getSigns();
	}

}
