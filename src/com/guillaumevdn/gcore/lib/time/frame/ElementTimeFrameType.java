package com.guillaumevdn.gcore.lib.time.frame;

import java.util.Comparator;
import java.util.List;

import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.concurrency.RWWeakHashMap;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.container.typable.ElementTypableElementType;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public final class ElementTimeFrameType extends ElementTypableElementType<TimeFrameType> {

	public ElementTimeFrameType(Element parent, String id, Text editorDescription) {
		super(TimeFrameTypes.inst(), parent, id, editorDescription);
	}

	private static RWWeakHashMap<Object, List<TimeFrameType>> cache = new RWWeakHashMap<>();
	@Override
	protected List<TimeFrameType> cacheOrBuild() {
		return cachedOrBuild(cache, () -> TimeFrameTypes.inst().values().stream().sorted(Comparator.comparing(e -> e.getId())));
	}

	@Override
	public Mat editorIconType() {
		return parseGeneric().ifPresentMap(TimeFrameType::getIcon).orElse(CommonMats.REDSTONE);
	}

}
