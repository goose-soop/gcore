package com.guillaumevdn.gcore.lib.element.struct;

/**
 * @author GuillaumeVDN
 */
public interface IElement {

	String getId();

	String getTypeName();

	String getConfigurationPath();

	SuperElement getSuperElement();

	boolean hasParseableLocations();

}
