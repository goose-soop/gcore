package com.guillaumevdn.gcore.lib.collection;

/**
 * @author GuillaumeVDN
 */
public final class IteratorControls {

	private boolean remove = false;
	private boolean stop = false;

	public boolean mustRemove() {
		return remove;
	}

	public boolean mustStop() {
		return stop;
	}

	public void remove() {
		remove = true;
	}

	public void stop() {
		stop = true;
	}

	public void removeAndStop() {
		remove = true;
		stop = true;
	}

	public void reset() {
		remove = false;
		stop = false;
	}

}
