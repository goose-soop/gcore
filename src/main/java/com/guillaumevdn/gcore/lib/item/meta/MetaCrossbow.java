package com.guillaumevdn.gcore.lib.item.meta;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.ItemMeta;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.type.container.ElementItem;
import com.guillaumevdn.gcore.lib.element.type.list.ElementItemList;
import com.guillaumevdn.gcore.lib.item.ItemCheck;
import com.guillaumevdn.gcore.lib.item.ItemReference;
import com.guillaumevdn.gcore.lib.item.ItemUtils;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.serialization.adapter.type.AdapterItemStack;
import com.guillaumevdn.gcore.lib.serialization.data.DataIO;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public final class MetaCrossbow {

	public static boolean match(ItemMeta itemMeta, ItemReference reference, ItemCheck check) {
		if (!reference.hasMeta(CrossbowMeta.class)) return true;
		CrossbowMeta meta = ObjectUtils.castOrNull(itemMeta, CrossbowMeta.class);  // might be null if exact match is false
		
		// charged projectiles
		if (check.isExact()) {
			if (meta.hasChargedProjectiles() != meta.hasChargedProjectiles()) return false;  // parce qu'on rappelle que .getChargedProjectiles est @NotNull mais qu'il est null quand même :CringeHarold: #956
			if (!reference.hasChargedProjectiles()) return true;
			if (meta.getChargedProjectiles().size() != reference.getChargedProjectiles().size()) return false;
		} else {
			if (reference.hasChargedProjectiles() && (meta == null || !meta.hasChargedProjectiles())) return false;
			if (!reference.hasChargedProjectiles()) return true;
		}
		main: for (ItemStack refProjectile : reference.getChargedProjectiles()) {
			for (ItemStack projectile : meta.getChargedProjectiles()) {
				if (ItemUtils.match(refProjectile, projectile, check)) {
					continue main;
				}
			}
			return false;
		}
		
		// seems good
		return true;
	}

	public static void write(ItemMeta itemMeta, DataIO writer) throws Throwable {
		CrossbowMeta meta = ObjectUtils.castOrNull(itemMeta, CrossbowMeta.class);
		if (meta != null) {
			if (meta.hasChargedProjectiles()) {
				List<DataIO> list = new ArrayList<>();
				for (ItemStack projectile : meta.getChargedProjectiles()) {
					DataIO d = new DataIO();
					AdapterItemStack.INSTANCE.write(projectile, d);
					list.add(d);
				}
				writer.writeDirectList("chargedProjectiles", list);
			}
		}
	}

	public static void read(ItemMeta itemMeta, DataIO reader) throws Throwable {
		CrossbowMeta meta = ObjectUtils.castOrNull(itemMeta, CrossbowMeta.class);
		if (meta != null) {
			List<DataIO> chargedProjectiles = reader.readDirectList("chargedProjectiles");
			if (chargedProjectiles != null) for (DataIO d : chargedProjectiles) {
				ItemStack proj = AdapterItemStack.INSTANCE.read(d);
				if (proj != null) {
					meta.addChargedProjectile(proj);
				}
			}
		}
	}

	public static void fillElements(ItemMeta sampleMeta, ElementItem item) {
		if (ObjectUtils.instanceOf(sampleMeta, CrossbowMeta.class)) {
			item.addItemList("charged_projectiles", Need.optional(), item.getMode(), TextEditorGeneric.descriptionItemCrossbowChargedProjectiles);
		}
	}

	public static void clearElements(ElementItem item) {
		item.remove("charged_projectiles");
	}

	public static void writeElements(ElementItem item, DataIO writer, Replacer replacer) throws Throwable {
		item.parseElementAsList("charged_projectiles", ItemStack.class, replacer).ifPresentDoThrowable(items -> {
			List<DataIO> list = new ArrayList<>();
			for (ItemStack it : items) {
				DataIO w = new DataIO();
				AdapterItemStack.INSTANCE.write(it, w);
				list.add(w);
			}
			writer.writeDirectList("chargedProjectiles", list);
		});
	}

	public static void importElements(ElementItem item, ItemMeta itemMeta, @Nullable Player clicker) {
		CrossbowMeta meta = ObjectUtils.castOrNull(itemMeta, CrossbowMeta.class);
		if (meta != null) {
			ElementItemList list = item.getElementAs("charged_projectiles");
			list.clear();
			for (ItemStack projectile : meta.getChargedProjectiles()) {
				list.createAndAddElement().importValue(projectile, clicker);
			}
		}
	}

}
