package com.guillaumevdn.gcore.lib.element.type.basic;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.particlescript.ParticleScript;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementParticleScript extends ElementFakeEnum<ParticleScript> {

	public ElementParticleScript(Element parent, String id, Need need, Text editorDescription) {
		super(ParticleScript.class, parent, id, need, editorDescription, CollectionUtils.asList(ConfigGCore.particleScripts.values()));
	}

	@Override
	public Mat editorIconType() {
		return CommonMats.NETHER_STAR;
	}

}
