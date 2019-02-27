package be.pyrrh4.pyrcore;

import be.pyrrh4.pyrcore.lib.Perm;

public class PCPerm {

	public static final Perm PYRCORE_ROOT = new Perm(null, "pyrcore.*");
	public static final Perm PYRCORE_ADMIN = new Perm(PYRCORE_ROOT, "pyrcore.admin");

}
