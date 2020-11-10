package com.guillaumevdn.gcore.lib.element.type.basic;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementWorldList extends ElementAbstractEnumList<World> {

	public ElementWorldList(Element parent, String id, Need need, Text editorDescription) {
		super(World.class, false, parent, id, need, editorDescription);
	}

	// get
	@Override
	public List<World> getValues() {
		return CollectionUtils.asList(Bukkit.getWorlds());
	}

	@Override
	public Mat editorIconType() {
		return CommonMats.FURNACE_MINECART;
	}

}
