package com.guillaumevdn.gcore.integration.citizens.position;

import java.util.List;
import java.util.stream.Stream;

import org.bukkit.Location;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.integration.citizens.element.ElementCitizensNPC;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementStringList;
import com.guillaumevdn.gcore.lib.location.position.ElementPosition;
import com.guillaumevdn.gcore.lib.location.position.Position;
import com.guillaumevdn.gcore.lib.location.position.PositionType;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

/**
 * @author GuillaumeVDN
 */
public abstract class PositionTypeCitizensNPCRelative extends PositionType {

	public PositionTypeCitizensNPCRelative(String id) {
		super(id, CommonMats.PLAYER_HEAD);
	}

	// ----- elements
	@Override
	protected void doFillTypeSpecificElements(ElementPosition position) {
		position.add(new ElementCitizensNPC(position, "npc", Need.optional(), TextEditorGeneric.descriptionPositionTypeCitizensNPCRelative));
		position.add(new ElementStringList(position, "npc_names", Need.optional(), TextEditorGeneric.descriptionPositionTypeCitizensNPCRelativeNames));
	}

	// ----- parse
	@Override
	public boolean mustCache(ElementPosition position) {
		return !position.getElementAs("npc_names").readContains();
	}

	@Override
	public final Position doParse(ElementPosition position, Replacer replacer) throws ParsingError {
		NPC npc;
		try {
			npc = position.getElementAs("npc", ElementCitizensNPC.class).parseNoCatchOrThrowParsingNull(replacer);
			if (!npc.isSpawned()) return null;
		} catch (ParsingError error) {
			List<String> names = position.directParseOrNull("npc_names", replacer);
			if (names == null || names.isEmpty()) throw error;
			Stream<NPC> stream = CollectionUtils.asList(CitizensAPI.getNPCRegistry()).stream().filter(n -> CollectionUtils.containsIgnoreCase(names, n.getName())).filter(NPC::isSpawned);
			Location loc = replacer.getReplacerData().getLocationOrPlayer();
			if (loc != null) {
				stream = stream.sorted((a, b) -> Double.compare(a.getEntity().getLocation().distance(loc), b.getEntity().getLocation().distance(loc)));
			}
			npc = stream.findFirst().orElse(null);
			if (npc == null) return null;
		}
		return doParse(position, npc, replacer);
	}

	protected abstract Position doParse(ElementPosition position, NPC npc, Replacer replacer) throws ParsingError;

}
