package com.guillaumevdn.gcore.lib.serialization.adapter.type;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.lib.gui.InventoryState;
import com.guillaumevdn.gcore.lib.serialization.adapter.DataAdapter;
import com.guillaumevdn.gcore.lib.serialization.data.DataIO;

/**
 * @author GuillaumeVDN
 */
public final class AdapterInventoryState extends DataAdapter<InventoryState> {

	public static final AdapterInventoryState INSTANCE = new AdapterInventoryState();

	private AdapterInventoryState() {
		super(InventoryState.class, 1);
	}

	@Override
	public void write(InventoryState state, DataIO writer) throws Throwable {
		writer.writeObjectOrThrow("items", wr -> {
			for (Entry<Integer, ItemStack> entry : state.getItems().entrySet()) {
				wr.write("" + entry.getKey(), entry.getValue());
			}
		});
	}

	@Override
	public InventoryState read(int version, DataIO reader) throws Throwable {
		if (version == 1) {
			Map<Integer, ItemStack> items = reader.readSimpleMapOrThrow("items", Integer.class, new HashMap<>(), (key, __, rd) -> rd.readItem(key));
			return new InventoryState(items);
		}
		throw new IllegalArgumentException("unknown adapter version " + version);
	}

}
