package com.guillaumevdn.gcore;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.guillaumevdn.gcore.lib.configuration.YMLConfiguration;
import com.guillaumevdn.gcore.lib.util.Utils;
import com.guillaumevdn.gcore.libs.org.apache.commons.io.FileUtils;

class ConversionV7 {

	// methods
	void start() {
		// GCore v7
		File old = new File(GCore.inst().getDataFolder().getParentFile() + "/PyrCore/");
		if (old.exists()) {
			GCore.inst().warning("--- Starting GCore v7 files conversion");
			moveFile(old, old = new File(GCore.inst().getDataFolder().getParentFile() + "/GCore_pre7/"));
			File neww = GCore.inst().getDataFolder();
			// copy data, except questcreator_quests
			GCore.inst().warning("- /data/");
			File data = new File(old + "/data/");
			if (data.exists()) {
				for (File sub : data.listFiles()) {
					if (sub.getName().equalsIgnoreCase("temp")) continue;
					copyFile(sub, new File(neww + "/data/" + sub.getName() + "/"));
				}
			}
			// copy userdata
			GCore.inst().warning("- /userdata/");
			File userData = new File(old + "/userdata/");
			if (userData.exists()) {
				copyFile(userData, new File(neww + "/userdata/"));
			}
			// convert config
			GCore.inst().warning("- /config.yml");
			File config = new File(old + "/config.yml");
			if (config.exists()) {
				YMLConfiguration yml = new YMLConfiguration(GCore.inst(), config, null, false, true);
				yml.set("npc_update_delay", 2L);
				yml.save(new File(neww + "/config.yml"));
				GCore.inst().warning("Converted " + config.getPath());
			}
			// copy texts
			GCore.inst().warning("- /texts.yml");
			File texts = new File(old + "/texts.yml");
			if (texts.exists()) {
				copyFile(texts, new File(neww + "/texts.yml"));
			}
			GCore.inst().warning("--- GCore v7 files conversion is done - some minor details might not have been converted, please make sure everything works as you intended");
		}
		// QuestCreator v5
		File neww = new File(GCore.inst().getDataFolder().getParentFile() + "/QuestCreator/");
		if (new File(neww + "/guis.yml").exists()) {
			GCore.inst().warning("--- Starting QuestCreator v5 files conversion");
			// move the whole directory to a "pre5" one
			old = new File(GCore.inst().getDataFolder().getParentFile() + "/QuestCreator_pre5/");
			moveFile(neww, old);
			// copy global variables
			File gvariables = new File(old + "/gvariables.yml");
			if (gvariables.exists()) {
				copyFile(gvariables, new File(neww + "/global_variables.yml"));
			}
			// copy texts
			File texts = new File(old + "/texts.yml");
			if (texts.exists()) {
				copyFile(texts, new File(neww + "/texts.yml"));
			}
			// convert guis.yml
			File guis = new File(old + "/guis.yml");
			YMLConfiguration guisConfig = null;
			if (guis.exists()) {
				GCore.inst().warning("- /guis.yml");
				guisConfig = new YMLConfiguration(GCore.inst(), guis, null, false, true);
				// convert guis
				for (String guiId : guisConfig.getKeysForSection("guis", false)) {
					// initialize file
					YMLConfiguration newGuiConfig = new YMLConfiguration(GCore.inst(), new File(neww + "/guis/" + guiId + ".yml"), null, true, false);
					newGuiConfig.setHeader(
							"#----------------------------------------------------------------------------------------------------",
							"# GUI configuration file (file name without the extension is the GUI identifier)",
							"#----------------------------------------------------------------------------------------------------", "");
					// copy settings
					newGuiConfig.set("name", guisConfig.getString("guis." + guiId + ".name", guiId));
					newGuiConfig.set("size", guisConfig.getInt("guis." + guiId + ".size", 9));
					// copy content
					for (String contentId : guisConfig.getKeysForSection("guis." + guiId + ".content", false)) {
						// display item
						copyConfig(guisConfig, "guis." + guiId + ".content." + contentId, newGuiConfig, "content." + contentId + ".item", Utils.asList("quest", "quest_group", "link", "commands"));;
						// commands
						if (guisConfig.contains("guis." + guiId + ".content." + contentId + ".commands")) {
							newGuiConfig.set("content." + contentId + ".commands", guisConfig.getList("guis." + guiId + ".content." + contentId + ".commands", null));
						}
						// type
						if (guisConfig.contains("guis." + guiId + ".content." + contentId + ".quest")) {
							newGuiConfig.set("content." + contentId + ".type", "QUEST " + guisConfig.getString("guis." + guiId + ".content." + contentId + ".quest", null));
						} else if (guisConfig.contains("guis." + guiId + ".content." + contentId + ".link")) {
							String link = guisConfig.getString("guis." + guiId + ".content." + contentId + ".link", null);
							newGuiConfig.set("content." + contentId + ".type", (link.equalsIgnoreCase("active_quests") ? "GUI_ACTIVE_QUESTS" : "GUI " + link));
						} else {
							newGuiConfig.set("content." + contentId + ".type", "NONE");
						}
					}
					// save
					newGuiConfig.save();
					GCore.inst().warning("Converted " + newGuiConfig.getFile().getPath());
				}
			}
			// convert config
			File config = new File(old + "/config.yml");
			if (config.exists()) {
				GCore.inst().warning("- /config.yml");
				YMLConfiguration yml = new YMLConfiguration(GCore.inst(), config, null, false, true);
				if (guisConfig != null) {
					copyConfig(guisConfig, "previous_page_item", yml, "gui.previous_page_item");
					copyConfig(guisConfig, "next_page_item", yml, "gui.next_page_item");
					copyConfig(guisConfig, "back_item", yml, "gui.back_item");
					yml.set("gui.main_gui", guisConfig.getString("main_gui", "main_gui"));
				} else {
					yml.set("gui.previous_page_item.type", "ARROW");
					yml.set("gui.previous_page_item.name", "&7Previous page");
					yml.set("gui.next_page_item.type", "ARROW");
					yml.set("gui.next_page_item.name", "&7Next page");
					yml.set("gui.back_item.type", "ARROW");
					yml.set("gui.back_item.name", "&7Back");
					yml.set("gui.main_gui", "main_gui");
				}
				File f = new File(neww + "/config.yml");
				yml.save(f);
				GCore.inst().warning("Converted " + f.getPath());
			}
			// convert categories.yml
			File categories = new File(old + "/categories.yml");
			if (categories.exists()) {
				GCore.inst().warning("- /categories.yml");
				// initialize activators yml
				YMLConfiguration activators = new YMLConfiguration(GCore.inst(), new File(neww + "/quests/activators.yml"),
						null, true, false);
				activators.setHeader(
						"#----------------------------------------------------------------------------------------------------",
						"# Quest activators registration file for QuestCreator",
						"#----------------------------------------------------------------------------------------------------",
						"", "# Register your quest activators here",
						"# Check the wiki to get a list of activator types and their settings", "");
				activators.set("activators.example_enabled.type", "ENABLED");
				GCore.inst().warning("Initialized " + activators.getFile().getPath());
				// initialize groups yml
				YMLConfiguration groups = new YMLConfiguration(GCore.inst(), new File(neww + "/quests/groups.yml"), null, true,
						false);
				groups.setHeader(
						"#----------------------------------------------------------------------------------------------------",
						"# Quest groups registration file for QuestCreator",
						"#----------------------------------------------------------------------------------------------------",
						"", "# Register your quest groups here", "");
				groups.set("groups.example_farm.max_concurrent", 1);
				groups.set("groups.example_farm.execution_order", true);
				groups.set("groups.example_farm.gui", "group_example_farm_gui");
				GCore.inst().warning("Initialized " + groups.getFile().getPath());
				// initialize registration yml
				YMLConfiguration registration = new YMLConfiguration(GCore.inst(), new File(neww + "/quests/registration.yml"),
						null, true, false);
				registration.setHeader(
						"#----------------------------------------------------------------------------------------------------",
						"# Quest registration file for QuestCreator",
						"#----------------------------------------------------------------------------------------------------",
						"", "# Register your quest models here", "");
				registration.set("quests.example.activators", Utils.asList("example_enabled"));
				registration.set("quests.example_farm_1.activators", Utils.asList("example_enabled"));
				registration.set("quests.example_farm_1.group", "example_farm");
				registration.set("quests.example_farm_2.activators", Utils.asList("example_enabled"));
				registration.set("quests.example_farm_2.group", "example_farm");
				GCore.inst().warning("Initialized " + registration.getFile().getPath());
				// load categories config
				YMLConfiguration categoriesConfig = new YMLConfiguration(GCore.inst(), categories, null, false, true);
				// convert regular categories
				for (String categoryId : categoriesConfig.getKeysForSection("categories", false)) {
					GCore.inst().warning("- Category " + categoryId);
					// quests
					List<String> categoryQuests = categoriesConfig.getList("categories." + categoryId + ".quest_list", Utils.emptyList());
					// activator
					String type = categoriesConfig.getString("categories." + categoryId + ".activator.type", "ENABLED");
					activators.set("activators.activator_" + categoryId + ".type", type.equalsIgnoreCase("NPC") ? "CITIZENS_NPC" : type);
					if (categoriesConfig.contains("categories." + categoryId + ".activator.selection_gui_when_one_quest")) {
						activators.set("activators.activator_" + categoryId + ".min_quests_for_gui", categoriesConfig.getBoolean("categories." + categoryId + ".activator.selection_gui_when_one_quest", false) ? 1 : 2);
					}
					if (categoriesConfig.contains("categories." + categoryId + ".activator.allow_gui_click_start")) {
						activators.set("activators.activator_" + categoryId + ".gui_click_type_start", categoriesConfig.getBoolean("categories." + categoryId + ".activator.allow_gui_click_start", true) ? "LEFT_CLICK" : "NONE");
					}
					if (categoriesConfig.contains("categories." + categoryId + ".activator.start_click_type")) {
						activators.set("activators.activator_" + categoryId + ".interaction_click", categoriesConfig.getString("categories." + categoryId + ".activator.start_click_type", null));
					}
					if (categoriesConfig.contains("categories." + categoryId + ".activator.gui_show_unavailable")) {
						activators.set("activators.activator_" + categoryId + ".gui_show_quest_unavailable", categoriesConfig.getBoolean("categories." + categoryId + ".activator.gui_show_unavailable", true));
					}
					if (categoriesConfig.contains("categories." + categoryId + ".activator.gui_show_cooldown")) {
						activators.set("activators.activator_" + categoryId + ".gui_show_quest_cooldown", categoriesConfig.getBoolean("categories." + categoryId + ".activator.gui_show_cooldown", true));
					}
					if (categoriesConfig.contains("categories." + categoryId + ".activator.gui_show_completed")) {
						activators.set("activators.activator_" + categoryId + ".gui_show_quest_completed", categoriesConfig.getBoolean("categories." + categoryId + ".activator.gui_show_completed", true));
					}
					copyConfig(categoriesConfig, "categories." + categoryId + ".activator", activators, "activators.activator_" + categoryId, Utils.asList("type", "selection_gui_when_one_quest", "start_click_type", "gui_show_unavailable", "gui_show_cooldown", "gui_show_completed", "allow_gui_click_start"));
					GCore.inst().warning("Created activator activator_" + categoryId);
					// group (if more than one quest)
					boolean createGroup = categoryQuests.size() > 1;
					if (createGroup) {
						// copy settings
						groups.set("groups.group_" + categoryId + ".max_concurrent", categoriesConfig.getInt("categories." + categoryId + ".max_concurrent", 100));
						groups.set("groups.group_" + categoryId + ".execution_order", categoriesConfig.getBoolean("categories." + categoryId + ".execution_order", true));
						groups.set("groups.group_" + categoryId + ".gui", "gui_" + categoryId);
						GCore.inst().warning("Created group group_" + categoryId);
						// create group gui
						YMLConfiguration groupGuiConfig = new YMLConfiguration(GCore.inst(), new File(neww + "/guis/gui_group_" + categoryId + ".yml"), null, true, false);
						groupGuiConfig.setHeader(
								"#----------------------------------------------------------------------------------------------------",
								"# GUI configuration file (file name without the extension is the GUI identifier)",
								"#----------------------------------------------------------------------------------------------------", "");
						groupGuiConfig.set("name", categoriesConfig.getString("categories." + categoryId + ".gui_name", "gui_group_" + categoryId));
						groupGuiConfig.set("size", Utils.getInventorySize(categoryQuests.size()));
						groupGuiConfig.set("quests", Utils.asList("group:group_" + categoryId));
						groupGuiConfig.set("show_quest_available", categoriesConfig.getBoolean("categories." + categoryId + ".gui_show_available", true));
						groupGuiConfig.set("show_quest_progress", categoriesConfig.getBoolean("categories." + categoryId + ".show_quest_progress", true));
						groupGuiConfig.set("show_quest_cooldown", categoriesConfig.getBoolean("categories." + categoryId + ".show_quest_cooldown", true));
						groupGuiConfig.set("show_quest_completed", categoriesConfig.getBoolean("categories." + categoryId + ".show_quest_completed", true));
						groupGuiConfig.set("show_quest_unavailable", categoriesConfig.getBoolean("categories." + categoryId + ".show_quest_unavailable", true));
						// save
						groupGuiConfig.save();
						GCore.inst().warning("Created gui gui_group_" + categoryId);
					}
					// registration
					for (String questId : categoryQuests) {
						registration.set("quests." + questId + ".activators", Utils.asList("activator_" + categoryId));
						if (createGroup) registration.set("quests." + questId + ".group", "group_" + categoryId);
						GCore.inst().warning("Registered quest " + questId + " with activator activator_" + categoryId + (!createGroup ? "" : " and group group_" + categoryId));
					}
				}
				// convert compact npc categories
				for (String categoryId : categoriesConfig.getKeysForSection("compact_npc_categories", false)) {
					Integer npcId = Utils.integerOrNull(categoryId);
					if (npcId == null) continue;
					GCore.inst().warning("- Compact npc category " + categoryId);
					String id = "npc_" + categoryId;
					// quests
					List<String> categoryQuests = categoriesConfig.getList("compact_npc_categories." + categoryId + ".quest_list", Utils.emptyList());
					// activator
					activators.set("activators.activator_" + id + ".type", "CITIZENS_NPC");
					if (categoriesConfig.contains("categories." + categoryId + ".selection_gui_when_one_quest")) {
						activators.set("activators.activator_" + categoryId + ".min_quests_for_gui", categoriesConfig.getBoolean("categories." + categoryId + ".selection_gui_when_one_quest", false) ? 1 : 2);
					}
					if (categoriesConfig.contains("categories." + categoryId + ".allow_gui_click_start")) {
						activators.set("activators.activator_" + categoryId + ".gui_click_type_start", categoriesConfig.getBoolean("categories." + categoryId + ".allow_gui_click_start", true) ? "LEFT_CLICK" : "NONE");
					}
					if (categoriesConfig.contains("categories." + categoryId + ".start_click_type")) {
						activators.set("activators.activator_" + categoryId + ".interaction_click", categoriesConfig.getString("categories." + categoryId + ".start_click_type", null));
					}
					if (categoriesConfig.contains("categories." + categoryId + ".gui_show_unavailable")) {
						activators.set("activators.activator_" + categoryId + ".gui_show_quest_unavailable", categoriesConfig.getBoolean("categories." + categoryId + ".gui_show_unavailable", true));
					}
					if (categoriesConfig.contains("categories." + categoryId + ".gui_show_cooldown")) {
						activators.set("activators.activator_" + categoryId + ".gui_show_quest_cooldown", categoriesConfig.getBoolean("categories." + categoryId + ".gui_show_cooldown", true));
					}
					if (categoriesConfig.contains("categories." + categoryId + ".gui_show_completed")) {
						activators.set("activators.activator_" + categoryId + ".gui_show_quest_completed", categoriesConfig.getBoolean("categories." + categoryId + ".gui_show_completed", true));
					}
					for (String field : Utils.asList("sneak_click_cancel", "particle_offy", "particle_available", "particle_progress", "particle_cooldown", "particle_completed")) {
						activators.set("activators.activator_" + categoryId + "." + field, categoriesConfig.getObject("categories." + categoryId + "." + field, null));
					}
					GCore.inst().warning("Created activator activator_" + id);
					// group (if more than one quest)
					boolean createGroup = categoryQuests.size() > 1;
					if (createGroup) {
						// copy settings
						groups.set("groups.group_" + id + ".max_concurrent", categoriesConfig.getInt("compact_npc_categories." + categoryId + ".max_concurrent", 100));
						groups.set("groups.group_" + id + ".execution_order", categoriesConfig.getBoolean("compact_npc_categories." + categoryId + ".execution_order", true));
						groups.set("groups.group_" + id + ".gui", "gui_" + categoryId);
						GCore.inst().warning("Created group group_" + id);
						// create group gui
						YMLConfiguration groupGuiConfig = new YMLConfiguration(GCore.inst(), new File(neww + "/guis/gui_group_" + id + ".yml"), null, true, false);
						groupGuiConfig.setHeader(
								"#----------------------------------------------------------------------------------------------------",
								"# GUI configuration file (file name without the extension is the GUI identifier)",
								"#----------------------------------------------------------------------------------------------------", "");
						groupGuiConfig.set("name", categoriesConfig.getString("compact_npc_categories." + categoryId + ".gui_name", "gui_group_" + categoryId));
						groupGuiConfig.set("size", Utils.getInventorySize(categoryQuests.size()));
						groupGuiConfig.set("quests", Utils.asList("group:group_" + categoryId));
						groupGuiConfig.set("show_quest_available", categoriesConfig.getBoolean("compact_npc_categories." + categoryId + ".gui_show_available", true));
						groupGuiConfig.set("show_quest_progress", categoriesConfig.getBoolean("compact_npc_categories." + categoryId + ".show_quest_progress", true));
						groupGuiConfig.set("show_quest_cooldown", categoriesConfig.getBoolean("compact_npc_categories." + categoryId + ".show_quest_cooldown", true));
						groupGuiConfig.set("show_quest_completed", categoriesConfig.getBoolean("compact_npc_categories." + categoryId + ".show_quest_completed", true));
						groupGuiConfig.set("show_quest_unavailable", categoriesConfig.getBoolean("compact_npc_categories." + categoryId + ".show_quest_unavailable", true));
						// save
						groupGuiConfig.save();
						GCore.inst().warning("Created gui gui_group_" + id);
					}
					// registration
					for (String questId : categoryQuests) {
						registration.set("quests." + questId + ".activators", Utils.asList("activator_" + id));
						if (createGroup) registration.set("quests." + questId + ".group", "group_" + id);
						GCore.inst().warning("Registered quest " + questId + " with activator activator_" + id + (!createGroup ? "" : " and group group_" + id));
					}
				}
				// save yml
				activators.save();
				groups.save();
				registration.save();
			}
			// convert models
			File models = new File(old + "/quests/");
			if (models.exists()) {
				GCore.inst().warning("- /models/");
				convertModels(models, "/models");
			}
			GCore.inst().warning("--- QuestCreator v5 files conversion is done - some minor details might not have been converted, please make sure everything works as you intended");
		}
	}

	private void convertModels(File file, String target) {
		if (file.isDirectory()) {
			for (File sub : file.listFiles()) {
				convertModels(sub, target + "/" + sub.getName());
			}
		} else if (file.getName().toLowerCase().endsWith(".yml")) {
			GCore.inst().warning("- " + target);
			YMLConfiguration from = new YMLConfiguration(GCore.inst(), file, null, false, true);
			YMLConfiguration to = new YMLConfiguration(GCore.inst(), new File(GCore.inst().getDataFolder().getParentFile() + "/QuestCreator/quests/" + target), null, true, false);
			convertModelConfig(from, to, "", "", "");
			to.save();
		}
	}

	private Map<String, String> SECTION_RENAMING = Utils.asMap(
			"conditions", "conditions.conditions",
			"start_conditions", "start_conditions.conditions"
			);

	private Map<String, String> FIELD_RENAMING = Utils.asMap(
			"auto_start", "starts_directly",
			"check_leader_only", "only_for_role",
			"fail_goto", "goto_if_not_valid",
			"conditions_type", "conditions.type",
			"sound", "sound.type",
			"post_sound", "post_sound.type",
			"leader_only", "for_leader_only",
			"restricted_worlds", "world_whitelist"
			);

	private Map<String, String> PARENT_AND_FIELD_RENAMING = Utils.asMap(
			"block.type", "block_type",
			"block.amount", "amount"
			);

	private void convertModelConfig(YMLConfiguration from, YMLConfiguration to, String fromPath, String newFieldParent, String newField) {
		if (from.isConfigurationSection(fromPath)) {
			for (String f : from.getKeysForSection(fromPath, false)) {
				if (f.equalsIgnoreCase("item_available_category")) continue;
				if (SECTION_RENAMING.containsKey(f.toLowerCase())) {
					GCore.inst().warning("Renamed " + f + " to " + SECTION_RENAMING.get(f.toLowerCase()));
				}
				convertModelConfig(from, to, fromPath.isEmpty() ? f : fromPath + "." + f, newFieldParent.isEmpty() || newField.isEmpty() ? newField : newFieldParent + "." + newField, SECTION_RENAMING.containsKey(f.toLowerCase()) ? SECTION_RENAMING.get(f.toLowerCase()) : f);
			}
		} else {
			// get final path
			String newPath = newFieldParent.isEmpty() ? newField : newFieldParent + "." + newField;
			String toPath = null;
			if (FIELD_RENAMING.containsKey(newField)) {
				toPath = newFieldParent.isEmpty() ? FIELD_RENAMING.get(newField) : newFieldParent + "." + FIELD_RENAMING.get(newField);
				GCore.inst().warning("Renamed " + newPath + " to " + toPath);
			} else {
				for (String str : PARENT_AND_FIELD_RENAMING.keySet()) {
					if (newPath.endsWith(str)) {
						toPath = newPath.substring(0, newPath.length() - str.length()) + PARENT_AND_FIELD_RENAMING.get(str);
						GCore.inst().warning("Renamed " + newPath + " to " + toPath);
						break;
					}
				}
				if (toPath == null) {
					toPath = newPath;
				}
			}
			// eventually modify value
			Object obj = from.getObject(fromPath, null);
			if (obj == null) GCore.inst().error("null : fromPath " + fromPath + ", toPath " + toPath + ", parent " + newFieldParent + ", field " + newField);
			if (obj instanceof String) {
				String str = (String) obj;
				if (str.contains("NPC")) obj = str.replace("NPC", "CITIZENS_NPC");
			} else if (obj instanceof List<?>) {
				List<Object> list = (List<Object>) obj;
				for (int i = 0; i < list.size(); ++i) {
					Object o = list.get(i);
					if (o instanceof String) {
						String s = (String) o;
						if (s.contains("NPC")) list.set(i, (Object) s.replace("NPC", "CITIZENS_NPC"));
					}
				}
			}
			// set value
			to.set(toPath, obj);
		}
	}

	private void copyConfig(YMLConfiguration from, String fromPath, YMLConfiguration to, String toPath) {
		copyConfig(from, fromPath, to, toPath, Utils.emptyList());
	}

	private void copyConfig(YMLConfiguration from, String fromPath, YMLConfiguration to, String toPath, List<String> ignoreKeys) {
		if (from.contains(fromPath)) {
			if (from.isConfigurationSection(fromPath)) {
				for (String key : from.getKeysForSection(fromPath, false)) {
					if (!ignoreKeys.contains(key)) {
						copyConfig(from, fromPath + "." + key, to, toPath + "." + key);
					}
				}
			} else {
				to.set(toPath, from.getObject(fromPath, null));
			}
		}
	}

	private void moveFile(File from, File to) {
		try {
			if (from.isDirectory()) {
				FileUtils.moveDirectory(from, to);
			} else {
				FileUtils.moveFile(from, to);
			}
			GCore.inst().warning("Moved " + from.getPath() + " to " + to.getPath());
		} catch (IOException exception) {
			exception.printStackTrace();
			GCore.inst().error("Couldn't move file " + from.getPath() + " to " + to.getPath());
		}
	}

	private void copyFile(File from, File to) {
		try {
			if (from.isDirectory()) {
				FileUtils.copyDirectory(from, to);
			} else {
				FileUtils.copyFile(from, to);
			}
			GCore.inst().warning("Copied " + from.getPath() + " to " + to.getPath());
		} catch (IOException exception) {
			exception.printStackTrace();
			GCore.inst().error("Couldn't copy file " + from.getPath() + " to " + to.getPath());
		}
	}

}
