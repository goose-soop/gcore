package com.guillaumevdn.gcore.lib.npc.behavior.event;

import java.lang.reflect.Constructor;
import java.util.List;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.npc.behavior.event.type.EventBehaviorCall;
import com.guillaumevdn.gcore.lib.npc.behavior.event.type.EventPlayerAttack;
import com.guillaumevdn.gcore.lib.npc.behavior.event.type.EventTimer;
import com.guillaumevdn.gcore.lib.parseable.ConfigData;
import com.guillaumevdn.gcore.lib.parseable.Parseable;
import com.guillaumevdn.gcore.lib.util.ServerVersion;
import com.guillaumevdn.gcore.lib.util.Utils;

public final class BEventType implements Comparable<BEventType> {

	// special
	public static final BEventType BEHAVIOR_CALL = registerType("BEHAVIOR_CALL", EventBehaviorCall.class);
	public static final BEventType PLAYER_ATTACK = registerType("PLAYER_ATTACK", EventPlayerAttack.class);
	public static final BEventType TIMER = registerType("TIMER", EventTimer.class);

	// registration
	public static BEventType registerType(String id, Class<? extends BEvent> objectClass) {
		return registerType(id, objectClass, ServerVersion.UNSUPPORTED, ServerVersion.HIGHEST);
	}

	public static BEventType registerType(String id, Class<? extends BEvent> objectClass, String... requiredPlugins) {
		return registerType(id, objectClass, ServerVersion.UNSUPPORTED, ServerVersion.HIGHEST, requiredPlugins);
	}

	public static BEventType registerType(String id, Class<? extends BEvent> objectClass, ServerVersion minVersion, String... requiredPlugins) {
		return registerType(id, objectClass, minVersion, ServerVersion.HIGHEST, requiredPlugins);
	}

	public static BEventType registerType(String id, Class<? extends BEvent> objectClass, ServerVersion minVersion, ServerVersion maxVersion, String... requiredPlugins) {
		id = id.toUpperCase();
		BEventType type = new BEventType(id, objectClass, minVersion, maxVersion, requiredPlugins);
		GCore.inst().getNpcManager().getBehaviorEventTypes().put(id, type);
		return type;
	}

	public static BEventType valueOf(String id) {
		return GCore.inst().getNpcManager().getBehaviorEventTypes().get(id.toUpperCase());
	}

	public static List<BEventType> values() {
		return Utils.asList(GCore.inst().getNpcManager().getBehaviorEventTypes().values());
	}

	// base
	private String id;
	private Class<? extends BEvent> objectClass;
	private ServerVersion minVersion, maxVersion;
	private List<String> requiredPlugins;

	private BEventType(String id, Class<? extends BEvent> objectClass, ServerVersion minVersion, ServerVersion maxVersion, String... requiredPlugins) {
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

	public Class<? extends BEvent> getObjectClass() {
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
	public BEventType unregister() {
		GCore.inst().getNpcManager().getBehaviorEventTypes().remove(id.toUpperCase());
		return null;
	}

	public BEvent createNew(String id, Parseable parent, ConfigData data, boolean loadOrSave, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		// invalid version
		if (!ServerVersion.CURRENT.isAtLeast(getMinVersion())) {
			data.log("behavior event type " + getId() + " requires at least server version " + getMinVersion().getName());
			return null;
		}
		if (!ServerVersion.CURRENT.isOrLess(getMaxVersion())) {
			data.log("behavior event type " + getId() + " supports at max server version " + getMaxVersion().getName());
			return null;
		}
		// invalid plugins
		for (String requiredPlugin : this.getRequiredPlugins()) {
			if (!Utils.isPluginEnabled(requiredPlugin)) {
				data.log("behavior event type " + getId() + " requires plugin " + requiredPlugin + " to be enabled");
				return null;
			}
		}
		// create
		try {
			// create instance
			Constructor<? extends BEvent> constructor = getObjectClass().getConstructor(String.class, Parseable.class, boolean.class, int.class, Mat.class, List.class);
			BEvent component = constructor.newInstance(id, parent, mandatory, editorSlot, editorIcon, editorDescription);
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
			if (data != null) data.log("unknown error when creating behavior event " + id + " (" + getObjectClass().getName() + ") with type " + getId());
			exception.printStackTrace();
			return null;
		}
	}

	// overriden methods
	@Override
	public boolean equals(Object obj) {
		return Utils.instanceOf(obj, BEventType.class) && getId().equalsIgnoreCase(((BEventType) obj).getId());
	}

	@Override
	public String toString() {
		return getId();
	}

	@Override
	public int compareTo(BEventType o) {
		return String.CASE_INSENSITIVE_ORDER.compare(getId(), o.getId());
	}

}
