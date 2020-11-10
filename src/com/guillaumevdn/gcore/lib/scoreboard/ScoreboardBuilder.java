package com.guillaumevdn.gcore.lib.scoreboard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author GuillaumeVDN
 */
public class ScoreboardBuilder {

	private String title = null;
	private List<String> entries = new ArrayList<>();

	// get
	public String getTitle() {
		return title;
	}

	public Stream<String> entries() {
		return entries.stream().limit(16);
	}

	// set
	public void setTitle(String title) {
		this.title = title.length() > 32 ? title.substring(0, 32) : title;
	}

	public ScoreboardBuilder add(String text) {
		entries.add(text.replace("§§", "§"));
		return this;
	}

	public ScoreboardBuilder addAll(Collection<String> text) {
		text.forEach(line -> add(line));
		return this;
	}

}
