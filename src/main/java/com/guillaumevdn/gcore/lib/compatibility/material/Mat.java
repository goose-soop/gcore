package com.guillaumevdn.gcore.lib.compatibility.material;

import java.util.Collection;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.variants.Variant;
import com.guillaumevdn.gcore.lib.exception.ConfigError;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;

/**
 * @author GuillaumeVDN
 */
public final class Mat extends Variant<MatData> {

	private static final List<String> AIR_TYPES = CollectionUtils.asList("AIR", "VOID_AIR", "CAVE_AIR");
	private static final List<String> WATER_TYPES = CollectionUtils.asList("WATER", "STATIONARY_WATER");

	private boolean air;
	private boolean water;
	private boolean skull;

	public Mat(String id, MatData data) {
		super(id, data);
		this.air = AIR_TYPES.contains(data.getDataName());
		this.water = WATER_TYPES.contains(data.getDataName());
		this.skull = data.getDataName().contains("HEAD") || data.getDataName().contains("SKULL");
	}

	// ----- get
	public boolean canHaveItem() {
		try {
			return !isVoid(newStack());
		} catch (Throwable ignored) {
			return false;
		}
	}

	public boolean isAir() {
		return air;
	}

	public boolean isWater() {
		return water;
	}

	public boolean isSkull() {
		return skull;
	}

	public static boolean isVoid(ItemStack item) {
		return item == null || item.getType() == null || AIR_TYPES.contains(item.getType().name());
	}

	// ----- item stack
	public ItemStack newStack() {
		return MatCompatItem.createStack(this);
	}

	public boolean match(ItemStack item) {
		return item == null ? isAir() : MatCompatItem.match(item, this);
	}

	// ----- block
	public void setBlock(Block block) {
		MatCompatBlock.setBlock(block, this);
	}

	public void setBlockChange(Block block) {
		block.getWorld().getPlayers().forEach(pl -> {
			setBlockChange(block, pl);
		});
	}

	public void setBlockChange(Block block, Player player) {
		MatCompatBlock.setBlockChange(block, this, player);
	}

	public boolean match(Block block) {
		return block == null ? isAir() : MatCompatBlock.match(block, this);
	}

	// ----- object
	@Override
	public Mat clone() {
		return new Mat(getId(), getData().clone());
	}

	// ----- static
	public static Collection<Mat> values() {
		return ConfigGCore.mats.values();
	}

	public static OptionalMat fromId(String string) {
		return new OptionalMat(string, ConfigGCore.mats.fromId(string).orNull());
	}

	public static OptionalMat firstFromIdOrDataName(String string) {
		return string == null ? OptionalMat.empty() : new OptionalMat(string, ConfigGCore.mats.fromIdOrDataName(string).orNull());
	}

	public static OptionalMat fromItem(ItemStack item) {
		return item == null ? OptionalMat.empty() : new OptionalMat(item.getType().name(), ConfigGCore.mats.fromItem(item).orNull());
	}

	public static OptionalMat fromBlock(Block block) {
		return block == null ? OptionalMat.empty() : new OptionalMat(block.getType().name(), ConfigGCore.mats.fromBlock(block).orNull());
	}

	public static OptionalMat fromBlock(BlockState block) {
		return block == null ? OptionalMat.empty() : new OptionalMat(block.getType().name(), ConfigGCore.mats.fromBlock(block).orNull());
	}

	public static class OptionalMat {

		private String id;
		private Mat mat;

		public OptionalMat(String id, Mat mat) {
			this.id = id;
			this.mat = mat;
		}

		public Mat get() {
			if (mat == null) {
				throw new ConfigError("Couldn't find material with id or data type " + id);  // config error because this is used on load in CommonMats ; also it's actually a config error so we good
			}
			return mat;
		}

		public Mat orElse(Mat def) {
			return mat != null ? mat : def;
		}

		public Mat orAir() {
			return orElse(CommonMats.AIR);
		}

		public boolean isPresent() {
			return mat != null;
		}

		public static OptionalMat empty() {
			return new OptionalMat("null", null);
		}
		
		@Override
		public String toString() {
			return "" + mat;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) return false;
			OptionalMat other = ObjectUtils.castOrNull(obj, OptionalMat.class);
			return mat == null ? other.mat == null : mat.equals(other.mat);
		}

	}

}
