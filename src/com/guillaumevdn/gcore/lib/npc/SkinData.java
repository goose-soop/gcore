/**
 * Parts of this code was from ZQuest, it was refactored by GuillaumeVDN
 */

package com.guillaumevdn.gcore.lib.npc;

import java.util.UUID;

import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.guillaumevdn.gcore.GCore;

public class SkinData {

	// base
	private UUID uuid;
	private long lastUpdate = 0L;
	private WrappedProperty skinData = null;
	private String name = null;

	public SkinData(UUID uuid) {
		this.uuid = uuid;
		refresh();
	}

	// get
	public UUID getUUID() {
		return uuid;
	}

	public String getName() {
		return name;
	}

	// methods
	private static final long MIN_DELAY_MILLIS = 60L * 1000L;
	public void refresh() {
		// last update
		if (System.currentTimeMillis() - lastUpdate < MIN_DELAY_MILLIS) {
			return;
		}
		lastUpdate = System.currentTimeMillis();
		// refresh
		Object[] skinPropandName = null;
		try {
			skinPropandName = MojangsterAPI.getSkinPropandName(uuid);
		} catch (Throwable exception) {
			exception.printStackTrace();
		}
		// push ; we made a request to Mojang, so the delay needs to be saved
		GCore.inst().getData().getNpcSkins().set(uuid, this);
		if (skinPropandName == null || skinPropandName.length < 2) {
			return;
		}
		name = (String) skinPropandName[0];
		// set data
		Multimap<String, WrappedSignedProperty> multimap = (Multimap<String, WrappedSignedProperty>) skinPropandName[1];
		if (multimap == null || !multimap.containsKey("textures")) {
			skinData = null;
			return;
		}
		WrappedSignedProperty wrappedSignedProperty = multimap.get("textures").iterator().next();
		if (wrappedSignedProperty == null) {
			skinData = null;
			return;
		}
		skinData = new WrappedProperty(wrappedSignedProperty.getName(), wrappedSignedProperty.getValue(), wrappedSignedProperty.getSignature());
		// log
		GCore.inst().debug("Refreshed NPC skin " + uuid);
	}

	public Multimap<String, WrappedSignedProperty> getSkinData() {
		if (skinData == null) {
			return null;
		}
		LinkedHashMultimap create = LinkedHashMultimap.create();
		create.put("textures", skinData.toProperty());
		return create;
	}

	// property
	public static class WrappedProperty {

		// base
		private String name;
		private String value;
		private String signature;

		public WrappedProperty(String name, String value, String signature) {
			this.name = name;
			this.value = value;
			this.signature = signature;
		}

		// methods
		public void setProperty(String name, String value, String signature) {
			this.name = name;
			this.value = value;
			this.signature = signature;
		}

		public WrappedSignedProperty toProperty() {
			return new WrappedSignedProperty(name, value, signature);
		}

	}

}
