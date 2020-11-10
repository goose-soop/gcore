package com.guillaumevdn.gcore.lib.string;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;

/**
 * @author GuillaumeVDN
 */
public final class TextFile<T extends TextEnumElement> {

	private GPlugin plugin;
	private String filePath, resourcePath;
	private Class<T> enumClass;
	private Map<String, T> values;

	public TextFile(GPlugin plugin, String filePath, Class<T> enumClass) {
		this(plugin, filePath, filePath, enumClass);
	}

	public TextFile(GPlugin plugin, String filePath, String resourcePath, Class<T> enumClass) {
		this(plugin, filePath, resourcePath, enumClass, CollectionUtils.asList(enumClass.getEnumConstants()));
	}

	public TextFile(GPlugin plugin, String filePath, Class<T> enumClass, List<T> values) {
		this(plugin, filePath, filePath, enumClass, values);
	}

	public TextFile(GPlugin plugin, String filePath, String resourcePath, Class<T> enumClass, List<T> values) {
		this.plugin = plugin;
		this.filePath = filePath;
		this.resourcePath = resourcePath;
		this.enumClass = enumClass;
		Map<String, T> vals = new HashMap<>();
		values.forEach(value -> vals.put(value.getId(), value));
		this.values = Collections.unmodifiableMap(vals);
	}

	// get
	public GPlugin getPlugin() {
		return plugin;
	}

	public String getFilePath() {
		return filePath;
	}

	public String getResourcePath() {
		return resourcePath;
	}

	public Class<T> getEnumClass() {
		return enumClass;
	}

	public Map<String, T> getValues() {
		return values;
	}

}
