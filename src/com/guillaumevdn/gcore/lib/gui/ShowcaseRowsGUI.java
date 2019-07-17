package com.guillaumevdn.gcore.lib.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.plugin.Plugin;

import com.guillaumevdn.gcore.lib.util.Utils;

public abstract class ShowcaseRowsGUI {

	// base
	private FilledGUI gui = null;
	private List<Row> rows = new ArrayList<Row>();

	public ShowcaseRowsGUI(Plugin plugin, String name, int size, List<Integer> regularItemSlots, boolean unregisterOnClose) {
		// initialize GUI
		gui = new FilledGUI(plugin, name, size, Utils.asList(regularItemSlots) /* copy list, because we might change it later when adding rows */) {
			@Override
			protected void fill() {
				ShowcaseRowsGUI.this.fill();
			}
			@Override
			protected boolean postFill() {
				return ShowcaseRowsGUI.this.postFill();
			}
		};
	}

	// abstract methods
	protected abstract void fill();
	protected abstract boolean postFill();

	// get
	public FilledGUI getGui() {
		return gui;
	}

	public List<Row> getRows() {
		return rows;
	}

	// methods
	public void addRow(Row row) {
		if (!rows.contains(row)) {
			// remove showcase slots from regular item slots
			gui.getRegularItemSlots().removeAll(row.getSlots());
			rows.add(row);
		}
	}

	// row
	public class Row {

		// base
		private List<Integer> slots;
		private List<ClickeableItem> items;
		private int startItemIndex = 0;

		public Row(List<Integer> slots) {
			this(slots, null);
		}

		public Row(List<Integer> slots, List<ClickeableItem> items) {
			this.slots = slots;
			this.items = items != null ? items : new ArrayList<ClickeableItem>();
		}

		// get
		public List<Integer> getSlots() {
			return slots;
		}

		public List<ClickeableItem> getItems() {
			return Collections.unmodifiableList(items);
		}

		// methods
		public void addItem(ClickeableItem item, boolean update) {
			items.add(item);
			if (update) update();
		}

		public void removeItem(String itemId, boolean update) {
			for (ClickeableItem item : items) {
				if (item.getItemData().getId().equals(itemId)) {
					items.remove(item);
					if (update) update();
					return;
				}
			}
		}

		public void replaceItems(List<ClickeableItem> items, boolean update) {
			this.items.clear();
			this.items.addAll(items);
			if (update) update();
		}

		public void clear(boolean update) {
			this.items.clear();
			if (update) update();
		}

		public void update() {
			// clear slots
			for (int slot : slots) {
				gui.removePersistentItem(slot);
			}
			// update start index
			if (startItemIndex < 0) startItemIndex = 0;
			else if (startItemIndex >= items.size()) startItemIndex = items.size() - 1;
			// must add page controls
			List<Integer> currentSlots = Utils.asList(slots);
			if (items.size() > slots.size()) {
				// previous page item
				if (startItemIndex != 0) {
					gui.setPersistentItem(new ClickeableItem(GUI.PREVIOUS_PAGE_ITEM.cloneWithIdAndSlot("back_row_" + UUID.randomUUID(), currentSlots.remove(0))) {
						@Override
						public boolean onClick(Player player, ClickType clickType, GUI gui, int pageIndex) {
							startItemIndex -= slots.size();
							if (startItemIndex < 0) startItemIndex = 0;
							update();
							return true;
						}
					});
				}
				// next page item
				if (items.size() - startItemIndex + 1 > slots.size()) {
					gui.setPersistentItem(new ClickeableItem(GUI.NEXT_PAGE_ITEM.cloneWithIdAndSlot("back_row_" + UUID.randomUUID(), currentSlots.remove(currentSlots.size() - 1))) {
						@Override
						public boolean onClick(Player player, ClickType clickType, GUI gui, int pageIndex) {
							startItemIndex += slots.size();
							update();
							return true;
						}
					});
				}
			}
			// add content
			if (!items.isEmpty()) {
				int index = startItemIndex - 1;
				for (int slot : currentSlots) {
					if (++index >= items.size()) break;
					ClickeableItem item = items.get(index);
					item.getItemData().setSlot(slot);
					gui.setPersistentItem(item);
				}
			}
			// update first slot ffs
			gui.updateFirstSlot();
		}

	}

}
