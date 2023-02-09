package com.guillaumevdn.gcore.lib.object;

/**
 * @author GuillaumeVDN
 */
public class OptionalIfPresentFail {

	private boolean wasPresent;

	public OptionalIfPresentFail(boolean wasPresent) {
		this.wasPresent = wasPresent;
	}

	// ----- do
	public void orDo(Runnable ifAbsent) {
		if (!wasPresent) {
			ifAbsent.run();
		}
	}

}
