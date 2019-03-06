package be.pyrrh4.pyrcore.lib.parseable.container;

import java.util.List;

import org.bukkit.entity.Player;

import be.pyrrh4.pyrcore.PCLocale;
import be.pyrrh4.pyrcore.lib.material.Mat;
import be.pyrrh4.pyrcore.lib.messenger.Title;
import be.pyrrh4.pyrcore.lib.parseable.ContainerParseable;
import be.pyrrh4.pyrcore.lib.parseable.Parseable;
import be.pyrrh4.pyrcore.lib.parseable.editor.EditorGUI;
import be.pyrrh4.pyrcore.lib.parseable.primitive.PPInteger;
import be.pyrrh4.pyrcore.lib.parseable.primitive.PPString;

public class CPTitle extends ContainerParseable {

	// base
	private PPString title = addComponent(new PPString("title", this, "Fresh Avocado", false, 0, EditorGUI.ICON_STRING, PCLocale.GUI_GENERIC_EDITOR_TITLE_TITLELORE.getLines()));
	private PPString subtitle = addComponent(new PPString("subtitle", this, "Fresh a voca doooo !", false, 1, EditorGUI.ICON_STRING, PCLocale.GUI_GENERIC_EDITOR_TITLE_SUBTITLELORE.getLines()));
	private PPInteger fadeIn = addComponent(new PPInteger("fade_in", this, "5", 0, Integer.MAX_VALUE, false, 2, EditorGUI.ICON_NUMBER, PCLocale.GUI_GENERIC_EDITOR_TITLE_FADEINLORE.getLines()));
	private PPInteger duration = addComponent(new PPInteger("duration", this, "50", 0, Integer.MAX_VALUE, false, 3, EditorGUI.ICON_NUMBER, PCLocale.GUI_GENERIC_EDITOR_TITLE_DURATIONLORE.getLines()));
	private PPInteger fadeOut = addComponent(new PPInteger("fade_out", this, "5", 0, Integer.MAX_VALUE, false, 4, EditorGUI.ICON_NUMBER, PCLocale.GUI_GENERIC_EDITOR_TITLE_FADEOUTLORE.getLines()));

	public CPTitle(String id, Parseable parent, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, "title", mandatory, editorSlot, editorIcon, editorDescription);
	}

	// get
	public PPString getTitle() {
		return title;
	}

	public String getTitle(Player parser) {
		return title.getParsedValue(parser);
	}

	public PPString getSubtitle() {
		return subtitle;
	}

	public String getSubtitle(Player parser) {
		return subtitle.getParsedValue(parser);
	}

	public PPInteger getFadeIn() {
		return fadeIn;
	}

	public Integer getFadeIn(Player parser) {
		return fadeIn.getParsedValue(parser);
	}

	public PPInteger getDuration() {
		return duration;
	}

	public Integer getDuration(Player parser) {
		return duration.getParsedValue(parser);
	}

	public PPInteger getFadeOut() {
		return fadeOut;
	}

	public Integer getFadeOut(Player parser) {
		return fadeOut.getParsedValue(parser);
	}

	// methods
	public Title getParsed(Player parser) {
		String title = getTitle(parser);
		String subtitle = getSubtitle(parser);
		Integer fadeIn = getFadeIn(parser);
		Integer duration = getDuration(parser);
		Integer fadeOut = getFadeOut(parser);
		return title != null && subtitle != null && fadeIn != null && duration != null && fadeOut != null ? new Title(title, subtitle, fadeIn, duration, fadeOut) : null;
	}

	// clone
	protected CPTitle() {
		super();
	}

	@Override
	public CPTitle clone() {
		return (CPTitle) super.clone();
	}

}
