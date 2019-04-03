package be.pyrrh4.pyrcore.lib.util;

import be.pyrrh4.pyrcore.PyrCore;
import be.pyrrh4.pyrcore.data.PCUser;
import be.pyrrh4.pyrcore.data.UserInfo;
import be.pyrrh4.pyrcore.lib.data.DataManager.Callback;

public abstract class PCUserOperator {

	// base
	private UserInfo info;
	private PCUser user = null;
	private boolean offline = false;

	public PCUserOperator(UserInfo info) {
		this.info = info;
	}

	// methods
	public void operate() {
		// get quests
		user = PCUser.get(info);
		if (user != null) {// online
			process();
		} else {// offline, load
			PyrCore.inst().getData().getUsers().loadUser(info, new Callback() {
				@Override
				public void callback() {
					user = PCUser.get(info);
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
			PyrCore.inst().getData().getUsers().unloadUser(info);
		}
	}

	// abstract methods
	protected abstract void process(PCUser user);

}
