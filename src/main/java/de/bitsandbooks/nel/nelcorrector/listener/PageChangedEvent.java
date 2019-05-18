package de.bitsandbooks.nel.nelcorrector.listener;

import java.util.EventObject;

import de.bitsandbooks.nel.interface1.Page;

public class PageChangedEvent extends EventObject {
	
	public Page page;

	public PageChangedEvent(Object source, Page page) {
		super(source);
		this.page = page;
	}

}
