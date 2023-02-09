package com.guillaumevdn.gcore.lib.data.board;

/**
 * @author GuillaumeVDN
 */
public enum BoardType {

	LOCAL,  // everything is loaded here, keeping an entire local valuesCache
	REMOTE;  // everything is not loaded here, elements are loaded when needed

}
