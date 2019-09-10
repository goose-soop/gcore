package com.guillaumevdn.gcore.lib.messenger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Replacer {

	// ------------------------------------------------------------
	// Fields
	// ------------------------------------------------------------

	private Map<String, String> replacements = new HashMap<String, String>();
	private Map<String, List<String>> replacementsMultiple = new HashMap<String, List<String>>();

	// ------------------------------------------------------------
	// Constructor
	// ------------------------------------------------------------

	public Replacer() {}

	public Replacer(Object... objs)
	{
		for (int i = 0; i < objs.length; )
		{
			if (objs[i] instanceof Object[]) {
				Object[] strs = (Object[]) objs[i];
				for (int j = 0; j < strs.length; j += 2) {
					String placeholder = String.valueOf(strs[j]);
					String replacer = String.valueOf(strs[j + 1]);
					replacements.put(placeholder, replacer);
				}
				i++;
			} else {
				String placeholder = String.valueOf(objs[i]);
				Object replacer = objs[i + 1];
				if (replacer instanceof Collection<?>) {
					replacementsMultiple.put(placeholder, (List<String>) replacer);
				} else {
					replacements.put(placeholder, String.valueOf(replacer));
				}
				i += 2;
			}
		}
	}

	public Replacer(List<Object> objs) {
		for (int i = 0; i < objs.size(); ) {
			if (objs.get(i) instanceof Object[]) {
				Object[] strs = (String[]) objs.get(i);
				for (int j = 0; j < strs.length; j += 2) {
					String placeholder = String.valueOf(strs[j]);
					String replacer = String.valueOf(strs[j + 1]);
					replacements.put(placeholder, replacer);
				}
				i++;
			} else {
				String placeholder = String.valueOf(objs.get(i));
				Object replacer = objs.get(i + 1);
				if (replacer instanceof Collection<?>) {
					replacementsMultiple.put(placeholder, (List<String>) replacer);
				} else {
					replacements.put(placeholder, String.valueOf(replacer));
				}
				i += 2;
			}
		}
	}

	// ------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------

	public String apply(String original) {
		if (original == null) {
			return null;
		}
		String result = original;
		for (String placeholder : replacements.keySet()) {
			result = result.replace(placeholder, replacements.get(placeholder));
		}
		return result;
	}

	public List<String> apply(Collection<String> original) {
		if (original == null) {
			return null;
		}
		List<String> result = new ArrayList<String>();
		for (String line : original) {
			keepApplying(line, result, 1);
		}
		return result;
	}

	private void keepApplying(String line, List<String> result, int deep) {
		// apply single placeholders first
		line = apply(line);
		// check for a multiple replacer
		boolean foundMultiple = false;
		for (String placeholder : replacementsMultiple.keySet()) {
			if (line.contains(placeholder)) {
				foundMultiple = true;
				int indexOf = line.indexOf(placeholder);
				String prefix = line.substring(0, indexOf);
				String suffix = line.substring(indexOf + placeholder.length());
				List<String> temp = replacementsMultiple.get(placeholder);
				for (int i = 0; i < temp.size(); i++) {
					// apply single replacers and add line
					String s = apply(temp.get(i));
					if (i == 0) s = prefix + s;
					if (i == temp.size() - 1) s = s + suffix;
					result.add(s);
				}
				// keep applying on the latest line
				if (!result.isEmpty()) {
					String latest = result.get(result.size() - 1);
					if (!latest.isEmpty()) {
						result.remove(result.size() - 1);
						keepApplying(latest, result, deep + 1);
					}
				}
			}
		}
		// didn't found multiple, so just add line
		if (!foundMultiple) {
			result.add(line);
		}
	}

}
