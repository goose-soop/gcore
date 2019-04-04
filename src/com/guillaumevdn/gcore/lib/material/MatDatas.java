package com.guillaumevdn.gcore.lib.material;

import com.guillaumevdn.gcore.lib.util.collection.SortedMap;

public class MatDatas {

	// base
	private SortedMap<Integer, MatDurabilities> datas = new SortedMap<Integer, MatDurabilities>(SortedMap.Type.KEY_SORTED, SortedMap.Order.NATURAL);

	public MatDatas(Mat... mats) {
		if (mats != null) {
			for (Mat mat : mats) {
				add(mat);
			}
		}
	}

	// get
	public boolean hasData(int legacyData) {
		return datas.containsKey(legacyData);
	}

	public MatDurabilities withData(int legacyData) {
		return datas.get(legacyData);
	}

	public MatDurabilities withDefaultData() {
		return datas.get(datas.getKeyAt(0));
	}

	public void add(Mat mat) {
		if (!datas.containsKey(mat.getLegacyData())) {
			datas.put(mat.getLegacyData(), new MatDurabilities());
		}
		datas.get(mat.getLegacyData()).add(mat);
	}

}
