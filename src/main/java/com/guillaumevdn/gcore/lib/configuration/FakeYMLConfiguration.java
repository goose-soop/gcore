package com.guillaumevdn.gcore.lib.configuration;

import java.io.File;

import com.guillaumevdn.gcore.lib.GPlugin;

/**
 * @author GuillaumeVDN
 */
public final class FakeYMLConfiguration extends YMLConfiguration {

	public FakeYMLConfiguration(GPlugin plugin) {
		super(plugin);
	}

	@Override
	public void load() {
	}

	@Override
	public void save(File file) {
	}

}
