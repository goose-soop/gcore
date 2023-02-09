package com.guillaumevdn.gcore.lib.element.type.basic;

import java.util.Comparator;
import java.util.List;

import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.economy.Currency;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementCurrencyList extends ElementFakeEnumList<Currency> {

	public ElementCurrencyList(Element parent, String id, Need need, Text editorDescription) {
		super(Currency.class, parent, id, need, editorDescription);
	}

	@Override
	protected List<Currency> cacheOrBuild() {
		return cachedOrBuild(ElementCurrency.cache, () -> Currency.values().stream().filter(curr -> curr.isEnabled()).sorted(Comparator.comparing(e -> e.getId())));
	}

	@Override
	public Mat editorIconType() {
		return CommonMats.GOLD_BLOCK;
	}

}
