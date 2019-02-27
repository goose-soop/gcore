package be.pyrrh4.pyrcore.lib.loadable;

import be.pyrrh4.pyrcore.lib.configuration.YMLConfiguration;

public interface Creatable<T> {

	public LoadResult<T> createNew(Loadable<?> parent, String id);
	public LoadResult<T> createNew(Loadable<?> parent, String id, YMLConfiguration config, String configPath, String configErrorPrefix);

}
