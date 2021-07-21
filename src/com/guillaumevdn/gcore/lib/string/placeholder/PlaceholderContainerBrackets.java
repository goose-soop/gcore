package com.guillaumevdn.gcore.lib.string.placeholder;

import java.util.List;
import java.util.function.BiFunction;

import org.bukkit.entity.Player;

/**
 * @author GuillaumeVDN
 */
public class PlaceholderContainerBrackets extends PlaceholderContainerSingleChar {

	public PlaceholderContainerBrackets(String id, int priority, boolean needPlayer, List<String> description, BiFunction<String, Player, String> replacer) {
		super(id, priority, needPlayer, description, '{', '}', replacer);
	}

}
