package com.guillaumevdn.gcore.lib.gui.struct.active;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.guillaumevdn.gcore.lib.concurrency.RWLowerCaseHashMap;
import com.guillaumevdn.gcore.lib.gui.element.ActiveElementGUI;
import com.guillaumevdn.gcore.lib.gui.element.ElementGUI;
import com.guillaumevdn.gcore.lib.gui.element.item.ElementGUIItem;
import com.guillaumevdn.gcore.lib.string.placeholder.Replacer;

/**
 * @author GuillaumeVDN
 */
public class ActiveExtraElementGUI extends ActiveElementGUI {

	private final RWLowerCaseHashMap<ItemHolder> more = new RWLowerCaseHashMap<>(5, 1f);

	public ActiveExtraElementGUI(ElementGUI element, Replacer replacer, Option... options) {
		super(element, replacer, options);
	}

	public RWLowerCaseHashMap<ItemHolder> getMore() {
		return more;
	}

	protected final void addItem(ItemHolder holder) {
		more.put(holder.getId(), holder);
	}

	protected List<ItemHolder> buildDynamic() {
		return new ArrayList<>();
	}

	@Override
	protected final Stream<ItemHolder> modifiedContentsStream() {
		Stream<ItemHolder> stream = getElement().getContents().stream().map(ElementGUIItem::getHolder);

		List<ItemHolder> more = new ArrayList<>();
		this.more.forEach((__, holder) -> more.add(holder));
		more.addAll(buildDynamic());

		return more.isEmpty() ? stream : Stream.concat(stream, more.stream());
	}

}
