package com.guillaumevdn.gcore.lib.gui.struct.active.modified;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.lib.collection.LowerCaseHashMap;
import com.guillaumevdn.gcore.lib.gui.element.ActiveElementGUI;
import com.guillaumevdn.gcore.lib.gui.element.ElementGUI;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;
import com.guillaumevdn.gcore.lib.gui.struct.active.ItemHolder;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public class ActiveModifiedConfigElementGUI extends ActiveElementGUI {

	private LowerCaseHashMap<ItemHolder> modified = new LowerCaseHashMap<>();

	public ActiveModifiedConfigElementGUI(ElementGUI element, Replacer replacer, ClickCall fromCall, Option... options) {
		super(element, replacer, fromCall, options);
	}

	// content
	protected void modifyItem(String itemId, Consumer<ClickCall> onClick) {
		modifyItem(itemId, null, onClick);
	}

	public void modifyItem(String itemId, UnaryOperator<ItemStack> iconModifier, Consumer<ClickCall> onClick) {
		modifyItem(itemId, iconModifier, null, onClick);
	}

	public void modifyItem(String itemId, UnaryOperator<ItemStack> iconModifier, Long forceRefreshDelayTicks, Consumer<ClickCall> onClick) {
		modified.put(itemId, new ModifiedConfigHolderItem(itemId, getElement().getContent(itemId).orThrow(() -> new NoSuchElementException(itemId))) {
			@Override
			protected ItemStack maybeModifyIcon(ItemStack icon) {
				return iconModifier != null ? iconModifier.apply(icon) : icon;
			}
			@Override
			protected void onClick(ClickCall call) {
				if (onClick != null) {
					onClick.accept(call);
				}
			}
		});
	}

	@Override
	public Collection<ItemHolder> getContents() {
		return modifiedContentsStream().collect(Collectors.toList());
	}

	protected Stream<ItemHolder> modifiedContentsStream() {
		return getElement().getContents().stream().map(item -> modified.getOrDefault(item.getId(), item.getHolder()));
	}

}
