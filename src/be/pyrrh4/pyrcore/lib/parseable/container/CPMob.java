package be.pyrrh4.pyrcore.lib.parseable.container;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import be.pyrrh4.pyrcore.PCLocale;
import be.pyrrh4.pyrcore.lib.material.Mat;
import be.pyrrh4.pyrcore.lib.parseable.ContainerParseable;
import be.pyrrh4.pyrcore.lib.parseable.Parseable;
import be.pyrrh4.pyrcore.lib.parseable.editor.EditorGUI;
import be.pyrrh4.pyrcore.lib.parseable.primitive.PPEnum;
import be.pyrrh4.pyrcore.lib.parseable.primitive.PPInteger;
import be.pyrrh4.pyrcore.lib.parseable.primitive.PPString;
import be.pyrrh4.pyrcore.lib.util.Utils;

public class CPMob extends ContainerParseable {

	// base
	private PPEnum<EntityType> type = addComponent(new PPEnum<EntityType>("type", this, null, EntityType.class, "mob type", false, 0, EditorGUI.ICON_MOB, PCLocale.GUI_GENERIC_EDITOR_MOB_TYPELORE.getLines()));
	private PPString name = addComponent(new PPString("name", this, null, false, 1, EditorGUI.ICON_STRING, PCLocale.GUI_GENERIC_EDITOR_MOB_NAMELORE.getLines()));
	private PPInteger amount = addComponent(new PPInteger("amount", this, "1", 1, Integer.MAX_VALUE, false, 2, EditorGUI.ICON_NUMBER, PCLocale.GUI_GENERIC_EDITOR_MOB_AMOUNTLORE.getLines()));

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

	public PPInteger getAmount() {
		return amount;
	}

	public Integer getAmount(Player parser) {
		return amount.getParsedValue(parser);
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

	// static fields
	private static final List<EntityType> mobTypes = new ArrayList<EntityType>();// TODO : use that (for the editor)

	static {
		List<String> ok = Utils.asList("ARMOR_STAND", "BAT", "BLAZE", "BOAT", "CAVE_SPIDER", "CHICKEN", "COD", "COW", "CREEPER", "DOLPHIN", "DONKEY", "ELDER_GUARDIAN", "ENDER_CRYSTAL", "ENDER_DRAGON", "ENDERMAN", "ENDERMITE", "EVOKER", "EXPERIENCE_ORB", "GHAST", "GIANT", "GUARDIAN", "HORSE", "HUSK", "ILLUSIONER", "IRON_GOLEM", "LLAMA", "MAGMA_CUBE", "MINECART", "MINECART_CHEST", "MINECART_COMMAND", "MINECART_FURNACE", "MINECART_HOPPER", "MINECART_MOB_SPAWNER", "MINECART_TNT", "MULE", "MUSHROOM_COW", "OCELOT", "PARROT", "PHANTOM", "PIG", "PIG_ZOMBIE", "PLAYER", "POLAR_BEAR", "PRIMED_TNT", "PUFFERFISH", "RABBIT", "SALMON", "SHEEP", "SHULKER", "SILVERFISH", "SKELETON", "SKELETON_HORSE", "SLIME", "SNOWMAN", "SPIDER", "SQUID", "STRAY", "THROWN_EXP_BOTTLE", "TROPICAL_FISH", "TURTLE", "VEX", "VILLAGER", "VINDICATOR", "WITCH", "WITHER", "WITHER_SKELETON", "WOLF", "ZOMBIE", "ZOMBIE_HORSE", "ZOMBIE_VILLAGER");
		for (EntityType type : EntityType.values()) {
			if (ok.contains(type.toString())) {
				mobTypes.add(type);
			}
		}
	}

	// clone
	protected CPMob() {
		super();
	}

	@Override
	public CPMob clone() {
		// clone
		CPMob clone = (CPMob) super.clone();
		// clone properties
		clone.type = type.clone();
		clone.name = name.clone();
		clone.amount = amount.clone();
		// success
		return clone;
	}

}
