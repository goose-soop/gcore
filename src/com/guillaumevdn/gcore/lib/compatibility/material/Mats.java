package com.guillaumevdn.gcore.lib.compatibility.material;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.Compat;
import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.compatibility.material.MatData.MatExtra;
import com.guillaumevdn.gcore.lib.compatibility.variants.Variants;
import com.guillaumevdn.gcore.lib.exception.ConfigError;
import com.guillaumevdn.gcore.lib.logic.ComparisonType;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.object.Optional;

/**
 * @author GuillaumeVDN
 */
public final class Mats extends Variants<Mat, MatExtra, MatData> {

	public Mats(boolean regenerate, boolean lenient) {
		super("material", Mat.class, MatData.class, MatExtra.class, regenerate, lenient);
	}

	// get
	public Optional<Mat> fromItem(ItemStack item) {
		// has existing
		Optional<Mat> mat = fromIdOrDataName(item.getType().name());
		if (mat.isPresent()) {
			return mat;
		}
		// create lenient
		Mat lenient = ConfigGCore.mats.createIfLenient(item.getType(), Compat.getLegacyData(item));
		if (lenient != null) {
			return Optional.of(lenient);
		}
		// none
		return Optional.empty();
	}

	public Optional<Mat> fromBlock(Block block) {
		// has existing
		Optional<Mat> mat = fromIdOrDataName(block.getType().name());
		if (mat.isPresent()) {
			return mat;
		}
		// create lenient
		Mat lenient = ConfigGCore.mats.createIfLenient(block.getType(), Compat.getLegacyData(block));
		if (lenient != null) {
			return Optional.of(lenient);
		}
		// none
		return Optional.empty();
	}

	// load
	@Override
	public MatData loadElementConfigAndCreateData(Version version, ComparisonType comparison, List<MatExtra> extra, String rawData) throws Throwable {
		try {
			String[] matSplit = rawData.split(":", -1);
			String name = matSplit[0];
			int legacyData = matSplit.length > 1 ? loadPositiveNumber(matSplit[1], "data") : 0;
			Material material = ObjectUtils.safeValueOf(name, Material.class);
			if (material == null && Version.ATLEAST_1_13) material = ObjectUtils.safeValueOf("LEGACY_" + name, Material.class);
			return new MatData(version, comparison, name, material, legacyData, extra);
		} catch (Throwable exception) {
			throw new ConfigError("invalid " + getTypeName() + " config " + rawData, exception);
		}
	}

	@Override
	protected Mat createElement(String id, MatData data) throws Throwable {
		return new Mat(id, data);
	}

	Mat createIfLenient(Material material, int legacyData) {
		if (!isLenient()) return null;
		// lenient already exists
		String id = material.name() + (legacyData == 0 ? "" : "_DATA" + legacyData);
		Optional<Mat> existing = fromId(id);
		if (existing.isPresent()) {
			return existing.orNull();
		}
		// create lenient
		if (legacyData != 0 && Version.ATLEAST_1_13) {
			legacyData = 0;
			GCore.inst().getMainLogger().warning("Creating lenient mat " + material.name() + ":" + legacyData + " (data won't be set since it's 1.13+)");
		} else {
			GCore.inst().getMainLogger().warning("Creating lenient mat " + material.name() + ":" + legacyData);
		}
		try {
			return registerIfHasCurrentVersion(id, CollectionUtils.asList(new MatData(Version.CURRENT, ComparisonType.EQUALS, material.name(), material, 0, null)));
		} catch (Throwable exception) {
			GCore.inst().getMainLogger().error("Couldn't create custom mat " + id, exception);
			return null;
		}
	}

}
