package be.pyrrh4.pyrcore.lib.parseable.container;

import java.util.List;

import org.bukkit.entity.Player;

import be.pyrrh4.pyrcore.PCLocale;
import be.pyrrh4.pyrcore.lib.material.Mat;
import be.pyrrh4.pyrcore.lib.messenger.Tab;
import be.pyrrh4.pyrcore.lib.parseable.ContainerParseable;
import be.pyrrh4.pyrcore.lib.parseable.Parseable;
import be.pyrrh4.pyrcore.lib.parseable.editor.EditorGUI;
import be.pyrrh4.pyrcore.lib.parseable.primitive.PPString;

public class CPTab extends ContainerParseable {

	// base
	private PPString header = addComponent(new PPString("header", this, "QuestCreator is like the best questing plugin", false, 0, EditorGUI.ICON_STRING, PCLocale.GUI_GENERIC_EDITOR_TAB_HEADERLORE.getLines()));
	private PPString footer = addComponent(new PPString("footer", this, "Honestly, just take a look ! #selfadINSIDEtheproduct :D", false, 1, EditorGUI.ICON_STRING, PCLocale.GUI_GENERIC_EDITOR_TAB_FOOTERLORE.getLines()));

	public CPTab(String id, Parseable parent, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, "tab", mandatory, editorSlot, editorIcon, editorDescription);
	}

	// get
	public PPString getHeader() {
		return header;
	}

	public String getHeader(Player parser) {
		return header.getParsedValue(parser);
	}

	public PPString getFooter() {
		return footer;
	}

	public String getFooter(Player parser) {
		return footer.getParsedValue(parser);
	}

	// methods
	public Tab getParsed(Player parser) {
		String header = getHeader(parser);
		String footer = getFooter(parser);
		return header != null && footer != null ? new Tab(header, footer) : null;
	}

	// clone
	protected CPTab() {
		super();
	}

	@Override
	public CPTab clone() {
		return (CPTab) super.clone();
	}

}
