package com.guillaumevdn.gcore.lib.material;

import com.guillaumevdn.gcore.lib.util.collection.SortedMap;

public class MatDurabilities {

	// base
	private SortedMap<Integer, Mat> durabilities = new SortedMap<Integer, Mat>(SortedMap.Type.KEY_SORTED, SortedMap.Order.NATURAL);

	public MatDurabilities(Mat... mats) {
		if (mats != null) {
			for (Mat mat : mats) {
				add(mat);
			}
		}
	}

	// get
	public boolean hasDurability(int durability) {
		return durabilities.containsKey(durability);
	}

	public Mat withDurability(int durability) {
		return durabilities.get(durability);
	}

	public Mat withDurabilityOrDefault(int durability) {
		return hasDurability(durability) ? durabilities.get(durability) : withDefaultDurability();
	}

	public Mat withDefaultDurability() {
		return durabilities.get(durabilities.getKeyAt(0));
	}

	public void add(Mat mat) {
		durabilities.put(mat.getDurability(), mat);
	}

}
