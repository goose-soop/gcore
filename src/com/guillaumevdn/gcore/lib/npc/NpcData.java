package com.guillaumevdn.gcore.lib.npc;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.GLocale;
import com.guillaumevdn.gcore.lib.gui.ItemData;
import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.parseable.ContainerParseable;
import com.guillaumevdn.gcore.lib.parseable.Parseable;
import com.guillaumevdn.gcore.lib.parseable.container.CPItem;
import com.guillaumevdn.gcore.lib.parseable.editor.EditorGUI;
import com.guillaumevdn.gcore.lib.parseable.list.LPString;
import com.guillaumevdn.gcore.lib.parseable.primitive.PPBoolean;
import com.guillaumevdn.gcore.lib.parseable.primitive.PPDouble;
import com.guillaumevdn.gcore.lib.parseable.primitive.PPEnumList;
import com.guillaumevdn.gcore.lib.parseable.primitive.PPLocation;
import com.guillaumevdn.gcore.lib.parseable.primitive.PPString;
import com.guillaumevdn.gcore.lib.parseable.primitive.PPStringList;
import com.guillaumevdn.gcore.lib.util.Utils;

public class NpcData extends ContainerParseable {

	// base
	private PPBoolean shown = addComponent(new PPBoolean("shown", this, "true", false, 0, EditorGUI.ICON_BOOLEAN, GLocale.GUI_GENERIC_EDITOR_NPC_SHOWLORE.getLines()));
	private PPString name = addComponent(new PPString("name", this, "GuillaumeVDN", false, 1, EditorGUI.ICON_STRING, GLocale.GUI_GENERIC_EDITOR_NPC_NAMELORE.getLines()));
	private PPString skinData = addComponent(new PPString("skin_data", this, null, false, 2, EditorGUI.ICON_TECHNICAL, GLocale.GUI_GENERIC_EDITOR_NPC_SKINDATALORE.getLines()));
	private PPString skinSignature = addComponent(new PPString("skin_signature", this, null, false, 3, EditorGUI.ICON_TECHNICAL, GLocale.GUI_GENERIC_EDITOR_NPC_SKINSIGNATURELORE.getLines()));
	private PPLocation location = addComponent(new PPLocation("location", this, "world,0,0,0", true, 4, EditorGUI.ICON_LOCATION, GLocale.GUI_GENERIC_EDITOR_NPC_LOCATIONLORE.getLines()));
	private PPDouble targetDistance = addComponent(new PPDouble("target_distance", this, "5", 0d, Double.MAX_VALUE, false, 5, EditorGUI.ICON_NUMBER, GLocale.GUI_GENERIC_EDITOR_NPC_TARGETDISTANCELORE.getLines()));
	private PPEnumList<NpcStatus> status = addComponent(new PPEnumList<NpcStatus>("status", this, Utils.emptyList(), NpcStatus.class, "npc status", false, 6, EditorGUI.ICON_NUMBER, GLocale.GUI_GENERIC_EDITOR_NPC_STATUSLORE.getLines()));
	private CPItem heldItem = addComponent(new CPItem("held_item", this, false, 9, EditorGUI.ICON_ITEM, GLocale.GUI_GENERIC_EDITOR_NPC_STUFFLORE.getLines()));
	private CPItem heldItemOff = addComponent(new CPItem("held_item_off", this, false, 10, EditorGUI.ICON_ITEM, GLocale.GUI_GENERIC_EDITOR_NPC_STUFFLORE.getLines()));
	private CPItem boots = addComponent(new CPItem("boots", this, false, 11, EditorGUI.ICON_ITEM, GLocale.GUI_GENERIC_EDITOR_NPC_STUFFLORE.getLines()));
	private CPItem leggings = addComponent(new CPItem("leggings", this, false, 12, EditorGUI.ICON_ITEM, GLocale.GUI_GENERIC_EDITOR_NPC_STUFFLORE.getLines()));
	private CPItem chestplate = addComponent(new CPItem("chestplate", this, false, 13, EditorGUI.ICON_ITEM, GLocale.GUI_GENERIC_EDITOR_NPC_STUFFLORE.getLines()));
	private CPItem helmet = addComponent(new CPItem("helmet", this, false, 14, EditorGUI.ICON_ITEM, GLocale.GUI_GENERIC_EDITOR_NPC_STUFFLORE.getLines()));
	private LPString variables = addComponent(new LPString("variables", this, false, 15, EditorGUI.ICON_NUMBER, GLocale.GUI_GENERIC_EDITOR_NPC_VARIABLESLORE.getLines()));
	private PPStringList behaviors = addComponent(new PPStringList("behaviors", this, Utils.emptyList(), false, 16, EditorGUI.ICON_TECHNICAL, GLocale.GUI_GENERIC_EDITOR_NPC_BEHAVIORSLORE.getLines()));

	public NpcData(String id, Parseable parent, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, "npc data", mandatory, editorSlot, editorIcon, editorDescription);
	}

	// get
	public PPBoolean getShown() {
		return shown;
	}

	public Boolean getShown(Player parser) {
		return shown.getParsedValue(parser);
	}

	public PPString getName() {
		return name;
	}

	public String getName(Player parser) {
		return name.getParsedValue(parser);
	}

	public PPString getSkinData() {
		return skinData;
	}

	public String getSkinData(Player parser) {
		return skinData.getParsedValue(parser);
	}

	public PPString getSkinSignature() {
		return skinSignature;
	}

	public String getSkinSignature(Player parser) {
		return skinSignature.getParsedValue(parser);
	}

	public PPLocation getLocation() {
		return location;
	}

	public Location getLocation(Player parser) {
		return location.getParsedValue(parser);
	}

	public PPDouble getTargetDistance() {
		return targetDistance;
	}

	public Double getTargetDistance(Player parser) {
		return targetDistance.getParsedValue(parser);
	}

	public PPEnumList<NpcStatus> getStatus() {
		return status;
	}

	public List<NpcStatus> getStatus(Player parser) {
		return status.getParsedValue(parser);
	}

	public CPItem getHeldItem() {
		return heldItem;
	}

	public ItemData getHeldItem(Player parser) {
		return heldItem.getParsedValue(parser);
	}

	public CPItem getHeldItemOff() {
		return heldItemOff;
	}

	public ItemData getHeldItemOff(Player parser) {
		return heldItemOff.getParsedValue(parser);
	}

	public CPItem getBoots() {
		return boots;
	}

	public ItemData getBoots(Player parser) {
		return boots.getParsedValue(parser);
	}

	public CPItem getLeggings() {
		return leggings;
	}

	public ItemData getLeggings(Player parser) {
		return leggings.getParsedValue(parser);
	}

	public CPItem getChestplate() {
		return chestplate;
	}

	public ItemData getChestplate(Player parser) {
		return chestplate.getParsedValue(parser);
	}

	public CPItem getHelmet() {
		return helmet;
	}

	public ItemData getHelmet(Player parser) {
		return helmet.getParsedValue(parser);
	}

	public LPString getVariables() {
		return variables;
	}

	public PPStringList getBehaviors() {
		return behaviors;
	}

	public List<String> getBehaviors(Player parser) {
		return behaviors.getParsedValue(parser);
	}

	// clone
	protected NpcData() {
	}

	@Override
	public NpcData clone() {
		return (NpcData) super.clone();
	}

}
