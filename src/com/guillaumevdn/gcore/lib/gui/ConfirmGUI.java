package com.guillaumevdn.gcore.lib.gui;

import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.TextGeneric;
import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.gui.struct.GUI;
import com.guillaumevdn.gcore.lib.gui.struct.GUIItem;
import com.guillaumevdn.gcore.lib.item.ItemUtils;

/**
 * @author GuillaumeVDN
 */
public class ConfirmGUI extends GUI {

	private List<String> confirmLore;
	private Runnable confirm;
	private Runnable cancel;

	public ConfirmGUI(GPlugin owner, List<String> confirmLore, Runnable confirm, Runnable cancel) {
		super(owner, "confirm_" + UUID.randomUUID().toString().split("-")[0], TextGeneric.guiConfirmName.parseLine(), ConfigGCore.guiConfirmType, null);
		this.confirmLore = confirmLore;
		this.confirm = confirm;
		this.cancel = cancel;
	}

	// fill
	@Override
	protected boolean doFill() {
		// confirm items
		setItem(new GUIItem("yes", ConfigGCore.guiConfirmItemYes.getC(), ItemUtils.addToLore(ConfigGCore.guiConfirmItemYes.getA().clone(), confirmLore), call -> {
			cancel = null;
			deactivate(true);
			confirm.run();
		}), ConfigGCore.guiConfirmItemYes.getB());
		setItem(new GUIItem("no", ConfigGCore.guiConfirmItemNo, call -> {
			cancel();
			deactivate(true);
		}), ConfigGCore.guiConfirmItemNo.getB());
		// add content
		ConfigGCore.guiConfirmContent.forEach(triple -> setItem(new GUIItem("content_" + UUID.randomUUID().toString().split("-")[0], triple), triple.getB()));
		// done
		return true;
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
		if (cancel != null) {
			Runnable cancel = this.cancel;
			this.cancel = null;  // in the cancel runnable might be something like "reopen another GUI" -> and that will trigger "onClose", that will trigger cancel() again, ect ; so prevent that
			cancel.run();
		}
	}

	// static
	public static void performOrConfirm(GPlugin owner, boolean mustConfirm, Player player, List<String> confirmLore, Runnable confirm, Runnable cancel) {
		if (mustConfirm) {
			new ConfirmGUI(owner, confirmLore, confirm, cancel).openFor(player);
		} else {
			confirm.run();
		}
	}


}
