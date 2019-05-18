package de.bitsandbooks.nel.nelcorrector.listener;

import java.util.EventListener;

public interface PageChangedListener extends EventListener {
	
	public void pageChanged(PageChangedEvent e);

}
