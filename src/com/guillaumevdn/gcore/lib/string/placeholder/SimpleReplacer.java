package com.guillaumevdn.gcore.lib.string.placeholder;

/**
 * @author GuillaumeVDN
 */
public class SimpleReplacer implements Replacer {

	private ReplacerData data;

	public SimpleReplacer(ReplacerData data) {
		this.data = data;
	}

	@Override
	public ReplacerData getReplacerData() {
		return data;
	}

}
