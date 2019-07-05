package com.guillaumevdn.gcore.lib.npc.behavior.action;

import java.lang.reflect.Constructor;
import java.util.List;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.npc.behavior.action.type.ActionNpcAnimate;
import com.guillaumevdn.gcore.lib.npc.behavior.action.type.ActionNpcHide;
import com.guillaumevdn.gcore.lib.npc.behavior.action.type.ActionNpcMoveTowardsPlayer;
import com.guillaumevdn.gcore.lib.npc.behavior.action.type.ActionNpcVariableModifyNumber;
import com.guillaumevdn.gcore.lib.npc.behavior.action.type.ActionNpcVariableMultiplyNumber;
import com.guillaumevdn.gcore.lib.npc.behavior.action.type.ActionNpcVariableSet;
import com.guillaumevdn.gcore.lib.npc.behavior.action.type.ActionPlayerHealthModify;
import com.guillaumevdn.gcore.lib.npc.behavior.action.type.ActionPlayerSendMessage;
import com.guillaumevdn.gcore.lib.parseable.ConfigData;
import com.guillaumevdn.gcore.lib.parseable.Parseable;
import com.guillaumevdn.gcore.lib.util.ServerVersion;
import com.guillaumevdn.gcore.lib.util.Utils;

public final class BActionType implements Comparable<BActionType> {

	// special
	public static final BActionType NPC_ANIMATE = registerType("NPC_ANIMATE", ActionNpcAnimate.class);
	public static final BActionType NPC_HIDE = registerType("NPC_HIDE", ActionNpcHide.class);
	public static final BActionType NPC_MOVE_TOWARDS_PLAYER = registerType("NPC_MOVE", ActionNpcMoveTowardsPlayer.class);
	public static final BActionType NPC_VARIABLE_MODIFY_NUMBER = registerType("NPC_VARIABLE_MODIFY_NUMBER", ActionNpcVariableModifyNumber.class);
	public static final BActionType NPC_VARIABLE_MULTIPLY_NUMBER = registerType("NPC_VARIABLE_MULTIPLY_NUMBER", ActionNpcVariableMultiplyNumber.class);
	public static final BActionType NPC_VARIABLE_SET = registerType("NPC_VARIABLE_SET", ActionNpcVariableSet.class);
	public static final BActionType PLAYER_HEALTH_MODIFY = registerType("PLAYER_HEALTH_MODIFY", ActionPlayerHealthModify.class);
	public static final BActionType PLAYER_SEND_MESSAGE = registerType("PLAYER_SEND_MESSAGE", ActionPlayerSendMessage.class);

	// registration
	public static BActionType registerType(String id, Class<? extends BAction> objectClass) {
		return registerType(id, objectClass, ServerVersion.UNSUPPORTED, ServerVersion.HIGHEST);
	}

	public static BActionType registerType(String id, Class<? extends BAction> objectClass, String... requiredPlugins) {
		return registerType(id, objectClass, ServerVersion.UNSUPPORTED, ServerVersion.HIGHEST, requiredPlugins);
	}

	public static BActionType registerType(String id, Class<? extends BAction> objectClass, ServerVersion minVersion, String... requiredPlugins) {
		return registerType(id, objectClass, minVersion, ServerVersion.HIGHEST, requiredPlugins);
	}

	public static BActionType registerType(String id, Class<? extends BAction> objectClass, ServerVersion minVersion, ServerVersion maxVersion, String... requiredPlugins) {
		id = id.toUpperCase();
		BActionType type = new BActionType(id, objectClass, minVersion, maxVersion, requiredPlugins);
		GCore.inst().getNpcManager().getBehaviorActionTypes().put(id, type);
		return type;
	}

	public static BActionType valueOf(String id) {
		return GCore.inst().getNpcManager().getBehaviorActionTypes().get(id.toUpperCase());
	}

	public static List<BActionType> values() {
		return Utils.asList(GCore.inst().getNpcManager().getBehaviorActionTypes().values());
	}

	// base
	private String id;
	private Class<? extends BAction> objectClass;
	private ServerVersion minVersion, maxVersion;
	private List<String> requiredPlugins;

	private BActionType(String id, Class<? extends BAction> objectClass, ServerVersion minVersion, ServerVersion maxVersion, String... requiredPlugins) {
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

	public Class<? extends BAction> getObjectClass() {
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
	public BActionType unregister() {
		GCore.inst().getNpcManager().getBehaviorActionTypes().remove(id.toUpperCase());
		return null;
	}

	public BAction createNew(String id, Parseable parent, ConfigData data, boolean loadOrSave, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		// invalid version
		if (!ServerVersion.CURRENT.isAtLeast(getMinVersion())) {
			data.log("behavior action type " + getId() + " requires at least server version " + getMinVersion().getName());
			return null;
		}
		if (!ServerVersion.CURRENT.isOrLess(getMaxVersion())) {
			data.log("behavior action type " + getId() + " supports at max server version " + getMaxVersion().getName());
			return null;
		}
		// invalid plugins
		for (String requiredPlugin : this.getRequiredPlugins()) {
			if (!Utils.isPluginEnabled(requiredPlugin)) {
				data.log("behavior action type " + getId() + " requires plugin " + requiredPlugin + " to be enabled");
				return null;
			}
		}
		// create
		try {
			// create instance
			Constructor<? extends BAction> constructor = getObjectClass().getConstructor(String.class, Parseable.class, boolean.class, int.class, Mat.class, List.class);
			BAction component = constructor.newInstance(id, parent, mandatory, editorSlot, editorIcon, editorDescription);
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
			if (data != null) data.log("unknown error when creating behavior action " + id + " (" + getObjectClass().getName() + ") with type " + getId());
			exception.printStackTrace();
			return null;
		}
	}

	// overriden methods
	@Override
	public boolean equals(Object obj) {
		return Utils.instanceOf(obj, BActionType.class) && getId().equalsIgnoreCase(((BActionType) obj).getId());
	}

	@Override
	public String toString() {
		return getId();
	}

	@Override
	public int compareTo(BActionType o) {
		return String.CASE_INSENSITIVE_ORDER.compare(getId(), o.getId());
	}

}
