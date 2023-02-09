package com.guillaumevdn.gcore.lib.gui.element.item.overrideclick;

import java.util.function.Consumer;
import java.util.function.Function;

import org.bukkit.Bukkit;

import com.guillaumevdn.gcore.lib.compatibility.material.Mat;
import com.guillaumevdn.gcore.lib.element.type.basic.LinearObjectType;
import com.guillaumevdn.gcore.lib.gui.struct.ClickCall;

/**
 * @author GuillaumeVDN
 */
public enum OverrideClickType implements LinearObjectType {

	NONE(false, null, action -> null),
	COMMANDS_AS_SERVER(false, null, action -> call -> {
		for (String command : action.getValidArguments(0).split(",")) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("{player}", call.getClicker().getName()));
		}
	}),
	COMMANDS_AS_PLAYER(false, null, action -> call -> {
		for (String command : action.getValidArguments(0).split(",")) {
			Bukkit.dispatchCommand(call.getClicker(), command.replace("{player}", call.getClicker().getName()));
		}
	})
	;

	private boolean requiredParam;
	private Mat icon;
	private Function<OverrideClick, Consumer<ClickCall>> handlerBuilder;

	OverrideClickType(boolean requiredParam, Mat icon, Function<OverrideClick, Consumer<ClickCall>> handlerBuilder) {
		this.requiredParam = requiredParam;
		this.icon = icon;
		this.handlerBuilder = handlerBuilder;
	}

	// ----- get
	@Override
	public boolean requireParam() {
		return requiredParam;
	}

	public Mat getIcon() {
		return icon;
	}

	public Consumer<ClickCall> buildHandler(OverrideClick action) {
		return handlerBuilder == null ? null : handlerBuilder.apply(action);
	}

}
