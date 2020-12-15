package com.guillaumevdn.gcore.lib.gui;

import java.util.List;

import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.gui.struct.active.modified.ActiveModifiedConfigElementGUI;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public class ConfirmGUI extends ActiveModifiedConfigElementGUI {

	private Runnable doCancel;

	public ConfirmGUI(GPlugin owner, Replacer replacer, List<String> confirmLore, Runnable doConfirm, Runnable doCancel) {
		super(ConfigGCore.guiConfirm, replacer, null);
		setPlugin(owner);
		modifyItem("confirm", icon -> Replacer.of("{confirm}", () -> confirmLore).parse(icon), call -> {
			ConfirmGUI.this.doCancel = null;
			deactivate(true);
			doConfirm.run();
		});
		modifyItem("cancel", call -> {
			deactivate(true);  // this will trigger onDeactivate(), so cancel() too
		});
		this.doCancel = doCancel;
	}

	@Override
	public void onClose(Player clicker) {
		cancel();
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

	// static
	public static void performOrConfirm(GPlugin owner, boolean mustConfirm, Player player, List<String> confirmLore, Runnable doConfirm, Runnable doCancel) {
		if (mustConfirm) {
			new ConfirmGUI(owner, Replacer.of(player), confirmLore, doConfirm, doCancel).openFor(player);
		} else {
			doConfirm.run();
		}
	}

}
