package com.guillaumevdn.gcore;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.guillaumevdn.gcore.lib.bukkit.BukkitThread;
import com.guillaumevdn.gcore.lib.chat.JsonMessage;
import com.guillaumevdn.gcore.lib.collection.LowerCaseHashMap;
import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.legacy_npc.NPCManager;
import com.guillaumevdn.gcore.lib.legacy_npc.NpcProtocols;
import com.guillaumevdn.gcore.lib.player.MojangUtils;
import com.guillaumevdn.gcore.lib.plugin.PluginUtils;
import com.guillaumevdn.gcore.lib.reflection.ReflectionObject;
import com.guillaumevdn.gcore.lib.string.Text;
import com.guillaumevdn.gcore.lib.tuple.Pair;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

/**
 * @author GuillaumeVDN
 */
public class WorkerGCore {

	// npc manager
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

	// offline players
	private LowerCaseHashMap<Pair<UUID, String>> offlinePlayersUUIDs = new LowerCaseHashMap<>();

	public void registerOfflinePlayer(String name, UUID uuid) {
		offlinePlayersUUIDs.put(name, Pair.of(uuid, name));
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

	// await inputs
	private Map<UUID, Pair<Consumer<String>, Runnable>> awaitingChats = new HashMap<>();
	private Set<UUID> awaitingLocationsCancelChat = new HashSet<>();
	private Map<UUID, Pair<Consumer<Location>, Runnable>> awaitingLocations = new HashMap<>();
	private Set<UUID> awaitingItemsCancelChat = new HashSet<>();
	private Map<UUID, Pair<Consumer<ItemStack>, Runnable>> awaitingItems = new HashMap<>();

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

	public Pair<Consumer<Location>, Runnable> consumeAwaitingLocations(Player player) {
		return awaitingLocations.remove(player.getUniqueId());
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
		player.closeInventory();
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
		player.closeInventory();
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
		Pair<Consumer<Location>, Runnable> currentLocation = awaitingLocations.remove(player.getUniqueId());
		if (currentChat != null && currentChat.getB() != null) currentChat.getB().run();
		if (currentLocation != null && currentLocation.getB() != null) currentLocation.getB().run();
		// ask
		if (message != null) {
			message.replace("{cancel}", () -> TextGeneric.textCancel.parseLine()).send(player);
		}
		awaitingLocations.put(player.getUniqueId(), Pair.of(onSelect, onCancel));
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

	// game profile / skull items
	private Map<UUID, GameProfile> profileCache = new HashMap<>();
	private final GameProfile DEFAULT_PROFILE = fromTexture(UUID.randomUUID(), "Steve", "ewogICJ0aW1lc3RhbXAiIDogMTYwODAzMTQ1MTk2MSwKICAicHJvZmlsZUlkIiA6ICJlYzU2MTUzOGYzZmQ0NjFkYWZmNTA4NmIyMjE1NGJjZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJBbGV4IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzFhNGFmNzE4NDU1ZDRhYWI1MjhlN2E2MWY4NmZhMjVlNmEzNjlkMTc2OGRjYjEzZjdkZjMxOWE3MTNlYjgxMGIiCiAgICB9CiAgfQp9", "J4QNnTc0p9NbhK5zkD5pd+N2lKtH/0y884MOFQyxVGcUYDuSaa5XkkoLBTe/iaOICjarfwd1gLgNNg8XqAW3imb7bsOlN1D+3A3POkjlrdTKgLqFU9ouGwhdhh6rbMa6Sz6Ir6b8bgbeniEKYQxzOjyLbZwaDfJgXycPuQ7dnXiycVrgMYAcSHv3FH/K2Fm4RfjeIWJctWWsgpZdxmX9E0o83LEKlqEH6bT1aMTVnWJDRcak9A/OR6iSwz6ABrsWzARtlwi10mVwZUEQovByOo+UHxGfQErWm6kXbn7U/faDI3Gfq3ovvP/KyhGjB64gYQN0OWFt99N8FM+jWnPuRxVZlH0jx0Sxe2PGPvNy/lwD4gDbJfKScMSsapYZqbTenZ4QakqPVfGYI23JdQMC3IcTjuz4hHlKNjF+AgGZEqz/gDyKUT+95eOJH+8Kr0+KCzmKaL2zKY1/or7zcCsaeAyY/M+trfr6nARfFVBInHVYLHkOPkRSj3xvjNKW1sP4szJvxhQ/V968ipydRTlnQ67H8J8Laz5TDxxB2uQlRkGi6bvk1T7LSNNY/GSTovJVatR9adxTjbndby+DmrfFb666XjZ6kJshwEsudnQs2BU/jG9zi3tvCKoma/d6LbcSr2hfSYCl+ErWCFDSuVB4zJZa5rOLGW2Ea5s1ePFeHiM=");

	private void fetchProfile(UUID ownerUUID, String ownerName, String skinData, String skinSignature, Consumer<GameProfile> callback) {
		// has data
		if (skinData != null) {
			callback.accept(fromTexture(ownerUUID, ownerName, skinData, skinSignature));
		}
		// no data
		else if (ownerUUID != null || ownerName != null) {
			// maybe fix UUID if player connected once
			if (ownerUUID == null) {
				OfflinePlayer pl = Bukkit.getOfflinePlayer(ownerName);
				if (pl != null && pl.getLastPlayed() > 0L) {
					ownerUUID = pl.getUniqueId();
				}
			}
			// cached
			GameProfile cached = profileCache.get(ownerUUID);
			if (cached != null) {
				callback.accept(cached);
				return;
			}
			// find by UUID or fetch name
			Consumer<UUID> fetcher = uuid -> {
				BukkitThread.ASYNC.operate(() -> {
					GameProfile profile = MojangUtils.fetchProfile(uuid);  // nullable
					if (profile == null) {
						profile = DEFAULT_PROFILE;
					}
					profileCache.put(uuid, profile);
					callback.accept(profile);
				}, error -> {
					error.printStackTrace();
					callback.accept(null);
				});
			};
			if (ownerUUID != null) {
				fetcher.accept(ownerUUID);
			} else {
				BukkitThread.ASYNC.operate(() -> {
					fetcher.accept(MojangUtils.fetchUUID(ownerName));
				}, error -> {
					error.printStackTrace();
					callback.accept(null);
				});
			}
		}
	}

	private GameProfile fromTexture(UUID ownerUUID, String ownerName, String skinData, String skinSignature) {
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
			BukkitThread.ASYNC.operate(() -> {
				UUID uuid = MojangUtils.fetchUUID(owner.getName());
				run.accept(uuid != null ? uuid : owner.getUniqueId());
			});
		} else {
			run.accept(owner.getUniqueId());
		}


	}

	// static
	public static WorkerGCore inst() {
		return GCore.inst().getWorler();
	}

}
