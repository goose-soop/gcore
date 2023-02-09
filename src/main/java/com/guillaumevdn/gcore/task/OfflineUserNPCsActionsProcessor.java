package com.guillaumevdn.gcore.task;

import java.util.UUID;

import com.guillaumevdn.gcore.data.usernpcs.UserNPCs;
import com.guillaumevdn.gcore.lib.concurrency.RWHashMap;
import com.guillaumevdn.gcore.lib.concurrency.RWWrapper;
import com.guillaumevdn.gcore.lib.function.ThrowableRunnable;

/**
 * @author GuillaumeVDN
 */
public class OfflineUserNPCsActionsProcessor implements ThrowableRunnable {

	private RWHashMap<UUID, RWWrapper<Boolean>> processingAction = new RWHashMap<>(5, 1f);

	@Override
	public void run() {
		UserNPCs.offlineActionsQueue.forEach((uuid, actions) -> {
			if (!actions.isEmpty()) {
				RWWrapper<Boolean> processing = processingAction.computeIfAbsent(uuid, __ -> RWWrapper.of(false));
				if (!processing.get()) {
					processing.set(true);
					actions.remove(0).accept(() -> {
						processing.set(false);
					});
				}
			}
		});
	}

}
