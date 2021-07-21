package com.guillaumevdn.gcore.lib.compatibility.material;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import com.guillaumevdn.gcore.lib.compatibility.Compat;
import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.reflection.Reflection;
import com.guillaumevdn.gcore.lib.reflection.ReflectionObject;
import com.guillaumevdn.gcore.lib.reflection.procedure.ReflectionProcedureBiConsumer;
import com.guillaumevdn.gcore.lib.reflection.procedure.ReflectionProcedureBiFunction;
import com.guillaumevdn.gcore.lib.reflection.procedure.ReflectionProcedureTriConsumer;

/**
 * @author GuillaumeVDN
 */
final class MatCompatBlock {

	// ----- set block
	private static final ReflectionProcedureBiConsumer<Block, Mat> SET_BLOCK = new ReflectionProcedureBiConsumer<Block, Mat>()
			.setIf(Version.ATLEAST_1_13, (block, mat) -> {
				Material material = mat.getData().getDataInstance();
				// door : set top
				if (mat.getData().isDoor()) {
					Block top = block.getRelative(BlockFace.UP);
					top.setType(material);
					Reflection.processBlockData(top, data -> {
						data.invokeMethod("setHalf", Reflection.getEnum("org.bukkit.block.data.Bisected.Half").safeValueOf("TOP").get());
					});
				}
				// set block whatsoever
				block.setType(material);
			})
			.orElse((block, mat) -> {
				int typeId = ReflectionObject.of(mat.getData().getDataInstance()).invokeMethod("getId").get(int.class);
				// door
				if (mat.getData().isDoor()) {
					ReflectionObject.of(block.getRelative(BlockFace.UP)).invokeMethod("setTypeIdAndData", typeId, (byte) 0x8, false);
					ReflectionObject.of(block).invokeMethod("setTypeIdAndData", typeId, (byte) 0x4, false);
				}
				// other
				else {
					ReflectionObject.of(block).invokeMethod("setTypeIdAndData", typeId, (byte) mat.getData().getLegacyDataOrZero(), false);
				}
			});

	static void setBlock(Block block, Mat mat) {
		SET_BLOCK.process(block, mat);
	}

	// ----- set block change
	private static final ReflectionProcedureTriConsumer<Mat, Block, Player> SET_BLOCK_CHANGE = new ReflectionProcedureTriConsumer<Mat, Block, Player>()
			.orElse((mat, block, player) -> {
				player.sendBlockChange(block.getLocation(), mat.getData().getDataInstance(), (byte) mat.getData().getLegacyDataOrZero()); // even in 1.13+, send data
			});

	static void setBlockChange(Block block, Mat mat, Player player) {
		SET_BLOCK_CHANGE.process(mat, block, player);
	}

	// ----- match
	private static final ReflectionProcedureBiFunction<Block, Mat, Boolean> MATCH = new ReflectionProcedureBiFunction<Block, Mat, Boolean>()
			.orElse((block, mat) -> {
				return block.getType().equals(mat.getData().getDataInstance()) && (Version.ATLEAST_1_13 || mat.getData().acceptsLegacyData(Compat.getLegacyData(block)));
			});

	static boolean match(Block block, Mat mat) {
		return mat.isNoCheck() || MATCH.process(block, mat);
	}

}
