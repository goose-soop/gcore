package com.guillaumevdn.gcore.lib.gui.element;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.guillaumevdn.gcore.lib.gui.element.item.ElementGUIItem;
import com.guillaumevdn.gcore.lib.gui.element.item.ElementGUIItemHolder;
import com.guillaumevdn.gcore.lib.gui.struct.GUI;
import com.guillaumevdn.gcore.lib.gui.struct.GUIType;
import com.guillaumevdn.gcore.lib.gui.struct.active.ActiveGUI;
import com.guillaumevdn.gcore.lib.gui.struct.active.ItemHolder;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.string.StringUtils;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public class ActiveElementGUI extends ActiveGUI {

	private ElementGUI element;

	public ActiveElementGUI(ElementGUI element, Replacer replacer, Option... options) {
		super(element.getPlugin(), "instance_" + element.getId() + "_" + StringUtils.generateRandomAlphanumericString(5), element.getName().parse(replacer).orElse("?"), element.getSize().parse(replacer).orElse(GUIType.CHEST_6_ROW), replacer, options);
		this.element = element;
	}

	public ElementGUI getElement() {
		return element;
	}

	@Override
	public final Collection<ItemHolder> getContents() {
		return modifiedContentsStream().sorted((a, b) -> {
			// process borders first, so other items can later override them if necessary
			boolean borderA = ObjectUtils.ifCanBeCastedDo(a, ElementGUIItemHolder.class, aa -> aa.getElement().getType().getId().contains("DYNAMIC_BORDER")).orElse(false);
			boolean borderB = ObjectUtils.ifCanBeCastedDo(b, ElementGUIItemHolder.class, bb -> bb.getElement().getType().getId().contains("DYNAMIC_BORDER")).orElse(false);
			if (borderA && !borderB) return -1;
			if (!borderA && borderB) return 1;
			return 0;
		}).collect(Collectors.toList());
	}

	protected Stream<ItemHolder> modifiedContentsStream() {
		return element.getContents().stream().map(ElementGUIItem::getHolder);
	}

	public static boolean isActiveElementGUI(GUI gui, ElementGUI element) {
		return gui instanceof ActiveElementGUI && gui.getId().startsWith("instance_" + element.getId() + "_");
	}

}
