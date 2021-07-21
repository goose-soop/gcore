package com.guillaumevdn.gcore.lib.element.type.basic;

import java.util.Comparator;
import java.util.List;

import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.compatibility.sound.Sound;
import com.guillaumevdn.gcore.lib.concurrency.RWWeakHashMap;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementSound extends ElementFakeEnum<Sound> {

	public ElementSound(Element parent, String id, Need need, Text editorDescription) {
		super(Sound.class, parent, id, need, editorDescription);
	}

	private static RWWeakHashMap<Object, List<Sound>> cache = new RWWeakHashMap<>();
	@Override
	protected List<Sound> cacheOrBuild() {
		return cachedOrBuild(cache, () -> Sound.values().stream().sorted(Comparator.comparing(e -> e.getId())));
	}

	@Override
	public Mat editorIconType() {
		return CommonMats.NOTE_BLOCK;
	}

}
