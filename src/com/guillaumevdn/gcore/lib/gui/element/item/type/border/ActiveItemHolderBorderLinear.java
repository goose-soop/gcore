package com.guillaumevdn.gcore.lib.gui.element.item.type.border;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.lib.compatibility.sound.Sound;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.gui.element.item.ElementGUIItem;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall.ClickType;
import com.guillaumevdn.gcore.lib.gui.struct.GUIItem;
import com.guillaumevdn.gcore.lib.gui.struct.active.ActiveGUI;
import com.guillaumevdn.gcore.lib.gui.struct.active.ActiveItemHolder;
import com.guillaumevdn.gcore.lib.gui.struct.active.ItemHolder;
import com.guillaumevdn.gcore.lib.tuple.IntegerPair;

public final class ActiveItemHolderBorderLinear extends ActiveItemHolder {

	private final ElementGUIItem element;

	private ItemStack itemIconOn = null;
	private ItemStack itemIconOff = null;
	private int onCount = 1;
	private List<IntegerPair> locations = null;
	private Sound clickSound = null;
	private Map<ClickType, Consumer<ClickCall>> overrideClicks = null;

	private boolean initialized = false;
	private int step = 0;
	private List<IntegerPair> previousOn = null;
	private boolean persistent = false;

	public ActiveItemHolderBorderLinear(ActiveGUI instance, ItemHolder holder, ElementGUIItem element) {
		super(instance, holder);
		this.element = element;
	}

	@Override
	public void onCreate() {
		setRefreshDelayTicks(ConfigGCore.dynamicBorderRefreshTicks);
	}

	@Override
	public void doRefresh() {
		// first time
		if (!initialized) {
			initialized = true;

			// parse settings
			try {
				locations = element.parseLocations(getInstance().getReplacer());
				if (locations.isEmpty()) {  // no point in going any further
					itemIconOn = null;
					return;
				}
				itemIconOn = element.directParseNoCatchOrThrowParsingNull("icon_on", getInstance().getReplacer());
				itemIconOff = element.directParseNoCatchOrThrowParsingNull("icon_off", getInstance().getReplacer());
				onCount = element.directParseOrElse("on_count", getInstance().getReplacer(), 1);
				clickSound = element.getClickSound().parse(getInstance().getReplacer()).orNull();
				overrideClicks = element.parseOverrideClicks(getInstance().getReplacer());
			} catch (ParsingError error) {
				itemIconOn = null;
				ParsingError.print(error, null);
			}

			// find actual locations by setting 'off' items everywhere
			GUIItem placeholder = new GUIItem(getHolder().getId(), itemIconOff, clickSound, overrideClicks, null);
			placeholder.setPreferredLocations(locations);
			getInstance().setItem(placeholder, persistent);
			locations = placeholder.getLocations();

			// at first frame, everything is off
			// -> consider that last location is enabled, so that next frame we start at the first location slot
			step = locations.size();
			previousOn = new ArrayList<>();

		}
		// already initialized
		else if (itemIconOn != null /* if parsing error */) {
			// increase step
			step = (step + 1) % locations.size();

			// calculate locations
			List<IntegerPair> locOn = new ArrayList<>();
			List<IntegerPair> locOff = new ArrayList<>();
			int diff = (int) Math.floor(locations.size() / onCount);
			for (int i = 0; i < locations.size(); ++i) {
				((i + step) % diff == 0 ? locOn : locOff).add(locations.get(locations.size() - i - 1));
			}

			// only update slots that changed to avoid heavy refreshes
			// we'll send page items directly using the handler
			// -> the GUI will think that it's just always off everywhere, we don't care, it's only display (as long as it knows there's something to avoid conflicts, we're good)
			previousOn.addAll(locOn);
			previousOn.forEach(pair -> getInstance().sendPageItem(pair, locOn.contains(pair) ? itemIconOn : itemIconOff));
			previousOn = locOn;
		}
	}

	@Override
	protected void buildItems(BiConsumer<Collection<? extends GUIItem>, Integer> callback) throws ParsingError {
		throw new UnsupportedOperationException();
	}

}
