package com.guillaumevdn.gcore.lib.element.editor;

import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.lib.GPlugin;
import com.guillaumevdn.gcore.lib.element.struct.IElement;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.gui.struct.GUI;
import com.guillaumevdn.gcore.lib.gui.struct.GUIType;
import com.guillaumevdn.gcore.lib.string.StringUtils;

/**
 * Editor GUIs are always refilled on open
 * @author GuillaumeVDN
 */
public class EditorGUI extends GUI {

	public EditorGUI(IElement element, ClickCall fromCall /* used just for title computation */) {
		super(element.getSuperElement().getPlugin(), "editor_" + StringUtils.generateRandomAlphanumericString(10) + (element == null ? "" : "_" + element.getClass().getSimpleName()), fromCall == null ? element.getId() : fromCall.getGUI().getName() + "/" + element.getId(), GUIType.CHEST_6_ROW, fromCall == null ? new Option[0] : new Option[] { Option.AUTO_BACK_ITEM });
	}

	public EditorGUI(GPlugin plugin, String title, ClickCall fromCall /* used just for title computation */) {
		this(plugin, title, fromCall, GUIType.CHEST_6_ROW);
	}

	public EditorGUI(GPlugin plugin, String title, ClickCall fromCall /* used just for title computation */, GUIType type) {
		super(plugin, "editor_" + StringUtils.generateRandomAlphanumericString(10), fromCall == null || fromCall.getGUI() == null /* can happen due to /qe edit <editor> */ ? title : fromCall.getGUI().getName() + "/" + title, type, fromCall == null ? new Option[0] : new Option[] { Option.AUTO_BACK_ITEM });
	}

	/** @return true if the GUI was opened */
	@Override
	public final boolean openFor(Player player, int pageIndex, ClickCall fromCall) {
		return refill() && super.openFor(player, pageIndex, fromCall);
	}

}
