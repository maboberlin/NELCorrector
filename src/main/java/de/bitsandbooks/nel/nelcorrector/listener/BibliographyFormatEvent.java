package de.bitsandbooks.nel.nelcorrector.listener;

import java.util.EventObject;

public class BibliographyFormatEvent extends EventObject {
	
	public int entryMode;
	public boolean cancelBibliographys;
	public boolean printComma;

	public BibliographyFormatEvent(Object source, int entryMode, boolean cancelBibliographys, boolean printComma) {
		super(source);
		this.entryMode = entryMode;
		this.cancelBibliographys = cancelBibliographys;
		this.printComma = printComma;
	}

}
