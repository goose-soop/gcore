package com.guillaumevdn.gcore.lib.compatibility.material;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
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

	// ----- get
	public Optional<Mat> fromItem(ItemStack item) {
		return from(item.getType(), Compat.getLegacyData(item));
	}

	public Optional<Mat> fromBlock(Block block) {
		return from(block.getType(), Compat.getLegacyData(block));
	}

	public Optional<Mat> fromBlock(BlockState block) {
		return from(block.getType(), Compat.getLegacyData(block));
	}

	private final Map<String, Optional<Mat>> byIdAndDataQueryCache = new HashMap<>();

	private Optional<Mat> from(Material type, int legacyData) {
		String typeName = type.name();
		String queryId = typeName + "-" + legacyData;
		// <1.13 ; check legacy data even if it's 0, because otherwise it'll take the first matching name (even if data is not 0 in config) ; example ACACIA_WOOD which is LOG_2:0 would be detected as any LOG_2, for instance DARK_OAK_WOOD even if it's LOG_2:1
		if (!Version.ATLEAST_1_13) {
			Optional<Mat> result = byIdAndDataQueryCache.get(queryId);
			if (result == null) {
				for (Mat mat : values()) {
					if (mat.getData().getDataName().equalsIgnoreCase(typeName) && mat.getData().acceptsLegacyData(legacyData)) {
						result = Optional.of(mat);
						byIdAndDataQueryCache.put(queryId, result);
						return result;
					}
				}
			}
			if (result != null) {
				return result;
			}
		}
		// >=1.13
		else {
			Optional<Mat> mat = fromIdOrDataName(typeName);  // this will valuesCache an empty optional if none found
			if (mat.isPresent() || !isLenient()) {
				return mat;
			}
		}
		// create lenient
		Mat lenient = ConfigGCore.mats.createIfLenient(type, legacyData);
		Optional<Mat> result = lenient != null ? Optional.of(lenient) : Optional.empty();
		// valuesCache result if legacy
		if (!Version.ATLEAST_1_13) {
			byIdAndDataQueryCache.put(queryId, result);
		}
		return result;
	}

	// ----- load
	@Override
	public MatData loadElementConfigAndCreateData(Version version, ComparisonType comparison, List<MatExtra> extra, String rawData) throws Throwable {
		try {
			String[] matSplit = rawData.split(":", -1);
			String name = matSplit[0];
			int legacyData = -1;
			List<Integer> legacyDatas = null;
			if (matSplit.length == 2) {
				legacyData = loadPositiveNumber(matSplit[1], "data");
			} else if (matSplit.length > 2) {
				legacyDatas = loadPositiveNumberList(matSplit, 1, matSplit.length, "data");
			}
			Material material = ObjectUtils.safeValueOf(name, Material.class);
			if (material == null && Version.ATLEAST_1_13) material = ObjectUtils.safeValueOf("LEGACY_" + name, Material.class);
			return new MatData(version, comparison, name, material, legacyData, legacyDatas, extra);
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
			return registerIfHasCurrentVersion(id, CollectionUtils.asList(new MatData(Version.CURRENT, ComparisonType.EQUALS, material.name(), material, 0, null, null)));
		} catch (Throwable exception) {
			GCore.inst().getMainLogger().error("Couldn't create custom mat " + id, exception);
			return null;
		}
	}

}
