package com.guillaumevdn.gcore.lib.util;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.data.GUser;
import com.guillaumevdn.gcore.data.UserInfo;
import com.guillaumevdn.gcore.lib.data.DataManager.Callback;

public abstract class PCUserOperator {

	// base
	private UserInfo info;
	private GUser user = null;
	private boolean offline = false;

	public PCUserOperator(UserInfo info) {
		this.info = info;
	}

	// methods
	public void operate() {
		// get quests
		user = GUser.get(info);
		if (user != null) {// online
			process();
		} else {// offline, load
			GCore.inst().getData().getUsers().loadUser(info, new Callback() {
				@Override
				public void callback() {
					user = GUser.get(info);
					offline = true;
					process();
				}
			});
		}
	}

	private void process() {
		// process
		process(user);
		// unload eventually
		if (offline) {
			GCore.inst().getData().getUsers().unloadUser(info);
		}
	}

	// abstract methods
	protected abstract void process(GUser user);

}
