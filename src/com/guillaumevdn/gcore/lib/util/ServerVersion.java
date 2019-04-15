package com.guillaumevdn.gcore.lib.util;

import com.guillaumevdn.gcore.lib.versioncompat.Compat;
import com.guillaumevdn.gcore.lib.versioncompat.Compat1_10;
import com.guillaumevdn.gcore.lib.versioncompat.Compat1_11;
import com.guillaumevdn.gcore.lib.versioncompat.Compat1_12;
import com.guillaumevdn.gcore.lib.versioncompat.Compat1_13;
import com.guillaumevdn.gcore.lib.versioncompat.Compat1_13_1;
import com.guillaumevdn.gcore.lib.versioncompat.Compat1_7_10;
import com.guillaumevdn.gcore.lib.versioncompat.Compat1_7_2;
import com.guillaumevdn.gcore.lib.versioncompat.Compat1_7_9;
import com.guillaumevdn.gcore.lib.versioncompat.Compat1_8;
import com.guillaumevdn.gcore.lib.versioncompat.Compat1_9;
import com.guillaumevdn.gcore.lib.versioncompat.Compat1_9_4;
import com.guillaumevdn.gcore.lib.versioncompat.npc.NpcProtocols;
import com.guillaumevdn.gcore.lib.versioncompat.npc.NpcProtocols1_10;
import com.guillaumevdn.gcore.lib.versioncompat.npc.NpcProtocols1_11;
import com.guillaumevdn.gcore.lib.versioncompat.npc.NpcProtocols1_12;
import com.guillaumevdn.gcore.lib.versioncompat.npc.NpcProtocols1_13;
import com.guillaumevdn.gcore.lib.versioncompat.npc.NpcProtocols1_13_1;
import com.guillaumevdn.gcore.lib.versioncompat.npc.NpcProtocols1_9;
import com.guillaumevdn.gcore.lib.versioncompat.npc.NpcProtocols1_9_4;
import com.guillaumevdn.gcore.lib.versioncompat.particle.ParticleManager;
import com.guillaumevdn.gcore.lib.versioncompat.particle.ParticleManager1_13;
import com.guillaumevdn.gcore.lib.versioncompat.particle.ParticleManager1_7_10;
import com.guillaumevdn.gcore.lib.versioncompat.particle.ParticleManager1_7_2;
import com.guillaumevdn.gcore.lib.versioncompat.particle.ParticleManager1_7_9;
import com.guillaumevdn.gcore.lib.versioncompat.particle.ParticleManager1_8;
import com.guillaumevdn.gcore.lib.versioncompat.particle.ParticleManager1_9;

public enum ServerVersion {

	// versions
	UNSUPPORTED("UNSUPPORTED", Compat1_8.class, ParticleManager1_8.class, null),
	MC_1_7_2("1.7.2", Compat1_7_2.class, ParticleManager1_7_2.class, null),
	MC_1_7_9("1.7.9", Compat1_7_9.class, ParticleManager1_7_9.class, null),
	MC_1_7_10("1.7.10", Compat1_7_10.class, ParticleManager1_7_10.class, null),
	MC_1_8("1.8", Compat1_8.class, ParticleManager1_8.class, null),
	MC_1_9("1.9", Compat1_9.class, ParticleManager1_9.class, NpcProtocols1_9.class),
	MC_1_9_4("1.9.4", Compat1_9_4.class, ParticleManager1_9.class, NpcProtocols1_9_4.class),
	MC_1_10("1.10", Compat1_10.class, ParticleManager1_9.class, NpcProtocols1_10.class),
	MC_1_11("1.11", Compat1_11.class, ParticleManager1_9.class, NpcProtocols1_11.class),
	MC_1_12("1.12", Compat1_12.class, ParticleManager1_9.class, NpcProtocols1_12.class),
	MC_1_13("1.13", Compat1_13.class, ParticleManager1_13.class, NpcProtocols1_13.class),
	MC_1_13_1("1.13.1", Compat1_13_1.class, ParticleManager1_13.class, NpcProtocols1_13_1.class),
	MC_1_13_2("1.13.2", Compat1_13_1.class, ParticleManager1_13.class, NpcProtocols1_13_1.class);
	;

	// current
	public static final ServerVersion CURRENT = Utils.getServerVersion();
	public static final ServerVersion HIGHEST = Utils.getHighestServerVersion();

	// bases
	private String name;
	private Class<? extends Compat> compatClass;
	private Class<? extends ParticleManager> particleCompatClass;
	private Class<? extends NpcProtocols> npcProtocolsClass;

	private ServerVersion(String name, Class<? extends Compat> compatClass, Class<? extends ParticleManager> particleCompatClass, Class<? extends NpcProtocols> npcProtocolsClass) {
		this.name = name;
		this.compatClass = compatClass;
		this.particleCompatClass = particleCompatClass;
		this.npcProtocolsClass = npcProtocolsClass;
	}

	// get
	public String getName() {
		return name;
	}

	public Class<? extends Compat> getCompatClass() {
		return compatClass;
	}

	public Class<? extends ParticleManager> getParticleCompatClass() {
		return particleCompatClass;
	}

	public Class<? extends NpcProtocols> getNpcProtocolsClass() {
		return npcProtocolsClass;
	}

	// methods
	public boolean isOrLess(ServerVersion other) {
		return ordinal() <= other.ordinal();
	}

	public boolean isLessThan(ServerVersion other) {
		return ordinal() < other.ordinal();
	}

	public boolean isAtLeast(ServerVersion other) {
		return ordinal() >= other.ordinal();
	}

	public boolean isGreaterThan(ServerVersion other) {
		return ordinal() > other.ordinal();
	}
}