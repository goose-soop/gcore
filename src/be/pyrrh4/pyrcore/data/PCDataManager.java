package be.pyrrh4.pyrcore.data;

import org.bukkit.event.Listener;

import be.pyrrh4.pyrcore.PyrCore;
import be.pyrrh4.pyrcore.lib.data.DataManager;

public class PCDataManager extends DataManager implements Listener {

	// base
	private DataProfiles dataProfiles = null;
	private Statistics statistics = null;

	public PCDataManager(BackEnd backend) {
		super(PyrCore.inst(), backend);
	}

	// get
	public DataProfiles getDataProfiles() {
		return dataProfiles;
	}

	public Statistics getStatistics() {
		return statistics;
	}

	// methods
	@Override
	protected void innerEnable() {
		// data profiles
		this.dataProfiles = new DataProfiles();
		dataProfiles.initAsync(new Callback() { @Override public void callback() {
			dataProfiles.pullAsync();
		}});
		// statistics
		this.statistics = new Statistics();
		statistics.initAsync(new Callback() { @Override public void callback() {
			statistics.pullAsync();
		}});
	}

	@Override
	protected void innerSynchronize() {
		dataProfiles.pullAsync();
		statistics.pullAsync();
	}

	@Override
	protected void innerReset() {
		dataProfiles.clearAll();
		statistics.clearAll();
	}

	@Override
	protected void innerDisable() {
		this.dataProfiles = null;
		this.statistics = null;
	}

}
