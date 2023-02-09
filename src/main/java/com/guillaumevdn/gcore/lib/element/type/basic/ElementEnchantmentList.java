package com.guillaumevdn.gcore.lib.element.type.basic;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.bukkit.enchantments.Enchantment;

import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementEnchantmentList extends ElementFakeEnumList<Enchantment> {

	public ElementEnchantmentList(Element parent, String id, Need need, Text editorDescription) {
		super(Enchantment.class, parent, id, need, editorDescription);
	}

	@Override
	protected List<Enchantment> cacheOrBuild() {
		return cachedOrBuild(ElementEnchantment.cache, () -> Arrays.stream(Enchantment.values()).sorted(Comparator.comparing(e -> e.getName())));
	}

	@Override
	public Mat editorIconType() {
		return CommonMats.ENCHANTED_BOOK;
	}

}
