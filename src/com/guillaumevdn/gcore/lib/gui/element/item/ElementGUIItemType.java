package com.guillaumevdn.gcore.lib.gui.element.item;

import java.util.Comparator;
import java.util.List;

import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.concurrency.RWWeakHashMap;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.container.typable.ElementTypableElementType;
import com.guillaumevdn.gcore.lib.gui.element.item.type.GUIItemType;
import com.guillaumevdn.gcore.lib.gui.element.item.type.GUIItemTypes;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public final class ElementGUIItemType extends ElementTypableElementType<GUIItemType> {

	public ElementGUIItemType(Element parent, String id, Text editorDescription) {
		super(GUIItemTypes.inst(), parent, id, editorDescription);
	}

	public static RWWeakHashMap<Object, List<GUIItemType>> valuesCache = new RWWeakHashMap<>(5, 1f);
	@Override
	protected List<GUIItemType> cacheOrBuild() {
		return cachedOrBuild(valuesCache, () -> GUIItemTypes.inst().values().stream().sorted(Comparator.comparing(e -> e.getId())));
	}

	@Override
	public Mat editorIconType() {
		return parseGeneric().ifPresentMap(GUIItemType::getIcon).orElse(CommonMats.REDSTONE);
	}

}
