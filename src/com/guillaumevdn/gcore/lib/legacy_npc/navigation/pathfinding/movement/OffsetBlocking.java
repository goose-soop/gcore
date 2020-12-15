package com.guillaumevdn.gcore.lib.legacy_npc.navigation.pathfinding.movement;

import java.util.Objects;

import com.guillaumevdn.gcore.lib.object.ObjectUtils;

/**
 * @author GuillaumeVDN
 */
public final class OffsetBlocking {

	private int x, z;
	private Integer backupX, backupZ;

	public OffsetBlocking(int x, int z) {
		if (x == 0 && z == 0) {
			throw new IllegalArgumentException("invalid offset blocking (x " + x + ", z " + z + ")");
		}
		this.x = x;
		this.z = z;
		this.backupX = null;
		this.backupZ = null;
	}

	public OffsetBlocking(int x, int z, int backupX, int backupZ) {
		if (Math.abs(x) > 1 || Math.abs(z) > 1 || Math.abs(backupX) > 1 || Math.abs(backupZ) > 1 || (x == 0 && z == 0) || (backupX == 0 && backupZ == 0)) {
			throw new IllegalArgumentException("invalid offset blocking (x " + x + ", z " + z + ", backupX " + backupX + ", backupZ " + backupZ + ")");
		}
		this.x = x;
		this.z = z;
		this.backupX = x;
		this.backupZ = z;
	}

	// get
	public int getX() {
		return x;
	}

	public int getZ() {
		return z;
	}

	public boolean hasBackup() {
		return backupX != null && backupZ != null;
	}

	public Integer getBackupX() {
		return backupX;
	}

	public Integer getBackupZ() {
		return backupZ;
	}

	// obj
	@Override
	public int hashCode() {
		return Objects.hash(x, z, backupX, backupZ);
	}

	@Override
	public boolean equals(Object obj) {
		return ObjectUtils.equals(obj, OffsetBlocking.class, other -> other.x == x && other.z == z && other.backupX == backupX && other.backupZ == backupZ);
	}

	@Override
	public String toString() {
		return "(" + x + "," + z + (hasBackup() ? "/" + backupX + "," + backupZ : "") + ")";
	}

}
