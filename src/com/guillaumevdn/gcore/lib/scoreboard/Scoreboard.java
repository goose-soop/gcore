package com.guillaumevdn.gcore.lib.scoreboard;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.string.StringUtils;

/**
 * @author GuillaumeVDN
 */
public final class Scoreboard {

	private final String id = StringUtils.generateRandomAlphanumericString(10);
	private GPlugin plugin;
	private Player player;
	private int updateTicks = 20;
	private boolean active = false;
	private Consumer<ScoreboardBuilder> updater;
	private Supplier<Boolean> taskPreProcessor;

	private Set<ScoreboardEntry> currentEntries = new HashSet<>();
	private Map<Integer, Team> teams = new HashMap<>();
	private org.bukkit.scoreboard.Scoreboard bukkit;
	private Objective objective;

	public Scoreboard(GPlugin plugin, Player holder, Consumer<ScoreboardBuilder> updater, Supplier<Boolean> taskPreProcessor, int updateTicks) {
		this.player = holder;
		this.updater = updater;
		this.taskPreProcessor = taskPreProcessor;
		this.plugin = plugin;
		this.updateTicks = updateTicks;
	}

	// ----- get
	public String getId() {
		return id;
	}

	public GPlugin getPlugin() {
		return plugin;
	}

	public Player getPlayer() {
		return player;
	}

	// ----- start/stop
	public void start() {
		if (active) return;
		active = true;

		getPlugin().operateSync(() -> {
			// register
			bukkit = Bukkit.getScoreboardManager().getNewScoreboard();
			bukkit.registerNewObjective("scb", "obj").setDisplaySlot(DisplaySlot.SIDEBAR);
			objective = bukkit.getObjective(DisplaySlot.SIDEBAR);
			// set player
			player.setScoreboard(bukkit);
			// start updating
			plugin.registerTask("scoreboard_" + id, true, updateTicks, () -> update());
		});
	}

	public void stop() {
		if (!active) return;
		active = false;

		// stop updating
		plugin.stopTask("scoreboard_" + id);

		getPlugin().operateSync(() -> {
			// unset player
			if (player.isOnline()) {
				player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
			}
			// unregister
			for (Team team : teams.values()) {
				try { team.unregister(); } catch (Throwable ignored) {}
			}
			teams.clear();
			try { objective.unregister(); } catch (Throwable ignored) {}
			try { bukkit.clearSlot(DisplaySlot.SIDEBAR); } catch (Throwable ignored) {}
		});
	}

	// ----- update
	public void update() {
		if (!active || !player.isOnline()) {
			return;
		}

		// preprocessor
		if (taskPreProcessor != null && !taskPreProcessor.get()) {
			return;
		}

		// maybe the scoreboard was unregistered for some reason : restart it
		try {
			objective.getDisplayName();  // this throws an IllegalStateException if the component is unregistered
		} catch (Throwable ignored) {
			stop();
			start();
		}

		// build
		ScoreboardBuilder builder = new ScoreboardBuilder();
		updater.accept(builder);

		// update title
		if (!objective.getDisplayName().equals(builder.getTitle())) {
			objective.setDisplayName(builder.getTitle());
		}

		// build new entries
		Map<String, Integer> offsets = new HashMap<>();
		List<ScoreboardEntry> builtEntries = builder.entries().map(entry -> buildEntry(entry, offsets)).collect(Collectors.toList());
		Set<ScoreboardEntry> newEntries = new HashSet<>();

		//System.out.println("---------------------------------------------");

		// process entries
		int score = builtEntries.size();
		for (ScoreboardEntry entry : builtEntries) {
			--score;

			// ensure team existence
			Team team = null;
			if (entry.getPrefix() != null) {
				int teamId = (entry.getPrefix() + entry.getSuffix()).hashCode();
				team = teams.get(teamId);
				if (team == null) {
					String teamName = "" + teamId;
					team = bukkit.getTeam(teamName);  // sometimes it apparently already exists (#1664 ; synchronization issue ?)
					if (team == null) {
						team = bukkit.registerNewTeam(teamName);
					}
					team.setPrefix(entry.getPrefix());
					team.setSuffix(entry.getSuffix());
					teams.put(teamId, team);
					//System.out.println("--created team, " + teamId);
				} else {
					//System.out.println("--team exists, " + teamId);
				}
			}

			// set score and team
			//System.out.println("--set score to " + score + " for " + entry);
			entry.setScore(score);
			entry.setTeam(team);
			newEntries.add(entry);
			//entries.put(entry.hashCode(), entry);
		}

		// reset scores of entries that are still present on the bukkit scoreboard but should be removed
		currentEntries.stream().filter(entry -> !newEntries.contains(entry)).forEach(entry -> {
			//System.out.println(">>>>> reset entry " + entry);
			entry.reset(bukkit);
		});

		//System.out.println("-- scores remaining : " + ReflectionObject.of(bukkit).getField("board").getField("playerScores").invokeMethod("keySet"));

		// set new scores
		currentEntries = newEntries;  // we do be collecting garbage
		newEntries.forEach(entry -> {
			//System.out.println(">>>>> set entry " + entry + " to score " + entry.getScore());
			entry.apply(objective);
		});
	}

	private static ScoreboardEntry buildEntry(String text, Map<String, Integer> offsets) {

		/**
		 * DO NOT EVER TOUCH THIS CODE AGAIN
		 * IT WORKS
		 * I SWEAR TO GOD
		 * DO NOT TOUCH IT
		 */

		// no need for teams
		if (text.length() <= 16) {
			int offset = offsets.compute(text, (__, o) -> o == null ? 0 : o + 1);
			text += StringUtils.repeatString("§r", offset);
			if (text.length() <= 16) {
				return new ScoreboardEntry(text, null, null);
			}
		}

		// need teams
		int start, end, colorEnd;
		String colors, prefix, name, suffix = "";

		// --- prefix
		end = text.charAt(15) == '§' ? 14 : 15;
		prefix = text.substring(0, end + 1);

		// --- name
		colorEnd = adaptColorCodeEnd(text, end);
		colors = StringUtils.getLastColors(text.substring(0, colorEnd + 1));

		start = colorEnd + 1;
		end = start + 16 - colors.length();

		// adapt name with offset (add §r's if player name already exists)
		int offset = offsets.compute(text.substring(start, Math.min(text.length() - 1, end)), (__, o) -> o == null ? 0 : o + 1);

		// adapt end index with offsets
		end -= offset * 2;

		// adapt end index with text length
		end = Math.min(text.length() - 1, end);

		// adapt end index if ending on color codes due to offset adaptation
		if (text.charAt(end) == '§') {
			--end;
		}

		// get name
		name = colors + text.substring(start, end + 1) + StringUtils.repeatString("§r", offset);

		// ----- suffix
		colorEnd = adaptColorCodeEnd(text, end);
		colors = StringUtils.getLastColors(text.substring(0, colorEnd + 1));

		start = colorEnd + 1;
		if (start < text.length()) {
			end = IntStream.of(48, text.length() - 1, start + 16 - colors.length()).min().getAsInt();
			suffix = colors + text.substring(start, end + 1);
		}

		return new ScoreboardEntry(name, prefix, suffix);
	}

	private static int adaptColorCodeEnd(String text, int endIndex) {
		if (text.charAt(endIndex) == '§') {
			if (endIndex + 1 < text.length()) {
				++endIndex;
			} else {
				--endIndex;
			}
		}
		return endIndex;
	}

}
