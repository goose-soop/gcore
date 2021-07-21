package com.guillaumevdn.gcore.lib.element.type.basic;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.concurrency.RWWeakHashMap;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.string.Text;
import com.guillaumevdn.gcore.lib.time.TimeUnit;

/**
 * @author GuillaumeVDN
 */
public class ElementTimeUnit extends ElementFakeEnum<TimeUnit> {

	public ElementTimeUnit(Element parent, String id, Need need, Text editorDescription) {
		super(TimeUnit.class, parent, id, need, editorDescription);
	}

	private static RWWeakHashMap<Object, List<TimeUnit>> cache = new RWWeakHashMap<>();
	@Override
	protected List<TimeUnit> cacheOrBuild() {
		return cachedOrBuild(cache, () -> Stream.of(TimeUnit.TICK, TimeUnit.SECOND, TimeUnit.MINUTE, TimeUnit.HOUR, TimeUnit.DAY, TimeUnit.WEEK, TimeUnit.MONTH).sorted(Comparator.comparing(e -> e.name())));
	}

	@Override
	public Mat editorIconType() {
		return CommonMats.CLOCK;
	}

}
