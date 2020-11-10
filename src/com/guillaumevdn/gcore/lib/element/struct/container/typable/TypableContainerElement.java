package com.guillaumevdn.gcore.lib.element.struct.container.typable;

import java.util.List;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.struct.container.ContainerElement;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public abstract class TypableContainerElement<T extends TypableElementType> extends ContainerElement {

	private final TypableElementTypes<T> types;
	private final ElementTypableElementType<T> type;

	public TypableContainerElement(TypableElementTypes<T> types, String valueTypeName, Element parent, String id, Need need, Text editorDescription) {
		super(valueTypeName, parent, id, need, editorDescription);
		this.types = types;
		this.type = addType();
	}

	protected abstract ElementTypableElementType<T> addType();

	// get
	public final TypableElementTypes<T> getTypes() {
		return types;
	}

	protected final ElementTypableElementType<T> getTypeElement() {
		return type;
	}

	public final T getType() {
		T type = this.type.parseGeneric().orNull();
		if (type == null) { // fix type, a null type can't happen !
			type = types.defaultValue();
			this.type.setValue(CollectionUtils.asList(type.getId()));
			type.fillTypeSpecificElements(this);
			onTypeChange(null, type);
		}
		return type;
	}

	public void onTypeChange(T oldType, T newType) {
	}

	// load
	@Override
	protected void doRead() throws Throwable {
		// clear elements from previous type eventually
		T type = getType();
		if (type != null) {
			type.clearElements(this);
		}
		// load type and fix it eventually ; then fill elements
		this.type.read();
		type = this.type.parseGeneric().orNull();
		if (type == null) { // fix type, a null type can't happen !
			type = types.defaultValue();
			this.type.setValue(CollectionUtils.asList(type.getId()));
			onTypeChange(null, type);
		}
		type.fillTypeSpecificElements(this);
		// load elements
		for (Element element : values()) {
			if (!element.equals(this.type)) {
				element.read();
			}
		}
	}

	// editor
	@Override
	public Mat editorIconType() {
		return getType().getIcon();
	}

	@Override
	public List<String> editorCurrentValue() {
		return CollectionUtils.asList(getTypeElement().getValueOrDefault());
	}

}
