package com.guillaumevdn.gcore.lib.element.type.basic;

import java.util.Comparator;
import java.util.List;

import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.concurrency.RWWeakHashMap;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementMat extends ElementFakeEnum<Mat> {

	public ElementMat(Element parent, String id, Need need, Text editorDescription) {
		super(Mat.class, parent, id, need, editorDescription);
	}

	static RWWeakHashMap<Object, List<Mat>> cache = new RWWeakHashMap<>(1, 1f);

	@Override
	protected List<Mat> cacheOrBuild() {
		return cachedOrBuild(cache, () -> Mat.values().stream().sorted(Comparator.comparing(e -> e.getId())));
	}

	@Override
	public Mat editorIconType() {
		return CommonMats.APPLE;
	}

}
