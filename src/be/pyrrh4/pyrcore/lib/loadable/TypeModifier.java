package be.pyrrh4.pyrcore.lib.loadable;

import org.bukkit.entity.Player;

public interface TypeModifier {

	public void modify(Loadable<?> modified, Player player);

}
