package com.guillaumevdn.gcore.lib.element.type.basic;

import java.util.Comparator;
import java.util.List;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.concurrency.RWWeakHashMap;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.particlescript.ParticleScript;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementParticleScript extends ElementFakeEnum<ParticleScript> {

	public ElementParticleScript(Element parent, String id, Need need, Text editorDescription) {
		super(ParticleScript.class, parent, id, need, editorDescription);
	}

	private static RWWeakHashMap<Object, List<ParticleScript>> cache = new RWWeakHashMap<>(1, 1f);

	@Override
	protected List<ParticleScript> cacheOrBuild() {
		return cachedOrBuild(cache, () -> ConfigGCore.particleScripts.values().stream().sorted(Comparator.comparing(e -> e.getId())));
	}

	@Override
	public Mat editorIconType() {
		return CommonMats.NETHER_STAR;
	}

}
