package com.guillaumevdn.gcore.lib.scoreboard;

import java.util.UUID;

import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

/**
 * @author GuillaumeVDN
 */
public class ScoreboardEntry {

	private final int hashCode = UUID.randomUUID().hashCode();
	private String name, prefix, suffix;
	private Team team = null;
	private int score = -1;

	public ScoreboardEntry(String name, String prefix, String suffix) {
		this.name = name;
		this.prefix = prefix;
		this.suffix = suffix;
	}

	// ----- get
	public String getName() {
		return name;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public Team getTeam() {
		return team;
	}

	public int getScore() {
		return score;
	}

	// ----- set
	public void setTeam(Team team) {
		this.team = team;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public void apply(Objective objective) {
		objective.getScore(name).setScore(score);
		if (team != null && !team.hasEntry(name)) {
			team.addEntry(name);
		}
	}

	public void reset(org.bukkit.scoreboard.Scoreboard bukkit) {
		try {
			if (team != null && team.hasEntry(name)) {
				team.removeEntry(name);
			}
		} catch (IllegalStateException ignored) {
		} // means he's not there, don't care, that was the point
		bukkit.resetScores(name);
	}

	// ----- obj

	public @Override int hashCode() {
		return hashCode;
	}

	public @Override boolean equals(Object other) {
		return other != null && hashCode() == other.hashCode();
	}

	public @Override String toString() {
		return "(" + (prefix == null ? "/" : prefix) + ", " + (name.isEmpty() ? "/" : name) + ", " + (suffix == null ? "/" : suffix) + ")";
	}

}
