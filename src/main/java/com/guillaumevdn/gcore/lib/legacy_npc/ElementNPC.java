package com.guillaumevdn.gcore.lib.legacy_npc;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.configuration.YMLConfiguration;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.SuperElement;
import com.guillaumevdn.gcore.lib.element.struct.container.ContainerElement;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementBoolean;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementDirectEnumList;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementDouble;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementEnumList;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementLocation;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementString;
import com.guillaumevdn.gcore.lib.element.type.container.ElementItem;
import com.guillaumevdn.gcore.lib.element.type.container.ElementItemMode;
import com.guillaumevdn.gcore.lib.string.Text;

public final class ElementNPC extends ContainerElement implements SuperElement {

	private ElementBoolean shown = addBoolean("shown", Need.optional(true), Text.of("§7To make this NPC visible by default"));
	private ElementString name = addString("name", Need.required(), Text.of("§7The name of this NPC"));
	private ElementString skinData = addString("skin_data", Need.optional(), Text.of("§7The skin data for this NPC"));
	private ElementString skinSignature = addString("skin_signature", Need.optional(), Text.of("§7The skin signature for this NPC"));
	private ElementLocation location = addLocation("location", Need.required(), Text.of("§7The default location of this NPC"));
	private ElementDouble targetDistance = addDouble("target_distance", Need.optional(5d), Text.of("§7In what range should the NPC look at players"));
	private ElementEnumList<NPCStatus> status = add(new ElementDirectEnumList<>(NPCStatus.class, this, "status", Need.optional(), Text.of("§7A bunch of status for this NPC")));
	private ElementItem heldItem = addItem("held_item", Need.optional(), ElementItemMode.BUILDABLE, Text.of("§7An item held in the hand"));
	private ElementItem heldItemOff = addItem("held_item_off", Need.optional(), ElementItemMode.BUILDABLE, Text.of("§7An item held in the off-hand"));
	private ElementItem boots = addItem("boots", Need.optional(), ElementItemMode.BUILDABLE, Text.of("§7A boots item"));
	private ElementItem leggings = addItem("leggings", Need.optional(), ElementItemMode.BUILDABLE, Text.of("§7A leggings item"));
	private ElementItem chestplate = addItem("chestplate", Need.optional(), ElementItemMode.BUILDABLE, Text.of("§7A chestplate item"));
	private ElementItem helmet = addItem("helmet", Need.optional(), ElementItemMode.BUILDABLE, Text.of("§7An helmet item"));

	public ElementNPC(String id) {
		super(null, id, Need.optional(), null);
	}

	// ----- super element
	private List<String> loadErrors = new ArrayList<>();
	private YMLConfiguration config = null;

	@Override public GPlugin getPlugin() { return GCore.inst(); }
	@Override public List<String> getLoadErrors() { return Collections.unmodifiableList(loadErrors); }
	@Override public File getOwnFile() { return null; }
	@Override public YMLConfiguration getConfiguration() { if (config == null) { reloadConfiguration(); } return config; }
	@Override public String getConfigurationPath() { return "npcs." + getId(); }
	@Override public void addLoadError(String error) { loadErrors.add(error); }
	@Override public void reloadConfiguration() { this.config = GCore.inst().loadConfigurationFile("npcs.yml"); }

	// ----- get
	public ElementBoolean getShown() {
		return shown;
	}

	public ElementString getName() {
		return name;
	}

	public ElementString getSkinData() {
		return skinData;
	}

	public ElementString getSkinSignature() {
		return skinSignature;
	}

	public ElementLocation getLocation() {
		return location;
	}

	public ElementDouble getTargetDistance() {
		return targetDistance;
	}

	public ElementEnumList<NPCStatus> getStatus() {
		return status;
	}

	public ElementItem getHeldItem() {
		return heldItem;
	}

	public ElementItem getHeldItemOff() {
		return heldItemOff;
	}

	public ElementItem getBoots() {
		return boots;
	}

	public ElementItem getLeggings() {
		return leggings;
	}

	public ElementItem getChestplate() {
		return chestplate;
	}

	public ElementItem getHelmet() {
		return helmet;
	}

	// ----- editor
	@Override
	public Mat editorIconType() {
		return CommonMats.PLAYER_HEAD;
	}

}
