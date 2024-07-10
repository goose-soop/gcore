package com.guillaumevdn.gcore.integration.citizens.element;

import java.util.List;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementAbstractEnumList;
import com.guillaumevdn.gcore.lib.string.Text;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

/**
 * @author GuillaumeVDN
 */
public class ElementCitizensNPCList extends ElementAbstractEnumList<NPC> {

	public ElementCitizensNPCList(Element parent, String id, Need need, Text editorDescription) {
		super(NPC.class, false, parent, id, need, editorDescription);
	}

	@Override
	public List<NPC> getValues() {
		return CollectionUtils.asList(CitizensAPI.getNPCRegistry().sorted());
	}

	// ----- TODO : import npc here too

	// ----- editor
	@Override
	public Mat editorIconType() {
		return CommonMats.PLAYER_HEAD;
	}

}
