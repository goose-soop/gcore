package be.pyrrh4.pyrcore.lib.loadable.setting.complex;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import be.pyrrh4.pyrcore.PCLocale;
import be.pyrrh4.pyrcore.lib.loadable.Loadable;
import be.pyrrh4.pyrcore.lib.loadable.setting.SettingEnum;
import be.pyrrh4.pyrcore.lib.loadable.setting.SettingString;
import be.pyrrh4.pyrcore.lib.material.Mat;
import be.pyrrh4.pyrcore.lib.util.Utils;

public class MobSetting extends Loadable<MobSetting> {

	// base
	public MobSetting(Loadable<?> parent, String id, boolean mandatory, Mat icon, List<String> description) {
		super(parent, id, mandatory, icon, description);
		registerSetting(new SettingEnum<EntityType>("type", null, false, EntityType.class, PCLocale.GUI_GENERIC_EDITOR_MOB_TYPELORE.getLines()));
		registerSetting(new SettingString("name", null, false, PCLocale.GUI_GENERIC_EDITOR_MOB_NAMELORE.getLines()));
	}

	// methods
	public boolean isValid(Entity entity, Player player) {
		EntityType type = getSettingEnum("type", EntityType.class).getParsed(player);
		String name = getSettingString("name").getParsed(player);
		return entity != null && (type == null ? true : type.equals(entity.getType())) && (name == null ? true : name.equals(entity.getCustomName()));
	}

	public Entity spawn(Location location, Player player) {
		EntityType type = getSettingEnum("type", EntityType.class).getParsed(player);
		String name = getSettingString("name").getParsed(player);
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
	private static final List<EntityType> mobTypes = new ArrayList<EntityType>();

	static {
		List<String> ok = Utils.asList("ARMOR_STAND", "BAT", "BLAZE", "BOAT", "CAVE_SPIDER", "CHICKEN", "COD", "COW", "CREEPER", "DOLPHIN", "DONKEY", "ELDER_GUARDIAN", "ENDER_CRYSTAL", "ENDER_DRAGON", "ENDERMAN", "ENDERMITE", "EVOKER", "EXPERIENCE_ORB", "GHAST", "GIANT", "GUARDIAN", "HORSE", "HUSK", "ILLUSIONER", "IRON_GOLEM", "LLAMA", "MAGMA_CUBE", "MINECART", "MINECART_CHEST", "MINECART_COMMAND", "MINECART_FURNACE", "MINECART_HOPPER", "MINECART_MOB_SPAWNER", "MINECART_TNT", "MULE", "MUSHROOM_COW", "OCELOT", "PARROT", "PHANTOM", "PIG", "PIG_ZOMBIE", "PLAYER", "POLAR_BEAR", "PRIMED_TNT", "PUFFERFISH", "RABBIT", "SALMON", "SHEEP", "SHULKER", "SILVERFISH", "SKELETON", "SKELETON_HORSE", "SLIME", "SNOWMAN", "SPIDER", "SQUID", "STRAY", "THROWN_EXP_BOTTLE", "TROPICAL_FISH", "TURTLE", "VEX", "VILLAGER", "VINDICATOR", "WITCH", "WITHER", "WITHER_SKELETON", "WOLF", "ZOMBIE", "ZOMBIE_HORSE", "ZOMBIE_VILLAGER");
		for (EntityType type : EntityType.values()) {
			if (ok.contains(type.toString())) {
				mobTypes.add(type);
			}
		}
	}

}
