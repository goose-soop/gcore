package com.guillaumevdn.gcore.lib.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.lib.material.Mat;

/**
 * @author GuillaumeVDN
 */
public class StateSetting {

	// base
	private Type type;
	private String setting;

	public StateSetting(Type type) {
		this(type, "0");// \o/
	}

	public StateSetting(Type type, String setting) {
		this.type = type;
		this.setting = setting;
	}

	// get
	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getSetting() {
		return setting;
	}

	public void setSetting(String setting) {
		this.setting = setting;
	}

	// apply
	public void set(Block block) {
		try {
			// not supported
			Class<?> clazz;
			try {
				clazz = Class.forName(type.dataClass);
			} catch (Throwable ignored) {
				GCore.inst().error("Can't set block state '" + type.name() + " " + setting + "' to block " + Mat.fromBlock(block) + ", not supported");
				return;
			}
			// no state
			BlockState state = block.getState();
			Object data = state == null ? null : state.getClass().getMethod(ServerVersion.IS_1_13 ? "getBlockData" : "getData").invoke(state);
			if (!Utils.instanceOf(data, clazz)) {
				GCore.inst().error("Can't set block state '" + type.name() + " " + setting + "' to block " + Mat.fromBlock(block) + ", invalid block");
				return;
			}
			// get setting
			Object setting;
			try {
				setting = type.requireSetting() ? type.getSettingDecoder().decode(this.setting.trim()) : null;
			} catch (Throwable exception) {
				exception.printStackTrace();
				GCore.inst().error("Can't set block state '" + type.name() + " " + this.setting + "' to block " + Mat.fromBlock(block) + ", can't parse setting");
				return;
			}
			// apply
			type.getApplier().apply(data, setting);
			// update state
			state.getClass().getMethod(ServerVersion.IS_1_13 ? "setBlockData" : "setData").invoke(state, data);
			state.update(true);
		} catch (Throwable exception) {
			exception.printStackTrace();
			GCore.inst().error("Couldn't set block state '" + type.name() + " " + setting + "' to block " + Mat.fromBlock(block) + ", unknown error");
		}
	}

	public boolean has(Block block) {
		try {
			// not supported
			Class<? extends BlockState> clazz;
			try {
				clazz = (Class<? extends BlockState>) Class.forName(type.dataClass);
			} catch (Throwable ignored) {
				GCore.inst().error("Can't check block state '" + type.name() + " " + setting + "' to block " + Mat.fromBlock(block) + ", not supported");
				return false;
			}
			// no state
			BlockState state = block.getState();
			Object data = state == null ? null : state.getClass().getMethod(ServerVersion.IS_1_13 ? "getBlockData" : "getData").invoke(state);
			if (!Utils.instanceOf(data, clazz)) {
				GCore.inst().error("Can't check block state '" + type.name() + " " + setting + "' to block " + Mat.fromBlock(block) + ", invalid block");
				return false;
			}
			// get setting
			Object setting;
			try {
				setting = type.requireSetting() ? type.getSettingDecoder().decode(this.setting.trim()) : null;
			} catch (Throwable exception) {
				exception.printStackTrace();
				GCore.inst().error("Can't check block state '" + type.name() + " " + this.setting + "' to block " + Mat.fromBlock(block) + ", can't parse setting");
				return false;
			}
			// check
			return type.getChecker().has(data, setting);
		} catch (Throwable exception) {
			exception.printStackTrace();
			GCore.inst().error("Couldn't check block state '" + type.name() + " " + setting + "' to block " + Mat.fromBlock(block) + ", unknown error");
			return false;
		}
	}

	// static methods
	public static boolean hasAll(List<StateSetting> states, Block block) {
		if (states == null) return false;
		for (StateSetting setting : states) {
			if (!setting.has(block)) {
				return false;
			}
		}
		return true;
	}

	public static void setAll(List<StateSetting> states, Block block) {
		if (states == null) return;
		for (StateSetting setting : states) {
			setting.set(block);
		}
	}

	public static List<StateSetting> decode(List<String> rawStates) {
		List<StateSetting> result = new ArrayList<>();
		if (rawStates == null) return result;
		for (String raw : rawStates) {
			try {
				String[] split = raw.split(" ");
				StateSetting.Type type = Utils.valueOfOrNull(StateSetting.Type.class, split[0]);
				if (type == null) {
					GCore.inst().error("Couldn't decode block state '" + raw + "', type " + split[0] + " is invalid");
				} else {
					result.add(new StateSetting(type, split[1]));
				}
			} catch (Throwable exception) {
				exception.printStackTrace();
				GCore.inst().error("Couldn't decode block state '" + raw + "', unknown error");
			}
		}
		return result;
	}

	// type
	public static enum Type {

		AGE("Ageable",
				(data, setting) -> {
					data.getClass().getMethod("setAge", int.class).invoke(setting);
				},
				(data, setting) -> {
					return (int) data.getClass().getMethod("getAge").invoke(data) == (int) setting;
				},
				raw -> {
					return Integer.parseInt(raw);
				}
				),
		ATTACHED("Attachable",
				(data, setting) -> {
					data.getClass().getMethod("setAttached", boolean.class).invoke(setting);
				},
				(data, setting) -> {
					return (boolean) data.getClass().getMethod("isAttached").invoke(data) == (boolean) setting;
				},
				raw -> {
					return Boolean.parseBoolean(raw);
				}
				),
		AXIS("Orientable",
				(data, setting) -> {
					data.getClass().getMethod("setAxis", setting.getClass()).invoke(setting);
				},
				(data, setting) -> {
					return data.getClass().getMethod("getAge").invoke(data).equals(setting);
				},
				raw -> {
					return Utils.unsafeValueOfOrNull(Class.forName("org.bukkit.Axis"), raw);
				}
				),
		BISECTED_HALF("Bisected",
				(data, setting) -> {
					data.getClass().getMethod("setHalf", setting.getClass()).invoke(setting);
				},
				(data, setting) -> {
					return data.getClass().getMethod("getHalf").invoke(data).equals(setting);
				},
				raw -> {
					return Utils.unsafeValueOfOrNull(Class.forName("org.bukkit.block.data.Bisected.Half"), raw);
				}
				),
		FACING("Directional",
				(data, setting) -> {
					data.getClass().getMethod("setFacing", BlockFace.class).invoke(setting);
				},
				(data, setting) -> {
					return data.getClass().getMethod("getFacing").invoke(data).equals(setting);
				},
				raw -> {
					return Utils.valueOfOrNull(BlockFace.class, raw);
				}
				),
		FACING_MULTIPLE("MultipleFacing",
				(data, setting) -> {
					List<BlockFace> faces = (List<BlockFace>) setting;
					Method method = data.getClass().getMethod("setFace", BlockFace.class, boolean.class);
					for (BlockFace face : faces) {
						method.invoke(data, face, true);
					}
				},
				(data, setting) -> {
					List<BlockFace> faces = (List<BlockFace>) setting;
					Set<BlockFace> blockFaces = (Set<BlockFace>) data.getClass().getMethod("getFaces").invoke(data);
					if (faces.size() != blockFaces.size()) {
						return false;
					}
					for (BlockFace face : blockFaces) {
						if (!blockFaces.contains(face)) {
							return false;
						}
					}
					return true;
				},
				raw -> {
					List<BlockFace> faces = new ArrayList<>();
					for (String str : Utils.split(",", raw, false)) {
						faces.add(BlockFace.valueOf(str.toUpperCase()));// throw an error if not existing
					}
					return faces;
				}
				),
		LIT("Lightable",
				(data, setting) -> {
					data.getClass().getMethod("setLit", boolean.class).invoke(setting);
				},
				(data, setting) -> {
					return (boolean) data.getClass().getMethod("isLit").invoke(data) == (boolean) setting;
				},
				raw -> {
					return Boolean.parseBoolean(raw);
				}
				),
		OPENED("Openable",
				(data, setting) -> {
					data.getClass().getMethod("setOpen", boolean.class).invoke(setting);
				},
				(data, setting) -> {
					return (boolean) data.getClass().getMethod("isOpen").invoke(data) == (boolean) setting;
				},
				raw -> {
					return Boolean.parseBoolean(raw);
				}
				),
		POWER("AnaloguePowerable",
				(data, setting) -> {
					data.getClass().getMethod("setPower", int.class).invoke(setting);
				},
				(data, setting) -> {
					return (int) data.getClass().getMethod("getPower").invoke(data) == (int) setting;
				},
				raw -> {
					return Integer.parseInt(raw);
				}
				),
		POWERED("Powerable",
				(data, setting) -> {
					data.getClass().getMethod("setPowered", boolean.class).invoke(setting);
				},
				(data, setting) -> {
					return (boolean) data.getClass().getMethod("isPowered").invoke(data) == (boolean) setting;
				},
				raw -> {
					return Boolean.parseBoolean(raw);
				})
		,
		RAIL_SHAPE("Rail",
				(data, setting) -> {
					data.getClass().getMethod("setShape", setting.getClass()).invoke(setting);
				},
				(data, setting) -> {
					return data.getClass().getMethod("getShape").invoke(data).equals(setting);
				},
				raw -> {
					return Utils.unsafeValueOfOrNull(Class.forName("org.bukkit.block.data.Rail.Shape"), raw);
				}
				),
		ROTATION("Rotatable",
				(data, setting) -> {
					data.getClass().getMethod("setRotation", BlockFace.class).invoke(setting);
				},
				(data, setting) -> {
					return data.getClass().getMethod("getRotation").invoke(data).equals(setting);
				},
				raw -> {
					return Utils.valueOfOrNull(BlockFace.class, raw);
				})
		,
		SNOWY("Snowy",
				(data, setting) -> {
					data.getClass().getMethod("setSnowy", boolean.class).invoke(setting);
				},
				(data, setting) -> {
					return (boolean) data.getClass().getMethod("isSnowy").invoke(data) == (boolean) setting;
				},
				raw -> {
					return Boolean.parseBoolean(raw);
				}
				),
		WATER_LEVEL("Levelled",
				(data, setting) -> {
					data.getClass().getMethod("setLevel", int.class).invoke(setting);
				},
				(data, setting) -> {
					return (int) data.getClass().getMethod("getLevel").invoke(data) == (int) setting;
				},
				raw -> {
					return Integer.parseInt(raw);
				}),
		WATERLOGGED("Waterlogged",
				(data, setting) -> {
					data.getClass().getMethod("setWaterlogged", boolean.class).invoke(setting);
				},
				(data, setting) -> {
					return (boolean) data.getClass().getMethod("isWaterlogged").invoke(data) == (boolean) setting;
				},
				raw -> {
					return Boolean.parseBoolean(raw);
				}
				)
		;

		private String dataClass;
		private Checker checker;
		private Applier applier;
		private SettingDecoder settingDecoder;

		private Type(String dataClass, Applier applier, Checker checker, SettingDecoder settingDecoder) {
			this.dataClass = (ServerVersion.IS_1_13 ? "org.bukkit.block.data." : "org.bukkit.material.") + dataClass;
			this.checker = checker;
			this.applier = applier;
			this.settingDecoder = settingDecoder;
		}

		public boolean requireSetting() {
			return settingDecoder != null;
		}

		public String getDataClass() {
			return dataClass;
		}

		public Checker getChecker() {
			return checker;
		}

		public Applier getApplier() {
			return applier;
		}

		public SettingDecoder getSettingDecoder() {
			return settingDecoder;
		}

	}

	public interface SettingDecoder {
		Object decode(String raw) throws Throwable;
	}

	public interface Applier {
		void apply(Object data, Object setting) throws Throwable;
	}

	public interface Checker {
		boolean has(Object data, Object setting) throws Throwable;
	}

}
