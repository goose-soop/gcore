package com.guillaumevdn.gcore.lib.compatibility.material;

import org.bukkit.inventory.ItemStack;

import com.guillaumevdn.gcore.lib.compatibility.Compat;
import com.guillaumevdn.gcore.lib.compatibility.Version;
import com.guillaumevdn.gcore.lib.reflection.Reflection;
import com.guillaumevdn.gcore.lib.reflection.procedure.ReflectionProcedureBiFunction;
import com.guillaumevdn.gcore.lib.reflection.procedure.ReflectionProcedureFunction;

/**
 * @author GuillaumeVDN
 */
public final class MatCompatItem {

	// ----- create stack
	private static final ReflectionProcedureFunction<Mat, ItemStack> CREATE_STACK = new ReflectionProcedureFunction<Mat, ItemStack>()
			.setIf(Version.ATLEAST_1_13, mat -> {
				return new ItemStack(mat.getData().getDataInstance());
			})
			.orElse(mat -> {
				ItemStack item = Reflection.newInstance("org.bukkit.inventory.ItemStack", Reflection
						.params(!mat.getData().hasLegacyData(), mat.getData().getDataInstance(), 1)
						.orElse(mat.getData().getDataInstance(), 1, (short) 0, (byte) mat.getData().getLegacyDataOrZero())
						.get())
						.get(ItemStack.class);
				return item;
			});

	public static ItemStack createStack(Mat mat) {
		return CREATE_STACK.process(mat);
	}

	// ----- match
	private static final ReflectionProcedureBiFunction<ItemStack, Mat, Boolean> MATCH = new ReflectionProcedureBiFunction<ItemStack, Mat, Boolean>()
			.setIf(Version.ATLEAST_1_13, (item, mat) -> item.getType().equals(mat.getData().getDataInstance()))
			.orElse((item, mat) -> item.getType().equals(mat.getData().getDataInstance()) && (Version.ATLEAST_1_13 || mat.getData().acceptsLegacyData(Compat.getLegacyData(item))));

	public static boolean match(ItemStack item, Mat mat) {
		return mat.isNoCheck() || MATCH.process(item, mat);
	}

}
