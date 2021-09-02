package com.guillaumevdn.gcore.lib.statistic;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

import com.guillaumevdn.gcore.lib.collection.CollectionUtils;
import com.guillaumevdn.gcore.lib.collection.LowerCaseHashMap;
import com.guillaumevdn.gcore.lib.concurrency.RWHashMap;
import com.guillaumevdn.gcore.lib.object.ObjectUtils;

/**
 * @author GuillaumeVDN
 */
public final class Statistic {

	private String id;
	private RWHashMap<UUID, Object> locks = new RWHashMap<>(1, 1f);  // this is to avoid fetching and manipulating statistics at the same time, in things such as logic objects in QC, causing for instance two formulas being applied at the same time on an original value (and thus overrides / points loss)

	private Statistic(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void lock(UUID player, Runnable action) {
		Object lock = locks.computeIfAbsent(player, __ -> new Object());
		synchronized (lock) {
			action.run();
		}
	}

	// ----- object

	@Override
	public String toString() {
		return getId();
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		Statistic other = ObjectUtils.castOrNull(obj, Statistic.class);
		return other != null && Objects.deepEquals(id, other.id);
	}

	// ----- registration

	private static LowerCaseHashMap<Statistic> registered = new LowerCaseHashMap<>(10, 0.75f);

	public static Collection<Statistic> values() {
		return Collections.unmodifiableList(CollectionUtils.asList(registered.values()));
	}

	public static Statistic safeValueOf(String id) {
		return registered.get(id);
	}

	public static Statistic valueOf(String id) throws IllegalArgumentException {
		Statistic stat = registered.get(id);
		if (stat == null) throw new IllegalArgumentException("there's no statistic with id " + id);
		return stat;
	}

	public static Statistic register(String id) {
		Statistic statistic = new Statistic(id);
		registered.put(id, statistic);
		return statistic;
	}

}
