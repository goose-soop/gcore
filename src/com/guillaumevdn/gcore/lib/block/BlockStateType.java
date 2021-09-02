package com.guillaumevdn.gcore.lib.block;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.compatibility.material.CommonMats;
import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.type.basic.LinearObjectType;
import com.guillaumevdn.gcore.lib.function.ThrowableBiConsumer;
import com.guillaumevdn.gcore.lib.function.ThrowableBiFunction;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;
import com.guillaumevdn.gcore.lib.reflection.ReflectionObject;
import com.guillaumevdn.gcore.lib.string.StringUtils;

/**
 * @author GuillaumeVDN
 */
public enum BlockStateType implements LinearObjectType {

	AGE(CommonMats.WHITE_WOOL, "Ageable",  // TODO : <1.13, we'll need to use Crops and their data, it would be interesting
			(state, data) -> data.invokeMethod("getAge").get(int.class) == state.getValidIntArgument(0),
			(state, data) -> data.invokeMethod("setAge", state.getValidIntArgument(0))
			),
	ATTACHED(CommonMats.WHITE_WOOL, "Attachable",
			(state, data) -> data.invokeMethod("isAttached").get(boolean.class) == state.getValidBooleanArgument(0),
			(state, data) -> data.invokeMethod("setAttached", state.getValidBooleanArgument(0))
			),
	AXIS(CommonMats.OAK_FENCE, "Orientable",
			(state, data) -> data.invokeMethod("getAxis").get().equals(state.getValidUnknownEnumTypeArgument("org.bukkit.Axis", 0)),
			(state, data) -> data.invokeMethod("setAxis", state.getValidUnknownEnumTypeArgument("org.bukkit.Axis", 0))
			),
	BISECTED_HALF(CommonMats.IRON_DOOR, "Bisected",
			(state, data) -> data.invokeMethod("getHalf").get().equals(state.getValidUnknownEnumTypeArgument("org.bukkit.block.data.Bisected$Half", 0)),
			(state, data) -> data.invokeMethod("setHalf", state.getValidUnknownEnumTypeArgument("org.bukkit.block.data.Bisected$Half", 0))
			),
	FACE_ATTACHABLE(CommonMats.LEVER, "FaceAttachable",
			(state, data) -> data.invokeMethod("getAttachedFace").get().equals(state.getValidUnknownEnumTypeArgument("org.bukkit.block.data.FaceAttachable$AttachedFace", 0)),
			(state, data) -> data.invokeMethod("setAttachedFace", state.getValidUnknownEnumTypeArgument("org.bukkit.block.data.FaceAttachable$AttachedFace", 0))
			),
	FACING(CommonMats.RED_BED, "Directional",
			(state, data) -> data.invokeMethod("getFacing").get().equals(state.getValidEnumTypeArgument(BlockFace.class, 0)),
			(state, data) -> data.invokeMethod("setFacing", state.getValidEnumTypeArgument(BlockFace.class, 0))
			),
	FACING_MULTIPLE(CommonMats.RED_BED, "MultipleFacing",
			(state, data) -> {
				List<BlockFace> faces = data.invokeMethod("getFaces").get();
				return CollectionUtils.contentEquals(faces, state.getValidEnumTypeArguments(BlockFace.class), false);
			},
			(state, data) -> {
				for (BlockFace face : state.getValidEnumTypeArguments(BlockFace.class)) {
					data.invokeMethod("setFace", face, true);
				}
			}
			),
	LIT(CommonMats.TORCH, "Lightable",
			(state, data) -> data.invokeMethod("isLit").get(boolean.class) == state.getValidBooleanArgument(0),
			(state, data) -> data.invokeMethod("setLit", state.getValidBooleanArgument(0))
			),
	HONEY_LEVEL(CommonMats.TORCH, "type.Beehive",
			(state, data) -> data.invokeMethod("getHoneyLevel").get(int.class) == state.getValidIntArgument(0),
			(state, data) -> data.invokeMethod("setHoneyLevel", state.getValidIntArgument(0))
			),
	OPENED(CommonMats.IRON_DOOR, "Openable",
			(state, data) -> data.invokeMethod("isOpen").get(boolean.class) == state.getValidBooleanArgument(0),
			(state, data) -> data.invokeMethod("setOpen", state.getValidBooleanArgument(0))
			),
	POWER(CommonMats.REDSTONE, "AnaloguePowerable",
			(state, data) -> data.invokeMethod("getPower").get(int.class) == state.getValidIntArgument(0),
			(state, data) -> data.invokeMethod("setPower", state.getValidIntArgument(0))
			),
	POWERED(CommonMats.REDSTONE, "Powerable",
			(state, data) -> data.invokeMethod("isPowered").get(boolean.class) == state.getValidBooleanArgument(0),
			(state, data) -> data.invokeMethod("setPowered", state.getValidBooleanArgument(0))
			),
	RAIL_SHAPE(CommonMats.RAIL, "Rail",
			(state, data) -> data.invokeMethod("getShape").get().equals(state.getValidUnknownEnumTypeArgument("org.bukkit.block.data.Rail$Shape", 0)),
			(state, data) -> data.invokeMethod("setShape", state.getValidUnknownEnumTypeArgument("org.bukkit.block.data.Rail$Shape", 0))
			),
	ROTATION(CommonMats.RAIL, "Rotatable",
			(state, data) -> data.invokeMethod("getRotation").get().equals(state.getValidEnumTypeArgument(BlockFace.class, 0)),
			(state, data) -> data.invokeMethod("setRotation", state.getValidEnumTypeArgument(BlockFace.class, 0))
			),
	SNOWY(CommonMats.ICE, "Snowy",
			(state, data) -> data.invokeMethod("isSnowy").get(boolean.class) == state.getValidBooleanArgument(0),
			(state, data) -> data.invokeMethod("setSnowy", state.getValidBooleanArgument(0))
			),
	LEVEL(CommonMats.WATER_BUCKET, "Levelled",
			(state, data) -> data.invokeMethod("getLevel").get(int.class) == state.getValidIntArgument(0),
			(state, data) -> data.invokeMethod("setLevel", state.getValidIntArgument(0))
			),
	WATERLOGGED(CommonMats.WATER_BUCKET, "Waterlogged",
			(state, data) -> data.invokeMethod("isWaterlogged").get(boolean.class) == state.getValidBooleanArgument(0),
			(state, data) -> data.invokeMethod("setWaterlogged", state.getValidBooleanArgument(0))
			)
	;

	private Mat icon;
	private Class<?> dataClass;
	private ThrowableBiFunction<BlockState, ReflectionObject, Boolean> checker;
	private ThrowableBiConsumer<BlockState, ReflectionObject> applier;

	BlockStateType(Mat icon, String dataClass, ThrowableBiFunction<BlockState, ReflectionObject, Boolean> checker, ThrowableBiConsumer<BlockState, ReflectionObject> applier) {
		this.icon = icon;
		try {
			this.dataClass = Class.forName((Version.ATLEAST_1_13 ? "org.bukkit.block.data." : "org.bukkit.material.") + dataClass);
			this.checker = checker;
			this.applier = applier;
		} catch (Throwable ignored) {
			this.dataClass = null;
			this.checker = checker;
			this.applier = applier;
		}
	}

	// ----- get
	@Override
	public boolean requireParam() {
		return true;
	}

	public Mat getIcon() {
		return icon;
	}

	// ----- check
	public boolean has(BlockState blockState, org.bukkit.block.BlockState block) {
		// not supported
		if (dataClass == null) {
			GCore.inst().getMainLogger().error("Can't check block state '" + name() + " " + StringUtils.toTextString(" ", blockState.getArguments()) + "', it's not supported on version " + Version.CURRENT);
			return false;
		}
		try {
			// invalid state
			ReflectionObject state = ReflectionObject.of(block);
			ReflectionObject data = state.get() == null ? null : state.invokeMethod(Version.ATLEAST_1_13 ? "getBlockData" : "getData");
			if (data == null || !ObjectUtils.instanceOf(data.justGet(), dataClass)) {
				return false;
			}
			// check
			return checker.apply(blockState, data);
		} catch (Throwable exception) {
			GCore.inst().getMainLogger().error("Can't check block state '" + name() + " " + StringUtils.toTextString(" ", blockState.getArguments()) + "' for block " + Mat.fromBlock(block) + " on version " + Version.CURRENT, exception);
			return false;
		}
	}

	// ----- set
	public void set(BlockState blockState, Block block) {
		// not supported
		if (dataClass == null) {
			GCore.inst().getMainLogger().error("Can't set block state '" + name() + " " + StringUtils.toTextString(" ", blockState.getArguments()) + "', it's not supported on version " + Version.CURRENT);
			return;
		}
		try {
			// invalid state
			ReflectionObject state = ReflectionObject.of(block.getState());
			ReflectionObject data = state.get() == null ? null : state.invokeMethod(Version.ATLEAST_1_13 ? "getBlockData" : "getData");
			if (data == null || !ObjectUtils.instanceOf(data.justGet(), dataClass)) {
				GCore.inst().getMainLogger().error("Can't set block state '" + name() + " " + StringUtils.toTextString(" ", blockState.getArguments()) + "', block " + Mat.fromBlock(block) + " doesn't match");
				return;
			}
			// apply and update state
			applier.accept(blockState, data);
			state.invokeMethod(Version.ATLEAST_1_13 ? "setBlockData" : "setData", data.justGet());
			state.invokeMethod("update", true);
		} catch (Throwable exception) {
			GCore.inst().getMainLogger().error("Can't set block state '" + name() + " " + StringUtils.toTextString(" ", blockState.getArguments()) + "' to block " + Mat.fromBlock(block) + " on version " + Version.CURRENT, exception);
		}
	}

}
