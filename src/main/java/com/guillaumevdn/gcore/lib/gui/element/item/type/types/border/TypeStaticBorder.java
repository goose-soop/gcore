package com.guillaumevdn.gcore.lib.gui.element.item.type.types.border;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.sound.Sound;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.element.type.container.ElementItem;
import com.guillaumevdn.gcore.lib.function.TriConsumer;
import com.guillaumevdn.gcore.lib.gui.element.item.ElementGUIItem;
import com.guillaumevdn.gcore.lib.gui.element.item.type.GUIItemType;
import com.guillaumevdn.gcore.lib.gui.element.item.type.IconNeed;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall.ClickType;
import com.guillaumevdn.gcore.lib.gui.struct.GUIItem;
import com.guillaumevdn.gcore.lib.gui.struct.active.ActiveGUI;
import com.guillaumevdn.gcore.lib.gui.struct.active.ActiveItemHolder;
import com.guillaumevdn.gcore.lib.gui.struct.active.ItemHolder;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;
import com.guillaumevdn.gcore.lib.tuple.IntegerPair;

/**
 * @author GuillaumeVDN
 */
public class TypeStaticBorder extends GUIItemType {

	public TypeStaticBorder(String id) {
		super(id, IconNeed.REQUIRED, CommonMats.GRAY_STAINED_GLASS_PANE);
	}

	@Override
	public ActiveItemHolder newActive(ActiveGUI instance, ItemHolder holder, ElementGUIItem element) {
		return new ActiveItemHolder(instance, holder) {
			@Override
			protected final void buildItems(TriConsumer<Collection<? extends GUIItem>, Set<String>, Integer> callback) throws ParsingError {
				ItemStack itemIcon = element.directParseNoCatchOrThrowParsingNull("icon", getInstance().getReplacer());

				// we parsed the item using the instance replacer so that item properties are parsed properly (for example meta owner_name and such)
				// take placeholders from initial icons
				ElementItem itemIconElement = element.getElementAs("icon");
				String unparsedName = itemIconElement.getName().directParseOrNull(Replacer.GENERIC);
				List<String> unparsedLore = itemIconElement.getLore().directParseOrNull(Replacer.GENERIC);
				Set<String> placeholders = new HashSet<>(0);
				if (unparsedName != null) placeholders.addAll(StringUtils.getPlaceholders(unparsedName));
				if (unparsedLore != null) placeholders.addAll(StringUtils.getPlaceholders(unparsedLore));

				List<IntegerPair> locations = element.parseLocations(getInstance().getReplacer());
				Sound clickSound = element.getClickSound().parse(getInstance().getReplacer()).orNull();
				Map<ClickType, Consumer<ClickCall>> overrideClicks = element.parseOverrideClicks(getInstance().getReplacer());

				callback.accept(CollectionUtils.asList(new BorderGUIItem(getHolder().getId(), locations, itemIcon, clickSound, overrideClicks, null)), placeholders, -1);
			}

		};
	}

}
