/**
 * Some parts of this code were found on the internet from an old plugin named "ZQuest"
 */

package com.guillaumevdn.gcore.lib.npc;

import java.util.UUID;

import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

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
	public void refresh() {
		// last update
		if (lastUpdate + 60000L > System.currentTimeMillis()) {
			return;
		}
		lastUpdate = System.currentTimeMillis();
		// refresh
		Object[] skinPropandName = MojangsterAPI.getSkinPropandName(uuid);
		if (skinPropandName == null) {
			return;
		}
		name = (String) skinPropandName[0];
		// set data
		setSkinData((Multimap<String, WrappedSignedProperty>) skinPropandName[1]);
	}

	private void setSkinData(Multimap<String, WrappedSignedProperty> multimap) {
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
