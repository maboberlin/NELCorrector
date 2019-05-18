package de.bitsandbooks.nel.nelcorrector.listener;

import java.util.EventListener;

public interface BibliographyFormatListener extends EventListener {
	
	public void formatIsSet(BibliographyFormatEvent e);

}
