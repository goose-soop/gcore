package be.pyrrh4.pyrcore.convert.v6;

import java.util.HashMap;

import org.bukkit.Location;

public class Pre6PyrSlotMachineMachine {

	// base
	private String id, type;
	private HashMap<Integer, Location> cases = new HashMap<Integer, Location>();
	private Location button;

	// get
	public String getId() {
		return id;
	}

	public String getType() {
		return type;
	}
	
	public HashMap<Integer, Location> getCases() {
		return cases;
	}

	public Location getButton() {
		return button;
	}

}
