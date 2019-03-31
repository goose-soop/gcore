package be.pyrrh4.pyrcore.lib.npc;

import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import be.pyrrh4.pyrcore.PCLocale;
import be.pyrrh4.pyrcore.lib.gui.ItemData;
import be.pyrrh4.pyrcore.lib.material.Mat;
import be.pyrrh4.pyrcore.lib.parseable.ContainerParseable;
import be.pyrrh4.pyrcore.lib.parseable.Parseable;
import be.pyrrh4.pyrcore.lib.parseable.container.CPItem;
import be.pyrrh4.pyrcore.lib.parseable.editor.EditorGUI;
import be.pyrrh4.pyrcore.lib.parseable.primitive.PPBoolean;
import be.pyrrh4.pyrcore.lib.parseable.primitive.PPDouble;
import be.pyrrh4.pyrcore.lib.parseable.primitive.PPEnumList;
import be.pyrrh4.pyrcore.lib.parseable.primitive.PPLocation;
import be.pyrrh4.pyrcore.lib.parseable.primitive.PPString;
import be.pyrrh4.pyrcore.lib.parseable.primitive.PPUUID;
import be.pyrrh4.pyrcore.lib.util.Utils;

public class NpcData extends ContainerParseable {

	// base
	private PPBoolean shown = addComponent(new PPBoolean("shown", this, "true", false, 0, EditorGUI.ICON_BOOLEAN, PCLocale.GUI_GENERIC_EDITOR_NPC_SHOWLORE.getLines()));
	private PPString name = addComponent(new PPString("name", this, "GuillaumeVDN", false, 1, EditorGUI.ICON_STRING, PCLocale.GUI_GENERIC_EDITOR_NPC_NAMELORE.getLines()));
	private PPUUID skin = addComponent(new PPUUID("skin", this, "6833a1c9-95a5-4c79-bc1f-45341ec0ff40", false, 2, EditorGUI.ICON_TECHNICAL, PCLocale.GUI_GENERIC_EDITOR_NPC_SKINLORE.getLines()));
	private PPLocation location = addComponent(new PPLocation("location", this, "world,0,0,0", true, 3, EditorGUI.ICON_LOCATION, PCLocale.GUI_GENERIC_EDITOR_NPC_LOCATIONLORE.getLines()));
	private PPDouble targetDistance = addComponent(new PPDouble("target_distance", this, "5", 0d, Double.MAX_VALUE, false, 4, EditorGUI.ICON_NUMBER, PCLocale.GUI_GENERIC_EDITOR_NPC_TARGEDISTANCELORE.getLines()));
	private PPEnumList<NpcStatus> status = addComponent(new PPEnumList<NpcStatus>("status", this, Utils.emptyList(), NpcStatus.class, "npc status", false, 5, EditorGUI.ICON_NUMBER, PCLocale.GUI_GENERIC_EDITOR_NPC_STATUSLORE.getLines()));
	private CPItem heldItem = addComponent(new CPItem("held_item", this, false, 9, EditorGUI.ICON_ITEM, PCLocale.GUI_GENERIC_EDITOR_NPC_STUFFLORE.getLines()));
	private CPItem heldItemOff = addComponent(new CPItem("held_item_off", this, false, 10, EditorGUI.ICON_ITEM, PCLocale.GUI_GENERIC_EDITOR_NPC_STUFFLORE.getLines()));
	private CPItem boots = addComponent(new CPItem("boots", this, false, 11, EditorGUI.ICON_ITEM, PCLocale.GUI_GENERIC_EDITOR_NPC_STUFFLORE.getLines()));
	private CPItem leggings = addComponent(new CPItem("leggings", this, false, 12, EditorGUI.ICON_ITEM, PCLocale.GUI_GENERIC_EDITOR_NPC_STUFFLORE.getLines()));
	private CPItem chestplate = addComponent(new CPItem("chestplate", this, false, 13, EditorGUI.ICON_ITEM, PCLocale.GUI_GENERIC_EDITOR_NPC_STUFFLORE.getLines()));
	private CPItem helmet = addComponent(new CPItem("helmet", this, false, 14, EditorGUI.ICON_ITEM, PCLocale.GUI_GENERIC_EDITOR_NPC_STUFFLORE.getLines()));

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

	public PPUUID getSkin() {
		return skin;
	}

	public UUID getSkin(Player parser) {
		return skin.getParsedValue(parser);
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

	// clone
	protected NpcData() {
	}

	@Override
	public NpcData clone() {
		return (NpcData) super.clone();
	}

}
