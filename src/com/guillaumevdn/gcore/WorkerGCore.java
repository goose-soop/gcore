package com.guillaumevdn.gcore;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.guillaumevdn.gcore.lib.bukkit.BukkitThread;
import com.guillaumevdn.gcore.lib.chat.JsonMessage;
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

	public void fetchProfile(UUID ownerUUID, String ownerName, String skinData, String skinSignature, Consumer<GameProfile> callback) {
		// has data
		if (skinData != null) {
			GameProfile profile = new GameProfile(ownerUUID != null ? ownerUUID : UUID.randomUUID(), ownerName != null ? ownerName : "SomeGuy");
			profile.getProperties().put("textures", skinSignature != null ? new Property("textures", skinData, skinSignature) : new Property("textures", skinData));
			callback.accept(profile);
		}
		// no data
		else if (ownerUUID != null || ownerName != null) {
			// maybe fix UUID if player connected once
			if (ownerUUID == null) {
				OfflinePlayer pl = Bukkit.getOfflinePlayer(ownerName);
				if (pl != null) {
					ownerUUID = pl.getUniqueId();
				}
			}
			// find by UUID or fetch name
			if (ownerUUID != null) {
				fetchProfile(ownerUUID, callback);
			} else {
				BukkitThread.ASYNC.operate(() -> {
					fetchProfile(MojangUtils.fetchUUID(ownerName), callback);
				}, error -> {
					error.printStackTrace();
					callback.accept(null);
				});
			}
		}
	}

	private void fetchProfile(UUID ownerUUID, Consumer<GameProfile> callback) {
		GameProfile cached = profileCache.get(ownerUUID);
		if (cached != null) {
			callback.accept(cached);
		} else {
			UUID uuid = ownerUUID;  // Widega
			BukkitThread.ASYNC.operate(() -> {
				GameProfile profile = MojangUtils.fetchProfile(uuid); // not null
				profileCache.put(uuid, profile);
			}, error -> {
				error.printStackTrace();
				callback.accept(null);
			});
		}
	}

	public void buildPlayerHead(OfflinePlayer owner, String name, List<String> lore, Consumer<ItemStack> done) {
		// is cached
		GameProfile cached = profileCache.get(owner.getUniqueId());
		if (cached != null) {
			ItemStack item = buildPlayerHead(owner, cached, name, lore);
			if (item != null) {
				done.accept(item);
			}
			return;
		}
		// not cached, fetch it
		BukkitThread.ASYNC.operate(() -> {
			try {
				GameProfile profile = MojangUtils.fetchProfile(owner.getUniqueId()); // not null
				profileCache.put(owner.getUniqueId(), profile);
				done.accept(buildPlayerHead(owner, profile, name, lore));
			} catch (Throwable error) {
				GCore.inst().getMainLogger().error("An error occured when fetching game profile to build head item for " + owner.getName(), error);
			}
		});
	}

	private ItemStack buildPlayerHead(OfflinePlayer owner, GameProfile profile, String name, List<String> lore) {
		try {
			ItemStack item = CommonMats.PLAYER_HEAD.newStack();
			SkullMeta meta = (SkullMeta) item.getItemMeta();
			ReflectionObject.of(meta).setField("profile", profile);
			if (name != null) meta.setDisplayName(name);
			if (lore != null) meta.setLore(lore);
			item.setItemMeta(meta);
			return item;
		} catch (Throwable error) {
			GCore.inst().getMainLogger().error("An error occured when building head item for " + owner.getName(), error);
			return null;
		}
	}

	// static
	public static WorkerGCore inst() {
		return GCore.inst().getWorler();
	}

}
