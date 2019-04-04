package com.guillaumevdn.gcore.lib.messenger;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.GLocale;
import com.guillaumevdn.gcore.lib.util.Utils;

// TODO : a simple way of replacing default messages in locale (so "lastDefaultVersion" or something)
public class Text {

	// registration
	private static final Map<String, Text> registered = new HashMap<String, Text>();
	private static final Map<File, List<Text>> registeredByFile = new HashMap<File, List<Text>>();

	public static Collection<Text> values() {
		return Collections.unmodifiableCollection(registered.values());
	}

	public static List<Text> values(File file) {
		return Collections.unmodifiableList(registeredByFile.containsKey(file) ? registeredByFile.get(file) : new ArrayList<Text>());
	}

	public static Text valueOf(String id) {
		return registered.get(id);
	}


	// base
	private String id;
	private File localeFile;
	private Map<String, List<String>> langs = new HashMap<String, List<String>>();

	public Text() {
		this("unknown_" + UUID.randomUUID().toString().split("-")[0], GLocale.fileUnknown);
	}

	public Text(Object... defaultLangs) {
		this("unknown_" + UUID.randomUUID().toString().split("-")[0], GLocale.fileUnknown, defaultLangs);
	}

	public Text(String id, File localeFile, Object... defaultLangs) {
		this.id = id;
		this.localeFile = localeFile;
		if (defaultLangs != null) {
			if (defaultLangs.length % 2 != 0) {
				GCore.inst().error("Trying to register message " + id + " but default langs size is invalid");
			} else {
				for (int i = 0; i < defaultLangs.length; i += 2) {
					set((String) defaultLangs[i], defaultLangs[i + 1]);
				}
			}
		}
		registered.put(id, this);
		if (localeFile != null) {
			if (registeredByFile.containsKey(localeFile)) {
				registeredByFile.get(localeFile).add(this);
			} else {
				registeredByFile.put(localeFile, Utils.asList(this));
			}
		}
	}

	public String getId() {
		return id;
	}

	public File getLocaleFile() {
		return localeFile;
	}

	// setter
	public void set(String lang, Object text) {
		List<String> formattedLines = text instanceof String ? Utils.asList(Utils.format((String) text)) : Utils.format((List<String>) text);
		langs.put(lang, formattedLines);
	}

	// get
	public Map<String, List<String>> getLangs() {
		return langs;
	}

	public List<String> getDefaultLines() {
		return langs.containsKey("en_US") ? langs.get("en_US") : (langs.isEmpty() ? Utils.emptyList() : langs.get(langs.keySet().iterator().next()));
	}

	public List<String> getActiveLines() {
		return langs.containsKey(GCore.inst().getActiveLocaleLang()) ? langs.get(GCore.inst().getActiveLocaleLang()) : getDefaultLines();
	}

	public List<String> getLines(String lang) {
		return langs.get(lang);
	}

	public String getLine() {
		List<String> lines = getActiveLines();
		return lines.isEmpty() ? "{no_text}" : lines.get(0);
	}

	public String getLine(Object... replacers) {
		return new Replacer(replacers).apply(getLine());
	}

	public String getLine(List<Object> replacers) {
		return new Replacer(replacers).apply(getLine());
	}

	public String getLine(Replacer replacer) {
		return replacer.apply(getLine());
	}

	public List<String> getLines() {
		return getActiveLines();
	}

	public List<String> getLines(Object... replacers) {
		return new Replacer(replacers).apply(getLines());
	}

	public List<String> getLines(List<Object> replacers) {
		return new Replacer(replacers).apply(getLines());
	}

	public List<String> getLines(Replacer replacer) {
		return replacer.apply(getLines());
	}

	public void send(Object target, Object... replacers) {
		Messenger.send(target, replacers == null || replacers.length == 0 ? getLines() : getLines(replacers));
	}

	public void broadcast(Object... replacers) {
		send(Utils.getOnlinePlayers(), replacers);
	}

	@Override
	public String toString() {
		return "Text{id=" + id + "}";
	}

}
