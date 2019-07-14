package com.guillaumevdn.gcore.lib.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
		// remove showcase slots from regular item slots
		for (Integer i = row.beginSlot; i < row.endSlot; ++i) {
			gui.getRegularItemSlots().remove(i);// Integer, so it removes the object and not the index
		}
		// add row
		rows.add(row);
	}

	// row
	public class Row {

		// base
		private int beginSlot, endSlot, size, startItemIndex = 0;
		private List<ClickeableItem> items;

		public Row(int beginSlot, int endSlot) {
			this(beginSlot, endSlot, null);
		}

		public Row(int beginSlot, int endSlot, List<ClickeableItem> items) {
			this.beginSlot = beginSlot;
			this.endSlot = endSlot;
			this.size = endSlot - beginSlot + 1;
			this.items = items != null ? items : new ArrayList<ClickeableItem>();
		}

		// get
		public int getBeginSlot() {
			return beginSlot;
		}

		public int getEndSlot() {
			return endSlot;
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
			for (int slot = beginSlot; slot <= endSlot; ++slot) {
				gui.removePersistentItem(slot);
			}
			// update start index
			if (startItemIndex < 0) startItemIndex = 0;
			else if (startItemIndex >= items.size()) startItemIndex = items.size() - 1;
			// must add page controls
			int currentBeginSlot = beginSlot, currentEndSlot = endSlot;
			if (items.size() > size) {
				// previous page item
				if (startItemIndex != 0) {
					++currentBeginSlot;
					gui.setPersistentItem(new ClickeableItem(GUI.PREVIOUS_PAGE_ITEM.cloneWithSlot(beginSlot)) {
						@Override
						public boolean onClick(Player player, ClickType clickType, GUI gui, int pageIndex) {
							startItemIndex -= size;
							if (startItemIndex < 0) startItemIndex = 0;
							update();
							return true;
						}
					});
				}
				// next page item
				if (items.size() - startItemIndex + 1 > size) {
					--currentEndSlot;
					gui.setPersistentItem(new ClickeableItem(GUI.NEXT_PAGE_ITEM.cloneWithSlot(endSlot)) {
						@Override
						public boolean onClick(Player player, ClickType clickType, GUI gui, int pageIndex) {
							startItemIndex += size;
							update();
							return true;
						}
					});
				}
			}
			// add content
			for (int index = startItemIndex, slot = currentBeginSlot; index < items.size() && slot <= currentEndSlot; ++index, ++slot) {
				ClickeableItem item = items.get(index);
				item.getItemData().setSlot(slot);
				gui.setPersistentItem(item);
			}
		}

	}

}
