package com.guillaumevdn.gcore.integration.chatcontrol;

import org.bukkit.Bukkit;

import com.guillaumevdn.gcore.lib.function.ThrowableRunnable;
import com.guillaumevdn.gcore.lib.integration.Integration;
import com.guillaumevdn.gcore.lib.integration.IntegrationInstance;

/**
 * @author GuillaumeVDN
 */
public class IntegrationChatControl extends IntegrationInstance {

	public IntegrationChatControl(Integration integration) {
		super(integration);
	}

	@Override
	public boolean activate() {
		int listenerCount = 0;
		// ChatControl premium
		listenerCount += attemptRegister(() -> Bukkit.getPluginManager().registerEvents(new ListenerChatChannelEvent(), getIntegration().getPlugin()));
		listenerCount += attemptRegister(() -> Bukkit.getPluginManager().registerEvents(new ListenerPreMessagePrivateEvent(), getIntegration().getPlugin()));
		// ChatControl free
		listenerCount += attemptRegister(() ->Bukkit.getPluginManager().registerEvents(new ListenerCompatPlayerChatEvent(), getIntegration().getPlugin()));
		// done
		return listenerCount != 0;
	}

	private int attemptRegister(ThrowableRunnable runnable) {
		try {
			runnable.run();
			return 1;
		} catch (Throwable ignored) {
			return 0;
		}
	}

	@Override
	public void deactivate() {
	}

}
