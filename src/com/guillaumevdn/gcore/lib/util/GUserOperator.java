package com.guillaumevdn.gcore.lib.util;

import com.guillaumevdn.gcore.GCore;
import com.guillaumevdn.gcore.data.GUser;
import com.guillaumevdn.gcore.data.UserInfo;
import com.guillaumevdn.gcore.lib.data.DataManager.Callback;
import com.guillaumevdn.gcore.lib.event.GUserPulledEvent;

public abstract class GUserOperator {

	// base
	private UserInfo info;
	private GUser user = null;
	private boolean offline = false;

	public GUserOperator(UserInfo info) {
		this.info = info;
	}

	// methods
	public void operate() {
		user = GUser.get(info);
		if (user != null) {// online
			process();
		} else {// offline, load
			GCore.inst().getData().getUsers().loadUser(info, GUserPulledEvent.Context.USER_OPERATOR, new Callback() {
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
