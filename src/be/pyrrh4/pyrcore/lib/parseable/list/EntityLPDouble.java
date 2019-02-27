package be.pyrrh4.pyrcore.lib.parseable.list;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import be.pyrrh4.pyrcore.lib.material.Mat;
import be.pyrrh4.pyrcore.lib.parseable.Parseable;
import be.pyrrh4.pyrcore.lib.parseable.data.DataLink;
import be.pyrrh4.pyrcore.lib.parseable.data.RegularDataLink;
import be.pyrrh4.pyrcore.lib.parseable.primitive.PPDouble;

public class EntityLPDouble extends EntityListParseable<PPDouble> {

	// base
	private String defaultValue;
	private Double min, max;

	public EntityLPDouble(String id, Parseable parent, boolean allowDefaultCase, String defaultValue, Double min, Double max, boolean mandatory, int editorSlot, Mat editorIcon, List<String> editorDescription) {
		super(id, parent, allowDefaultCase, "decimal number", mandatory, editorSlot, editorIcon, editorDescription);
		this.defaultValue = defaultValue;
		this.min = min;
		this.max = max;
	}

	// get
	public Double getValue(String key, Player parser) {
		PPDouble elem = getValue(key);
		return elem != null ? elem.getParsedValue(parser) : null;
	}

	public Double getValue(Entity entity, Player parser) {
		PPDouble elem = getValue(entity);
		return elem != null ? elem.getParsedValue(parser) : null;
	}

	// methods
	@Override
	public PPDouble createElement(String elementId) {
		// create data
		DataLink data;
		if (getLastData() instanceof RegularDataLink) {
			RegularDataLink compact = (RegularDataLink) getLastData();
			data = new RegularDataLink(null, compact.getPlugin(), compact.getSuperId(), compact.getConfig(), compact.getPath() + "." + elementId);
		} else if (getLastData() instanceof RegularDataLink) {
			RegularDataLink compact = (RegularDataLink) getLastData();
			data = new RegularDataLink(null, compact.getPlugin(), compact.getSuperId(), compact.getConfig(), compact.getPath() + "." + elementId);
		} else {
			return null;
		}
		// create
		PPDouble element = new PPDouble(elementId.toLowerCase(), this, defaultValue, min, max, false, -1, getEditorIcon(), getEditorDescription());
		data.setComponent(element);
		element.setLastData(data);
		element.load(data);
		return element;
	}

	@Override
	public PPDouble loadElement(String elementId, DataLink data) {
		// create
		PPDouble element = new PPDouble(elementId.toLowerCase(), this, defaultValue, min, max, false, -1, getEditorIcon(), getEditorDescription());
		data.setComponent(element);
		element.setLastData(data);
		element.load(data);
		return element;
	}

}
