package com.guillaumevdn.gcore.lib.gui.element.item.type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;

import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.sound.Sound;
import com.guillaumevdn.gcore.lib.element.struct.Need;
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
public class TypeDynamicBorderLinear extends GUIItemType {

	public TypeDynamicBorderLinear(String id) {
		super(id, false, CommonMats.GRAY_STAINED_GLASS_PANE);
	}

	@Override
	protected void doFillTypeSpecificElements(ElementGUIItem item) {
		super.doFillTypeSpecificElements(item);
		item.addInteger("on_count", Need.optional(2), TextEditorGeneric.descriptionGuiItemDynamicBorderLinearOnCount);
		item.addItem("icon_on", Need.optional(), TextEditorGeneric.descriptionGuiItemDynamicBorderLinearIconOn);
		item.addItem("icon_off", Need.optional(), TextEditorGeneric.descriptionGuiItemDynamicBorderLinearIconOff);
	}

	@Override
	public ActiveHolderItem newActive(ActiveGUI gui, ItemHolder holder, ElementGUIItem item, Replacer replacer) throws ParsingError {
		// parse settings
		ItemStack itemIconOn = item.directParseNoCatchOrThrowParsingNull("icon_on", replacer);
		ItemStack itemIconOff = item.directParseNoCatchOrThrowParsingNull("icon_off", replacer);
		int onCount = item.directParseOrElse("on_count", replacer, 1);
		List<IntegerPair> locations = item.parseLocations(replacer);
		Sound clickSound = item.getClickSound().parse(replacer).orNull();
		Map<ClickType, Consumer<ClickCall>> overrideClicks = item.parseOverrideClicks(replacer);
		// build item
		return instanceCache.computeIfAbsent(holder, __ -> new Active(holder, onCount, locations, item.getPersistent().parse(replacer).orElse(true), itemIconOn, itemIconOff, clickSound, overrideClicks));
	}

	private WeakHashMap<ItemHolder, Active> instanceCache = new WeakHashMap<>();  // keep a cache of built items for holders, so we don't rebuild one every tick and we also keep the step

	public final class Active extends ActiveHolderItem {  // this keeps a cache of parsed icons and locations

		private int step;
		private List<IntegerPair> locations;
		private List<IntegerPair> previousOn = null;
		private int onCount;
		private boolean persistent;
		private GUIItem on, off;

		public Active(ItemHolder holder, int onCount, List<IntegerPair> locations, boolean persistent, ItemStack itemIconOn, ItemStack itemIconOff, Sound clickSound, Map<ClickType, Consumer<ClickCall>> overrideClicks) {
			super(holder, ConfigGCore.dynamicBorderRefreshTicks);
			this.onCount = onCount;
			this.locations = locations;
			this.step = locations.size() - 1;
			this.persistent = persistent;
			on = new GUIItem(getHolder().getId() + "_on", null, itemIconOn, clickSound, overrideClicks, null);
			off = new GUIItem(getHolder().getId() + "_off", null, itemIconOff, clickSound, overrideClicks, null);
		}

		@Override
		public void fill(ActiveGUI instance, Replacer replacer, Runnable callback) {
			// increase step
			step = (step + 1) % locations.size();
			// find actual locations and not preferred (this is to avoid recalculating slots for those everytime)
			if (previousOn == null) {
				off.setPreferredLocations(locations);
				instance.setItem(off, persistent);
				instance.removeItem(off, persistent);
				locations = off.getLocations();
			}
			// calculate locations
			List<IntegerPair> locOn = new ArrayList<>();
			List<IntegerPair> locOff = new ArrayList<>();
			int diff = (int) Math.floor(locations.size() / onCount);
			for (int i = 0; i < locations.size(); ++i) {
				((i + step) % diff == 0 ? locOn : locOff).add(locations.get(locations.size() - i - 1));
			}
			// update (only update slots that changed to avoid heavy refreshes)
			on.setLocations(locOn);
			on.setPreferredLocations(locOn);
			off.setLocations(locOff);
			off.setPreferredLocations(locOff);
			if (previousOn == null) {
				instance.setItem(on, persistent);
				instance.setItem(off, persistent);
			} else {
				previousOn.addAll(locOn);
				previousOn.forEach(pair -> instance.sendPageItem(pair, (locOn.contains(pair) ? on : off).getItem()));
			}
			previousOn = locOn;
			// callback
			if (callback != null) {
				callback.run();
			}
		}

		@Override
		protected void build(ActiveGUI instance, Replacer replacer, Consumer<GUIItem> callback) {
			throw new UnsupportedOperationException();
		}

	}

}
