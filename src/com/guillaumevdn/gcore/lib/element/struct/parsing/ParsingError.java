package com.guillaumevdn.gcore.lib.element.struct.parsing;

import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.TextEditorGeneric;
import com.guillaumevdn.gcore.lib.element.struct.IElement;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.string.StringUtils;

/**
 * @author GuillaumeVDN
 */
public class ParsingError extends Exception {

	private static final long serialVersionUID = -1956842084897823970L;
	private IElement element;

	public ParsingError(IElement element, String message) {
		super(message);
		this.element = element;
	}

	public ParsingError(IElement element, Throwable cause) {
		super(cause);
		this.element = element;
	}

	// FIXME : use those logging methods everywhere logs are needed, instead of the other logging methods

	// send error
	public void send(Player player) {
		TextEditorGeneric.messageCantImportValue.replace("{error}", () -> getMessage()).send(player);
	}

	public static void print(Throwable error, IElement element) {
		ParsingError pe = ObjectUtils.castOrNull(error, ParsingError.class);
		if (pe != null) {
			if (pe.element != null) {
				element = pe.element;
			}
			if (error.getCause() != null) {  // sometimes we create a dummy ParsingError with a cause when we're forced to catch something
				error = error.getCause();
			}
		}
		pe = ObjectUtils.castOrNull(error, ParsingError.class);
		if (error instanceof ParsingError) {
			if (pe.element != null) {
				element = pe.element;
			}
			element.getSuperElement().getPlugin().getMainLogger().error(ParsingError.buildErrorAtPathInFile(error.getMessage(), element));
		} else {
			element.getSuperElement().getPlugin().getMainLogger().error(ParsingError.buildUnexpectedHeaderErrorAtPathInFile(error.getMessage(), element), error);
		}
	}

	// build error
	public static String buildErrorAtPath(String error, IElement element) {
		return StringUtils.capitalize(error) + " " + atPath(element);
	}

	public static String buildErrorAtPathInFile(String error, IElement element) {
		return StringUtils.capitalize(error) + " " + atPathInFile(element);
	}

	public static String buildUnexpectedHeaderErrorAtPathInFile(String error, IElement element) {
		return "Unexpected error when parsing " + element.getTypeName() + " " + atPathInFile(element) + " : " + error;
	}

	private static String atPath(IElement element) {
		return "at path §4" + element.getConfigurationPath() + "§r§c";
	}

	private static String atPathInFile(IElement element) {
		return "in file §4" + element.getSuperElement().getConfiguration().getLogFilePath() + "§r§c at path §4" + element.getConfigurationPath() + "§r§c";
	}

}
