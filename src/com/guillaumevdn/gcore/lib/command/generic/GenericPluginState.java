package com.guillaumevdn.gcore.lib.command.generic;

import java.util.ArrayList;
import java.util.List;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.TextGCore;
import com.guillaumevdn.gcore.TextGeneric;
import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.command.CommandCall;
import com.guillaumevdn.gcore.lib.command.Subcommand;
import com.guillaumevdn.gcore.lib.plugin.PluginUtils;
import com.guillaumevdn.gcore.lib.string.StringUtils;

/**
 * @author GuillaumeVDN
 */
public final class GenericPluginState extends Subcommand {

	private final GPlugin<?, ?> plugin;

	public GenericPluginState(GPlugin<?, ?> plugin) {
		super(false, plugin.getPermissionContainer().getAdminPermission(), TextGeneric.commandDescriptionGenericPluginState, ConfigGCore.genericCommandsAliasesPluginState);
		this.plugin = plugin;
	}

	@Override
	public void perform(CommandCall call) {
		TextGCore.messagePluginInternalState
		.replace("{plugin}", () -> plugin.getName())
		.replace("{version}", () -> plugin.getDescription().getVersion())
		.replace("{lifecycle_reference}", () -> "" + plugin.getLifecycleReference().hashCode())
		.replace("{data_boards}", () -> {
			if (plugin.getData().isEmpty()) {
				return "/";
			}
			List<String> desc = CollectionUtils.asList("");
			plugin.getData().forEach((id, value) -> desc.add("§a" + id + " §7= §2" + value.getBackEnd().name()));
			desc.sort(String::compareTo);
			return desc;
		})
		.replace("{loggers}", () -> {
			if (plugin.getLoggers().isEmpty()) {
				return "/";
			}
			List<String> desc = new ArrayList<>();
			plugin.getLoggers().forEach((id, value) -> desc.add("§a" + id));
			desc.sort(String::compareTo);
			return StringUtils.toTextString("§r, ", desc);
		})
		.replace("{guis}", () -> {
			if (plugin.getGUIs().isEmpty()) {
				return "/";
			}
			List<String> desc = CollectionUtils.asList("");
			plugin.getGUIs().forEach(value -> desc.add("§a" + value.getId() + " §7= §2" + value.getType() + ", " + StringUtils.pluralizeAmountDesc("page", value.getPageCount()) + ", " + StringUtils.pluralizeAmountDesc("viewer", value.getViewers().size())));
			desc.sort(String::compareTo);
			return desc;
		})
		.replace("{tasks}", () -> {
			if (plugin.getTasks().isEmpty()) {
				return "/";
			}
			List<String> desc = CollectionUtils.asList("");
			plugin.getTasks().forEach((id, value) -> desc.add("§a" + id + " §7= §2interval " + value.getTicksPeriod()));
			desc.sort(String::compareTo);
			return desc;
		})
		.replace("{listeners}", () -> {
			if (plugin.getListeners().isEmpty()) {
				return "/";
			}
			List<String> desc = new ArrayList<>();
			plugin.getListeners().forEach((id, value) -> desc.add("§a" + id));
			desc.sort(String::compareTo);
			return StringUtils.toTextString("§r, ", desc);
		})
		.replace("{integrations}", () -> {
			if (plugin.getIntegrations().isEmpty()) {
				return "/";
			}
			List<String> desc = CollectionUtils.asList("");
			plugin.getIntegrations().forEach((id, value) -> desc.add("§a" + value.getPluginName() + " §7= §2" + (!PluginUtils.isPluginEnabled(value.getPluginName()) ? "§eplugin not installed" : (value.isActivated() ? "§aenabled" : "§cdisabled"))));
			desc.sort(String::compareTo);
			return desc;
		})
		.replace("{bossbars}", () -> {
			if (plugin.getBossbars().isEmpty()) {
				return "/";
			}
			List<String> desc = CollectionUtils.asList("");
			plugin.getBossbars().forEach((id, value) -> desc.add("§a" + value.getId() + " §7= §2" + StringUtils.pluralizeAmountDesc("player", value.getPlayers().size())));
			desc.sort(String::compareTo);
			return desc;
		})
		.send(call);
	}

}
