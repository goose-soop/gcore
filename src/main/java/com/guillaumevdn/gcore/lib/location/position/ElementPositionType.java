package com.guillaumevdn.gcore.lib.location.position;

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
public final class ElementPositionType extends ElementTypableElementType<PositionType> {

	public ElementPositionType(Element parent, String id, Text editorDescription) {
		super(PositionTypes.inst(), parent, id, editorDescription);
	}

	public static RWWeakHashMap<Object, List<PositionType>> valuesCache = new RWWeakHashMap<>(1, 1f);
	@Override
	protected List<PositionType> cacheOrBuild() {
		return cachedOrBuild(valuesCache, () -> PositionTypes.inst().values().stream().sorted(Comparator.comparing(e -> e.getId())));
	}

	@Override
	public Mat editorIconType() {
		return parseGeneric().ifPresentMap(PositionType::getIcon).orElse(CommonMats.REDSTONE);
	}

}
