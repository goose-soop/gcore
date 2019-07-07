package com.guillaumevdn.gcore.lib.npc.behavior.condition;

import java.lang.reflect.Constructor;
import java.util.List;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.npc.behavior.condition.type.ConditionNpcPlayerDistanceCheck;
import com.guillaumevdn.gcore.lib.npc.behavior.condition.type.ConditionNpcVariableCheck;
import com.guillaumevdn.gcore.lib.npc.behavior.condition.type.ConditionRandomCheck;
import com.guillaumevdn.gcore.lib.parseable.ConfigData;
import com.guillaumevdn.gcore.lib.parseable.Parseable;
import com.guillaumevdn.gcore.lib.util.ServerVersion;
import com.guillaumevdn.gcore.lib.util.Utils;

public final class BConditionType implements Comparable<BConditionType> {

	// special
	public static final BConditionType NPC_PLAYER_DISTANCE_CHECK = registerType("NPC_PLAYER_DISTANCE_CHECK", ConditionNpcPlayerDistanceCheck.class);
	public static final BConditionType NPC_VARIABLE_CHECK = registerType("NPC_VARIABLE_CHECK", ConditionNpcVariableCheck.class);
	public static final BConditionType RANDOM_CHECK = registerType("RANDOM_CHECK", ConditionRandomCheck.class);

	// registration
	public static BConditionType registerType(String id, Class<? extends BCondition> objectClass) {
		return registerType(id, objectClass, ServerVersion.UNSUPPORTED, ServerVersion.HIGHEST);
	}

	public static BConditionType registerType(String id, Class<? extends BCondition> objectClass, String... requiredPlugins) {
		return registerType(id, objectClass, ServerVersion.UNSUPPORTED, ServerVersion.HIGHEST, requiredPlugins);
	}

	public static BConditionType registerType(String id, Class<? extends BCondition> objectClass, ServerVersion minVersion, String... requiredPlugins) {
		return registerType(id, objectClass, minVersion, ServerVersion.HIGHEST, requiredPlugins);
	}

	public static BConditionType registerType(String id, Class<? extends BCondition> objectClass, ServerVersion minVersion, ServerVersion maxVersion, String... requiredPlugins) {
		id = id.toUpperCase();
		BConditionType type = new BConditionType(id, objectClass, minVersion, maxVersion, requiredPlugins);
		GCore.inst().getNpcManager().getBehaviorConditionTypes().put(id, type);
		return type;
	}

	public static BConditionType valueOf(String id) {
		return GCore.inst().getNpcManager().getBehaviorConditionTypes().get(id.toUpperCase());
	}

	public static List<BConditionType> values() {
		return Utils.asList(GCore.inst().getNpcManager().getBehaviorConditionTypes().values());
	}

	// base
	private String id;
	private Class<? extends BCondition> objectClass;
	private ServerVersion minVersion, maxVersion;
	private List<String> requiredPlugins;

	private BConditionType(String id, Class<? extends BCondition> objectClass, ServerVersion minVersion, ServerVersion maxVersion, String... requiredPlugins) {
		this.id = id;
		this.objectClass = objectClass;
		this.minVersion = minVersion;
		this.maxVersion = maxVersion;
		this.requiredPlugins = requiredPlugins == null ? Utils.emptyList() : Utils.asList(requiredPlugins);
	}

	// get
	public String getId() {
		return id;
	}

	public Class<? extends BCondition> getObjectClass() {
		return objectClass;
	}

	public ServerVersion getMinVersion() {
		return minVersion;
	}

	public ServerVersion getMaxVersion() {
		return maxVersion;
	}

	public List<String> getRequiredPlugins() {
		return requiredPlugins;
	}

	// methods
	/**
	 * Unregister the object type
	 * @return null
	 */
	public BConditionType unregister() {
		GCore.inst().getNpcManager().getBehaviorConditionTypes().remove(id.toUpperCase());
		return null;
	}

	public BCondition createNew(String id, Parseable parent, ConfigData data, boolean loadOrSave, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		// invalid version
		if (!ServerVersion.CURRENT.isAtLeast(getMinVersion())) {
			data.log("behavior condition type " + getId() + " requires at least server version " + getMinVersion().getName());
			return null;
		}
		if (!ServerVersion.CURRENT.isOrLess(getMaxVersion())) {
			data.log("behavior condition type " + getId() + " supports at max server version " + getMaxVersion().getName());
			return null;
		}
		// invalid plugins
		for (String requiredPlugin : this.getRequiredPlugins()) {
			if (!Utils.isPluginEnabled(requiredPlugin)) {
				data.log("behavior condition type " + getId() + " requires plugin " + requiredPlugin + " to be enabled");
				return null;
			}
		}
		// create
		try {
			// create instance
			Constructor<? extends BCondition> constructor = getObjectClass().getConstructor(String.class, Parseable.class, boolean.class, int.class, Mat.class, List.class);
			BCondition component = constructor.newInstance(id, parent, mandatory, editorSlot, editorIcon, editorDescription);
			// load or save data
			if (loadOrSave) {
				component.load(data);
			} else {
				component.save(data);
			}
			// return
			return component;
		}
		// couldn't create
		catch (Throwable exception) {
			if (data != null) data.log("unknown error when creating behavior condition " + id + " (" + getObjectClass().getName() + ") with type " + getId());
			exception.printStackTrace();
			return null;
		}
	}

	// overriden methods
	@Override
	public boolean equals(Object obj) {
		return Utils.instanceOf(obj, BConditionType.class) && getId().equalsIgnoreCase(((BConditionType) obj).getId());
	}

	@Override
	public String toString() {
		return getId();
	}

	@Override
	public int compareTo(BConditionType o) {
		return String.CASE_INSENSITIVE_ORDER.compare(getId(), o.getId());
	}

}
