package com.guillaumevdn.gcore.lib.element.type.list;

import java.util.Objects;

import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.lib.item.ItemCheck;
import com.guillaumevdn.gcore.lib.item.ItemReference;
import com.guillaumevdn.gcore.lib.item.ItemUtils;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;

/**
 * @author GuillaumeVDN
 */
public class ItemMatch {

	private String id;
	private ItemReference reference;
	private int goal;
	private ItemCheck check;

	public ItemMatch(String id, ItemReference reference, int goal, ItemCheck check) {
		this.id = id;
		this.reference = reference;
		this.goal = goal;
		this.check = check;
	}

	public String getId() {
		return id;
	}

	public ItemReference getReference() {
		return reference;
	}

	public int getGoal() {
		return goal;
	}

	public ItemCheck getCheck() {
		return check;
	}

	// -----

	public boolean match(ItemStack item) {
		return ItemUtils.match(item, this.reference, check);
	}

	// ----- obj

	@Override
	public boolean equals(Object obj) {
		return this == obj || ObjectUtils.ifCanBeCastedDo(obj, ItemMatch.class, other -> other.id.equalsIgnoreCase(id)).orElse(false);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

}
