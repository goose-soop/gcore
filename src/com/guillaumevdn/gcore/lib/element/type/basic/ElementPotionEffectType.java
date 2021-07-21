package com.guillaumevdn.gcore.lib.element.type.basic;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.bukkit.potion.PotionEffectType;

import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.concurrency.RWWeakHashMap;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementPotionEffectType extends ElementFakeEnum<PotionEffectType> {

	public ElementPotionEffectType(Element parent, String id, Need need, Text editorDescription) {
		super(PotionEffectType.class, parent, id, need, editorDescription);
	}

	static RWWeakHashMap<Object, List<PotionEffectType>> cache = new RWWeakHashMap<>();
	@Override
	protected List<PotionEffectType> cacheOrBuild() {
		return cachedOrBuild(cache, () -> Arrays.stream(PotionEffectType.values()).sorted(Comparator.comparing(e -> e.getId())));
	}

	@Override
	public Mat editorIconType() {
		return CommonMats.POTION;
	}

}
