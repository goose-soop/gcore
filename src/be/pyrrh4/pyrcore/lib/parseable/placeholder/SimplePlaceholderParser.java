package be.pyrrh4.pyrcore.lib.parseable.placeholder;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

public abstract class SimplePlaceholderParser extends PlaceholderParser {

	// base
	private char beginChar, endChar;

	/** @param priority priority for parsing (PlaceholderAPI is 100 and {math:EXPRESSION} is 200) */
	public SimplePlaceholderParser(int priority, char beginChar, char endChar, List<String> description) {
		super(priority, description);
		this.beginChar = beginChar;
		this.endChar = endChar;
	}

	// abstract
	/**
	 * Parse the placeholder
	 * @param player the player (a null check should be made)
	 * @param placeholderContent the placeholder content, without begin and end chars
	 * @return a value, or null if the placeholder isn't handled by this method
	 */
	protected abstract String parsePlaceholders(Player player, String placeholderContent);

	// overriden
	@Override
	protected String parse(Player player, String raw) {
		List<Character> chars = new ArrayList<Character>();
		for (char c : raw.toCharArray()) {
			chars.add(c);
		}
		Integer ignoreEndBrackets = 0, lastBegin = null, lastInnerBegin = null;
		for (int index = 0; index < chars.size(); ++index) {
			char c = chars.get(index);
			// placeholder start
			if (c == beginChar) {
				// we're doing a placeholder
				if (lastBegin != null) {
					++ignoreEndBrackets;
					if (lastInnerBegin == null) lastInnerBegin = index;
				}
				// start a placeholder
				else {
					lastBegin = index;
				}
			}
			// placeholder end
			else if (c == endChar) {
				// we're doing a placeholder
				if (lastBegin != null) {
					// eventually ignore it
					if (ignoreEndBrackets > 0) {
						--ignoreEndBrackets;
					}
					// parse it
					else {
						String placeholder = build(chars, lastBegin + 1, index - 1);
						String parsed = parsePlaceholders(player, placeholder);
						// result is null (couldn't parse)
						if (parsed == null) {
							// try again at last inner begin if there's any
							if (lastInnerBegin != null) {
								index = lastInnerBegin - 1;// -1 because the loop will do ++index
								lastInnerBegin = null;
							}
						}
						// replace result in chars
						else {
							for (int i = 0; i < placeholder.length() + 2; ++i) {// +2 to take {} into consideration
								chars.remove(lastBegin.intValue());
							}
							char[] parsedChars = parsed.toCharArray();
							for (int i = 0; i < parsedChars.length; ++i) {
								chars.add(lastBegin + i, parsedChars[i]); 
							}
							index = lastBegin + parsed.length();
						}
						// reset
						lastBegin = null;
					}
				}
			}
		}
		return build(chars, 0, chars.size() - 1);
	}

	private static String build(List<Character> chars, int begin, int end) {
		StringBuilder builder = new StringBuilder();
		for (int i = begin; i <= end; ++i) {
			if (i >= 0 && i < chars.size()) {
				builder.append(chars.get(i));
			}
		}
		return builder.toString();
	}

}