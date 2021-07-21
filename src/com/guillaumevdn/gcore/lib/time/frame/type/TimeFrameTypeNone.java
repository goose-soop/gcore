package com.guillaumevdn.gcore.lib.time.frame.type;

import java.time.ZonedDateTime;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;
import com.guillaumevdn.gcore.lib.time.frame.ElementTimeFrame;
import com.guillaumevdn.gcore.lib.time.frame.TimeFrameType;
import com.guillaumevdn.gcore.lib.tuple.Pair;

/**
 * @author GuillaumeVDN
 */
public class TimeFrameTypeNone extends TimeFrameType {

	public TimeFrameTypeNone(String id) {
		super(id, CommonMats.BEDROCK);
	}

	// ----- get
	@Override
	public Pair<ZonedDateTime, ZonedDateTime> getBounds(ElementTimeFrame frame, Replacer replacer, int offset) {
		// 10 years ought to be enough
		return Pair.of(
				ConfigGCore.timeNow().minusYears(10L).withHour(0).withMinute(0).withSecond(0),
				ConfigGCore.timeNow().plusYears(10L).withHour(23).withMinute(59).withSecond(59)
				);
	}

}
