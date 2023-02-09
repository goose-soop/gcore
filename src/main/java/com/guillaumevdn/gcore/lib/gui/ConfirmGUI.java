package com.guillaumevdn.gcore.lib.gui;

import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.function.QuadriConsumer;
import com.guillaumevdn.gcore.lib.gui.element.ExtraElementGUIItemHolder;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.gui.struct.active.ActiveExtraElementGUI;
import com.guillaumevdn.gcore.lib.item.ItemUtils;
import com.guillaumevdn.gcore.lib.plugin.PluginUtils;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.string.Text;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public class ConfirmGUI extends ActiveExtraElementGUI {

	private Runnable doCancel;

	public ConfirmGUI(GPlugin owner, Replacer replacer, Text confirmLore, Replacer confirmLoreParser, Runnable doConfirm, Runnable doCancel) {
		super(ConfigGCore.guiConfirm, replacer);

		setPlugin(owner);

		addItem(new ExtraElementGUIItemHolder(ConfigGCore.guiConfirm.getItemConfirm()) {
			@Override
			protected void build(ItemStack itemIcon, QuadriConsumer<ItemStack, Set<String>, Integer, Consumer<ClickCall>> callback) throws ParsingError {
				Set<String> placeholders = StringUtils.getPlaceholders(itemIcon);
				itemIcon = replacer.parse(itemIcon);
				if (confirmLore != null && !confirmLore.isEmpty()) {
					placeholders.addAll(StringUtils.getPlaceholders(confirmLore.getCurrentLines()));
					ItemUtils.addToLore(itemIcon, confirmLore.parseLines(confirmLoreParser));
				}

				callback.accept(itemIcon, placeholders, -1, call -> {
					ConfirmGUI.this.doCancel = null;
					deactivate(true);
					doConfirm.run();
				});
			}
		});

		addItem(new ExtraElementGUIItemHolder(ConfigGCore.guiConfirm.getItemCancel()) {
			@Override
			protected void build(ItemStack itemIcon, QuadriConsumer<ItemStack, Set<String>, Integer, Consumer<ClickCall>> callback) throws ParsingError {
				Set<String> placeholders = StringUtils.getPlaceholders(itemIcon);
				itemIcon = getReplacer().parse(itemIcon);

				callback.accept(itemIcon, placeholders, -1, call -> {
					deactivate(true);  // this will trigger onDeactivate(), so cancel() too
				});
			}
		});

		this.doCancel = doCancel;
	}

	@Override
	public void onClose(Player clicker) {
		getPlugin().operateSyncLater(() -> {
			deactivate(true);  // this will trigger onDeactivate(), so cancel() too
		}, ConfigGCore.allowProtocolGUIs && PluginUtils.isPluginEnabled("ProtocolLib") ? 0L : 1L);  // when triggered by the ESC key, it bugs when vanilla handler is used
	}

	@Override
	public void onDeactivate() {
		cancel();
	}

	private void cancel() {
		if (doCancel != null) {
			Runnable doCancel = this.doCancel;
			this.doCancel = null;  // in the cancel runnable might be something like "reopen another GUI" -> and that will trigger "onClose", that will trigger cancel() again, ect ; so prevent that
			doCancel.run();
		}
	}

	// ----- static
	public static void performOrConfirm(GPlugin owner, boolean mustConfirm, Player player, Text confirmLore, Replacer confirmLoreParser, Runnable doConfirm, Runnable doCancel) {
		if (mustConfirm) {
			new ConfirmGUI(owner, Replacer.justPlayer(player), confirmLore, confirmLoreParser, doConfirm, doCancel).openFor(player, null);
		} else {
			doConfirm.run();
		}
	}

}
