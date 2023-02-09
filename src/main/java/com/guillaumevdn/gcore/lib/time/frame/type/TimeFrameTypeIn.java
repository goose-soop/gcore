package com.guillaumevdn.gcore.lib.time.frame.type;

import java.time.ZonedDateTime;

import com.guillaumevdn.gcore.ConfigGCore;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;
import com.guillaumevdn.gcore.lib.time.frame.ElementTimeFrame;
import com.guillaumevdn.gcore.lib.time.frame.TimeFrameType;
import com.guillaumevdn.gcore.lib.time.in.ElementTimeIn;
import com.guillaumevdn.gcore.lib.time.in.TimeIn;
import com.guillaumevdn.gcore.lib.tuple.Pair;

/**
 * @author GuillaumeVDN
 */
public abstract class TimeFrameTypeIn<T extends TimeIn> extends TimeFrameType {

	private T defaultStart;
	private T defaultEnd;

	public TimeFrameTypeIn(String id, T defaultStart, T defaultEnd) {
		super(id, CommonMats.CLOCK);
		this.defaultStart = defaultStart;
		this.defaultEnd = defaultEnd;
	}

	@Override
	public Pair<ZonedDateTime, ZonedDateTime> getBounds(ElementTimeFrame frame, Replacer replacer, int offset) {
		ZonedDateTime start = (frame.getElement("start").orNull().readContains() ? ((ElementTimeIn<T>) frame.getElementAs("start")).parse(replacer).orElse(defaultStart) : defaultStart).getCurrent();
		ZonedDateTime end = (frame.getElement("end").orNull().readContains() ? ((ElementTimeIn<T>) frame.getElementAs("end")).parse(replacer).orElse(defaultEnd) : defaultEnd).getCurrent();

		// start is before end : regular start/end times
		if (start.isBefore(end)) {
		}
		// start is after end : check which part of the week we're in, and adjust start/end times
		else {
			ZonedDateTime now = ConfigGCore.timeNow();
			if (now.isBefore(end)) {  // we're before end time ; set start time to last period
				start = deltaPeriod(start, -1);
			} else {  // we're after end time ; set end time to new period
				end = deltaPeriod(end, 1);
			}
		}

		// ensure is in current period if has no offset
		if (offset == 0) {
			ZonedDateTime now = ConfigGCore.timeNow();
			if (now.isBefore(start) || now.isAfter(end)) {
				return null;
			}
		}
		// otherwise just adjust start and end
		else {
			start = start.plusWeeks(1);
			end = end.plusWeeks(1);
		}
		return Pair.of(start, end);
	}

	protected abstract ZonedDateTime deltaPeriod(ZonedDateTime start, int delta);

}
