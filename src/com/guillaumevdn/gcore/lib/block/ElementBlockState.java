package com.guillaumevdn.gcore.lib.block;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.struct.Element;
import com.guillaumevdn.gcore.lib.element.struct.Need;
import com.guillaumevdn.gcore.lib.element.type.basic.ElementLinear;
import com.guillaumevdn.gcore.lib.serialization.Serializer;
import com.guillaumevdn.gcore.lib.string.Text;

/**
 * @author GuillaumeVDN
 */
public class ElementBlockState extends ElementLinear<BlockStateType, BlockState> {

	public ElementBlockState(Element parent, String id, Need need, Text editorDescription) {
		super(Serializer.BLOCK_STATE, parent, id, need, editorDescription, CollectionUtils.asList(BlockStateType.values()));
	}

	@Override
	public Mat editorIconType() {
		return CommonMats.REDSTONE;
	}

}
