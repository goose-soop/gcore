package be.pyrrh4.pyrcore.convert.v6;

import java.util.HashMap;

import org.bukkit.Location;

public class Pre6PotatoesData {

	private Location mainLobby;
	private HashMap<String, Pre6PotatoesArena> arenas = new HashMap<String, Pre6PotatoesArena>();

	public Location getMainLobby() {
		return mainLobby;
	}

	public HashMap<String, Pre6PotatoesArena> getArenas() {
		return arenas;
	}

}
