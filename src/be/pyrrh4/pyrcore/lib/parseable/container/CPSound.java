package be.pyrrh4.pyrcore.lib.parseable.container;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import be.pyrrh4.pyrcore.PCLocale;
import be.pyrrh4.pyrcore.lib.material.Mat;
import be.pyrrh4.pyrcore.lib.parseable.ContainerParseable;
import be.pyrrh4.pyrcore.lib.parseable.Parseable;
import be.pyrrh4.pyrcore.lib.parseable.editor.EditorGUI;
import be.pyrrh4.pyrcore.lib.parseable.primitive.PPEnum;
import be.pyrrh4.pyrcore.lib.parseable.primitive.PPFloat;
import be.pyrrh4.pyrcore.lib.versioncompat.sound.Sound;

public class CPSound extends ContainerParseable {

	// base
	private PPEnum<Sound> type = addComponent(new PPEnum<Sound>("type", this, null, Sound.class, "PyrCore sound", false, 0, EditorGUI.ICON_SOUND, PCLocale.GUI_GENERIC_EDITOR_SOUND_TYPELORE.getLines()));
	private PPFloat volume = addComponent(new PPFloat("volume", this, "1", Float.MIN_VALUE, Float.MAX_VALUE, false, 1, EditorGUI.ICON_NUMBER, PCLocale.GUI_GENERIC_EDITOR_SOUND_VOLUMELORE.getLines()));
	private PPFloat pitch = addComponent(new PPFloat("pitch", this, "1", Float.MIN_VALUE, Float.MAX_VALUE, false, 2, EditorGUI.ICON_NUMBER, PCLocale.GUI_GENERIC_EDITOR_SOUND_PITCH.getLines()));

	public CPSound(String id, Parseable parent, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, "sound", mandatory, editorSlot, editorIcon, editorDescription);
	}

	// get
	public PPEnum<Sound> getType() {
		return type;
	}

	public Sound getType(Player parser) {
		return type.getParsedValue(parser);
	}

	public PPFloat getVolume() {
		return volume;
	}

	public Float getVolume(Player parser) {
		return volume.getParsedValue(parser);
	}

	public PPFloat getPitch() {
		return pitch;
	}

	public Float getPitch(Player parser) {
		return pitch.getParsedValue(parser);
	}

	// methods
	public boolean isValid(Player parser) {
		return getType(parser) != null;
	}

	public void play(List<Player> players, Player parser) {
		Sound type = getType(parser);
		Float volume = getVolume(parser);
		Float pitch = getPitch(parser);
		if (type != null && volume != null && pitch != null) {
			type.play(players, volume, pitch);
		}
	}

	public void play(Player player, Player parser) {
		Sound type = getType(parser);
		Float volume = getVolume(parser);
		Float pitch = getPitch(parser);
		if (type != null && volume != null && pitch != null) {
			type.play(player, volume, pitch);
		}
	}

	public void play(Location location, Player parser) {
		Sound type = getType(parser);
		Float volume = getVolume(parser);
		Float pitch = getPitch(parser);
		if (type != null && volume != null && pitch != null) {
			type.play(location, volume, pitch);
		}
	}

	// clone
	protected CPSound() {
		super();
	}

	@Override
	public CPSound clone() {
		// clone
		CPSound clone = (CPSound) super.clone();
		// clone properties
		clone.type = type.clone();
		clone.volume = volume.clone();
		clone.pitch = pitch.clone();
		// success
		return clone;
	}

}
