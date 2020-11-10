package com.guillaumevdn.gcore.lib.element.editor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.WorkerGCore;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.gui.struct.GUI;
import com.guillaumevdn.gcore.lib.gui.struct.GUIItem;
import com.guillaumevdn.gcore.lib.gui.struct.GUIType;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall.ClickType;
import com.guillaumevdn.gcore.lib.item.ItemUtils;
import com.guillaumevdn.gcore.lib.serialization.Serializer;
import com.guillaumevdn.gcore.lib.tuple.Pair;

/**
 * @author GuillaumeVDN
 */
public class EnumSelectorGUI<V> extends GUI {

	private Serializer<V> serializer;
	private List<V> values;
	private LinkedHashMap<V, Mat> valuesIcons;
	private Function<V, String> customGetValueName;
	private Mat icon;

	private Map<UUID, Pair<Consumer<V>, Runnable>> awaiting = new HashMap<>();
	private Set<UUID> awaitingSearchChat = new HashSet<>();

	public EnumSelectorGUI(Serializer<V> serializer, List<V> values, Mat icon, LinkedHashMap<V, Mat> valuesIcons, Function<V, String> customGetValueName) {
		super(GCore.inst(), "editor_select_" + serializer.getTypeClass().getSimpleName(), TextEditorGeneric.guiSelectTitle.parseLine(), GUIType.CHEST_6_ROW, new ClickCall(), Option.DONT_UNREGISTER_ON_CLOSE);
		this.serializer = serializer;
		this.values = values;
		this.icon = icon;
		this.valuesIcons = valuesIcons;
		this.customGetValueName = customGetValueName;
	}

	// fill
	@Override
	protected boolean doFill() {
		for (V value : (valuesIcons != null ? valuesIcons.keySet() : values)) {
			try {
				String valueName = customGetValueName != null ? customGetValueName.apply(value) : serializer.serialize(value);
				Mat valueIconType = value instanceof Mat ? (Mat) value : (valuesIcons != null ? valuesIcons.get(value) : this.icon);
				ItemStack valueIcon = ItemUtils.createItem(valueIconType, "§6" + valueName, null);
				if (valueIcon != null) {
					setRegularItem(new GUIItem("element_" + valueName, ItemUtils.addAllFlags(valueIcon), call -> success(call.getClicker(), value)));
				}
			} catch (Throwable ignored) {
				// ignore things when building icon (mat have that sometimes, some items don't have meta and stuff)
			}
		}
		setPersistentItem(new GUIItem("search", 50, ItemUtils.createItem(CommonMats.PAPER, TextEditorGeneric.guiSearchName.parseLine(), null), call -> {
			awaitingSearchChat.add(call.getClicker().getUniqueId());
			WorkerGCore.inst().awaitChat(call.getClicker(), TextEditorGeneric.messageElementBasicEditSearch, val -> {
				String value = val.toLowerCase().trim();
				awaitingSearchChat.remove(call.getClicker().getUniqueId());
				Pair<Consumer<V>, Runnable> awaiting = this.awaiting.get(call.getClicker().getUniqueId());
				if (awaiting != null) {
					List<V> matching;
					// there's a specific one matching
					V exact = serializer.deserialize(value);
					if (exact != null) {
						matching = CollectionUtils.asList(exact);
					} else {
						// filter
						matching = (valuesIcons != null ? valuesIcons.keySet() : values).stream().filter(v -> serializer.serialize(v).toLowerCase().contains(value)).collect(Collectors.toList());
					}
					if (matching.isEmpty()) {
						TextEditorGeneric.messageElementBasicEditSearchNoMatch.replace("{value}", () -> value).send(call.getClicker());
						openFor(call.getClicker());
					} else if (matching.size() != 1) {
						TextEditorGeneric.messageElementBasicEditSearchTooManyMatches.replace("{value}", () -> value).send(call.getClicker());
						openFor(call.getClicker());
					} else {
						TextEditorGeneric.messageElementBasicEditSearchMatch.replace("{value}", () -> matching.get(0)).send(call.getClicker());
						success(call.getClicker(), matching.get(0));
					}
				}
			}, () -> call.getGUI().openFor(call.getClicker(), call.getPageIndex()));
		}));
		return true;
	}
	
	@Override
	public void onPlayerInventoryClick(Player clicker, int slot, ItemStack item, ClickType clickType, int clickPageIndex) {
		if (serializer.getTypeClass().equals(Mat.class)) {
			Mat mat = Mat.fromItem(item).orNull();
			if (mat != null) {
				success(clicker, (V) mat);
			}
		}
	}

	@Override
	public void onBack(Player clicker) {
		cancel(clicker);
	}

	@Override
	public void onClose(Player clicker) {
		if (!awaitingSearchChat.contains(clicker.getUniqueId())) {
			cancel(clicker);
		}
	}

	private void cancel(Player clicker) {
		Pair<Consumer<V>, Runnable> awaiting = this.awaiting.remove(clicker.getUniqueId());
		if (awaiting != null) {
			awaiting.getB().run();
		}
	}

	private void success(Player clicker, V value) {
		Pair<Consumer<V>, Runnable> awaiting = this.awaiting.remove(clicker.getUniqueId());
		if (awaiting != null) {
			awaiting.getA().accept(value);
		}
	}

	// static
	private static final Map<Class<?>, EnumSelectorGUI<?>> selectorCache = new HashMap<>();

	public static <V> void openSelector(Player player, boolean cache, Serializer<V> serializer, Supplier<List<V>> values, Mat icon, Consumer<V> onSelect, Runnable onBack) {
		openSelector(player, cache, serializer, values, icon, null, null, onSelect, onBack);
	}

	public static <V> void openSelector(Player player, boolean cache, Serializer<V> serializer, Supplier<List<V>> values, Mat icon, Function<V, String> customGetValueName, Consumer<V> onSelect, Runnable onBack) {
		openSelector(player, cache, serializer, values, icon, null, customGetValueName, onSelect, onBack);
	}

	public static <V> void openSelector(Player player, boolean cache, Serializer<V> serializer, Supplier<LinkedHashMap<V, Mat>> valuesIcons, Consumer<V> onSelect, Runnable onBack) {
		openSelector(player, cache, serializer, null, null, valuesIcons, null, onSelect, onBack);
	}

	public static <V> void openSelector(Player player, boolean cache, Serializer<V> serializer, Supplier<LinkedHashMap<V, Mat>> valuesIcons, Function<V, String> customGetValueName, Consumer<V> onSelect, Runnable onBack) {
		openSelector(player, cache, serializer, null, null, valuesIcons, customGetValueName, onSelect, onBack);
	}

	public static <V> void openSelector(Player player, boolean cache, Serializer<V> serializer, Supplier<List<V>> values, Mat icon, Supplier<LinkedHashMap<V, Mat>> valuesIcons, Function<V, String> customGetValueName, Consumer<V> onSelect, Runnable onBack) {
		cache = cache && serializer.isRegistered(); // don't cache unregistered serializers
		EnumSelectorGUI gui = !cache ? null : selectorCache.get(serializer.getTypeClass());
		if (gui == null) {
			gui = new EnumSelectorGUI<V>(serializer, values == null ? null : values.get(), icon, valuesIcons == null ? null : valuesIcons.get(), customGetValueName);
			if (cache) {
				selectorCache.put(serializer.getTypeClass(), gui);
			}
		}
		gui.awaiting.put(player.getUniqueId(), Pair.of(onSelect, onBack));
		gui.openFor(player);
	}

}
