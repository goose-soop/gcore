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

import com.guillaumevdn.gcore.ConfigGCore;
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
import com.guillaumevdn.gcore.lib.item.ItemUtils;
import com.guillaumevdn.gcore.lib.serialization.Serializer;
import com.guillaumevdn.gcore.lib.tuple.Pair;

/**
 * @author GuillaumeVDN
 */
public class EnumSelectorGUI<V> extends GUI {

	private static final Set<String> INVISIBLE_MATERIALS = CollectionUtils.asSet("ACACIA_WALL_SIGN", "ATTACHED_MELON_STEM", "ATTACHED_PUMPKIN_STEM", "BAMBOO_SAPLING", "BEETROOTS", "BIRCH_WALL_SIGN", "BLACK_WALL_BANNER", "BLUE_WALL_BANNER", "BRAIN_CORAL_WALL_FAN", "BROWN_WALL_BANNER", "BUBBLE_COLUMN", "BUBBLE_CORAL_WALL_FAN", "CARROTS", "COCOA", "CREEPER_WALL_HEAD", "CRIMSON_WALL_SIGN", "CYAN_WALL_BANNER", "DARK_OAK_WALL_SIGN", "DEAD_BRAIN_CORAL_WALL_FAN", "DEAD_BUBBLE_CORAL_WALL_FAN", "DEAD_FIRE_CORAL_WALL_FAN", "DEAD_HORN_CORAL_WALL_FAN", "DEAD_TUBE_CORAL_WALL_FAN", "DRAGON_WALL_HEAD", "END_GATEWAY", "END_PORTAL", "FIRE", "FIRE_CORAL_WALL_FAN", "FROSTED_ICE", "GRAY_WALL_BANNER", "HORN_CORAL_WALL_FAN", "JUNGLE_WALL_SIGN", "KELP_PLANT", "LAVA", "LIGHT_BLUE_WALL_BANNER", "LIME_WALL_BANNER", "LIGHT_GRAY_WALL_BANNER", "MAGENTA_WALL_BANNER", "MELON_STEM", "MOVING_PISTON", "NETHER_PORTAL", "OAK_WALL_SIGN", "ORANGE_WALL_BANNER", "PINK_WALL_BANNER", "PISTON_HEAD", "PLAYER_WALL_HEAD", "POTATOES", "POTTED_ACACIA_SAPLING", "POTTED_ALLIUM", "POTTED_AZURE_BLUET", "POTTED_BIRCH_SAPLING", "POTTED_BLUE_ORCHID", "POTTED_BROWN_MUSHROOM", "POTTED_CACTUS", "POTTED_CORNFLOWER", "POTTED_CRIMSON_FUNGUS", "POTTED_CRIMSON_ROOTS", "POTTED_DANDELION", "POTTED_DARK_OAK_SAPLING", "POTTED_DEAD_BUSH", "POTTED_FERN", "POTTED_JUNGLE_SAPLING", "POTTED_LILY_OF_THE_VALLEY", "POTTED_OAK_SAPLING", "POTTED_ORANGE_TULIP", "POTTED_OXEYE_DAISY", "POTTED_PINK_TULIP", "POTTED_POPPY", "POTTED_RED_MUSHROOM", "POTTED_RED_TULIP", "POTTED_SPRUCE_SAPLING", "POTTED_WARPED_FUNGUS", "POTTED_WARPED_ROOTS", "POTTED_WHITE_TULIP", "POTTED_WITHER_ROSE", "PUMPKIN_STEM", "PURPLE_WALL_BANNER", "REDSTONE_WALL_TORCH", "REDSTONE_WIRE", "RED_WALL_BANNER", "SKELETON_WALL_SKULL", "SOUL_FIRE", "SOUL_WALL_TORCH", "SPRUCE_WALL_SIGN", "STATIONARY_LAVA", "STATIONARY_WATER", "SWEET_BERRY_BUSH", "TALL_SEAGRASS", "TRIPWIRE", "TUBE_CORAL_WALL_FAN", "TWISTING_VINES_PLANT", "WALL_TORCH", "WARPED_WALL_SIGN", "WATER", "WEEPING_VINES_PLANT", "WHITE_WALL_BANNER", "WITHER_SKELETON_WALL_SKULL", "YELLOW_WALL_BANNER", "ZOMBIE_WALL_HEAD");

	private Serializer<V> serializer;
	private List<V> values;
	private LinkedHashMap<V, Mat> valuesIcons;
	private Function<V, String> customGetValueName;
	private Mat icon;

	private Map<UUID, Pair<Consumer<V>, Runnable>> awaiting = new HashMap<>(1);
	private Set<UUID> awaitingSearchChat = new HashSet<>(1);

	public EnumSelectorGUI(Serializer<V> serializer, List<V> values, Mat icon, LinkedHashMap<V, Mat> valuesIcons, Function<V, String> customGetValueName) {
		super(GCore.inst(), "editor_select_" + serializer.getTypeClass().getSimpleName(), TextEditorGeneric.guiSelectTitle.parseLine(), GUIType.CHEST_6_ROW, Option.DONT_UNREGISTER_ON_CLOSE);
		this.serializer = serializer;
		this.values = values;
		this.icon = icon;
		this.valuesIcons = valuesIcons;
		this.customGetValueName = customGetValueName;
	}

	// ----- fill
	@Override
	protected boolean doFill() {
		for (V value : (valuesIcons != null ? valuesIcons.keySet() : values)) {
			try {
				String valueName = customGetValueName != null ? customGetValueName.apply(value) : serializer.serialize(value);
				Mat valueIconType = value instanceof Mat ? (INVISIBLE_MATERIALS.contains(valueName) ? CommonMats.BEDROCK : (Mat) value) : (valuesIcons != null ? valuesIcons.get(value) : this.icon);
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
						openFor(call.getClicker(), call.getGUI().getFromCall(call.getClicker()));
					} else if (matching.size() != 1) {
						TextEditorGeneric.messageElementBasicEditSearchTooManyMatches.replace("{value}", () -> value).send(call.getClicker());
						openFor(call.getClicker(), call.getGUI().getFromCall(call.getClicker()));
					} else {
						TextEditorGeneric.messageElementBasicEditSearchMatch.replace("{value}", () -> matching.get(0)).send(call.getClicker());
						success(call.getClicker(), matching.get(0));
					}
				}
			}, () -> call.reopenGUI());
		}));
		setPersistentItem(new GUIItem("back", getType().getBackItemSlot(), ConfigGCore.backItem, call -> {
			onBack(call.getClicker());
		}));
		return true;
	}

	@Override
	public void onPlayerInventoryClick(ClickCall call, ItemStack item) {
		if (serializer.getTypeClass().equals(Mat.class)) {
			Mat mat = Mat.fromItem(item).orElse(null);
			if (mat != null) {
				success(call.getClicker(), (V) mat);
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
		/*try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File("C:/Users/vande/Desktop/w.txt"), true))) {
			writer.write("\"" + value + "\", ");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
		 */
		Pair<Consumer<V>, Runnable> awaiting = this.awaiting.remove(clicker.getUniqueId());
		if (awaiting != null) {
			awaiting.getA().accept(value);
		}
	}

	// ----- static
	private static final Map<Class<?>, EnumSelectorGUI<?>> selectorCache = new HashMap<>(1);

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
		cache = cache && serializer.isRegistered(); // don't valuesCache unregistered serializers
		EnumSelectorGUI gui = !cache ? null : selectorCache.get(serializer.getTypeClass());
		if (gui == null) {
			gui = new EnumSelectorGUI<V>(serializer, values == null ? null : values.get(), icon, valuesIcons == null ? null : valuesIcons.get(), customGetValueName);
			if (cache) {
				selectorCache.put(serializer.getTypeClass(), gui);
			}
		}
		gui.awaiting.put(player.getUniqueId(), Pair.of(onSelect, onBack));
		gui.openFor(player, null);
	}

}
