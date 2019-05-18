package de.bitsandbooks.nel.nelcorrector.util;

import javax.swing.text.DefaultCaret;

public class PersistentCaret extends DefaultCaret {
	
	public PersistentCaret() {
		super();
		setBlinkRate(500);
	}
	
	@Override
	public void setSelectionVisible(boolean visible) {
		super.setSelectionVisible(true);
	}
}
