package com.guillaumevdn.gcore.integration.deluxechat;

import com.guillaumevdn.gcore.lib.function.ThrowableRunnable;
import com.guillaumevdn.gcore.lib.integration.Integration;
import com.guillaumevdn.gcore.lib.integration.IntegrationInstance;

/**
 * @author GuillaumeVDN
 */
public class IntegrationDeluxeChat extends IntegrationInstance {

	public IntegrationDeluxeChat(Integration integration) {
		super(integration);
	}

	@Override
	public boolean activate() {
		int listenerCount = 0;
		// DeluxeChat 1.13.2 + 1.15
		listenerCount += attemptRegister(() -> ListenerDeluxeChatEvent.register(this));
		listenerCount += attemptRegister(() -> ListenerDeluxeChatJSONEvent.register(this));
		listenerCount += attemptRegister(() -> ListenerChatToPlayerEvent.register(this));
		// DeluxeChat 1.15
		listenerCount += attemptRegister(() -> ListenerPrivateMessageEvent.register(this));
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
