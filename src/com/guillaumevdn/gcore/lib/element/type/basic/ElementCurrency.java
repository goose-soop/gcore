package com.guillaumevdn.gcore.lib.element.type.basic;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.economy.Currency;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementCurrency extends ElementFakeEnum<Currency> {

	public ElementCurrency(Element parent, String id, Need need, Text editorDescription) {
		super(Currency.class, parent, id, need, editorDescription, CollectionUtils.asList(Currency.values()));
	}

	@Override
	public Mat editorIconType() {
		return CommonMats.GOLD_BLOCK;
	}

}
