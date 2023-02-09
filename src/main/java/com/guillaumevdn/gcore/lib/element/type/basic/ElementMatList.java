package com.guillaumevdn.gcore.lib.element.type.basic;

import java.util.Comparator;
import java.util.List;

import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementMatList extends ElementFakeEnumList<Mat> {

	public ElementMatList(Element parent, String id, Need need, Text editorDescription) {
		super(Mat.class, parent, id, need, editorDescription);
	}

	@Override
	protected List<Mat> cacheOrBuild() {
		return cachedOrBuild(ElementMat.cache, () -> Mat.values().stream().sorted(Comparator.comparing(e -> e.getId())));
	}

	@Override
	public Mat editorIconType() {
		return CommonMats.APPLE;
	}

}
