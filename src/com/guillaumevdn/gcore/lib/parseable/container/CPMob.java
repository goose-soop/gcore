package com.guillaumevdn.gcore.lib.parseable.container;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.GLocale;
import com.guillaumevdn.gcore.lib.material.Mat;
import com.guillaumevdn.gcore.lib.parseable.ContainerParseable;
import com.guillaumevdn.gcore.lib.parseable.Parseable;
import com.guillaumevdn.gcore.lib.parseable.editor.EditorGUI;
import com.guillaumevdn.gcore.lib.parseable.primitive.PPEnum;
import com.guillaumevdn.gcore.lib.parseable.primitive.PPString;

public class CPMob extends ContainerParseable {

	// base
	private PPEnum<EntityType> type = addComponent(new PPEnum<EntityType>("type", this, null, EntityType.class, "mob type", false, 0, EditorGUI.ICON_MOB, GLocale.GUI_GENERIC_EDITOR_MOB_TYPELORE.getLines()));
	private PPString name = addComponent(new PPString("name", this, null, false, 1, EditorGUI.ICON_STRING, GLocale.GUI_GENERIC_EDITOR_MOB_NAMELORE.getLines()));

	public CPMob(String id, Parseable parent, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, "mob", mandatory, editorSlot, editorIcon, editorDescription);
	}

	// get
	public PPEnum<EntityType> getType() {
		return type;
	}

	public EntityType getType(Player parser) {
		return type.getParsedValue(parser);
	}

	public PPString getName() {
		return name;
	}

	public String getName(Player parser) {
		return name.getParsedValue(parser);
	}

	// methods
	public boolean isValid(Entity entity, Player parser) {
		// has type and/or name
		EntityType type = getType(parser);
		String name = getName(parser);
		if (type != null || name != null) {
			return entity != null && (type != null ? type.equals(entity.getType()) : true) && (name != null ? name.equals(entity.getCustomName()) : true);
		}
		// no type or name, it's valid
		return true;
	}

	public Entity spawn(Location location, Player parser) {
		EntityType type = getType(parser);
		String name = getName(parser);
		if (type != null) {
			Entity ent = location.getWorld().spawnEntity(location, type);
			if (name != null) {
				ent.setCustomName(name);
				ent.setCustomNameVisible(true);
			}
			return ent;
		}
		return null;
	}

	// clone
	protected CPMob() {
		super();
	}

	@Override
	public CPMob clone() {
		return (CPMob) super.clone();
	}

}
