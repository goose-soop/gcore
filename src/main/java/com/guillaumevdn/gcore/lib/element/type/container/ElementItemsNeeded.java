package com.guillaumevdn.gcore.lib.element.type.container;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.concurrency.RWHashMap;
import com.guillaumevdn.gcore.lib.concurrency.RWWeakHashMap;
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
		super(parent, id, need, editorDescription);
	}

	// ----- get
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

	// ----- ref
	private RWWeakHashMap<Object, RWWeakHashMap<Player, RWHashMap<ItemMatch, Integer>>> matchingItems = new RWWeakHashMap<>(1, 1f);  // kind of dirty, but this avoid re-checking for matches/locations when taking items later OR when willing to check which items match

	@Nullable
	public RWWeakHashMap<Player, RWHashMap<ItemMatch, Integer>> lastMatches(Object ref) {
		return matchingItems.get(ref);
	}

	@Nullable
	public Map<ItemMatch, Integer> lastMatches(Object ref, Player player) {
		Map<Player, RWHashMap<ItemMatch, Integer>> matches = matchingItems.get(ref);
		return matches != null ? matches.get(player) : null;
	}

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
					if (ItemUtils.has(player, item.getReference(), item.getGoal(), item.getCheck())) {
						return false;
					}
				}
			}
			return true;
		}
		// check players
		boolean inHand = getInHand().parse(replacer).orElse(false);
		for (Player player : playersOnline) {
			// in hand : look for first matching item in his hand
			if (inHand) {
				neededCount = 1;
				// invalid slot : don't match anyways
				Integer slot = getInHandSlot().parse(replacer).orNull();
				if (slot != null && slot >= 0 && slot <= 8 && slot != player.getInventory().getHeldItemSlot()) {
					continue;
				}
				// no specific slot, or it maches
				else {
					ItemStack playerInHand = player.getItemInHand();
					if (playerInHand != null) {
						for (ItemMatch item : items) {
							if (ItemUtils.match(playerInHand, item.getReference(), item.getCheck())) {
								setMatch(ref, player, item, playerInHand.getAmount());
								if (playerInHand.getAmount() >= item.getGoal()) {
									neededCount = 0;
								}
								break;
							}
						}
					}
				}
			}
			// not in hand : look for all matching items in his inventory
			else {
				for (ItemMatch item : items) {
					int count = ItemUtils.countMax(player, item.getReference(), item.getCheck(), item.getGoal());
					setMatch(ref, player, item, count);
					if (count >= item.getGoal()) {
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

	private void setMatch(Object ref, Player player, ItemMatch item, int count) {
		matchingItems.computeIfAbsent(ref, __ -> new RWWeakHashMap<>(5, 1f)).computeIfAbsent(player, __ -> new RWHashMap<>(10, 1f)).put(item, count > item.getGoal() ? item.getGoal() : count);
	}

	public void takeIfNeeded(Object ref, Replacer replacer, List<Player> players) {
		RWWeakHashMap<Player, RWHashMap<ItemMatch, Integer>> items = matchingItems.remove(ref);
		if (items != null && getTake().parse(replacer).orElse(false)) {
			boolean inHand = getInHand().parse(replacer).orElse(false);
			getSuperElement().getPlugin().operateSync(() -> {
				players.forEach(player -> {
					RWHashMap<ItemMatch, Integer> playerMatches = items.remove(player);
					if (playerMatches != null) {
						// in hand : replace item on his hand
						if (inHand) {
							ItemMatch item = playerMatches.streamResultKeys(s -> s.findFirst().orElse(null));
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
							playerMatches.forEach((match, __) -> {
								ItemUtils.take(player, match.getReference(), match.getGoal(), match.getCheck(), false);
							});
						}
						player.updateInventory();
					}
				});
			});
		}
	}

	// ----- editor
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
