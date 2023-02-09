package com.guillaumevdn.gcore.migration;

import java.io.File;
import java.util.List;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.configuration.YMLConfiguration;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public class YMLMigrationReading extends YMLConfiguration {

	public YMLMigrationReading(GPlugin plugin, File file) {
		super(plugin, file);
	}

	// ----- read
	private static final Replacer placeholdersReplacer = Replacer
			.of("{objective}", () -> "{objective_name}")
			.with("{objective_progress}", () -> "{objective_progression}")
			.with("{objective_detail_progress}", () -> "{objective_progression}")
			.with("{objective_detail_goal}", () -> "{objective_goal}")
			.with("{objective_detail_percentage}", () -> "{objective_progression_percentage}")
			;

	private static final Replacer detailedProgressionPlaceholderReplacer = Replacer
			.of("{detailed_progression}", () -> "{objective}")
			;

	@Override
	public String readString(String path, String def) {
		String result = super.readString(path, def);
		return result != null ? result : def;
	}

	@Override
	protected String doReadString(String path) {
		String value = super.doReadString(path);
		if (value == null || value.isEmpty()) { // don't trim here
			return null;
		}
		value = detailedProgressionPlaceholderReplacer.parse(value);
		return placeholdersReplacer.parse(value);
	}

	@Override
	public List<String> readStringList(String path, List<String> def) {
		List<String> result = super.readStringList(path, def);
		return result != null ? result : def;
	}

	@Override
	protected List<String> doReadStringList(String path) {
		List<String> value = super.doReadStringList(path);
		if (value == null || value.isEmpty()) {
			return null;
		}
		if (!path.contains("gui.item_")) {  // don't replace placeholder {detailed_progression} in item descriptions lore
			value = detailedProgressionPlaceholderReplacer.parse(value);
		}
		return placeholdersReplacer.parse(value);
	}

}
