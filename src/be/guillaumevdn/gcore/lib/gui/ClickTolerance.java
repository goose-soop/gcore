package be.guillaumevdn.gcore.lib.gui;

import java.util.ArrayList;
import java.util.List;

public enum ClickTolerance {

	// values
	ALLOW(InventoryClickType.TOP, InventoryClickType.BOTTOM),
	ONLY_TOP(InventoryClickType.TOP),
	ONLY_BELOW(InventoryClickType.BOTTOM),
	DISALLOW();

	// bases
	private List<InventoryClickType> allowedClickTypes = new ArrayList<InventoryClickType>();

	private ClickTolerance(InventoryClickType... types) {
		if (types != null) {
			for (InventoryClickType type : types) {
				allowedClickTypes.add(type);
			}
		}
	}

	// get
	public List<InventoryClickType> getAllowedClickTypes() {
		return allowedClickTypes;
	}

	public boolean isClickTypeAllowed(InventoryClickType type) {
		return allowedClickTypes.contains(type);
	}

}
