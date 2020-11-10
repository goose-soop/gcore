package com.guillaumevdn.gcore.integration.citizens.position;

import org.bukkit.Location;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.integration.citizens.element.ElementCitizensNPC;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.element.type.container.ElementRelativeLocation;
import com.guillaumevdn.gcore.lib.location.position.ElementPosition;
import com.guillaumevdn.gcore.lib.location.position.Position;
import com.guillaumevdn.gcore.lib.location.position.PositionType;
import com.guillaumevdn.gcore.lib.location.position.type.area.PositionAreaInside;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

import net.citizensnpcs.api.npc.NPC;

/**
 * @author GuillaumeVDN
 */
public class PositionTypeCitizensNPCRelativeAreaInside extends PositionType {

	public PositionTypeCitizensNPCRelativeAreaInside(String id) {
		super(id, CommonMats.MINECART);
	}

	// elements
	@Override
	protected void doFillTypeSpecificElements(ElementPosition position) {
		position.add(new ElementCitizensNPC(position, "npc", Need.required(), TextEditorGeneric.descriptionPositionTypeCitizensNPCRelative));
		position.addRelativeLocation("bound1", Need.required(), TextEditorGeneric.descriptionPositionTypeRelativeBound1);
		position.addRelativeLocation("bound2", Need.required(), TextEditorGeneric.descriptionPositionTypeRelativeBound2);
	}

	// parse
	@Override
	public Position doParse(ElementPosition position, Replacer replacer) throws ParsingError {
		NPC npc = position.getElementAs("npc", ElementCitizensNPC.class).parseNoCatchOrThrowParsingNull(replacer);
		if (!npc.isSpawned()) return null;
		Location a = position.getElementAs("bound1", ElementRelativeLocation.class).parseNoCatchOrThrowParsingNull(replacer.cloneReplacer().with(npc.getEntity().getLocation()));
		Location b = position.getElementAs("bound2", ElementRelativeLocation.class).parseNoCatchOrThrowParsingNull(replacer.cloneReplacer().with(npc.getEntity().getLocation()));
		return new PositionAreaInside(a, b);
	}

}
