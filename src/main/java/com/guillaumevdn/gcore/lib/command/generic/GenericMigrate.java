package com.guillaumevdn.gcore.lib.command.generic;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.TextGeneric;
import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.command.Subcommand;
import com.guillaumevdn.gcore.lib.command.argument.ArgumentString;
import com.guillaumevdn.gcore.lib.migration.Migration;
import com.guillaumevdn.gcore.lib.object.NeedType;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public final class GenericMigrate extends Subcommand {

	private final GPlugin plugin;
	private final ArgumentString argVersion = addArgumentString(NeedType.REQUIRED, false, null, Text.of("migration version"));

	public GenericMigrate(GPlugin plugin) {
		super(false, plugin.getPermissionContainer().getAdminPermission(), TextGeneric.commandDescriptionGenericMigrate,
				ConfigGCore.genericCommandsAliasesMigrate);
		this.plugin = plugin;
	}

	@Override
	public void perform(CommandCall call) {
		String version = argVersion.get(call);
		String migrationClassName = "MigrationV" + version.replace('.', '_');
		Class<? extends Migration> migrationClass = ((GPlugin<?, ?>) plugin).getMigrations().stream()
				.filter(cls -> cls.getSimpleName().equals(migrationClassName)).findAny().orElse(null);

		if (migrationClass == null) {
			call.getSender().sendMessage("§cThere's no migration with version '" + version + "'.");
			return;
		}

		try {
			Migration migration = migrationClass.newInstance();
			migration.markNotMade();
			if (migration.process()) {
				call.getSender().sendMessage("§aMigration " + version + " was successful. See console for details.");
			} else {
				call.getSender().sendMessage("§cMigration " + version + " was not successful. See console for details.");
			}
		} catch (Throwable exception) {
			plugin.getMainLogger().error("Couldn't use migration command on migration " + migrationClass, exception);
			call.getSender().sendMessage("§cMigration " + version + " was not successful. See console for details.");
		}

	}

}
