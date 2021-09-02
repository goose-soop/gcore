package com.guillaumevdn.gcore.lib.element.type.basic;

import java.util.Comparator;
import java.util.List;

import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.concurrency.RWWeakHashMap;
import com.guillaumevdn.gcore.lib.economy.Currency;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementCurrency extends ElementFakeEnum<Currency> {

	public ElementCurrency(Element parent, String id, Need need, Text editorDescription) {
		super(Currency.class, parent, id, need, editorDescription);
	}

	static RWWeakHashMap<Object, List<Currency>> cache = new RWWeakHashMap<>(1, 1f);
	@Override
	protected List<Currency> cacheOrBuild() {
		return cachedOrBuild(cache, () -> Currency.values().stream().sorted(Comparator.comparing(e -> e.getId())));
	}

	@Override
	public Mat editorIconType() {
		return CommonMats.GOLD_BLOCK;
	}

}
