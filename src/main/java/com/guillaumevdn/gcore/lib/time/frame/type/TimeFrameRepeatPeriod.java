package com.guillaumevdn.gcore.lib.time.frame.type;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;
import com.guillaumevdn.gcore.lib.time.TimeUnit;
import com.guillaumevdn.gcore.lib.time.frame.ElementTimeFrame;
import com.guillaumevdn.gcore.lib.time.frame.TimeFrameType;
import com.guillaumevdn.gcore.lib.tuple.Pair;

/**
 * @author GuillaumeVDN
 */
public class TimeFrameRepeatPeriod extends TimeFrameType {

	public TimeFrameRepeatPeriod(String id) {
		super(id, CommonMats.CLOCK);
	}

	// ----- elements
	@Override
	protected void doFillTypeSpecificElements(ElementTimeFrame frame) {
		super.doFillTypeSpecificElements(frame);
		frame.addDuration("duration", Need.optional(), 60, TimeUnit.MINUTE, TextEditorGeneric.descriptionTimeFrameRepeatPeriodDuration);
	}

	// ----- get
	private static final ZonedDateTime START_REFERENCE = ZonedDateTime.of(2021, 5, 15, 0, 0, 0, 0, ConfigGCore.timeZone()); // we set a fixed start, so that the
																															// period will not reset infinitely
																															// every time getBounds() is called,
																															// and it will be consistent no
																															// matter the period duration

	@Override
	public Pair<ZonedDateTime, ZonedDateTime> getBounds(ElementTimeFrame frame, Replacer replacer, int offset) {
		long durationMillis = frame.directParseOrNull("duration", replacer);
		long durationSeconds = durationMillis / 1000L;

		// calculate offset from start reference to now
		long diff = ChronoUnit.SECONDS.between(START_REFERENCE, ConfigGCore.timeNow());
		offset += Math.floorDiv(diff, durationSeconds);

		// calculate bounds
		ZonedDateTime start = START_REFERENCE.plusSeconds(offset * durationSeconds);
		ZonedDateTime end = start.plusSeconds(durationSeconds);

		return Pair.of(start, end);
	}

}
