package com.guillaumevdn.gcore.lib.element.struct.container.typable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.collection.LowerCaseArrayList;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;

/**
 * @author GuillaumeVDN
 */
public abstract class TypableElementType<T extends TypableContainerElement> implements Comparable<TypableElementType> {

	private final String id;
	private final Mat icon;

	public TypableElementType(String id, Mat icon) {
		this.id = id;
		this.icon = icon;
	}

	// ----- get
	public final String getId() {
		return id;
	}

	public final Mat getIcon() {
		return icon;
	}

	// ----- elements
	private transient List<String> lastFilled = new ArrayList<>();

	public final void fillTypeSpecificElements(T value) {
		value.getStartRowSlots().removeAll(lastFilled);
		value.getSkipOneSlots().removeAll(lastFilled);
		LowerCaseArrayList previousIds = CollectionUtils.asLowercaseList(value.keys());
		doFillTypeSpecificElements(value);
		lastFilled = CollectionUtils.findNew(previousIds, CollectionUtils.asLowercaseList(value.keys()));
		if (!lastFilled.isEmpty()) {
			if (!value.getTypeName().contains("quest object")) { // otherwise it dépasses :saperlipopette:
				value.getStartRowSlots().add(lastFilled.get(0));
			}
		}
	}

	protected void doFillTypeSpecificElements(T value) {
	}

	public final void clearElements(T value) {
		lastFilled.forEach(elementId -> value.remove(elementId));
	}

	// ----- object
	@Override
	public final String toString() {
		return id;
	}

	@Override
	public final boolean equals(Object obj) {
		TypableElementType other = ObjectUtils.castOrNull(obj, getClass());
		return other != null && other.id.equalsIgnoreCase(id);
	}

	@Override
	public final int hashCode() {
		return Objects.hash(getClass(), id);
	}

	@Override
	public final int compareTo(TypableElementType other) {
		return other == null ? 1 : id.compareToIgnoreCase(other.id);
	}

}
