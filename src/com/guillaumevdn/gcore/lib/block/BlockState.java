package com.guillaumevdn.gcore.lib.block;

import java.util.List;

import com.guillaumevdn.gcore.lib.element.type.basic.LinearObject;

/**
 * @author GuillaumeVDN
 */
public class BlockState extends LinearObject<BlockStateType> {

	public BlockState(BlockStateType type) {
		this(type, null);
	}

	public BlockState(BlockStateType type, List<String> arguments) {
		super(type, arguments);
	}

}
