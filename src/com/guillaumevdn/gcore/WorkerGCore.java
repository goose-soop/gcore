package com.guillaumevdn.gcore;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.guillaumevdn.gcore.lib.chat.JsonMessage;
import com.guillaumevdn.gcore.lib.collection.LowerCaseHashMap;
import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.concurrency.RWHashMap;
import com.guillaumevdn.gcore.lib.legacy_npc.NPCManager;
import com.guillaumevdn.gcore.lib.legacy_npc.NpcProtocols;
import com.guillaumevdn.gcore.lib.player./*MojangUtils*/MineToolsUtils;
import com.guillaumevdn.gcore.lib.plugin.PluginUtils;
import com.guillaumevdn.gcore.lib.reflection.ReflectionObject;
import com.guillaumevdn.gcore.lib.string.Text;
import com.guillaumevdn.gcore.lib.tuple.Pair;
import com.guillaumevdn.gcore.lib.tuple.Triple;
import com.guillaumevdn.gcore.lib.wrapper.Wrapper;
import com.guillaumevdn.gcore.lib.wrapper.WrapperString;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

/**
 * @author GuillaumeVDN
 */
public class WorkerGCore {

	// ----- npc manager
	private NPCManager npcManager = null;

	public WorkerGCore() {
		try {
			if (Version.ATLEAST_1_9 && PluginUtils.isPluginEnabled("ProtocolLib")) {
				new NpcProtocols(); // init npc protocols ; this sets the instance field if success
				(npcManager = new NPCManager()).enable();
				GCore.inst().getMainLogger().info("Enabled NPC manager with ProtocolLib");
			}
		} catch (Throwable exception) {
			GCore.inst().getMainLogger().error("Couldn't enable NPC manager with ProtocolLib", exception);
			npcManager = null;
		}
	}

	public NPCManager getNpcManager() {
		return npcManager;
	}

	// ----- offline players
	private final LowerCaseHashMap<Pair<UUID, String>> offlinePlayersUUIDs = new LowerCaseHashMap<>(10, 0.75f);
	private final RWHashMap<UUID, String> offlinePlayersNames = new RWHashMap<>(10, 0.75f);

	public void registerOfflinePlayer(String name, UUID uuid) {
		if (name != null) {
			offlinePlayersUUIDs.put(name, Pair.of(uuid, name));
		}
	}

	public Pair<UUID, String> getOfflinePlayer(String name) {
		return offlinePlayersUUIDs.get(name);
	}

	public Stream<String> getOfflinePlayersNamesLowercase() {
		return offlinePlayersUUIDs.keySet().stream();
	}

	public Stream<String> getOfflinePlayersNames() {
		return offlinePlayersUUIDs.values().stream().map(Pair::getB);
	}

	public String getOrFetchOfflinePlayerNameAsync(OfflinePlayer player) {
		final WrapperString wrapper = WrapperString.of("<?>");  // ewww
		getOrFetchOfflinePlayerName(player, name -> {
			wrapper.set(name);
		});
		return wrapper.get();
	}

	public void getOrFetchOfflinePlayerName(OfflinePlayer player, Consumer<String> callback) {
		String name = player.getName();
		if (name == null) {
			name = offlinePlayersNames.get(player.getUniqueId());
		}
		if (name != null) {
			callback.accept(name);
		} else {
			fetchProfile(player.getUniqueId(), null, null, null, profile -> {
				final String n = profile != null && profile.getName() != null ? profile.getName() : "<name?>";
				offlinePlayersNames.put(player.getUniqueId(), n);
				callback.accept(n);
			});
		}
	}

	@Nullable
	public UUID getOrFetchOfflinePlayerUUIDAsync(String name) {
		final Wrapper<UUID> wrapper = Wrapper.of(null);  // ewww
		getOrFetchOfflinePlayerUUID(name, uuid -> {
			wrapper.set(uuid);
		});
		return wrapper.get();
	}

	public void getOrFetchOfflinePlayerUUID(String name, Consumer<UUID> callback) {
		fetchProfile(null, name, null, null, profile -> {
			final UUID uuid = profile != null ? profile.getId() : null;
			final String n = profile != null && profile.getName() != null ? profile.getName() : "<name?>";
			offlinePlayersNames.put(uuid, n);
			callback.accept(uuid);
		});
	}

	// ----- await inputs
	private Map<UUID, Pair<Consumer<String>, Runnable>> awaitingChats = new HashMap<>(1);
	private Set<UUID> awaitingLocationsCancelChat = new HashSet<>(1);
	private Map<UUID, Triple<Consumer<Location>, Runnable, Long>> awaitingLocations = new HashMap<>(1);  // third is the timestamp when it was asked ; because the GUI button is often shift + click, the GUI is closed and so the player shifts automatically quickly ; set a 1s delay
	private Set<UUID> awaitingItemsCancelChat = new HashSet<>(1);
	private Map<UUID, Pair<Consumer<ItemStack>, Runnable>> awaitingItems = new HashMap<>(1);

	public boolean hasAwaitingChat(Player player) {
		return player != null && awaitingChats.containsKey(player.getUniqueId());
	}

	public Pair<Consumer<String>, Runnable> consumeAwaitingChat(Player player) {
		return awaitingChats.remove(player.getUniqueId());
	}

	public boolean hasAwaitingLocationCancelChat(Player player) {
		return player != null && awaitingLocationsCancelChat.contains(player.getUniqueId());
	}

	public boolean consumeAwaitingLocationCancelChat(Player player) {
		return awaitingLocationsCancelChat.remove(player.getUniqueId());
	}

	public Triple<Consumer<Location>, Runnable, Long> consumeAwaitingLocations(Player player) {
		Triple<Consumer<Location>, Runnable, Long> awaiting = awaitingLocations.get(player.getUniqueId());
		if (awaiting != null && System.currentTimeMillis() - awaiting.getC() > 1000L) {
			awaitingLocations.remove(player.getUniqueId());
			return awaiting;
		}
		return null;
	}

	public boolean hasAwaitingItemCancelChat(Player player) {
		return player != null && awaitingItemsCancelChat.contains(player.getUniqueId());
	}

	public boolean consumeAwaitingItemCancelChat(Player player) {
		return awaitingItemsCancelChat.remove(player.getUniqueId());
	}

	public Pair<Consumer<ItemStack>, Runnable> consumeAwaitingItems(Player player) {
		return awaitingItems.remove(player.getUniqueId());
	}

	public void awaitChat(Player player, Text message, Consumer<String> onChat, Runnable onCancel) {
		// cancel current
		Pair<Consumer<String>, Runnable> currentChat = awaitingChats.remove(player.getUniqueId());
		if (currentChat != null && currentChat.getB() != null) currentChat.getB().run();

		// ask
		GCore.inst().operateSync(() -> {
			player.closeInventory();
		});
		if (message != null) {
			message.replace("{cancel}", () -> TextGeneric.textCancel.parseLine()).send(player);
		}
		awaitingChats.put(player.getUniqueId(), Pair.of(onChat, onCancel));
	}

	public void awaitChatWithSuggestedValue(Player player, Text message, String suggestValue, Consumer<String> onChat, Runnable onCancel) {
		// cancel current
		Pair<Consumer<String>, Runnable> currentChat = awaitingChats.remove(player.getUniqueId());
		if (currentChat != null && currentChat.getB() != null) currentChat.getB().run();

		// ask
		GCore.inst().operateSync(() -> {
			player.closeInventory();
		});
		if (suggestValue != null && !suggestValue.isEmpty()) {
			JsonMessage json = new JsonMessage();
			json.append(message.replace("{cancel}", () -> TextGeneric.textCancel.parseLine()).parseLine()).setSuggest(suggestValue).build();
			json.send(player);
		} else {
			message.replace("{cancel}", () -> TextGeneric.textCancel.parseLine()).send(player);
		}
		awaitingChats.put(player.getUniqueId(), Pair.of(onChat, onCancel));
	}

	public void awaitLocation(Player player, Text message, Consumer<Location> onSelect, Runnable onCancel) {
		// cancel current
		Pair<Consumer<String>, Runnable> currentChat = awaitingChats.remove(player.getUniqueId());
		Triple<Consumer<Location>, Runnable, Long> currentLocation = awaitingLocations.remove(player.getUniqueId());
		if (currentChat != null && currentChat.getB() != null) currentChat.getB().run();
		if (currentLocation != null && currentLocation.getB() != null) currentLocation.getB().run();
		// ask
		if (message != null) {
			message.replace("{cancel}", () -> TextGeneric.textCancel.parseLine()).send(player);
		}
		awaitingLocations.put(player.getUniqueId(), Triple.of(onSelect, onCancel, System.currentTimeMillis()));
		awaitingLocationsCancelChat.add(player.getUniqueId());
	}

	public void awaitItem(Player player, Text message, Consumer<ItemStack> onSelect, Runnable onCancel) {
		// cancel current
		Pair<Consumer<String>, Runnable> currentChat = awaitingChats.remove(player.getUniqueId());
		Pair<Consumer<ItemStack>, Runnable> currentLocation = awaitingItems.remove(player.getUniqueId());
		if (currentChat != null && currentChat.getB() != null) currentChat.getB().run();
		if (currentLocation != null && currentLocation.getB() != null) currentLocation.getB().run();
		// ask
		if (message != null) {
			message.replace("{cancel}", () -> TextGeneric.textCancel.parseLine()).send(player);
		}
		awaitingItems.put(player.getUniqueId(), Pair.of(onSelect, onCancel));
		awaitingItemsCancelChat.add(player.getUniqueId());
	}

	// ----- game profile / skull items
	private static final Predicate<String> USERNAME_MATCHER = Pattern.compile("\\w{3,16}").asPredicate();
	private final static GameProfile DEFAULT_PROFILE = fromTexture(UUID.randomUUID(), "Steve", "ewogICJ0aW1lc3RhbXAiIDogMTYwODAzMTQ1MTk2MSwKICAicHJvZmlsZUlkIiA6ICJlYzU2MTUzOGYzZmQ0NjFkYWZmNTA4NmIyMjE1NGJjZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJBbGV4IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzFhNGFmNzE4NDU1ZDRhYWI1MjhlN2E2MWY4NmZhMjVlNmEzNjlkMTc2OGRjYjEzZjdkZjMxOWE3MTNlYjgxMGIiCiAgICB9CiAgfQp9", "J4QNnTc0p9NbhK5zkD5pd+N2lKtH/0y884MOFQyxVGcUYDuSaa5XkkoLBTe/iaOICjarfwd1gLgNNg8XqAW3imb7bsOlN1D+3A3POkjlrdTKgLqFU9ouGwhdhh6rbMa6Sz6Ir6b8bgbeniEKYQxzOjyLbZwaDfJgXycPuQ7dnXiycVrgMYAcSHv3FH/K2Fm4RfjeIWJctWWsgpZdxmX9E0o83LEKlqEH6bT1aMTVnWJDRcak9A/OR6iSwz6ABrsWzARtlwi10mVwZUEQovByOo+UHxGfQErWm6kXbn7U/faDI3Gfq3ovvP/KyhGjB64gYQN0OWFt99N8FM+jWnPuRxVZlH0jx0Sxe2PGPvNy/lwD4gDbJfKScMSsapYZqbTenZ4QakqPVfGYI23JdQMC3IcTjuz4hHlKNjF+AgGZEqz/gDyKUT+95eOJH+8Kr0+KCzmKaL2zKY1/or7zcCsaeAyY/M+trfr6nARfFVBInHVYLHkOPkRSj3xvjNKW1sP4szJvxhQ/V968ipydRTlnQ67H8J8Laz5TDxxB2uQlRkGi6bvk1T7LSNNY/GSTovJVatR9adxTjbndby+DmrfFb666XjZ6kJshwEsudnQs2BU/jG9zi3tvCKoma/d6LbcSr2hfSYCl+ErWCFDSuVB4zJZa5rOLGW2Ea5s1ePFeHiM=");
	private RWHashMap<UUID, GameProfile> profileCache = new RWHashMap<>(10, 1f);

	public void fetchProfile(@Nullable UUID ownerUUID, @Nullable String ownerName, Consumer<GameProfile> callback) {
		fetchProfile(ownerUUID, ownerName, null, null, callback);
	}

	public void fetchProfile(@Nullable UUID ownerUUID, @Nullable String ownerName, @Nullable String skinData, @Nullable String skinSignature, Consumer<GameProfile> callback) {
		if (ownerName != null && !USERNAME_MATCHER.test(ownerName)) {
			ownerName = null;
		}

		// has data already
		if (skinData != null) {
			callback.accept(fromTexture(ownerUUID, ownerName, skinData, skinSignature));
		}
		// no data, fetch it
		else if (ownerUUID != null || ownerName != null) {
			// disabled in config, don't fetch at all
			if (ConfigGCore.dontFetchPlayerProfiles) {
				callback.accept(DEFAULT_PROFILE);
				return;
			}

			// determine UUID to use for fetching
			final UUID correctUUID;
			if (Bukkit.getOnlineMode()) {
				// ... online mode : either use said UUID or attempt to check server name cache
				if (ownerUUID != null) {
					correctUUID = ownerUUID;
				} else {
					final OfflinePlayer pl = Bukkit.getOfflinePlayer(ownerName);
					if (pl != null && pl.getLastPlayed() > 0L) {
						correctUUID = pl.getUniqueId();
					} else {
						correctUUID = null;
					}
				}
			} else {
				// ... offline mode : force fetching of UUID if has a name
				if (ownerName != null) {
					correctUUID = null;
				} else {
					correctUUID = ownerUUID;
				}
			}

			// no correct UUID, fetch it from name
			if (correctUUID == null) {
				if (ownerName == null) {
					callback.accept(null);
				} else {
					final String ownerNameF = ownerName;
					GCore.inst().operateAsync(() -> {
						final UUID fetchedUUID = MineToolsUtils.fetchUUID(ownerNameF);
						fetchProfileByUUID(fetchedUUID != null ? fetchedUUID : ownerUUID, ownerUUID, callback);
					}, error -> {
						error.printStackTrace();
						callback.accept(null);
					});
				}
				return;
			}

			// has correct UUID, use it
			final GameProfile cached = profileCache.get(ownerUUID);
			if (cached != null) {
				callback.accept(cached);
			} else {
				fetchProfileByUUID(correctUUID, ownerUUID, callback);
			}
		}
		// no data and nothing to fetch from
		else {
			callback.accept(DEFAULT_PROFILE);
		}
	}

	private void fetchProfileByUUID(UUID correctUUID, UUID originalUUID, Consumer<GameProfile> callback) {
		GCore.inst().operateAsync(() -> {
			GameProfile profile = correctUUID == null ? null : MineToolsUtils.fetchProfile(correctUUID);  // nullable
			if (profile == null) {
				profile = DEFAULT_PROFILE;
			}
			profileCache.put(correctUUID, profile);
			if (originalUUID != null && !originalUUID.equals(correctUUID)) {
				profileCache.put(correctUUID, profile);
			}
			callback.accept(profile);
		}, error -> {
			error.printStackTrace();
			callback.accept(null);
		});
	}

	private static GameProfile fromTexture(UUID ownerUUID, String ownerName, String skinData, String skinSignature) {
		GameProfile profile = new GameProfile(ownerUUID != null ? ownerUUID : UUID.randomUUID(), ownerName != null ? ownerName : "SomeGuy");
		profile.getProperties().put("textures", skinSignature != null ? new Property("textures", skinData, skinSignature) : new Property("textures", skinData));
		return profile;
	}

	public void buildPlayerHead(OfflinePlayer owner, String name, List<String> lore, Consumer<ItemStack> done) {
		Consumer<UUID> run = uuid -> {
			fetchProfile(uuid, owner.getName(), null, null, profile -> {
				try {
					ItemStack item = CommonMats.PLAYER_HEAD.newStack();
					SkullMeta meta = (SkullMeta) item.getItemMeta();
					ReflectionObject.of(meta).setField("profile", profile);
					if (name != null) meta.setDisplayName(name);
					if (lore != null) meta.setLore(lore);
					item.setItemMeta(meta);
					done.accept(item);
				} catch (Throwable error) {
					GCore.inst().getMainLogger().error("An error occured when building head item for " + owner.getName(), error);
				}
			});
		};
		if (!Bukkit.getOnlineMode()) {  // fetch UUID by name if it's an offline server, to get the correct skin
			GCore.inst().operateAsync(() -> {
				UUID uuid = /*MojangUtils*/MineToolsUtils.fetchUUID(owner.getName());
				run.accept(uuid != null ? uuid : owner.getUniqueId());
			});
		} else {
			run.accept(owner.getUniqueId());
		}


	}

	// ----- static
	public static WorkerGCore inst() {
		return GCore.inst().getWorler();
	}

}
