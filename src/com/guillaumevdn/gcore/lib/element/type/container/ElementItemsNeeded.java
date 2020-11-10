package com.guillaumevdn.gcore.lib.element.type.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.bukkit.BukkitThread;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.container.ContainerElement;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementBoolean;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementInteger;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementText;
import com.guillaumevdn.gcore.lib.element.type.list.ElementItemMatchList;
import com.guillaumevdn.gcore.lib.element.type.list.ItemMatch;
import com.guillaumevdn.gcore.lib.item.ItemUtils;
import com.guillaumevdn.gcore.lib.string.Text;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public class ElementItemsNeeded extends ContainerElement {

	private ElementItemMatchList items = addItemMatchList("items", Need.optional(), true, TextEditorGeneric.descriptionItemsNeededItems);
	private ElementInteger count = addInteger("count", Need.optional(999), 0, TextEditorGeneric.descriptionItemsNeededCount);
	private ElementBoolean inHand = addBoolean("in_hand", Need.optional(false), TextEditorGeneric.descriptionItemsNeededInHand);
	private ElementInteger inHandSlot = addInteger("in_hand_slot", Need.optional(-1), TextEditorGeneric.descriptionItemsNeededInHandSlot);
	private ElementBoolean take = addBoolean("take", Need.optional(false), TextEditorGeneric.descriptionItemsNeededTake);
	private ElementText errorMessage = addText("error_message", Need.optional(), TextEditorGeneric.descriptionItemsNeededErrorMessage);

	public ElementItemsNeeded(Element parent, String id, Need need, Text editorDescription) {
		super("items needed", parent, id, need, editorDescription);
	}

	// get
	public ElementItemMatchList getItems() {
		return items;
	}

	public ElementBoolean getInHand() {
		return inHand;
	}

	public ElementInteger getInHandSlot() {
		return inHandSlot;
	}

	public ElementInteger getCount() {
		return count;
	}

	public ElementBoolean getTake() {
		return take;
	}

	public ElementText getErrorMessage() {
		return errorMessage;
	}

	// ref
	private WeakHashMap<Object, Map<Player, List<ItemMatch>>> matchingItems = new WeakHashMap<>();

	public boolean match(Object ref, Replacer replacer, List<UUID> players, List<Player> playersOnline) {
		return match(ref, replacer, players, playersOnline, true);
	}

	public boolean match(Object ref, Replacer replacer, List<UUID> players, List<Player> playersOnline, boolean sendErrorMessage) {
		// not everyone's there
		if (players.size() != playersOnline.size()) {
			return false;
		}
		// no items configured
		List<ItemMatch> items = getItems().parse(replacer).orEmptyList();
		if (items.isEmpty()) {
			return true;
		}
		int neededCount = getCount().parse(replacer).orElse(999);
		if (neededCount > items.size()) neededCount = items.size();
		// needed count is zero : the player musn't have any of the items
		if (neededCount <= 0) {
			for (Player player : playersOnline) {
				for (ItemMatch item : items) {
					if (ItemUtils.has(player, item.getItem(), item.getGoal(), item.getCheck())) {
						return false;
					}
				}
			}
			return true;
		}
		// check players
		boolean take = getTake().parse(replacer).orElse(false);
		boolean inHand = getInHand().parse(replacer).orElse(false);
		for (Player player : playersOnline) {
			// in hand : look for first matching item in his hand
			if (inHand) {
				neededCount = 1;
				// invalid slot : don't match anyways
				Integer slot = getInHandSlot().parse(replacer).orNull();
				if (slot != null && slot >= 0 && slot <= 8 && slot != player.getInventory().getHeldItemSlot()) {
				}
				// no specific slot, or it maches
				else {
					ItemStack playerInHand = player.getItemInHand();
					if (playerInHand != null) {
						for (ItemMatch item : items) {
							if (ItemUtils.match(playerInHand, item.getItem(), item.getCheck()) && playerInHand.getAmount() >= item.getGoal()) {
								if (take) {
									matchingItems.computeIfAbsent(ref, __ -> new HashMap<>()).computeIfAbsent(player, __ -> new ArrayList<>()).add(item);
								}
								neededCount = 0;
								break;
							}
						}
					}
				}
			}
			// not in hand : look for all matching items in his inventory
			else {
				for (ItemMatch match : items) {
					if (ItemUtils.has(player, match.getItem(), match.getGoal(), match.getCheck())) {
						if (take) {
							matchingItems.computeIfAbsent(ref, __ -> new HashMap<>()).computeIfAbsent(player, __ -> new ArrayList<>()).add(match);
						}
						if (--neededCount == 0) {
							break;
						}
					}
				}
			}
			// there are remaining items, no success
			if (neededCount > 0) {
				if (sendErrorMessage) {
					getErrorMessage().parse(replacer).ifPresentDo(errorMessage -> errorMessage.send(playersOnline));
				}
				return false;
			}
		}
		// we good
		return true;
	}

	public void takeIfNeeded(Object ref, Replacer replacer, List<Player> players) {
		Map<Player, List<ItemMatch>> items = matchingItems.remove(ref);
		if (items != null) {
			boolean inHand = getInHand().parse(replacer).orElse(false);
			BukkitThread.SYNC.operate(() -> {
				players.forEach(player -> {
					List<ItemMatch> playerMatches = items.remove(player);
					if (playerMatches != null) {
						// in hand : replace item on his hand
						if (inHand) {
							ItemMatch item = playerMatches.get(0);
							ItemStack playerInHand = player.getItemInHand();
							if (playerInHand.getAmount() > item.getGoal()) {
								playerInHand.setAmount(playerInHand.getAmount() - item.getGoal());
								player.setItemInHand(playerInHand);
							} else {
								player.setItemInHand(null);
							}
						}
						// not in hand : take all needed items
						else {
							playerMatches.forEach(match -> ItemUtils.take(player, match.getItem(), match.getGoal(), match.getCheck(), false));
						}
						player.updateInventory();
					}
				});
			});
		}
	}

	// editor
	@Override
	public Mat editorIconType() {
		return CommonMats.APPLE;
	}

	@Override
	public List<String> editorCurrentValue() {
		if (items.size() == 0) {
			return null;
		}
		List<String> desc = new ArrayList<>();
		if (inHand.parseGeneric().orElse(false)) {
			Integer slot = inHandSlot.parseGeneric().orNull();
			if (slot != null) {
				desc.add("in hand (slot " + slot + ")");
			} else {
				desc.add("in hand");
			}
		} else {
			int count = this.count.parseGeneric().orElse(999);
			if (count == 0) {
				desc.add("must have none");
			} else if (count >= items.size()) {
				desc.add("must have all");
			} else {
				desc.add("must have " + count);
			}
		}
		items.values().stream().map(item -> item.editorCurrentValue().stream().map(line -> line.contains("check") ? "- " : "  ")).forEach(d -> d.forEach(l -> desc.add(l)));
		return desc;
	}

}
