package com.guillaumevdn.gcore;

import com.guillaumevdn.gcore.lib.string.TextElement;
import com.guillaumevdn.gcore.lib.string.TextEnumElement;

/**
 * @author GuillaumeVDN
 */
public enum TextGCore implements TextEnumElement {

	// ----- plugin
	messagePluginUnknown,
	messagePluginReloaded,
	messagePluginManipulateError,
	messagePluginInternalState,
	messagePluginList,
	messageGcoreExportFile,
	messageGcoreExportCouldnt,

	// ----- command
	commandDescriptionGcore,
	commandDescriptionGcorePlugins,
	commandDescriptionGcoreExport,
	commandDescriptionGcoreItemRead,
	commandDescriptionGcoreItemReadChat,
	commandDescriptionGcoreItemReadClick,

	messageItemReadNull,
	messageItemReadExported,

	;

	private TextElement text = new TextElement();

	TextGCore() {
	}

	// ----- get
	@Override
	public String getId() {
		return name();
	}

	@Override
	public TextElement getText() {
		return text;
	}

}
