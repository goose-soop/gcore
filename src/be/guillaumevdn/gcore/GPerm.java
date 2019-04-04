package be.guillaumevdn.gcore;

import be.guillaumevdn.gcore.lib.Perm;

public class GPerm {

	public static final Perm GCORE_ROOT = new Perm(null, "gcore.*");
	public static final Perm GCORE_ADMIN = new Perm(GCORE_ROOT, "gcore.admin");

}
