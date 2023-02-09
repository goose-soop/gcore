package com.guillaumevdn.gcore.integration.mythicmobs.v4.position;

import java.util.List;
import java.util.stream.Stream;

import org.bukkit.Location;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.integration.mythicmobs.v4.element.ElementMythicMobsMobList;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.parsing.ParsingError;
import com.guillaumevdn.gcore.lib.location.position.ElementPosition;
import com.guillaumevdn.gcore.lib.location.position.Position;
import com.guillaumevdn.gcore.lib.location.position.PositionType;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;

/**
 * @author GuillaumeVDN
 */
public abstract class PositionTypeClosestMythicMob extends PositionType {

	public PositionTypeClosestMythicMob(String id) {
		super(id, CommonMats.DIAMOND_SWORD);
	}

	// ----- elements
	@Override
	protected void doFillTypeSpecificElements(ElementPosition position) {
		position.add(new ElementMythicMobsMobList(position, "mobs", Need.optional(), TextEditorGeneric.descriptionPositionTypeClosestMythicMobMobs));
	}

	// ----- parse
	@Override
	public boolean mustCache(ElementPosition position) {
		return false;
	}

	@Override
	public final Position doParse(ElementPosition position, Replacer replacer) throws ParsingError {
		Location mobLocation = findMatching(position, replacer);
		return mobLocation == null ? null : doParseMob(position, mobLocation, replacer);
	}

	protected abstract Position doParseMob(ElementPosition position, Location mobLocation, Replacer replacer) throws ParsingError;

	public static Location findMatching(ElementPosition position, Replacer replacer) {
		Location parsingLocation = replacer.getReplacerData().getLocationOrPlayer();
		if (parsingLocation == null) {
			return null;
		}
		Stream<ActiveMob> stream = MythicMobs.inst().getMobManager().getActiveMobs().stream();
		// filter by type
		List<MythicMob> mobs = position.parseElementAsList("mobs", MythicMob.class, replacer).orNull();
		if (mobs != null) {
			stream = stream.filter(mob -> mobs.stream().anyMatch(type -> type.equals(mob.getType())));
		}
		// sort and find first
		return stream
				.map(mob -> mob.getEntity() == null || mob.getEntity().getBukkitEntity() == null ? null : mob.getEntity().getBukkitEntity().getLocation())
				.filter(loc -> loc != null && loc.getWorld().equals(parsingLocation.getWorld()))
				.sorted((a, b) -> Double.compare(a.distance(parsingLocation), b.distance(parsingLocation)))
				.findFirst().orElse(null);
	}

}
