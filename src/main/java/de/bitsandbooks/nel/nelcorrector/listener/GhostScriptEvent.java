package de.bitsandbooks.nel.nelcorrector.listener;

import java.util.EventObject;

public class GhostScriptEvent extends EventObject {

	public boolean ghostScriptSet;
	
	public GhostScriptEvent(Object source, boolean scriptSet) {
		super(source);
		ghostScriptSet = scriptSet;
	}

}
