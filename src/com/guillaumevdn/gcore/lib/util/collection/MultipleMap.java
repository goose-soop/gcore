package com.guillaumevdn.gcore.lib.util.collection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.guillaumevdn.gcore.lib.util.Pair;

public class MultipleMap<K, VA, VB> {

	private Map<K, Pair<VA, VB>> map = new HashMap<K, Pair<VA, VB>>();

	public void putA(K key, VA va) {
		put(key, va, null);
	}

	public void putB(K key, VB vb) {
		put(key, null, vb);
	}

	public void put(K key, VA va, VB vb) {
		if (map.containsKey(key)) {
			Pair<VA, VB> pair = map.get(key);
			if (va != null) pair.setA(va);
			if (vb != null) pair.setB(vb);
		} else {
			map.put(key, new Pair<VA, VB>(va, vb));
		}
	}

	public VA getA(K key) {
		return map.containsKey(key) ? map.get(key).getA() : null;
	}

	public VB getB(K key) {
		return map.containsKey(key) ? map.get(key).getB() : null;
	}

	public List<K> keySet() {
		return new ArrayList<K>(map.keySet());
	}

	public void clear() {
		map.clear();
	}

	public void remove(K key) {
		map.remove(key);
	}

}
