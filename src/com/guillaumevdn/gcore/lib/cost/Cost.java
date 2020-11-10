package com.guillaumevdn.gcore.lib.cost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.TextGeneric;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.economy.Currency;
import com.guillaumevdn.gcore.lib.item.ItemCheck;
import com.guillaumevdn.gcore.lib.item.ItemUtils;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.string.StringUtils;

/**
 * @author GuillaumeVDN
 */
public class Cost {

	private Map<Currency, Double> currencies = new HashMap<>();
	private List<CostItem> items = new ArrayList<>();

	public Cost() {
	}

	// get
	public Map<Currency, Double> getMoney() {
		return currencies;
	}

	public List<CostItem> getItems() {
		return items;
	}

	public boolean isEmpty() {
		return currencies.isEmpty() && items.isEmpty();
	}

	// methods
	public void add(Currency currency, double amount) {
		currencies.compute(currency, (c, am) -> am == null ? amount : am + amount);
	}

	public void add(ItemStack item) {
		add(item, null);
	}

	public void add(ItemStack item, String displayName) {
		for (CostItem costItem : items) {
			if (ItemUtils.match(costItem.getItem(), item, ItemCheck.ExactSame)) {
				costItem.alterAmount(item.getAmount());
				return;
			}
		}
		items.add(new CostItem(item, displayName));
	}

	public void remove(Currency currency, double amount) {
		currencies.computeIfPresent(currency, (c, am) -> {
			am -= amount;
			return am < 0d ? null : am;
		});
	}

	public void remove(ItemStack item, int amount) {
		CollectionUtils.iterate(items, (iterator, next, breaker) -> {
			if (ItemUtils.match(next.getItem(), item, ItemCheck.ExactSame)) {
				next.alterAmount(-amount);
				if (next.getAmount() < 0) {
					iterator.remove();
				}
				breaker.set(true);
			}
		});
	}

	public List<String> describe() {
		List<String> desc = new ArrayList<>();
		currencies.forEach((currency, amount) -> desc.add(currency.format(amount)));
		items.forEach(item -> desc.addAll(ItemUtils.describe(item.getItem())));
		return desc;
	}

	public String describeSingleLine() {
		List<String> desc = new ArrayList<>();
		currencies.forEach((currency, amount) -> desc.add(currency.format(amount)));
		items.forEach(item -> desc.add(ItemUtils.describeSingleLine(item.getItem())));
		return StringUtils.toTextString(", ", desc);
	}

	// methods
	public boolean ensureHas(OfflinePlayer player, boolean notify) {
		// cost has items
		if (!items.isEmpty()) {
			// offline, can't check items
			Player playerOnline = player.getPlayer();
			if (playerOnline == null) {
				return false;
			}
			// check items
			for (CostItem item : items) {
				int has = ItemUtils.count(playerOnline, item.getItem(), ItemCheck.ExactSame);
				if (has < item.getAmount()) {
					if (notify) (has == 0 ? TextGeneric.messageMustHaveItem : TextGeneric.messageMustHaveMoreItem).replace("{item}", () -> ItemUtils.describeSingleLine(item.getItem())).replace("{has_amount}", () -> has).send(playerOnline);
					return false;
				}
			}
		}
		// check currencies
		for (Currency currency : currencies.keySet()) {
			double balance = currency.get(player);
			double amount = currencies.get(currency);
			if (balance < amount) {
				if (notify) TextGeneric.messageMustHaveCurrency.replace("{money}", () -> currency.format(amount)).replace("{balance}", () -> currency.format(balance)).send(player);
				return false;
			}
		}
		// seems good
		return true;
	}

	public void take(OfflinePlayer player, boolean updadeInv) {
		// cost has items
		if (!items.isEmpty()) {
			// offline, can't take items
			Player playerOnline = player.getPlayer();
			if (playerOnline == null) {
				return;
			}
			// take items
			for (CostItem item : items) {
				ItemUtils.take(playerOnline, item.getItem(), ItemCheck.ExactSame, false);
			}
			// update inv
			if (updadeInv) {
				playerOnline.updateInventory();
			}
		}
		// check currencies
		for (Currency currency : currencies.keySet()) {
			if (!currency.take(player, currencies.get(currency))) {
				return;
			}
		}
	}

	// object
	@Override
	public int hashCode() {
		return Objects.hash(currencies, items);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		Cost other = ObjectUtils.castOrNull(obj, Cost.class);
		return other != null && CollectionUtils.contentEquals(currencies, other.currencies) && CollectionUtils.contentEquals(items, other.items);
	}

}
