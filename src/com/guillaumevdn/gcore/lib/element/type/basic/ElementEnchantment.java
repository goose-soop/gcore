package com.guillaumevdn.gcore.lib.element.type.basic;

import org.bukkit.enchantments.Enchantment;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementEnchantment extends ElementFakeEnum<Enchantment> {

	public ElementEnchantment(Element parent, String id, Need need, Text editorDescription) {
		super(Enchantment.class, parent, id, need, editorDescription, CollectionUtils.asList(Enchantment.values()));
	}

	@Override
	public Mat editorIconType() {
		return CommonMats.ENCHANTED_BOOK;
	}

}
