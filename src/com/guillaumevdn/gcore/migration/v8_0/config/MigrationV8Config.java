package com.guillaumevdn.gcore.migration.v8_0.config;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mats;
import com.guillaumevdn.gcore.lib.compatibility.nbt.NBTCompound;
import com.guillaumevdn.gcore.lib.compatibility.nbt.NBTList;
import com.guillaumevdn.gcore.lib.configuration.YMLConfiguration;
import com.guillaumevdn.gcore.lib.configuration.file.YMLError;
import com.guillaumevdn.gcore.lib.configuration.file.node.SectionNode;
import com.guillaumevdn.gcore.lib.configuration.file.node.SectionNode.SectionNodeType;
import com.guillaumevdn.gcore.lib.file.FileUtils;
import com.guillaumevdn.gcore.lib.file.ResourceExtractor;
import com.guillaumevdn.gcore.lib.migration.BackupBehavior;
import com.guillaumevdn.gcore.lib.migration.Migration;
import com.guillaumevdn.gcore.lib.migration.SilentFail;
import com.guillaumevdn.gcore.lib.number.NumberUtils;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.reflection.Reflection;
import com.guillaumevdn.gcore.lib.reflection.ReflectionObject;
import com.guillaumevdn.gcore.lib.reflection.procedure.ReflectionProcedureFunction;
import com.guillaumevdn.gcore.lib.serialization.Serializer;
import com.guillaumevdn.gcore.lib.serialization.adapter.type.AdapterNBTCompound;
import com.guillaumevdn.gcore.lib.serialization.data.DataIO;
import com.guillaumevdn.gcore.migration.YMLMigrationReading;

/**
 * @author GuillaumeVDN
 */
public final class MigrationV8Config extends Migration {

	public MigrationV8Config() {
		super(GCore.inst(), "v7", "v7 -> v8 (config)", "data_v8/migrated_v8.0.0_config.DONTREMOVE");
	}

	@Override
	public boolean mustMigrate() {
		return super.mustMigrate() && Stream.of("data", "userdata", "texts.yml").anyMatch(path -> new File(getPluginFolder() + "/" + path).exists());
	}

	@Override
	protected void doMigrate() throws Throwable {
		// init serializers
		Serializer.BANNER_PATTERN.getGsonAdapter();

		// make sure all .yml files can be loaded with our new parser
		List<String> errors = new ArrayList<>();
		attemptFilesOperation("making sure all files can be loaded by the new YAML parser", "file", BackupBehavior.RESTORE, getPluginFolder(), FILTER_YML,
				file -> {
					try {
						new YMLConfiguration(getPlugin(), file);
					} catch (Throwable cause) {
						YMLError causeYML = ObjectUtils.findCauseOrNull(cause, YMLError.class);
						YMLError causeConfig = ObjectUtils.findCauseOrNull(cause, YMLError.class);
						List<String> errs = CollectionUtils.asList("This file can't be loaded with the new YAML parser : " + file.getPath());
						if (causeYML != null || causeConfig != null) {
							errs.add((causeYML != null ? causeYML : causeConfig).getMessage());
							errors.addAll(errs);
						} else {
							fail(errs, cause, BackupBehavior.RESTORE);
							throw new SilentFail();
						}
					}
				});

		attemptOperation("making sure all files can be loaded by the new YAML parser", BackupBehavior.RESTORE, () -> {
			if (!errors.isEmpty()) {
				fail(errors, null, BackupBehavior.RESTORE);
				throw new SilentFail();
			}
		});

		// copy the old files in the GCoreLegacy directory
		if (Stream.of("SupremeShops", "BettingGames", "Potatoes", "CustomCommands", "GParticles", "GSlotMachine").anyMatch(pl -> new File(getPluginFolder().getParentFile() + "/" + pl).exists())) {
			attemptOperation("copying current files to legacy directory for other plugins support", BackupBehavior.RESTORE, () -> {
				File legacyBase = new File(getPluginFolder().getParentFile() + "/GCoreLegacy");
				FileUtils.delete(legacyBase);
				FileUtils.copy(getPluginFolder(), legacyBase);
			});
		}

		// extract new default configs
		attemptOperation("extracting default configs", BackupBehavior.RESTORE, () -> {
			if (!FileUtils.delete(getPluginFolder())) {
				fail("couldn't delete current folder", null, BackupBehavior.RESTORE);
				throw new SilentFail();
			}
			new ResourceExtractor(getPlugin(), getPluginFolder(), "resources/").extract(false, true);
		});

		// load materials
		loadMaterialsForMigration(this);

		// config.yml
		attemptOperation("converting config.yml", BackupBehavior.RESTORE, () -> {
			File oldConfigFile = new File(getBackupFolder() + "/config.yml");
			if (oldConfigFile.exists()) {
				YMLConfiguration oldConfig = new YMLConfiguration(getPlugin(), oldConfigFile);
				YMLConfiguration config = new YMLConfiguration(getPlugin(), new File(getPluginFolder() + "/config.yml"));
				// data
				if (oldConfig.contains("data.mysql.host")) {
					config.getBackingYML().getBase().insertBeforeFirstConfigNode(new SectionNode(config.getBackingYML().getBase(), "mysql", SectionNodeType.REGULAR, null));
					config.write("mysql.host", oldConfig.readString("data.mysql.host", null));
					config.write("mysql.name", oldConfig.readString("data.mysql.name", null));
					config.write("mysql.user", oldConfig.readString("data.mysql.user", null));
					config.write("mysql.pass", oldConfig.readString("data.mysql.pass", null));
					config.write("mysql.args", oldConfig.readString("data.mysql.args", null));
				}
				if (oldConfig.readString("data.backend", "").equalsIgnoreCase("MYSQL")) {
					config.write("data_backend.gcore_statistics_v8", "MYSQL");
					config.write("data_backend.gcore_users_npcs_v8", "MYSQL");
				}
				config.save();
			}
		});

		// config.yml
		attemptOperation("copying npcs.yml", BackupBehavior.RESTORE, () -> {
			File npcs = new File(getBackupFolder() + "/npcs.yml");
			if (npcs.exists() && !new YMLConfiguration(getPlugin(), npcs).readKeysForSection("").isEmpty()) {
				File target = new File(getPluginFolder() + "/npcs.yml");
				FileUtils.delete(target);
				FileUtils.copy(npcs, target);
			}
		});
	}

	public static void loadMaterialsForMigration(Migration migration) throws Throwable {
		if (ConfigGCore.mats != null) {
			return;
		}
		// load materials
		migration.attemptOperation("loading materials", BackupBehavior.RESTORE, () -> {
			try {
				ConfigGCore.mats = ConfigGCore.loadVariants(new Mats(false, true));
			} catch (Throwable cause) {
				YMLError causeYML = ObjectUtils.findCauseOrNull(cause, YMLError.class);
				YMLError causeConfig = ObjectUtils.findCauseOrNull(cause, YMLError.class);
				List<String> errors = CollectionUtils.asList("Couldn't load materials");
				if (causeYML != null || causeConfig != null) {
					errors.add((causeYML != null ? causeYML : causeConfig).getMessage());
				}
				migration.fail(errors, causeYML != null || causeConfig != null ? null : cause, BackupBehavior.RESTORE);
				throw new SilentFail();
			}
		});
		migration.attemptOperation("loading common materials", BackupBehavior.RESTORE, () -> {
			try {
				CommonMats.init();
			} catch (Throwable cause) {
				YMLError causeYML = ObjectUtils.findCauseOrNull(cause, YMLError.class);
				YMLError causeConfig = ObjectUtils.findCauseOrNull(cause, YMLError.class);
				List<String> errors = CollectionUtils.asList("Couldn't load materials");
				if (causeYML != null || causeConfig != null) {
					errors.add((causeYML != null ? causeYML : causeConfig).getMessage());
				}
				migration.fail(errors, causeYML != null || causeConfig != null ? null : cause, BackupBehavior.RESTORE);
				throw new SilentFail();
			}
		});
	}

	public static String secondsToDuration(String seconds) {
		if (seconds == null) {
			return null;
		}
		Integer nb = NumberUtils.integerOrNull(seconds);
		return nb != null ? millisToDuration(nb * 1000L) : seconds + " SECOND";
	}

	public static String ticksToDuration(String ticks) {
		if (ticks == null) {
			return null;
		}
		Integer nb = NumberUtils.integerOrNull(ticks);
		return nb != null ? millisToDuration(nb * 20L * 1000L) : ticks + " TICK";
	}

	public static String minutesToDuration(String minutes) {
		if (minutes == null) {
			return null;
		}
		Integer nb = NumberUtils.integerOrNull(minutes);
		return nb != null ? millisToDuration(nb * 60L * 1000L) : minutes + " MINUTE";
	}

	public static String millisToDuration(long milliseconds) {
		if (milliseconds < 0L) {
			return null;
		}
		if (milliseconds < 1000L || milliseconds % 1000L != 0L) {
			return milliseconds + " MILLISECOND";
		} else if (milliseconds / 1000L % 60L != 0L) {
			return (milliseconds / 1000L) + " SECOND";
		} else if (milliseconds / 1000L / 60L % 60L != 0L) {
			return (milliseconds / 1000L / 60L) + " MINUTE";
		} else if (milliseconds / 1000L / 60L / 60L % 24L != 0L) {
			return (milliseconds / 1000L / 60L / 60L) + " HOUR";
		} else {
			return (milliseconds / 1000L / 60L / 60L / 24L) + " DAY";
		}
	}

	public static void migrateItem(Migration migration, YMLMigrationReading src, String o, YMLConfiguration target, String n, boolean allowAmount) {
		if (!src.contains(o)) {
			return;
		}

		// base
		String potion = src.readString(o + ".potion", null);
		String[] potionSplit = potion == null ? null : potion.split(",");
		if (potionSplit != null) {
			target.write(n + ".type", src.readString(o + ".type", Boolean.parseBoolean(potionSplit[3]) ? "SPLASH_POTION" : "POTION"));
		}
		else {
			target.write(n + ".type", src.readString(o + ".type", null));
			target.write(n + ".durability", src.readString(o + ".durability", null));
		}
		if (allowAmount) {
			target.write(n + ".amount", src.readString(o + ".amount", null));
		}

		// meta
		target.write(n + ".unbreakable", src.readString(o + ".unbreakable", null));
		for (String key : src.readKeysForSection(o + ".enchants")) {
			target.write(n + ".enchantments." + src.readString(o + ".enchants." + key + ".type", null), src.readString(o + ".enchants." + key + ".level", null));
		}
		target.write(n + ".name", src.readString(o + ".name", null));
		target.write(n + ".lore", src.readStringList(o + ".lore", null));

		// specific meta
		if (potionSplit != null) {
			target.write(n + ".potion_type", potionSplit[0]);
			if (Boolean.parseBoolean(potionSplit[2])) {
				target.write(n + ".potion_extra", "EXTENDED");
			} else {
				Integer level = NumberUtils.integerOrNull(potionSplit[1]);
				if (level != null && level > 1) {
					target.write(n + ".potion_extra", "UPGRADED");
				}
			}
		}
		int i = 0;
		for (String effect : src.readStringList(o + ".effects", new ArrayList<>())) {
			String[] split = effect.split(",");
			if (split.length == 3) {
				String path = n + ".custom_effects." + (++i);
				target.write(path + ".type", split[0]);
				target.write(path + ".amplifier", split[1]);
				target.write(path + ".duration", ticksToDuration(split[2]));
			}
		}

		// nbt
		String nbtString = src.readString(o + ".nbt", null);
		if (nbtString != null) {
			try {
				DataIO nbtWriter = new DataIO();
				NBTCompound nbt = new NBTCompound(null, "root", 0, (nbtString.toLowerCase().startsWith("custom:") ? PARSE_MOJANGSON : UNSERIALIZE_NBT).process(nbtString));
				// skull ?
				try {
					if (nbt.hasKey("SkullOwner")) {
						NBTCompound skull = nbt.getCompound("SkullOwner");
						if (skull.hasKey("Id")) {
							target.write(n + ".owner_id", skull.getString("Id"));
						}
						if (skull.hasKey("Name")) {
							target.write(n + ".owner_name", skull.getString("Name"));
						}
						if (skull.hasKey("Properties")) {
							NBTCompound properties = skull.getCompound("Properties");
							if (properties.hasKey("textures")) {
								NBTList textures = properties.getList("textures");
								if (textures.size() != 0) {
									NBTCompound prop = textures.getCompound(0);
									target.write(n + ".skin_data", prop.getString("Value"));
									target.write(n + ".skin_signature", prop.getString("Signature"));
								}
							}
						}
					}
				} catch (Throwable ignored) {}
				// write nbt
				AdapterNBTCompound.INSTANCE.write(nbt, nbtWriter);
				SectionNode parent = target.getBackingYML().mkdirs(n);
				parent.setConfigNode(nbtWriter.toYML(parent, "nbt", false));
			} catch (Throwable exception) {
				migration.error("Couldn't migrate item nbt '" + nbtString + "'", exception);
			}
		}
	}

	public static final ReflectionProcedureFunction<String, ReflectionObject> PARSE_MOJANGSON = new ReflectionProcedureFunction<String, ReflectionObject>()
			.setIf(Version.ATLEAST_1_7_9, serialized -> Reflection.invokeNmsMethod("MojangsonReplacer", "parse", null, serialized))
			.orElse(serialized -> Reflection.invokeNmsMethod("MojangsonReplacer", "a", null, serialized));

	public static final ReflectionProcedureFunction<String, ReflectionObject> UNSERIALIZE_NBT = new ReflectionProcedureFunction<String, ReflectionObject>()
			.set(serialized -> {
				if (serialized != null) {
					ByteArrayInputStream in = new ByteArrayInputStream(Reflection.invokeMethod(Version.ATLEAST_1_14 ? "org.bukkit.craftbukkit.libs.org.apache.commons.codec.binary.Base64" : "org.apache.commons.codec.binary.Base64", "decodeBase64", null, serialized).get(new byte[0].getClass()));
					return Reflection.invokeNmsMethod("NBTCompressedStreamTools", "a", null, in);
				}
				return null;
			});

}
