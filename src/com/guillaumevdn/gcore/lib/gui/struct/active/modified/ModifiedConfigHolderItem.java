package com.guillaumevdn.gcore.lib.gui.struct.active.modified;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.lib.compatibility.sound.Sound;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.gui.element.item.element.ElementGUIItem;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall.ClickType;
import com.guillaumevdn.gcore.lib.gui.struct.GUIItem;
import com.guillaumevdn.gcore.lib.gui.struct.active.ActiveGUI;
import com.guillaumevdn.gcore.lib.gui.struct.active.ActiveHolderItem;
import com.guillaumevdn.gcore.lib.gui.struct.active.ItemHolder;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;
import com.guillaumevdn.gcore.lib.tuple.IntegerPair;

/**
 * @author GuillaumeVDN
 */
public abstract class ModifiedConfigHolderItem extends ItemHolder {

	private ElementGUIItem elementItem;

	public ModifiedConfigHolderItem(String id, ElementGUIItem elementItem) {
		super(id);
		this.elementItem = elementItem;
	}

	@Override
	public ActiveHolderItem newActive(ActiveGUI gui) throws ParsingError {
		ItemStack itemIcon = maybeModifyIcon(elementItem.directParseNoCatchOrThrowParsingNull("icon", gui.getReplacer()));
		List<IntegerPair> locations = elementItem.parseLocations(gui.getReplacer());
		Sound clickSound = elementItem.getClickSound().parse(gui.getReplacer()).orNull();
		Map<ClickType, Consumer<ClickCall>> overrideClicks = elementItem.parseOverrideClicks(gui.getReplacer());
		return new ActiveHolderItem(this, forceRefreshDelayTicks()) {
			@Override
			protected void build(ActiveGUI instance, Replacer replacer, Consumer<GUIItem> callback) {
				callback.accept(new GUIItem(getId(), locations, itemIcon, clickSound, overrideClicks, call -> onClick(call)));
			}
		};
	}

	@Override
	public boolean getPersistent(Replacer replacer) {
		return elementItem.getPersistent().parse( replacer).orElse(false);
	}

	protected long forceRefreshDelayTicks() {
		return -1L;
	}

	protected ItemStack maybeModifyIcon(ItemStack icon) {
		return icon;
	}

	protected abstract void onClick(ClickCall call);

}
