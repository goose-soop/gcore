package com.guillaumevdn.gcore.lib.element.type.basic;

import java.util.Arrays;
import java.util.List;

import org.bukkit.block.Biome;

import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.concurrency.RWWeakHashMap;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementBiome extends ElementFakeEnum<Biome> {

	static RWWeakHashMap<Object, List<Biome>> cache = new RWWeakHashMap<>(1, 1f);

	public ElementBiome(Element parent, String id, Need need, Text editorDescription) {
		super(Biome.class, parent, id, need, editorDescription);
	}

	@Override
	public Mat editorIconType() {
		return CommonMats.GRASS_BLOCK;
	}

	@Override
	protected List<Biome> cacheOrBuild() {
		return cachedOrBuild(cache, () -> Arrays.stream(Biome.values()));
	}

}
