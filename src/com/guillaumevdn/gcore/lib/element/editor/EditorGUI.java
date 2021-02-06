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

	public EditorGUI(IElement element, ClickCall fromCall) {
		super(element.getSuperElement().getPlugin(), "editor_" + StringUtils.generateRandomAlphanumericString(10) + (element == null ? "" : "_" + element.getClass().getSimpleName()), fromCall == null ? element.getId() : fromCall.getGUI().getName() + "/" + element.getId(), GUIType.CHEST_6_ROW, fromCall);
	}

	public EditorGUI(GPlugin plugin, String title, ClickCall fromCall) {
		super(plugin, "editor_" + StringUtils.generateRandomAlphanumericString(10), fromCall == null || fromCall.getGUI() == null /* can happen due to /qc edit <editor> */ ? title : fromCall.getGUI().getName() + "/" + title, GUIType.CHEST_6_ROW, fromCall);
	}

	/** @return true if the GUI was opened */
	@Override
	public final boolean openFor(Player player, int pageIndex) {
		return refill() && super.openFor(player, pageIndex);
	}

}
