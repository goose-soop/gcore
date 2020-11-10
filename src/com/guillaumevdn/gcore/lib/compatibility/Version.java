package com.guillaumevdn.gcore.lib.compatibility;

import java.util.Collections;
import java.util.List;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;

/**
 * @author GuillaumeVDN
 */
public enum Version {

	// values ; versions are checked from bottom to top so it'll take latest if necessary

	ANY("ANY", null),

	MC_1_7_R1("1.7.2", "v1_7_R1"),
	MC_1_7_R3("1.7.9", "v1_7_R3"),
	MC_1_7_R4("1.7.10", "v1_7_R4"),

	MC_1_8_R3("1.8", "v1_8_R3"),
	MC_1_9_R2("1.9", "v1_9_R2"),
	MC_1_10_R1("1.10", "v1_10_R1"),
	MC_1_11_R1("1.11", "v1_11_R1"),
	MC_1_12_R1("1.12", "v1_12_R1"),
	MC_1_13_R2("1.13", "v1_13_R2"),
	MC_1_14_R1("1.14", "v1_14_R1"),
	MC_1_15_R1("1.15", "v1_15_R1"),

	MC_1_16_R1("1.16", "v1_16_R1"),
	MC_1_16_R2(CollectionUtils.asList("1.16.2", "1.16.3"), "v1_16_R2"), // TODO : remove support for old when 1.17 comes out
	;

	public static final Version CURRENT = VersionUtils.getCurrent();
	public static final boolean IS_1_7 = CURRENT.getNames().get(0).contains("1.7");
	public static final boolean ATLEAST_1_7_2 = CURRENT.isMoreOrEqualsTo(MC_1_7_R1);
	public static final boolean ATLEAST_1_7_9 = CURRENT.isMoreOrEqualsTo(MC_1_7_R3);
	public static final boolean ATLEAST_1_7_10 = CURRENT.isMoreOrEqualsTo(MC_1_7_R4);
	public static final boolean ATLEAST_1_8 = CURRENT.isMoreOrEqualsTo(MC_1_8_R3);
	public static final boolean ATLEAST_1_9 = CURRENT.isMoreOrEqualsTo(MC_1_9_R2);
	public static final boolean ATLEAST_1_10 = CURRENT.isMoreOrEqualsTo(MC_1_10_R1);
	public static final boolean ATLEAST_1_11 = CURRENT.isMoreOrEqualsTo(MC_1_11_R1);
	public static final boolean ATLEAST_1_12 = CURRENT.isMoreOrEqualsTo(MC_1_12_R1);
	public static final boolean ATLEAST_1_13 = CURRENT.isMoreOrEqualsTo(MC_1_13_R2);
	public static final boolean ATLEAST_1_14 = CURRENT.isMoreOrEqualsTo(MC_1_14_R1);
	public static final boolean ATLEAST_1_15 = CURRENT.isMoreOrEqualsTo(MC_1_15_R1);
	public static final boolean ATLEAST_1_16 = CURRENT.isMoreOrEqualsTo(MC_1_16_R1);

	private List<String> names;
	private String packageName;

	Version(String name, String packageName) {
		this(CollectionUtils.asList(name), packageName);
	}

	Version(List<String> names, String packageName) {
		this.names = Collections.unmodifiableList(names);
		this.packageName = packageName;
	}

	// get
	public List<String> getNames() {
		return names;
	}

	public String getPackageName() {
		return packageName;
	}

	public boolean isLessThan(Version version) {
		return compareTo(version) < 0;
	}

	public boolean isLessOrEqualsTo(Version version) {
		return compareTo(version) <= 0;
	}

	public boolean isMoreThan(Version version) {
		return compareTo(version) > 0;
	}

	public boolean isMoreOrEqualsTo(Version version) {
		return compareTo(version) >= 0;
	}

	// static methods
	public static Version fromNameOrNull(String name) {
		for (Version version : Version.values()) {
			if (CollectionUtils.containsIgnoreCase(version.names, name)) {
				return version;
			}
		}
		return null;
	}

}
