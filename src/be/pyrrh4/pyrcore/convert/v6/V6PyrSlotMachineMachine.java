package be.pyrrh4.pyrcore.convert.v6;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;

@SuppressWarnings("unused")
public class V6PyrSlotMachineMachine {

	private String type = null;
	private Map<Integer, Location> cases = new HashMap<Integer, Location>();
	private Location button;

	public V6PyrSlotMachineMachine(Pre6PyrSlotMachineMachine machine) {
		this.type = machine.getType();
		this.cases = machine.getCases();
		this.button = machine.getButton();
	}

}
